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
    String target; // rename to better word classe?
    String actor;
    
    // constr
    Query(String action, String question, String target, String actor){ // simple String[] constructor:
        // neglecting setting objectStrings for now.    
        this.action=action;
        this.actor=actor;
        this.target=target;
        this.question=question;
    }
    Query(String action, String target) { // 2 param overload
        this.action=action;
        this.target=target;
    }
    
    // public methods
    public boolean checkNotNull(){
        if (action == null || target == null)
            return false;
        else return true;
    }
    public boolean hardCheckNotNull(){
        if (action == null || question == null || target == null || actor == null)
            return false;
        else return true;
    }
    
    public String[] getMetadata() {
        if(hardCheckNotNull())
            return new String[] {action, question, target, actor};
        else if(checkNotNull())
            return new String[] {action, target};
        else return new String[] {""};
    }   
}
