/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbupdater;
import StarcraftBot.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author jakob
 */
public class DbUpdater {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
        // example of data that's pulled from somewhere (like the unknown DB)
            if(args.length>0){
                // use arguments to choose function
                
            }
            else{
                // choose from user input
                Scanner input = new Scanner(System.in);
                System.out.println("what would you like to do?");
                String mode = input.nextLine();
                if(mode.toLowerCase().equals("extenddb"))
                    extendDB_poc();
                else if(mode.toLowerCase().equals("addconsole"))
                    runAddConsole();
                else if(mode.toLowerCase().equals("addcsv")) //ashwin is writing this program
                    runAddCSV();
                else if (mode.toLowerCase().equals("html"))
                    System.out.println("//HTMLParser.run();");
                else
                System.out.println(String.format("Mode '%s' not available. Try 'extenddb','addconsole' or 'addcsv'",mode));
                // do nothing
            }
        }// end of try
        catch(java.lang.Exception e){
            System.err.println("caught exception in main!!");
            System.err.println(e.getMessage());
            e.printStackTrace();
                    
        }
    }
    private static void runAddCSV(){

    }
    private static void runAddConsole(){
        System.out.println(" - Running interactive add-to-database program -");

    }
    private static void extendDB_poc() throws IOException {
        
        String queryWithoutAnswer = "how do I counter zerglings with marines";
        String databaseFile = "../bot/corpus/database";

        String question = "how";
        String action = "counter";
        String noun = "zergling";
        String actor = "marine";

        boolean plural = true; // isn't used for now

        Scanner scan = new Scanner(System.in);

        // Here the question is in the database, but the itemCard lacks certain fields.
        String type = "UNKNOWN";

        String[] nQs = new String[] {String.format("is %s a UNIT?", noun),
            String.format("what's the %s tier?",type),
            String.format("where is the %s built?", noun)
        }; // etc...

        ArrayList<String> updates = new ArrayList<String>();

        for(int i = 0;i<nQs.length;i++){
            System.out.println(nQs[i]);
            String answer = scan.nextLine();
            if(i==0)// match the question, actually I don't know whats the best approach.. anyway
            {
                if(answer.toLowerCase().contains("yes") || answer.toLowerCase().equals("y"))
                {
                    type = "unit";
                    nQs[1] = String.format("what's the %s tier?",type); // this is cumbersome...
                    //updates.add("type:unit"); //this is another way of doing a similar thing...
                }
            }
            else if(i==1)
            {
                try{
                    int tier = Integer.parseInt(answer);
                    updates.add("tier:"+Integer.toString(tier));
                } catch(NumberFormatException e){
                    System.err.println("Couldn't parse tier from reply. Doing nothing.");
                }
            }
            else if (i==3)
            {
                // no checking here, but there probably should be.
                updates.add("buildsAt:"+answer);
            }
        }
        String cardName = noun; // just to show that it's the name of the card that is to be passed!
        
        DatabaseAccessor dba = new DatabaseAccessor(databaseFile);
        dba.debug();
        try{ // here the write method throws an exceptino if the card is present.
            // of course you could also check before attempting write...
            ItemCard card = new ItemCard(type, cardName);
            dba.write(card);
        } catch(DatabaseAccessor.DatabaseException e){
            // if the card exists!
            System.err.println(e.getMessage());
        }
        // update the card info and save back to db.
        dba.updateItemCard(cardName, updates.toArray(new String[updates.size()]));
    }
}
