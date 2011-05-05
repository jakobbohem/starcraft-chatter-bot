/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package postagger1;
import java.util.*;

/**
 *
 * @author jakob
 */
public class POStagger1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int exitcode = 0;
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
        }
        
    }
}
