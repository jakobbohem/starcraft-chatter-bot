/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
        
/**
 *
 * @author jakob
 */
public class ItemCard implements Serializable {
    // member fields:
    public String type;
    public String name;
    public String[] techTree;
    public ArrayList<String> techTreeList

//    Addon[] abilities;
//    Addon[] upgrades;
    public String buildsAt;
    public String[] counter;
    public String[] strongAgainst;
    public int buildTime;
    public int health;
    public int armour;
    public int food;
    public int mineralCost;
    public int gasCost;
    
    public int tier;
    
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
                    TechTree = new ArrayList<String>();
                    for (int i = 0;i<techTree.length;i++)
                        techTreeList.add(techTree[i]);
                    
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
                    //abilities = new Addon[1];
                    //upgrades = new Addon[1];
                    //abilities[0] = new Addon("stim pack", name, "Academy");
                    //upgrades[0] = new Addon("U-238 shells", name, "Academy");

                }
                else throw new Exception("couldn't find match for unit type '"+unitType+"'. try again.");
        } catch (Exception ex) {
            Logger.getLogger(ItemCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    // methods
    class Addon implements Serializable {
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
