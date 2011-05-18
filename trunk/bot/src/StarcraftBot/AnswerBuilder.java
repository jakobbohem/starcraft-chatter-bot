/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.*; //Lists
import java.util.regex.*; //Regex. Self expanatory.
import java.io.IOException; //Thrown in case the answer needs an ItemCard which cannot be fetched (name missing etc.).
import StarcraftBot.DatabaseAccessor.DatabaseException;

/**
 * Creates an answer String from a query and a question-id. The file should be 
 * constructed with the databaseaccessor which accesses the database you're going
 * to use. Then just call AnswerBuilder.getAnswer(qID, query) and you'll get a 
 * string returned (or an exception. there's a lot of those) which can be 
 * presented to the user.
 * 
 * 
 * Notes for usage: The cannedPhrase string in the database should use placeholders
 * on the form %object&objectField&grammarTag%. If the object is "action", the
 * objectField mustn't be present. The grammarTag is defined as:
 * <b>Nouns</b>:<br>
 *  i: <i>indefinite singular</i> (a marine)<br>
 *  p: <i>plural</i> (marines)<br>
 *  anything else: <i>base form</i> (marine)<br>
 * <b>Verbs</b>:<br>
 *  p: <i>past tense</i> (created, countered)<br>
 *  anything else: <i>base form</i> (create, counter)<br>
 * @author anton sorensen
 */
public class AnswerBuilder {

    private DatabaseAccessor dba;

    public AnswerBuilder(DatabaseAccessor dba) {
        this.dba = dba;
    }

    /**
     * The method which goes through the canned answer and replaces 
     * placeholders with the correct words. Also checks whether or not the answer
     * is actually in the knowledge base. If it isn't, the missing knowledge is 
     * logged and the user is told a generic "dunno" answer.
     * @param qID The ID of the question to be answered. Needed to get the right
     * answer from the database.
     * @param query The query which was sent to the database. Used to get proper
     * phrasing.
     * @param actor The object or unit which is the focus of the query.
     * @param items All the other StarCraft objects which are relevant to the query.
     * @param dba Needed to access the database.
     * @return The finished answer.
     */
    public String getAnswer(int qID, Query query) throws IOException, DatabaseException {
        String answer = "";
        try {
            String cannedPhrase = null;
            if (qID == 0)
                answer = "I don't have an answet to that in my database, sorry!";
            else
            {
                cannedPhrase = dba.getCannedPhrase(qID).replace(".","&"); //"%object&name&p% is %action&p% at %object&buildsAt&o%";//dba.getCannedPhrase(qID);
            String action = query.action;

            ItemCard actor = null;
            ItemCard item = null;
            LinkedList<String> replacements = new LinkedList<String>();
            

            Pattern phP = Pattern.compile("%[\\w&]*%");
            Matcher phM = phP.matcher(cannedPhrase);

            while (phM.find()) {
                String group = phM.group();
                Pattern sphP = Pattern.compile("[\\w]+");
                Matcher sphM = sphP.matcher(group);
                String replacement = "";
                sphM.find();
                String objectType = sphM.group();
                //If the object is an action, perform action-related formatting
                if (objectType.equals("action")) {
                    sphM.find();
                    char grammar = sphM.group().charAt(0);

                    replacement = buildAction(action, grammar);
                } //If the object is an actor, check which field is requested and do the corresponding formatting.
                else if (objectType.equals("actor")) {
                    if (query.actorNotNull())
                        if(actor==null)
                            actor = dba.getItemCard(query.actor);
                    else
                        throw new IOException("Need actor. No Actor.");

                    sphM.find();
                    String objectField = sphM.group();

                    sphM.find();
                    char grammar = sphM.group().charAt(0);
                    if (objectField.equals("name")) {
                        replacement = readName(actor, grammar);
                    } else if (objectField.equals("buildsAt")) {
                        replacement = readBuildsAt(actor, grammar);
                    } else if (objectField.equals("techTree")) {
                        replacement = readTechTree(actor, grammar);
                    } else if (objectField.equals("builtBy")) {
                        replacement = readBuiltBy(actor, grammar);
                    } else if (objectField.equals("type")) {
                        replacement = readType(actor, grammar);
                    } else if (objectField.equals("size")) {
                        replacement = readSize(actor, grammar);
                    }
                    replacement = "[unknown]";
                    //etc. One elif for every field in the itemCard. Not very pretty. Better way to do this?
                    /*Fields remaining: counter, 
                     * strongAgainst, buildTime, health, armour, food, 
                     * mineralCost, gasCos, tier
                     */
                } //Like actor, but checks which entries in the list 'items' to use.
                else if (objectType.equals("object")) {
                    if (query.objectNotNull()){
                        if(item==null) {
                            item = dba.getItemCard(query.object);
                        }
                    }
                    else
                        throw new IOException("Need object. No object.");
                    
                    /*TODO: if first match after "object" is "all", then loop through items.
                     * otherwise, if given a digit, use items.get(digit).
                     * otherwise, if there's something neither a digit or "a", use
                     * the first entry of items.
                     */
                    
                    sphM.find();
                    String objectField = sphM.group();
                    
                    char grammar = 'o';
                    if(sphM.find())
                        grammar = sphM.group().charAt(0);

                    if (objectField.equals("name")) {
                        replacement = readName(item, grammar);
                    } else if (objectField.equals("buildsAt")) {
                        replacement = readBuildsAt(item, grammar);
                    } else if (objectField.equals("techTree")) {
                        replacement = readTechTree(item, grammar);
                    } else if (objectField.equals("builtBy")) {
                        replacement = readBuiltBy(item, grammar);
                    } else if (objectField.equals("type")) {
                        replacement = readType(item, grammar);
                    } else if (objectField.equals("size")) {
                        replacement = readSize(item, grammar);
                    } else if (objectField.equals("buildingTier")) {
                        replacement = readBuildingTier(item, grammar);
                    } else if (objectField.equals("upgradeAt")) {
                        replacement = readUpgradeAt(item, grammar);
                    }
                    if(replacement.isEmpty())
                        replacement = "[unknown]";
                }
                else if (objectType.equals("yn")){
                    //Do YN question
                    if (query.objectNotNull()){
                        if(item==null) {
                            item = dba.getItemCard(query.object);
                            
                        }
                    }
                    else
                        throw new IOException("Need object. No object.");
                    
                    if (query.actorNotNull())
                    {
                        if(actor==null)
                            actor = dba.getItemCard(query.actor);
                    }
                    else
                        throw new IOException("Need actor. No Actor.");
                    
                    sphM.find();
                    String objectField = sphM.group();
                    
                    boolean isIn = false;
                    
                    if (objectField.equals("counter")) {
                        if (isInCounter(actor,item))
                            replacements.add("Yes.");
                        else
                            replacements.add("No.");
                    } else if (objectField.equals("buildsAt")) {
                        //isIn = isInBuildsAt(actor,item);
                    } else if (objectField.equals("builtBy")) {
                        //isIn = isInBuiltBy(actor,item);
                    } else if (objectField.equals("strongAgainst")) {
                        //isIn = isInStrongAgainst(actor,item);
                    } 
                    
                }
                replacements.add(replacement);
            }
            phM.reset();
            for (int i = 0; i < replacements.size(); i++) {
                answer = phM.replaceFirst(replacements.get(i));
                phM.reset(answer);
            }
            if(answer.equals(""))
                answer = "I know that I know this, but I don't know how to explain it.";
            }
            return answer+".";
        } catch (SQLite.Exception sqle) {
            System.out.print("SQLite error in 'AnswerBuilder'.");
            sqle.printStackTrace();
        } catch (ItemCardException ice) {
            dba.handleUnknownQuery(ice.toString(),ice.getProblems(),query);
            return "Don't know. I'll ask an expert, ask again later.";
        }
        return "DEBUG: PASSED THROUGH ENTIRE ANSWERBUILDER WITH NO ANSWER!!";
    }

    /**
     * Applies grammar to the action.
     * @param action Action to be grammarised.
     * @param grammar How to grammarise.
     * @return Grammarised action string.
     */
    private String buildAction(String action, char grammar) {
        if (grammar == 'p') {
            return GrammarEngine.verbPastTense(action);
        } else {
            return action;
        }
    }
    /**
     * Reads the name field. Checks if it's there (if not, throw exception). 
     * Grammarise, then return.
     * @param item Contains the field.
     * @param grammar How to grammarise.
     * @return Grammarised field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readName(ItemCard item, char grammar) throws ItemCardException {
        if (item.name == null) {
            throw new ItemCardException("missing field",new String[]{"name"});
        }

        String name = item.name;
        if (grammar == 'i') {
            return GrammarEngine.nounIndefinite(name);
        } else if (grammar == 'p') {
            return GrammarEngine.nounPlural(name);
        } else {
            return name;
        }
    }
    
    /**
     * Reads the type field. Checks if it's there (if not, throw exception). 
     * Return value of field.
     * @param item Contains the field.
     * @param grammar Dummy. There because I couldn't be assed to remove it.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readType(ItemCard item, char grammar) throws ItemCardException {
        if (item.type == null) {
            throw new ItemCardException("missing field",new String[]{"type"});
        }
        return item.type;
    }
    
    /**
     * Reads the buildingTier field. Checks if it's there (if not, throw exception). 
     * Return value of field.
     * @param item Contains the field.
     * @param grammar Dummy. There because I couldn't be assed to remove it.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readBuildingTier(ItemCard item, char grammar) throws ItemCardException {
        if (item.buildingTier == null) {
            throw new ItemCardException("missing field",new String[]{"buildingTier"});
        }
        return item.buildingTier;
    }

    /**
     * Reads the size field. Checks if it's there (if not, throw exception). 
     * Return value of field.
     * @param item Contains the field.
     * @param grammar Dummy. There because I couldn't be assed to remove it.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readSize(ItemCard item, char grammar) throws ItemCardException {
        if (item.size == null) {
            throw new ItemCardException("missing field",new String[]{"size"});
        }
        return item.size;
    }

    /**
     * Reads the counter field. Checks if it's there (if not, throw exception). 
     * Returns a properly formatted list, like "a, b, c and d", grammarised using
     * 'grammar'.
     * @param item Contains the field.
     * @param grammar How to grammarise the field.
     * @return Formatted string.
     * @throws ItemCardException if the field is empty.
     */
    private String readCounter(ItemCard item, char grammar) throws ItemCardException {
        if (item.counter == null) {
            throw new ItemCardException("missing field",new String[]{"counter"});
        }
        
        String[] counter = item.counter;
        
        if (counter.length==0) {
            throw new ItemCardException("empty field",new String[]{"counter"});
        } else if (counter.length == 1) {
            return String.format("%s",
                    geNoun(counter[0],grammar));
        } else if (counter.length == 2) {
            return String.format("%s and %s",
                    geNoun(counter[0],grammar),
                    geNoun(counter[1],grammar));
        } else {
            String returnStr = returnStr = String.format("%s",
                    geNoun(counter[0],grammar));
            for (int i = 1; i < counter.length - 1; i++) {
                returnStr += String.format(", %s",
                        geNoun(counter[i],grammar));
            }
            returnStr = returnStr += String.format(" and %s",
                    geNoun(counter[counter.length-1],grammar));
            return returnStr;
        }
    }
    
    /**
     * Reads the counter field. Checks if it's there (if not, throw exception). 
     * Returns a properly formatted list, like "a, b, c and d", grammarised using
     * 'grammar'.
     * @param item Contains the field.
     * @param grammar How to grammarise the field.
     * @return Formatted string.
     * @throws ItemCardException if the field is empty.
     */
    private String readUpgradeAt(ItemCard item, char grammar) throws ItemCardException {
        if (item.upgradeAt == null) {
            throw new ItemCardException("missing field",new String[]{"upgrade at"});
        }
        
        String[] upgradeAt = item.upgradeAt;
        
        if (upgradeAt.length==0) {
            throw new ItemCardException("empty field",new String[]{"counter"});
        } else if (upgradeAt.length == 1) {
            return String.format("%s",
                    geNoun(upgradeAt[0],grammar));
        } else if (upgradeAt.length == 2) {
            return String.format("%s or %s",
                    geNoun(upgradeAt[0],grammar),
                    geNoun(upgradeAt[1],grammar));
        } else {
            String returnStr = returnStr = String.format("%s",
                    geNoun(upgradeAt[0],grammar));
            for (int i = 1; i < upgradeAt.length - 1; i++) {
                returnStr += String.format(", %s",
                        geNoun(upgradeAt[i],grammar));
            }
            returnStr = returnStr += String.format(" or %s",
                    geNoun(upgradeAt[upgradeAt.length-1],grammar));
            return returnStr;
        }
    }

    /**
     * Reads the buildsat field. Checks if it's there (if not, throw exception). 
     * Grammarise, then return string.
     * @param item Contains the field.
     * @param grammar How to grammarise the answer.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readBuildsAt(ItemCard item, char grammar) throws ItemCardException {
        if (item.buildsAt == null) {
            throw new ItemCardException("missing field",new String[]{"BuildsAt"});
        }

        String buildsAt = item.buildsAt;

        if (grammar == 'i') {
            buildsAt = GrammarEngine.nounIndefinite(buildsAt);
        } else if (grammar == 'p') {
            buildsAt = GrammarEngine.nounPlural(buildsAt);
        }
        return buildsAt;
    }

    /**
     * Reads the builtby field. Checks if it's there (if not, throw exception). 
     * Grammarise, then return string.
     * @param item Contains the field.
     * @param grammar How to grammarise the answer.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readBuiltBy(ItemCard item, char grammar) throws ItemCardException {
        if (item.builtBy == null) {
            throw new ItemCardException("missing field",new String[]{"builtBy"});
        }

        String builtBy = item.builtBy;

        if (grammar == 'i') {
            builtBy = GrammarEngine.nounIndefinite(builtBy);
        } else if (grammar == 'p') {
            builtBy = GrammarEngine.nounPlural(builtBy);
        }
        return builtBy;
    }

    /**
     * Returns a formatted tech tree. It reads the list of items and comma
     * separates as needed. Uses and for the last one. Pretty neat.
     * @param item The card containing the field. 
     * @param grammar Dummy. Not used atm.
     * @return A string consisting of the list of requirements for the unit. On
     * the form "req1, req2, req3 and req4" etc. 
     * @throws ItemCardException if the field is empty.
     */
    private String readTechTree(ItemCard item, char grammar) throws ItemCardException {
        if (item.techTreeList == null && item.techTree == null) {
            throw new ItemCardException("missing field",new String[]{"TechTree"});
        }

        List<String> ttl = item.techTreeList;

        if (ttl.isEmpty()) {
            return "";
        } else if (ttl.size() == 1) {
            return String.format("%s",
                    GrammarEngine.nounIndefinite(ttl.get(0)));
        } else if (ttl.size() == 2) {
            String firstItem = ttl.get(0);
            String lastItem = ttl.get(1);
            return String.format("%s and %s",
                    GrammarEngine.nounIndefinite(firstItem),
                    GrammarEngine.nounIndefinite(lastItem));
        } else {
            String returnStr = returnStr = String.format("%s",
                    GrammarEngine.nounIndefinite(ttl.get(0)));
            for (int i = 1; i < ttl.size() - 1; i++) {
                String techItem = ttl.get(i);
                returnStr += String.format(", %s",
                        GrammarEngine.nounIndefinite(techItem));
            }
            returnStr = returnStr += String.format(" and %s",
                    GrammarEngine.nounIndefinite(ttl.get(0)));
            return returnStr;
        }
    }
    
    private boolean isInCounter(ItemCard actor, ItemCard target){
        String[] counters = target.counter;
        String[] strongAgainst = actor.strongAgainst;
        String actName = actor.name.toLowerCase();
        String tarName = target.name.toLowerCase();
        
        boolean isStrongAgainst = false;
        boolean doesCounter = false;
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].toLowerCase().equals(actName)){
                isStrongAgainst = true;
                break;
            }
            
        }
        for (int i = 0; i < strongAgainst.length; i++) {
            if (strongAgainst[i].toLowerCase().equals(tarName)){
                doesCounter = true;
                break;
            }
            
        }
        return (isStrongAgainst&&doesCounter);
    }
    
    private String geNoun(String noun, char grammar){
        if(grammar=='p')
            return GrammarEngine.nounPlural(noun);
        else if(grammar=='i')
            return GrammarEngine.nounIndefinite(noun);
        else
            return noun;
    }
    
    public String testMethod(ItemCard icTest, ItemCard icTest2, char gram) throws ItemCardException{
        if(isInCounter(icTest2,icTest))
            return "yes";
        else
            return "no";
    
    }
}
