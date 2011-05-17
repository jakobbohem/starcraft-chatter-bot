package StarcraftBot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import SQLite.*;

/**
 *
 * @author jakob
 */
public interface DatabaseInterface {
    // here should be the database accessor interface to put the list of words etc.
    
    Database db = new Database(); // integer primary key becomes 'alias' for rowid.
    
    
    
}
