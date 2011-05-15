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

    public String builtBy;

    public String size;
    public String name;
    public String[] techTree;
    public ArrayList<String> techTreeList;
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
                    type="unit";
                    size = "light";
                    tier=1;
                    this.name=name;
                    
                    techTree=new String[]{"Command Centre","Barracks"};
                    techTreeList = new ArrayList<String>();
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
    public void update(String... arguments) throws ItemCardException {
        for (int i = 0;i<arguments.length;i++){
            // note switching 'string' arguments is coming first in JDK 7
            String[] keyval = arguments[i].split(":");
            if(keyval.length!=2)
                throw new IllegalArgumentException("The update statement must be an existing fiels"
                        + " followed by the new value, separated by colon (:)");
            String key = keyval[0].trim();
            String value = keyval[1].trim();

            // going through the fields of ItemCard
            if(key.toLowerCase().equals("name"))
                this.name = value;
            else if(key.toLowerCase().equals("size"))
                this.size = value;
            else if(key.toLowerCase().equals("tier"))
                this.tier = Integer.parseInt(value);
            else if(key.toLowerCase().equals("techtree")){
                String[] array = new String[counter.length+1];
                array[array.length] = value;
                counter = array;
                // is this a better approach in general?
                techTreeList.add(value);
            }
            else if(key.toLowerCase().equals("counter")){
                String[] array = new String[counter.length+1];
                array[array.length] = value;
                counter = array;
            }
            else if(key.toLowerCase().equals("buildsat"))
                this.buildsAt = value;
            else if(key.toLowerCase().equals("strongagainst")) {
                String[] array = new String[strongAgainst.length+1];
                array[array.length] = value;
                strongAgainst = array;
            }
            else if(key.toLowerCase().equals("buildtime"))
                this.buildTime = Integer.parseInt(value);
            else if(key.toLowerCase().equals("mineralcost"))
                this.mineralCost = Integer.parseInt(value);
            else if(key.toLowerCase().equals("gascost"))
                this.gasCost = Integer.parseInt(value);
            else if(key.toLowerCase().equals("food"))
                this.food = Integer.parseInt(value);
            else if(key.toLowerCase().equals("armour") || key.toLowerCase().equals("armor"))
                this.armour = Integer.parseInt(value);
            else if(key.toLowerCase().equals("health"))
                this.health = Integer.parseInt(value);
            else if(key.toLowerCase().equals("builtBy")) {
                if(type =="building")
                    this.builtBy = value;
                else
                    throw new ItemCardException("builtBy only applies to building.");
            }
            else throw new ItemCardException("Couldn't interpret input string: "+arguments[i]);
                     
                        
        }
    }


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
