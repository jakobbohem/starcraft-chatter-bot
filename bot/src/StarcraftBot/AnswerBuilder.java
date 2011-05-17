/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.*; //Lists
import java.util.regex.*; //Regex. Self expanatory.
import java.io.IOException; //Thrown in case the answer needs an ItemCard which cannot be fetched (name missing etc.).

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
    public String getAnswer(int qID, Query query) throws IOException, ItemCardException {
        try {
            String cannedPhrase = dba.getCannedPhrase(qID);
            String action = query.action;

            ItemCard actor, item;

            if (query.actorNotNull()) {
                actor = dba.getItemCard(query.actor);
            } else 
                actor = new ItemCard(null);
            
            if (query.objectNotNull()) {
                item = dba.getItemCard(query.object);
            } else
                item = new ItemCard(null);



            LinkedList<String> replacements = new LinkedList<String>();
            String answer = "";

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
                    if (!query.actorNotNull())
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
                    //etc. One elif for every field in the itemCard. Not very pretty. Better way to do this?
                    /*Fields remaining: counter, 
                     * strongAgainst, buildTime, health, armour, food, 
                     * mineralCost, gasCos, tier
                     */
                } //Like actor, but checks which entries in the list 'items' to use.
                else if (objectType.equals("object")) {
                    if (!query.objectNotNull())
                        throw new IOException("Need object. No object.");
                    
                    /*TODO: 
                     * if first match after "object" is "all", then loop through items.
                     * otherwise, if given a digit, use items.get(digit).
                     * otherwise, if there's something neither a digit or "a", use
                     * the first entry of items.
                     */
                    String objectField = sphM.group();
                    char grammar = sphM.group().charAt(0);

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
                    }
                }
                replacements.add(replacement);
            }
            phM.reset();
            for (int i = 0; i < replacements.size(); i++) {
                answer = phM.replaceFirst(replacements.get(i));
                phM.reset(answer);
            }
            return answer;
        } catch (SQLite.Exception sqle) {
            System.out.print("SQLite error in 'AnswerBuilder'.");
            sqle.printStackTrace();
        } catch (IOException dbE) {
            throw dbE;
        } catch (ItemCardException ice) {
            dba.handleUnknownQuery(ice.toString(),ice.getProblems(),query);
            return "Don't know. I'll ask an expert, ask again later.";
        }
        return null;
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
     * Reads the type field. Checks if it's there (if not, throw exception). 
     * This field needs to be formatted - the counters field is an array of stuff.
     * Probably needs to be grammarised as well.
     * @param item Contains the field.
     * @param grammar Dummy. There because I couldn't be assed to remove it.
     * @return Field value.
     * @throws ItemCardException if the field is empty.
     */
    private String readCounter(ItemCard item, char grammar) throws ItemCardException {
        if (item.counter == null) {
            throw new ItemCardException("missing field",new String[]{"counter"});
        }
        return null;
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
            returnStr = returnStr += String.format("and %s",
                    GrammarEngine.nounIndefinite(ttl.get(0)));
            return returnStr;
        }
    }
}
