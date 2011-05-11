/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import java.util.*;

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
        DatabaseAccessor.test2(dba);
                
        // Test just READING from database:
        ItemCard unit = (ItemCard)dba.read(1);
        Tools.printCard(unit);
        
        // Run program
        int exitcode = 1;
        Scanner scan = new Scanner(System.in);
        
        while(exitcode != 1)
        {
            System.out.println("Enter a phrase for tagging: \n(1 to exit)");
            System.out.print("User: ");
            String inp = scan.nextLine();
            
            // get the exit code, if any
            try
            {
                exitcode = Integer.parseInt(inp);
            }
            catch(NumberFormatException e){}
                // no number was in the exit string - that's fine for now                
                
            // do the parsing and interpretation:
            try{
                Tagger tagger = new Tagger();
                String[] inputs = tagger.chopString(inp); // also sets the lastUsedString property
                // equal to getLastChunks
                
                String[] tags = tagger.getTagsFromDatabase();
                Tools.printArray(tagger.getLastChunks());
                Tools.printArray(tags);
                
                // do some interpretation:
                Interpreter Jeeves = new Interpreter();
                Query q = Jeeves.interpretTags(tags, inputs);
                String answer = Jeeves.getReply(q);
                System.out.println(Jeeves.name+": "+answer);
            }
            catch(Exception e){
                System.out.println("caught exception in main.");
                System.out.println(e);
                e.printStackTrace();
            
            }
            // now get the labels and print them!
        } // end of while
        
    }
}
