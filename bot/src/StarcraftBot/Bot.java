/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.*;
import StarcraftBot.DatabaseAccessor.DatabaseException; // for database read/write issues
import StarcraftBot.Tagger.ExecutionOrderException; // general Exception for bad coding issues (CATCH INSIDE?)
import java.io.IOException; //Thrown by the AnswerBuilder in case there's something wrong with the ItemCards.

/**
 * Testing harmless code submit through Netbeans SVN plugin
 * @author jakob
 */
public class Bot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
        // Run basic database implementation test:
        //DbTests.createSingleMarineDB("corpus/database");
        DatabaseAccessor dba = new DatabaseAccessor();
        
//       DbTests.test2(dba);
//        DbTests.testAdders(dba);

        // Run program
        int exitcode = 0;
        Scanner scan = new Scanner(System.in);
        
            
        AnswerBuilder ab = new AnswerBuilder(dba);
        QuestionBuilder qb = new QuestionBuilder(dba); 
        System.out.println("Enter a phrase for tagging: \n(1 to exit)");
            while (exitcode != 1) {
                
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
                Interpreter Jeeves = new Interpreter();
                try {
                    Tagger tagger = new Tagger();
                    String[] inputs = tagger.chopString(inp); // also sets the lastUsedString property
                    // equal to getLastChunks

                    ArrayList<ArrayList> tags = tagger.getTagList();
                    //Tools.printArray(tagger.getLastChunks());
                    //Tools.printArray(tags);
                    
                    Query q = Jeeves.interpretTags(tags, inp, exitcode);
                    
                    // String answer = Jeeves.getReply(q); // old file read method
                    //String answer = Jeeves.getReply(q, dba);
                    
                    //New shiny getAnswer method.
                    if(q == null)
                       throw new Exception("Query object is NULL!");
                    answer = ab.getAnswer(qb.getQID(q), q);

                    
                } catch (DatabaseException dbe) {
                    System.out.println("DEBUG: DatabaseException in main!");
                    System.out.println(dbe);
                    dbe.printStackTrace();

                } catch (SparseSpecException e) {
                    System.out.println("DEBUG: SparseException in main!");
                    answer = "I couldn't understand that. Please ask a question (incl. action and object)";
                    //System.out.println(e);
                    e.printStackTrace();

                } catch (ExecutionOrderException e) {
                    System.out.println("DEBUG: ExecutionOrderException in main!");
                    System.out.println(e);
                    e.printStackTrace();
                    
                } catch (IOException ioe) {
                    System.out.println("DEBUG: IOException. Probably in AnswerBuilder. Probably something wrong with the 'action' and 'object' cards...");
                    System.out.println(ioe);
                    ioe.printStackTrace();
                } finally{
                    System.out.println(Jeeves.name + ": " + answer);
                }
                // now get the labels and print them!
            } // end of while
            dba.close();
        }// end of BIG try
        catch (java.lang.Exception e) {
            System.err.println("RUNTIME ERROR!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
