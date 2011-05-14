/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import SQLite.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

/**
 *
 * @author jakob
 */
public class DatabaseAccessor implements SQLite.Trace, SQLite.Profile {

    // member fields:
    String dbFile = "corpus/database";
    String unitsTable = "units"; //"Corpus";
    String qTable = "questions";
    String rTable = "replies";
    String unknownDB = "unknownDB";
    
    String cards = "cards";
    String cardColumn = "unitData";
    SQLite.Database db = new SQLite.Database(); // integer primary key becomes 'alias' for rowid.
    
    // schemas
    String[] querySchema = new String[] {"id integer primary key", "question text", "action text","actor integer", "object text", "queryId integer"};
    String[] responseSchema = new String[] {"id integer primary key", "queryId integer", "cPhrase text"};
    String[] schema = new String[] {"id integer primary key", "name text", cardColumn+" blob"}; // for the unit cards with info
    String[] learningSchema = new String[] {"id integer primary key", "newNouns text"};
    
    // insert finder strins:
    String blobCols = Tools.parseColumns(schema);//"id, name, type, "+ cardColumn;
    
    int lineCount;
    int blobSize;
    
    int trycount; // times to retry operation if fail.
    
    // constr
    /**
     * Constructor for the database access Object. Create once per session.
     * Will connect to the database file and
     * initiate tables if they don't already exist.
     * The variable names are set in code here.
     */
    public DatabaseAccessor(){
        this.db = new SQLite.Database();
        try {
            blobSize = (int)Math.pow(2,11); // in 2-power size; 2^11 = 2048
            db.open(dbFile, 0666); // what is the number?

            createTable(unitsTable, this.schema);
            createTable(qTable, this.querySchema);
            createTable(rTable, this.responseSchema);
            createTable(unknownDB, this.learningSchema);
            
            trycount=5; // init the try count
            
            
        } catch (SQLite.Exception ex) {
            // auto generated exception handling.
            System.err.println("Couldn't create Database Accessor!");
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    private void createTable(String tablename, String[] schema){
        try{
            // create the three tables (unit data, query table, response table)
            do_exec(db, "create table "+tablename+"("+Tools.mergeStrings(schema, ",") +")");
            // key is not the "TYPE" the 'id' is set to be the "primary key" though
	    
            }
            catch(SQLite.Exception ee){
                System.out.println("["+ee.getMessage()+"]");
            }
    }
    
    // methods:
    /**
     *
     * @param id
     */
    public void delete(int id){
        try {
            // delete the id
            String query = "delete from "+unitsTable+" where id="+id;
            do_exec(db, query);
        } catch (SQLite.Exception ex) {
            System.err.printf("Couldn't delete from database row with id: %d\n",id);
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public int updateItemCard(String cardName, String... fields) throws DatabaseException, IllegalArgumentException, IOException {
        int row=0;
        try{
        int[] matches = getRowNumbers("name:marine");//String.format("select * from %s where name='%s'",unitsTable, cardName.toLowerCase()));
        if (matches.length==0)
            throw new DatabaseException("Found no match for '"+cardName+"' in database.");

        if (matches.length > 1)
            System.err.println("WARNING. found more than 1 match. Using 1st occurrence");

        row = matches[0];
        ItemCard dbCard = getItemCard(cardName);

        dbCard.update(fields);

        // write back to database:
        overwrite(dbCard, row);
        }
        catch(SQLite.Exception e){
            System.err.println("Error when writing to database");
            System.err.println(e.getMessage());
        } catch(ItemCard.CardException e){
            System.err.println("Error when writing to database");
            System.err.println(e.getMessage());
        }
        return row;
    }
    /** Writes an itemCard to the ItemCard database table with the name and a unique id.
     *
     * @param card
     * @return the row number/ unique id of the new entry
     */
    public void overwrite(ItemCard card, int row) throws DatabaseException, IOException, SQLite.Exception {
            
            // get blob output stream
            Blob writer = db.open_blob("main", unitsTable, cardColumn, row, true);

            OutputStream writeStream = writer.getOutputStream();
            byte[] sCard = Tools.Serialise(card);
            writeStream.write(sCard);
            writeStream.close();
            writer.close();

    }
    public int write(ItemCard card) throws DatabaseException,IOException {
        try{
            if (getRowNumbers("name: "+card.name).length != 0)
                throw new DatabaseException("Card Already Exists in database. Use update() instead.");

            // ID is currently set to 1 more than # of rows.
            this.lineCount = do_get_rows()+1;
            String id = Integer.toString(lineCount); // what should this be?
            
            // Add row with empty blob (no row number ... )
            String[] vals = new String[] {id, card.name};
            do_exec(db, "insert into "+unitsTable+"("+blobCols+") values("+Tools.mergeStrings2(vals)+", zeroblob("+blobSize+"))");

            
            // get blob output stream 
            Blob writer = db.open_blob("main", unitsTable, cardColumn, lineCount, true);

            OutputStream writeStream = writer.getOutputStream();
            byte[] sCard = Tools.Serialise(card);
            writeStream.write(sCard);
            writeStream.close();
            writer.close();
            
        }
        catch(SQLite.Exception e){
            System.err.println("Couldn't write card to database.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return lineCount;
        
    }

    // helper methods:
    /**
     *
     * @param tablename
     */
    public void printDB(String tablename){
        try {
            Stmt stmt = db.prepare("select * from "+tablename);
            ArrayList<String[]> list = new ArrayList<String[]>();
            int ncol = stmt.column_count();
            int nrow = 0;
            
            while (stmt.step())
            {
                String[] row = new String[ncol];
                for (int i =0;i<ncol;i++)
                    row[i] = stmt.column_string(i);
                System.out.println(" -"+nrow+" "+Tools.mergeStrings2(row));
                list.add(row);
                nrow++;
            }
            stmt.close();
            list.toArray(new String[list.size()][]);
            //return list;
            
        } catch (SQLite.Exception ex) {
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // high level getter- setter methods:
    /**
     *
     * @param name
     * @return
     */
    public ItemCard getItemCard(String name) throws DatabaseException, SQLite.Exception{
        String query = "select id from "+unitsTable+" where name='"+name.toLowerCase()+"'";
        
        int lineNo = 0; // default
        lineNo = getInt(query); // get return integer from Query ('Count(*)')
        if (lineNo == 0)
            throw new DatabaseException("Coudln't find matching card in the database");
        
        return read(lineNo);        
    }

    /**
     *
     * @param searchIds
     * @return
     */
    public int getQid(String... searchIds){
        // try to get all rows which match.

        // if > 1 matches match with empty, the "lost rows"

        // if no matches throw exception
        return 0;
    }
    /**
     *
     * @param Qid
     * @return
     */
    public Response getCannedPhrase(int Qid){
        return new Response();
    }

    /**
     *
     * @param searchId
     * @return
     */
    private Object read(String searchId){
        String query = "select id from "+unitsTable+" where name='"+searchId+"'";
        
        int lineNo = 0; // default
        try {
            lineNo = getInt(query); // get from searchId
        } catch (SQLite.Exception ex) {
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return read(lineNo);        
    }
    /**
     *
     * @param lineNo
     * @return
     */
    public ItemCard read(int lineNo){
        // just read some line.
        try {

            Blob inputBlob = db.open_blob("main", unitsTable, cardColumn, lineNo, true);
            InputStream reader = inputBlob.getInputStream();
            byte[] byteFlow = new byte[blobSize];
            reader.read(byteFlow);
            reader.close();
            inputBlob.close();

            Object returnObject = Tools.DeSerialise(byteFlow);
            return (ItemCard)returnObject;
        } catch (java.lang.Exception ex) {
            System.err.println("Couldn't read blob from database");
            System.err.println(ex);
            ex.printStackTrace();
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    /** The input argument should be the searches you want to match with the
     * database separated by colon.
     * 
     * @param searchIds the string array with columns and search strings separated by colon.
     * @return an integer array of the matching row numbers (ids)
     * @throws SQLite.Exception
     */
    private int[] getRowNumbers(String... searchIds) throws SQLite.Exception, DatabaseException {
        String query = "select id from "+unitsTable;
        for(int i=0;i<searchIds.length;i++)
        {
            String[] s = searchIds[i].split(":");
            if (s.length!=2)
                throw new DatabaseException("search IDs must be two strings, a column name and a search string, separated by colon (:)");
            query = query+" where "+s[0].trim()+"='"+s[1].trim()+"'";
            
        }
        Stmt stmt = prep_ins(db, query);
        
        ArrayList<Integer> hits = new ArrayList<Integer>();
        while(stmt.step()){
            // assuming only 1 col
            hits.add(stmt.column_int(0));
            
        } // end of while
        int[] ret = new int[hits.size()];
        for (int i =0;i<hits.size();i++){
            ret[i] = hits.get(i);
        }
        return ret;
    }

    // primitive functions
    private String[] getRows(String... searchIds) throws SQLite.Exception {
        String query = "select * from "+unitsTable;
        for(int i=0;i<searchIds.length;i++)
        {
            String[] s = searchIds[i].split(":");
            query = query+" where "+s[0]+"='"+s[1]+"'";

        }
        Stmt stmt = prep_ins(db, query);

        ArrayList<String> hits = new ArrayList<String>();
        while(stmt.step()){
            String row = "";
            for (int i =0;i<stmt.column_count();i++)
                row = row+", "+stmt.column_string(i);
            hits.add(row);

        } // end of while
        String[] ret = hits.toArray(new String[hits.size()]);
        return ret;
    }
    private String[] getRows(String query) throws SQLite.Exception {
        
        Stmt statement = prep_ins(db, query);
        ArrayList<String> list = new ArrayList<String>();
        int ncol = statement.column_count();
                
        while(statement.step())
        {
            String[] row = new String[ncol];
            for (int i =0;i<ncol;i++)
                row[i] = statement.column_string(i);
            String stringrow = Tools.mergeStrings2(row, "|");
            list.add(stringrow);
        }
        statement.close();
        return list.toArray(new String[list.size()]);
        
    }
    /** Returns a string representation of the query posted.
     * for example a single column name. In the case of too much data, the first
     *  element will be returned;
    */
    private String getString(String query) throws SQLite.Exception {
        Stmt statement = prep_ins(db, query);
        String delim = " | ";
        while(statement.step())
        {
            if("text".equals(statement.column_decltype(0)))
            {
                String rets = "";
                for (int i = 0;i<statement.column_count();i++)
                    rets = rets+delim+statement.column_string(i);
                return rets;
            }
        }
        throw new SQLite.Exception("couldn't parse string type");
    }
    private int getInt(String query) throws SQLite.Exception {
        
        Stmt stmt = prep_ins(db, query);
        int outp = 0;
            try{
                stmt.step();
                for (int i = 0;i<stmt.column_count();i++)
                    outp = Integer.parseInt(stmt.column_string(i));
            } catch(NumberFormatException ee){
                System.err.println("Couldn't parse Integer");
                // couldn't parse this int...
            }
        
        return outp; // should return parsed number of rows!
    }
    
    /**
     * 
     * @param tablename
     */
    public void flushDB(String tablename){
        try {
            System.out.println("Are you sure, you want to deleta table "+tablename+" (answer true / false)");
            System.out.println("A backup will be made to: '"+tablename+"_bkp'.");
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            boolean accept = Boolean.parseBoolean(r.readLine());
            if(accept)
            {
                do_exec(db, "drop table "+tablename+"_bkp");
                createTable(tablename+"_bkp", this.schema);
                do_exec(db, "insert into "+tablename+"_bkp select * from "+tablename);
                do_exec(db, "drop table "+tablename);
            }
            else return;
        } catch (java.lang.Exception ex) {
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /// - - - - - - - -
    ///     INHERITED METHODS FROM JAVASQLITE EXAMPLE
    /// - - - - - - - -
    
    private int do_get_rows() throws SQLite.Exception {
        String statement = "select Count(*) from "+unitsTable;
        Stmt stmt = prep_ins(db, statement);
        int outp = 0;
            try{
                stmt.step();
                for (int i = 0;i<stmt.column_count();i++)
                    outp = Integer.parseInt(stmt.column_string(i));
            } catch(NumberFormatException ee){
                System.err.println("Coudln't get number of rows");
                // couldn't parse this int...
            }
        
        return outp; // should return parsed number of rows!
    }
    // test file implementation methods:
    /**
     *
     * @param stmt
     */
    public void trace(String stmt) {
	System.out.println("TRACE: " + stmt);
    }
    /**
     *
     * @param stmt
     * @param est
     */
    public void profile(String stmt, long est) {
	System.out.println("PROFILE(" + est + "): " + stmt);
    }
    SQLite.Stmt prep_ins(SQLite.Database db, String sql)
	throws SQLite.Exception {
	return db.prepare(sql);
    }
    void do_ins(SQLite.Stmt stmt) throws SQLite.Exception {
	stmt.reset();
	while (stmt.step()) {
	}
    }
    void do_ins(SQLite.Stmt stmt, int i, double d, String t, byte[] b)
        throws SQLite.Exception {
	stmt.reset();
	stmt.bind(1, i);
	stmt.bind(2, d);
	stmt.bind(3, t);
	stmt.bind(4, b);
	while (stmt.step()) {
	}
    }
    void do_exec(SQLite.Database db, String sql) throws SQLite.Exception {
	SQLite.Stmt stmt = db.prepare(sql);
	while (stmt.step()) {
	}
	stmt.close();
    }
    void do_select(SQLite.Database db, String sql) throws SQLite.Exception {
	SQLite.Stmt stmt = db.prepare(sql);
	int row = 0;
	while (stmt.step()) {
	    int i, ncol = stmt.column_count();
	    System.out.println("=== ROW " + row + "===");
	    for (i = 0; i < ncol; i++) {
		try {
		    System.out.print(stmt.column_database_name(i) + "." +
				     stmt.column_table_name(i) + "." +
				     stmt.column_origin_name(i) + "<" +
				     stmt.column_decltype(i) + ">=");
		} catch (SQLite.Exception se) {
		    System.out.print("COLUMN#" + i + "<" +
				     stmt.column_decltype(i) + ">=");
		}
		Object obj = stmt.column(i);
		if (obj == null) {
		    System.out.println("null<null>");
		} else if (obj instanceof byte[]) {
		    byte[] b = (byte[]) obj;
		    String sep = "";
		    System.out.print("{");
		    for (i = 0; i < b.length; i++) {
			System.out.print(sep + b[i]);
			sep = ",";
		    }
		    System.out.print("}");
		} else {
		    System.out.print(obj.toString());
		}
		if (obj != null) {
		    System.out.println("<" + obj.getClass().getName() + ">");
		}
	    }
	    row++;
	}
	stmt.close();
    }

   class DatabaseException extends IOException{ // a local exception for database issues.
        public DatabaseException(String message){
            super(message);
        }
    }
}

