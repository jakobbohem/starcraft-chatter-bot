/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.ArrayList;

/**
 *
 * @author jakob
 * The Response class, containing response data read from database and also other metadata
 * Has flexibility to add more field, i.e. context etc.
 */
public class Response {
    // member fields
    String cPhrase; // the canned phrase reply. (could be >1 in the future)
    public ArrayList<String> cPhrases; // if there are multiple ones.
    String[] knowledgeTokens; // should deprecate
    private String stringResponse;
    
    public Response(){this.stringResponse = "No Response Initialized";} // empty constructior SOOO DEPRICATED
    public Response(String response){ // should DEFINITELY depricate
        stringResponse = response;
    }
    public Response(String[] tokens){
        this.knowledgeTokens = tokens;
        String tokenlist = "";
        for (int i = 0;i<tokens.length;i++)
            tokenlist = tokenlist+", ";
        stringResponse = tokenlist;
    }
    public Response(String cannedPhrase, String[] insertTokens){
        this.cPhrase = cannedPhrase;
        cPhrases = new ArrayList<String>();
        this.knowledgeTokens = insertTokens;
        cPhrases.add(cannedPhrase);
    }
    public void addCannedPhrase(String cannedPhrase){
        if(cPhrases ==null){
            cPhrases = new ArrayList<String>();
            cPhrases.add(cPhrase);
        }
        cPhrases.add(cannedPhrase);
    }
    // public methods:
    public String processCannedPhrase(boolean plural){
        return Tools.DumbInsert(cPhrase, knowledgeTokens, plural);
    }
    public String getStringResponse(){
        return stringResponse;
    }
    public int getNumberOfPhrases(){
        return cPhrases.size();
    }
}
