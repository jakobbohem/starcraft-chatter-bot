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
    String userPostedAction;
    String action;
    String question;
    String[] objectStrings;
    String object; // rename to better word classe?
    String actor;
    String[] inputTokens; // string[] representation of the input words:
    String[] tags;

    public boolean isPlural;
    
    // constr
    Query(String action, String question, String object, String actor){ // simple String[] constructor:
        // neglecting setting objectStrings for now.    
        this.action=action;
        this.actor=actor;
        this.object=object;
        this.question=question;
    }
    Query(String action, String object) { // 2 param overload
        this.action=action;
        this.object=object;
    }
    Query(String action, String object, String question) { // 2 param overload
        this.question = question;
        this.action = action;
        // super simple plural check:
        this.object = object.endsWith("s") ? object.substring(0, object.length()-1): object;
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
    
    public boolean actorNotNull(){
        if (actor==null)
            return false;
        return true;
    }
    
    public boolean objectNotNull(){
        if (object==null)
            return false;
        return true;
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
    
    public String[] buildSearchPhrase(DatabaseAccessor dba) throws Exception {
        if(hardCheckNotNull()){
            ItemCard objCard = dba.getItemCard(object);
            ItemCard aCard = dba.getItemCard(actor);
            return new String[] {"action:"+action, "question:"+question, "object:"+objCard.type, "actor:"+aCard.type};
        }
        else if(checkNotNull()){
            ItemCard objCard = dba.getItemCard(object);
            return new String[] {"question:"+question, "action:"+action, "object:"+objCard.type};
        }
        else if(baseCheckNotNull()){
            ItemCard objCard = dba.getItemCard(object);
            return new String[] {"action:"+action, "object:"+objCard.type};
            
        }
        throw new java.lang.Exception("Not enough labels!");
    }

    // setter methods:
    public void setTokens(String[] inp){
        this.inputTokens = inp;
    }
    public void setTags(String[] tags){
        this.tags = tags;
    }
}
