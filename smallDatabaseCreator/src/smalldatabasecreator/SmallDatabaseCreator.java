/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smalldatabasecreator;
//import StarcraftBot.*;
/**
 *
 * @author jakob
 */
public class SmallDatabaseCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        qid = 2;
        cannedPhrase = ""
        dbEntry entry = new dbEntry();
        String[] match = new String[]{"question:when", "action:build", "object:marine"};
        String cPhrase = "[obj] is a first tier unit, you should [action] it in the start of the game";

        entry.addMath(match);
        entry.doLinking();
        dba.extendDatabase(dbEntry);
        dba.updateCard();
    }

    public dbEntry
}
