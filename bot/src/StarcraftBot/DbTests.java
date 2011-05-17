/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import SQLite.*;

import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.io.*;
import java.util.ArrayList;
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
                //dba.debug(); // this isn't needed, but gives output of database queries.
                ItemCard marineCard = new ItemCard("MAKE_");
                Tools.printCard(marineCard);
                int rownumber = dba.write(marineCard); // this throws!
                dba.delete(rownumber);
            }
            catch(DatabaseException e){
                System.out.println("Couldn't write card. OK!");
            }
                dba.updateItemCard("Marine", "buildsAt: Command Centre", "buildTime: 200");
                ItemCard newItem = dba.getItemCard("Marine");
                Tools.printCard(newItem);


                // TESTING a simple read/write case:
                System.out.println("User: How do I build Marines?");
                // no parsing logic in test...
                String action = "build";
                String question = "how";
                String strObject = "marine";

                ItemCard objectCard = dba.getItemCard(strObject);
                int Qid = dba.getQid("question:"+question, "action: "+action, "object:"+strObject);

                //Response resp = dba.getResponse(Qid); // not used for now
                String cPhrase = dba.getCannedPhrase(Qid);

                // handle the cPhrase, just insert in order for now.
                String[] inserts = new String[] {action, strObject, objectCard.buildsAt};
                String reply = Tools.DumbInsert(cPhrase, inserts, true); // true for plural.

                System.out.println("AI Reply: "+reply);
            }
        catch(java.lang.Exception e){
            System.err.println("Error in test!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void testAdders(DatabaseAccessor dba){
        try {
            System.out.println("Testing adding question data to database...");
            String question = "how";
            ArrayList<String> members = new ArrayList<String>();
            members.add("action: counter");
            members.add("object: zergling");
            Response r = new Response("[object] are countered by cheap tier [tier] units!");
            r.addCannedPhrase("You can counter [object] with [object.counter]");
            long Qid = dba.addQuery(question, members, r); //throws. maybe it shouldn't. we'll see
            // think about deleting after...
        } catch (DatabaseException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(DbTests.class.getName()).log(Level.INFO, null, ex);
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

	    OutputStream os = writer.getOutputStream();
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
//		try {
//                    // DONT DROP THE TABLE! Want to check it!
//		    T.do_exec(db, ".tables");
//
//		} catch(SQLite.Exception e) {
//		}
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
