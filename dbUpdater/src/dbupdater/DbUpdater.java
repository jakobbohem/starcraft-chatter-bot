/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbupdater;
import SQLite.Exception;
import StarcraftBot.*;
import StarcraftBot.DatabaseAccessor.DatabaseException;
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
    private static void runAddCSV(){

    }
    public static void runAddConsole(String[] itemCardsList) throws Exception, DatabaseException, IllegalArgumentException, IOException, ItemCardException{
        System.out.println("\n - Manually update itemCards downloaded from online - ");
        String dbfile = "../bot/corpus/database";
        DatabaseAccessor dba = new DatabaseAccessor(dbfile);
        Scanner s = new Scanner(System.in);
        for(int k = 0;k<itemCardsList.length;k++){
            String theUnit = itemCardsList[k];
        System.out.printf("enter a comma-separated list of the fields in '%s' you would like to update:\n",theUnit);
        String input = s.nextLine();

            String[] fields = input.split(",");
            if(!fields[0].isEmpty()){
                String[] upgrades = new String[fields.length];
                for (int i = 0;i<fields.length;i++){
                    System.out.println("Enter value for: "+fields[i]);
                    String val = s.nextLine();
                    upgrades[i] = fields[i]+":"+val;
                }
                dba.updateItemCard(theUnit, upgrades);
            }
            Tools.printCard(dba.getItemCard(theUnit));
       }
       dba.close();
    }
    public static void runAddConsole() throws DatabaseException, IllegalArgumentException, IOException, Exception, ItemCardException{
        System.out.println("\n - Running interactive add-to-database program (1 to quit) -");
        String dbfile = "../bot/corpus/database";
        DatabaseAccessor dba = new DatabaseAccessor(dbfile);
        Scanner s = new Scanner(System.in);
        int exitcode = 0;
        
        while(exitcode!=1){
            System.out.println("what (1) unit would you like to update?");
            String theUnit = s.nextLine();
            String input = theUnit;
            try{
                exitcode = Integer.parseInt(input);
                if(exitcode==1)
                    break;
            }catch(NumberFormatException e){}
            System.out.print("enter a comma-separated list of the fields you would like to update:");
            input = s.nextLine();

            String[] fields = input.split(",");
            if(!fields[0].isEmpty()){
                String[] upgrades = new String[fields.length];
                for (int i = 0;i<fields.length;i++){
                    System.out.println("Enter value for: "+fields[i]);
                    String val = s.nextLine();
                    upgrades[i] = fields[i]+":"+val;
                }
                dba.updateItemCard(theUnit, upgrades);
            }
            Tools.printCard(dba.getItemCard(theUnit));
       }
       dba.close();
    }
    private static void extendDB_poc() throws IOException, DatabaseException, IllegalArgumentException, Exception, ItemCardException {
        
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
