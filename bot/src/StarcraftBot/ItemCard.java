/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
        
/**
 *
 * @author jakob
 */
public class ItemCard implements Serializable {
    // member fields:
    String type;
    String name;
    String[] techtree;
    Addon[] abilities;
    Addon[] upgrades;
    String buildsAt;
    String[] counter;
    String[] strongAgainst;
    int buildTime;
    int health;
    int armour;
    int food;
    int mineralCost;
    int gasCost;
    
    int tier;
    
    // not necessarily there (in user query..)
    String nUnits;
            
    // constr;
    public ItemCard(String unitType) {
        try {
            // just create a simple unit (marine) for now. then read from database.
                
            
                if("marine".equals(unitType.toLowerCase()))
                {
                    String name = "marine";
                    type="light";
                    tier=1;
                    this.name=name;
                    techtree=new String[]{"Command Centre","Barracks"};
                    counter=new String[] {"vulture"};
                    buildsAt="Barracks";
                    strongAgainst=new String[] {"light"};

                    buildTime=24;
                    mineralCost=50;
                    gasCost=0;
                    food=1;
                    armour=0;
                    health=40;

                    // create the addons:
                    abilities = new Addon[1];
                    upgrades = new Addon[1];
                    abilities[0] = new Addon("stim pack", name, "Academy");
                    upgrades[0] = new Addon("U-238 shells", name, "Academy");

                }
                else throw new Exception("couldn't find match for unit type '"+unitType+"'. try again.");
        } catch (Exception ex) {
            Logger.getLogger(ItemCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    // methods
    class Addon{
        // class for the add-ons for each unit with additional requirements
        
        String name;
        String parent;
        String buildsAt;
        String[] effects;
        
        public Addon(String name, String parent, String buildsAt){
            this.name=name;
            this.parent=parent;
            this.buildsAt=buildsAt;
            
        }
    }
}
