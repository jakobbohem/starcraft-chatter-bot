/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author antonsorensen
 */
public class QuestionBuilder {
    
    private DatabaseAccessor dba;
    public QuestionBuilder(DatabaseAccessor dba) throws SQLite.Exception{
        this.dba = dba;
    }
    
    public int getQID(Query q) {
        try {
            String[] md = q.buildSearchPhrase(dba);
            return dba.getQid(md);
        }
        //This class could possibly be used to define what keywords to search for to narrow down the search in case of multiple answers.
        catch (DatabaseException ex) {
            return 0;
        } catch(SparseSpecException ex){
            return 0;
        }catch(SQLite.Exception ex){
            return 0;
        }
    }


    
    
    //This class could possibly be used to define what keywords to search for to narrow down the search in case of multiple answers.
}
