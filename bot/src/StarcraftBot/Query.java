/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

/**
 *
 * @author jakob
 */
public class Query {
    // instance fields:
    String action;
    String question;
    String[] objectStrings;
    String object; // rename to better word classe?
    String actor;
    String[] inputTokens; // string[] representation of the input words:
    String[] tags;

    public boolean isPlural;
    
    // constr
    Query(String action, String question, String target, String actor){ // simple String[] constructor:
        // neglecting setting objectStrings for now.    
        this.action=action;
        this.actor=actor;
        this.object=target;
        this.question=question;
    }
    Query(String action, String target) { // 2 param overload
        this.action=action;
        this.object=target;
    }
    Query(String action, String target, String question) { // 2 param overload
        this.question = question;
        this.action = action;
        // super simple plural check:
        this.object = target.endsWith("s") ? target.substring(0, target.length()-1): target;
    }
    
    // public methods
    public boolean baseCheckNotNull(){
        if (action == null || object == null)
            return false;
        else return true;
    }
    public boolean checkNotNull(){
        if (action == null || object == null || question == null)
            return false;
        else return true;
    }
    public boolean hardCheckNotNull(){
        if (action == null || question == null || object == null || actor == null)
            return false;
        else return true;
    }
    
    public String[] getMetadata() {
        if(hardCheckNotNull())
            return new String[] {action, question, object, actor};
        else if(checkNotNull())
            return new String[] {question, action, object};
        else if(baseCheckNotNull())
            return new String[] {action, object};
        else return new String[] {""};
    }

    // setter methods:
    public void setTokens(String[] inp){
        this.inputTokens = inp;
    }
    public void setTags(String[] tags){
        this.tags = tags;
    }
}
