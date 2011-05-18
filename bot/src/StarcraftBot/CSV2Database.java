/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

/**
 *
 * @author Console
 */
public class CSV2Database {
    public static void main(String[] args) throws Throwable {
        String filename = "C:\\Users\\Console\\Documents\\bot-units.csv";
        new CSVSerializer().databaseToCsv(filename);
    }
}
