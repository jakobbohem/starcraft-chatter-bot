/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import SQLite.Exception;
import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Console
 */
public class CSVSerializer {

    public void databaseToCsv(String filename) throws DatabaseException, IOException, Exception {
        DatabaseAccessor dba = new DatabaseAccessor();
        List<ItemCard> cards = dba.getAllItemCards();

        FileWriter fw = new FileWriter(filename);
        fw.write("type,name,armour,counter[],strongAgainst[]\n");
        
        for (ItemCard c: cards) {
            System.out.println("Card: " + c.name);
            
            String csv = "";
            csv += c.type + ",";
            csv += c.name + ",";
            csv += c.armour + ",";
            if (c.counter.length > 0) {
                csv += c.counter[0];
                for (int i = 1; i < c.counter.length; ++i) {
                    csv += " " + c.counter[i];
                }
            }
            csv += ",";
            if (c.strongAgainst.length > 0) {
                csv += c.strongAgainst[0];
                for (int i = 1; i < c.strongAgainst.length; ++i) {
                    csv += " " + c.strongAgainst[i];
                }
            }
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
            ItemCard c = new ItemCard(tokens[0], tokens[1]);
            c.armour = Integer.parseInt(tokens[2]);
            c.counter = tokens[3].split(" "); 
            c.strongAgainst = tokens[4].split(" ");
            dba.write(c);
        }
        
        br.close();
        dba.close();
    }

    public static void main(String[] args) throws Throwable {
        String filename = "C:\\Users\\Console\\Documents\\bot-units.csv";
        new CSVSerializer().databaseToCsv(filename);
        new CSVSerializer().csvToDatabase(filename);
    }
    
}
