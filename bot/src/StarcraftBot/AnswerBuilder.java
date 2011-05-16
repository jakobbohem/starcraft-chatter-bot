/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.List;
import java.util.regex.*;

/**
 *
 * @author antonsorensen
 */
public class AnswerBuilder {

    public AnswerBuilder() {
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
    public String getAnswer(int qID, Query query, ItemCard actor, List<ItemCard> items, DatabaseAccessor dba) {
        try {
            String cannedPhrase = dba.getCannedPhrase(qID);

            String action = query.action;

            Pattern phP = Pattern.compile("(?<=%)[\\w&]*(?=%)");
            Matcher phM = phP.matcher(cannedPhrase);

            while (phM.find()) {
                String group = phM.group();
                
                Pattern sphP = Pattern.compile("[\\w]*");
                Matcher sphM = sphP.matcher(group);
                String replacement = "";
                String object = sphM.group();
                //If the object is an action, perform action-related formatting
                if (object.equals("action")){
                    char grammar = sphM.group().charAt(0);
                    replacement = buildAction(action,grammar);
                } 
                //If the object is an actor, check which field is requested and do the corresponding formatting.
                else if (object.equals("actor")){
                    String objectField = sphM.group();
                    char grammar = sphM.group().charAt(0);
                    
                    if (objectField.equals("name"))
                        replacement = readName(actor,grammar);
                    else if (objectField.equals("buildsAt"))
                        replacement = readBuildsAt(actor,grammar);
                    else if (objectField.equals("techTree"))
                        replacement = readTechTree(actor, grammar);
                    //etc. One elif for every field in the itemCard. Not very pretty. Better way to do this?
                    /*Fields remaining: type, builtBy, size, counter, 
                     * strongAgainst, buildTime, health, armour, food, 
                     * mineralCost, gasCos, tier
                     */
                }
                //Like actor, but checks which entries in the list 'items' to use.
                else if (object.equals("object")){
                    /*TODO: 
                     * if first match after "object" is "all", then loop through items.
                     * otherwise, if given a digit, use items.get(digit).
                     * otherwise, if there's something neither a digit or "a", use
                     * the first entry of items.
                     */
                    String objectField = sphM.group();
                    char grammar = sphM.group().charAt(0);
                    
                    if (objectField.equals("name"))
                        replacement = readName(actor,grammar);
                    else if (objectField.equals("buildsAt"))
                        replacement = readBuildsAt(actor,grammar);
                    else if (objectField.equals("techTree"))
                        replacement = readTechTree(actor, grammar);
                }
                phM.reset();
                phM.replaceFirst(replacement);
            }

            readName(actor, 'a');
            return null;

        } catch (SQLite.Exception sqle) {
            sqle.printStackTrace();
        } catch (ItemCardException ice) {
            //Print to log or something.
            return "Don't know. I'll ask an expert, ask again later.";

        };
        return null;
    }
    /**
     * 
     * @param action
     * @param grammar
     * @return 
     */
    private String buildAction(String action, char grammar){
        if(grammar=='p')
            return GrammarEngine.verbPastTense(action);
        else
            return action;
    }
    
    private String readName(ItemCard item, char grammar) throws ItemCardException {
        if (item.name == null) {
            throw new ItemCardException("name", item);
        }

        String name = item.name;
        if (grammar == 'i')
            return GrammarEngine.nounIndefinite(name);
        else if (grammar == 'p')
            return GrammarEngine.nounPlural(name);
        else
            return name;
    }

    private String readBuildsAt(ItemCard item, char grammar) throws ItemCardException {
        if (item.buildsAt == null) {
            throw new ItemCardException("BuildsAt", item);
        }

        String buildsAt = item.buildsAt;

        if (grammar == 'i') {
            buildsAt = GrammarEngine.nounIndefinite(buildsAt);
        } else if (grammar == 'p') {
            buildsAt = GrammarEngine.nounPlural(buildsAt);
        }
        return buildsAt;
    }

    private String readTechTree(ItemCard item, char grammar) throws ItemCardException {
        if (item.techTreeList == null && item.techTree == null) {
            throw new ItemCardException("TechTree", item);
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
