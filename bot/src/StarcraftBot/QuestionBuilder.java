/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

/**
 *
 * @author antonsorensen
 */
public class QuestionBuilder {
    private DatabaseAccessor dba;
    public QuestionBuilder(DatabaseAccessor dba){
        this.dba = dba;
    }
    
    public int getQID(Query q) throws Exception{
        String[] md = q.getMetadata();
        return dba.getQid(md);
    }
    
    //This class could possibly be used to define what keywords to search for to narrow down the search in case of multiple answers.
}
