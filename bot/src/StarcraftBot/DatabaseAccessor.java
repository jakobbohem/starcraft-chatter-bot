/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import SQLite.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jakob
 */
public class DatabaseAccessor implements SQLite.Trace, SQLite.Profile {

    // member fields:
    String dbFile = "corpus/database";
    String tableName = "test"; //"Corpus";
    String cardColumn = "unitData";
    SQLite.Database db = new SQLite.Database();
    String[] schema = new String[] {"id integer primary key", "name text","type text", cardColumn+" blob"};
    String blobCols = "id, name, type, "+ cardColumn;
    
    int lineCount;
    int blobSize;
    
    int trycount; // times to retry operation if fail.
    
    // constr
    public DatabaseAccessor(){
        this.db = new SQLite.Database();
        try {
            blobSize = (int)Math.pow(2,11); // in 2-power size; 2^11 = 2048
            db.open(dbFile, 0666); // what is the number?
            createTable(tableName, this.schema);
            trycount=5; // init the try count
            do_select(db, "select Count(*) from "+tableName);
            
        } catch (SQLite.Exception ex) {
            // auto generated exception handling.
            System.err.println("Couldn't create Database Accessor!");
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    private void createTable(String tablename, String[] schema){
        try{
                
            do_exec(db, "create table "+tablename+"("+
                    Tools.mergeStrings(schema, ",") +")");
            // key is not the "TYPE" the 'id' is set to be the "primary key" though
	    
            }
            catch(SQLite.Exception ee){
                System.out.println("["+ee.getMessage()+"]");
            }
    }
    
    // methods:
    public void write(ItemCard card, String[] identifiers){
        write(card);
    }
    public void write(ItemCard card){
        try{
            
            // DEBUG: Always writing to row ==1 for now!!
            int tries = 0;
            this.lineCount = 1;
            String id = Integer.toString(lineCount); // what should this be?
            // Add row with empty blob (no row number ... )
            String[] vals = new String[] {id, card.name, card.type};
            while(tries<trycount)
            try{
                do_exec(db, "insert into "+tableName+"("+blobCols+") values("+Tools.mergeStrings(vals)+", zeroblob("+blobSize+"))");
            } catch(SQLite.Exception ee){
                // couldn't write to database - retry
                tries++;
            }
            
            
            // Insert other data (i.e. name, type etc.)
            // currently the schema is id, name, type, card-object
//            Stmt stmt = prep_ins(db, "insert into "+tableName+"("+Tools.mergeStrings(blobCols, ",") +")" +
//                    " VALUES("+Tools.mergeStrings(inputs,",")+")");
                    //" VALUES(:one, :two, :three, :four)"); // this isn't quite used atm.
				  
//            stmt.bind(1, id);
//            stmt.bind(2, card.name);
//            stmt.bind(3, card.type);
//            stmt.bind(4, new byte[blobSize]);
//            do_ins(stmt);
            
            // get blob output stream 
            Blob writer = db.open_blob("main", tableName, cardColumn, lineCount, true);

            OutputStream writeStream = writer.getOutputStream();
            byte[] sCard = Tools.Serialise(card);
            writeStream.write(sCard);
            writeStream.close();
            writer.close();
        }
        catch(java.lang.Exception e){
            System.err.println("Couldn't write card to database.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
    }
    public Object read(int lineNo){
        // just read some line.
        try {
            
            Blob inputBlob = db.open_blob("main", tableName, cardColumn, lineNo, true);
            InputStream reader = inputBlob.getInputStream();
            byte[] byteFlow = new byte[blobSize];
            reader.read(byteFlow);
            reader.close();
            inputBlob.close();

            // deserialise it!!
                        //throws SQLException, IOException, ClassNotFoundException {
            //ResultSet rs =  st.executeQuery("SELECT * FROM SerialTest");
            
            Object returnObject = Tools.DeSerialise(byteFlow);
            return returnObject;
        } catch (java.lang.Exception ex) {
            System.err.println("Couldn't read blob from database");
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public Object read(String searchId){
        int lineNo = 1;// get from searchId
        return read(lineNo);
        
        
    }
    public void flushDB(){
        try {
            System.out.println("Are you sure, you want to flush database (answer true / false)");
            System.out.println("A backup will be made to: '"+tableName+"_bkp'.");
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            boolean accept = Boolean.parseBoolean(r.readLine());
            if(accept)
            {
                do_exec(db, "drop table "+tableName+"_bkp");
                createTable(tableName+"_bkp", this.schema);
                do_exec(db, "insert into "+tableName+"_bkp select * from "+tableName);
                do_exec(db, "drop table "+tableName);
            }
            else return;
        } catch (java.lang.Exception ex) {
            Logger.getLogger(DatabaseAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    // --- TEST METHODS FROM EXAMPLE FILE(S) --- 
    public static void test2(DatabaseAccessor dba){
        //  Test saving an object to database
        //dba.db.trace(dba);
        ItemCard marineCard = new ItemCard("marine");
        dba.write(marineCard); // String[] of idents currently not variable...
        
        Object fromDB = dba.read("marine");
        ItemCard newItem = (ItemCard)fromDB;
        Tools.printCard(newItem);
    }
    public static void test(String dbfile) {
	boolean error = true;
        DatabaseAccessor T = new DatabaseAccessor();
	System.out.println("LIB version: " + SQLite.Database.version());
	SQLite.Database db = new SQLite.Database();
	try {
	    byte[] b;
	    db.open(dbfile, 0666);
	    System.out.println("DB version: " + db.dbversion());
	    db.busy_timeout(1000);
	    db.busy_handler(null);
	    db.trace(T);
	    //db.profile(T);
            
            try{
                
            T.do_exec(db, "create table B("+
                    "id integer primary key, val blob)"); // key is not the "TYPE" the 'id' is set to be the "primary key" though
	    T.do_exec(db, "create table Corpus("+
		      "i integer, d double, action text, b blob)");
            }
            catch(SQLite.Exception ee){
                System.out.println(ee.getMessage());
                // couldn't create table
            }
            
            
	    T.do_select(db, "select * from sqlite_master");
	    Stmt ins = T.prep_ins(db, "insert into Corpus(i,d, action,b)" +
				  " VALUES(:one,:two,:three,:four)");
	    System.out.println("INFO: " + ins.bind_parameter_count() +
			       " parameters");
            
	    for (int i = 1; i <= ins.bind_parameter_count(); i++) {
		String name = ins.bind_parameter_name(i);
		if (name != null) {
		    System.out.println("INFO: name for " + i + " is " + name);
		    System.out.println("INFO: index of " + name + " is " +
				       ins.bind_parameter_index(name));
		}
	    }
	    // actually write somthing to database
            b = new byte[4];
	    b[0] = 1; b[1] = 1; b[2] = 2; b[3] = 3;
	    T.do_ins(ins, 1, 2.4, "two point four", b);
	    T.do_ins(ins);
            
	    b[0] = -1; b[1] = -2; b[2] = -3; b[3] = -4;
	    T.do_ins(ins, 2, 4.8, "four point eight", b);
	    T.do_ins(ins);
            
	    T.do_ins(ins, 3, -3.333, null, null);
	    ins.close();
            
            
            // WRITE A BLOB:
	    T.do_exec(db, "insert into B values(NULL, zeroblob(128))");
	    T.do_exec(db, "insert into B values(NULL, zeroblob(128))");
	    T.do_exec(db, "insert into B values(NULL, zeroblob(128))");
	    T.do_select(db, "select id from B");
            
            byte[] b128 = new byte[128];
	    for (int i = 0; i < b128.length; i++) {
		b128[i] = (byte) i;
	    }
            
            Blob blob = db.open_blob("main", "B", "val", 1, true);
	    OutputStream os = blob.getOutputStream();
	    os.write(b128);
	    os.close();
	    blob.close();
            
            // now read the data back!
            String rdTableName="B";
            T.do_select(db, "select * from "+rdTableName); // is this necessary? NO...
            
            // main is the "schema?" B is the database, val is the column, number?, error code.
            blob = db.open_blob("main", "B", "val", 1, true); // this likely IS necessary
            
            InputStream is = blob.getInputStream();
	    is.skip(96);
	    is.read(b);
	    is.close();
	    blob.close();
	    System.out.println("INFO: expecting {96,97,98,99} got {" +
			       b[0] + "," + b[1] + "," +
			       b[2] + "," + b[3] + "}");
            
            
            /* ------------------------ 
                > > > SECTION 2 (My code) < < < 
            * ------------------------ */
            
            // OK this works, now: can I write a java class?
            class SimpleClass implements Serializable {
                String name="simple name";
               
                
            }
            
            SimpleClass cls = new SimpleClass() ;
            // serialise it:
            byte[] serialisedCls = Tools.Serialise(cls);
            
            Blob writer = db.open_blob("main", "B", "val", 2, true); // what's the number line!?
            
	    os = writer.getOutputStream();
	    os.write(serialisedCls);
	    os.close();
	    writer.close();
            
            Blob inputBlob = db.open_blob("main", "B", "val", 2, true); // what's the number?
            InputStream reader = inputBlob.getInputStream();
            byte[] byteFlow = new byte[serialisedCls.length];
            reader.read(byteFlow);
            reader.close();
            inputBlob.close();
            
            // deserialise it!!
			//throws SQLException, IOException, ClassNotFoundException {
            //ResultSet rs =  st.executeQuery("SELECT * FROM SerialTest");
		byte[] buf = byteFlow;
                Object returnCls = Tools.DeSerialise(byteFlow);
		SimpleClass newCls = (SimpleClass)returnCls;
                
                System.out.println("Got class from db!!! Name is: "+newCls.name);
            
            // pseudo C code for reading the BLOB data
//            execute(
// "select type, data where key=%s order by serial", key
//)
            //T.do_select(db, "select ") DONT DO SELECT...
            // unit = db.open_blob("main")
            
            
            // some basic cleanup (like dropping the created database)
            
            //ins.close();
            //T.do_exec(db, "drop table Corpus");
            
            
            // end of 'try' statement. 
	    error = false;
	} 
        catch (java.lang.Exception e) {
	    System.err.println("error: " + e);
	    e.printStackTrace();
	} finally {
	    try {
		System.err.println("cleaning up ...");
		try {
                    // DONT DROP THE TABLE! Want to check it!
		    T.do_exec(db, ".tables"); 
		    
		} catch(SQLite.Exception e) {
		}
		db.close();
	    } catch(java.lang.Exception e) {
		System.err.println("error (at cleanup): " + e);
		error = true;
	    } finally {
		System.err.println("done.");
	    }
	}
	if (error) {
	    System.exit(1);
	}
    }
    // test file implementation methods:
    public void trace(String stmt) {
	System.out.println("TRACE: " + stmt);
    }

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
}
