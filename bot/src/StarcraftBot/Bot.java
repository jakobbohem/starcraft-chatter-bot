/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.*;
import StarcraftBot.DatabaseAccessor.DatabaseException; // for database read/write issues
import StarcraftBot.Interpreter.SparseSpecException; // for lack of information in input issues
import StarcraftBot.Tagger.ExecutionOrderException; // general Exception for bad coding issues (CATCH INSIDE?)

/**
 * Testing harmless code submit through Netbeans SVN plugin
 * @author jakob
 */
public class Bot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Run basic database implementation test:
        DatabaseAccessor dba = new DatabaseAccessor();
        DbTests.test2(dba);
        DbTests.testAdders(dba);

        // Run program
        int exitcode = 0;
        Scanner scan = new Scanner(System.in);
        try {
            while (exitcode != 1) {
                System.out.println("Enter a phrase for tagging: \n(1 to exit)");
                System.out.print("User: ");
                String inp = scan.nextLine();

                // get the exit code, if any
                try {
                    exitcode = Integer.parseInt(inp);
                } catch (NumberFormatException e) {
                }
                // no number was in the exit string - that's fine for now                

                // do the parsing and interpretation:
                String answer = "[empty]";
                try {
                    Tagger tagger = new Tagger();
                    String[] inputs = tagger.chopString(inp); // also sets the lastUsedString property
                    // equal to getLastChunks

                    String[] tags = tagger.getTagsFromDatabase();
                    Tools.printArray(tagger.getLastChunks());
                    Tools.printArray(tags);

                    // do some interpretation:
                    Interpreter Jeeves = new Interpreter();
                    Query q = Jeeves.interpretTags(tags, inputs);
                    // String answer = Jeeves.getReply(q); // old file read method
                    answer = Jeeves.getReply(q, dba);
                    System.out.println(Jeeves.name + ": " + answer);
                } catch (DatabaseException dbe) {
                    System.out.println("DEBUG: Exception in main!");
                    System.out.println(dbe);
                    //e.printStackTrace();

                } catch (SparseSpecException e) {
                    System.out.println("DEBUG: Exception in main!");
                    answer = "I couldn't understand that. Please ask a question (incl. action and object)";
                    //System.out.println(e);
                    //e.printStackTrace();

                } catch (ExecutionOrderException e) {
                    System.out.println("DEBUG: Exception in main!");
                    System.out.println(e);
                    //e.printStackTrace();

                } finally{
                    System.out.println("Jeeves: "+answer);
                }
                // now get the labels and print them!
            } // end of while

        }// end of BIG try
        catch (java.lang.Exception e) {
            System.err.println("RUNTIME ERROR!");
        }
    }
}
