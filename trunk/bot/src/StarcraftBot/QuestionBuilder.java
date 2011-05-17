/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author antonsorensen
 */
public class QuestionBuilder {
    
    long defaultQID;
    
    private DatabaseAccessor dba;
    public QuestionBuilder(DatabaseAccessor dba) throws SQLite.Exception{
        this.dba = dba;
        defaultQID = dba.getDefaultQID();
    }
    
    public int getQID(Query q) {
        try {
            String[] md = q.buildSearchPhrase();
            return dba.getQid(md);
        }
        //This class could possibly be used to define what keywords to search for to narrow down the search in case of multiple answers.
        catch (Exception ex) {
            return (int) defaultQID;
        }
    }
    
    
    //This class could possibly be used to define what keywords to search for to narrow down the search in case of multiple answers.
}
