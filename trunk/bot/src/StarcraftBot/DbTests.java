/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import SQLite.*;

import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author jakob
 */
public class DbTests {



    // --- TEST METHODS FROM EXAMPLE FILE(S) ---
    /**
     *
     * @param dba
     */
    public static void test2(DatabaseAccessor dba){
        try {
            try {
                //  Test saving an object to database
                dba.db.trace(dba);
                ItemCard marineCard = new ItemCard("marine");
                Tools.printCard(marineCard);
                int rownumber = dba.write(marineCard); // this throws!
                dba.delete(rownumber);
            }
            catch(DatabaseException e){
                System.out.println("Coudln't write card. OK!");
            }

                dba.updateItemCard("Marine", "buildsAt: Command Centre", "buildTime: 200");

                ItemCard newItem = dba.getItemCard("Marine");
                Tools.printCard(newItem);
            }
        catch(java.lang.Exception e){
            System.err.println("Error in test!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     *
     * @param dbfile
     */
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
}
