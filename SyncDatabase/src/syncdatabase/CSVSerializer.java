/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncdatabase;

import SQLite.Exception;
import StarcraftBot.*;
import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Console
 */
public class CSVSerializer {
    
    public void databaseToCsv(String filename) throws DatabaseException, IOException, Exception {
        DatabaseAccessor dba = new DatabaseAccessor("../bot/corpus/database");
        List<ItemCard> cards = dba.getAllItemCards();

        FileWriter fw = new FileWriter(filename);
        fw.write("type,builtBy,size,name,techTree[],buildsAt,counter[],strongAgainst[],buildTime,health,armour,food,mineralCost,gasCost,\n");
        
        for (ItemCard c: cards) {
            System.out.println("Card: " + c.name);
            
            String csv = "";
            csv += c.type + ",";
            csv += c.builtBy+",";
            csv += c.name + ",";
            
             csv += ",";
            if(c.techTree!=null){
                if (c.techTree.length > 0) {
                    csv += c.techTree[0];
                    for (int i = 1; i < c.techTree.length; ++i) {
                        csv += " " + c.techTree[i];
                    }
                }
             }
            csv += c.buildsAt + ",";
            
            csv += ",";
            if(c.counter!=null){
            if (c.counter.length > 0) {
                csv += c.counter[0];
                for (int i = 1; i < c.counter.length; ++i) {
                    csv += " " + c.counter[i];
                }
            }
            }
            csv += ",";
            if(c.strongAgainst!=null){
            if (c.strongAgainst.length > 0) {
                csv += c.strongAgainst[0];
                for (int i = 1; i < c.strongAgainst.length; ++i) {
                    csv += " " + c.strongAgainst[i];
                }
            }
            }
            csv +=c.buildTime+ ",";
            csv +=c.health+",";
            csv += c.armour + ",";
            csv += c.food + ",";
            csv += c.mineralCost + ",";
            csv += c.gasCost + ",";
            csv += "\n";
            
            fw.write(csv);
        }
        
        fw.close();
    }
    
    public void csvToDatabase(String filename) throws DatabaseException, IOException, Exception, java.lang.Exception {
        DatabaseAccessor dba = new DatabaseAccessor();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();  // Skip the header line

        dba.removeAllUnitCards();
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            ItemCard c = new ItemCard(tokens[0], tokens[3]);
            c.builtBy= (tokens[1]);
            c.size = (tokens[2]);
            c.armour = Integer.parseInt(tokens[10]);
            c.counter = tokens[6].split(" ");
            c.techTree= tokens[4].split(" ");
            c.buildsAt= tokens[5];
            c.strongAgainst = tokens[7].split(" ");
            c.buildTime=Integer.parseInt(tokens[8]);
            c.health = Integer.parseInt(tokens[9]);
            c.food = Integer.parseInt(tokens[11]);
            c.mineralCost=Integer.parseInt(tokens[12]);
            c.gasCost=Integer.parseInt(tokens[13]);
            
            dba.write(c);
        }
        
        br.close();
        dba.close();
    }

    public static void main(String[] args) throws Throwable {
        String filename = "/Users/jakob/Desktop/bot-units.csv";
        String windowsPath = "C:\\Users\\Console\\Documents\\bot-units.csv";
        int action =0;
        
        String inp = null;
        if(args.length >0){
            //first arg is action, 2nd is path
            inp = args[0];
        }
        else {
            // ask user:
            Scanner scan = new Scanner(System.in);
            System.out.println("enter an action: 'csv2db' or 'db2csv'");
            inp = scan.nextLine();
        }
        if("db2csv".equals(inp.toLowerCase()))
                action =1;
            else if("csv2db".equals(inp.toLowerCase()))
                action=2;
        
        if(action ==1)
            new CSVSerializer().databaseToCsv(filename);
        else if (action ==2)
            new CSVSerializer().csvToDatabase(filename);
        else throw new Exception("no good action");
        // TODO: Commit out one of those
       // 
        
    }
    
}
