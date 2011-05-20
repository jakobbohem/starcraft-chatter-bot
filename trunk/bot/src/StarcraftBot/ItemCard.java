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
 * Contains all the information regarding the objects of the topic of the bot, 
 * in this case Starcraft. The cards are stored as blobs in the database.. 
 * 
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
    public String[] counter = null;
    public String[] strongAgainst;
    public String[] upgradeAt = null;
    public String buildingTier; //Advanced or Basic
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
    /**
     * A constructor for test purposes.
     * @param itemType The "type" of the object
     * @param name  The name of the object
     */
    public ItemCard(String itemType, String name){
        this.type=itemType;
        this.name=name;
    }
    
    public ItemCard(String name) {
            // just create a simple unit (marine) for now. then read from database.
                
                if("MAKE_".equals(name))
                {
                    name = "marine";
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
                    upgradeAt = new String[] {"Academy","Engineering Bay"};

                    buildTime=24;
                    mineralCost=50;
                    gasCost=0;
                    food=1;
                    armour=0;
                    health=40;

                    // create the addons:
                    //abilities = new Addon[1];
                    //upgrades = new Addon[1];
                    //abilities[0] = new Addon("stim pack", this.name, "Academy");
                    //upgrades[0] = new Addon("U-238 shells", this.name, "Academy");

                }
                else {
                    this.name=name;
                }
        
    
    }
    /**
     * 
     * 
     * Changed to be able to update 'type' as well - this is needed for the new 
     * way of creating ItemCards that is used in the onlineUpdater.
     * @param arguments
     * @throws ItemCardException 
     */
    public void update(String... arguments) throws ItemCardException {
        matchKeys:
        for (int i = 0;i<arguments.length;i++){
            // note switching 'string' arguments is coming first in JDK 7
            String[] keyval = arguments[i].split(":");
            if(keyval.length!=2)
                throw new IllegalArgumentException("The update statement must be an existing fiels"
                        + " followed by the new value, separated by colon (:)");
            String key = keyval[0].trim();
            String value = keyval[1].trim();

            try{
            // going through the fields of ItemCard
            if(key.toLowerCase().equals("name"))
                this.name = value;
            else if(key.toLowerCase().equals("type"))
                this.type = value;
            else if(key.toLowerCase().equals("size"))
                this.size = value;
            else if(key.toLowerCase().equals("tier"))
                this.tier = Integer.parseInt(value);
            else if(key.toLowerCase().equals("techtree")){
                String[] array;
                if (techTree!=null){
                    array = new String[techTree.length+1];
                }
                else {
                    techTree = new String[] {value};
                    continue;
                }
                array[array.length-1] = value;
                System.arraycopy(techTree, 0, array, 0, techTree.length);
                techTree = array;
                techTreeList.add(value);
            }
            else if(key.toLowerCase().equals("counter")){
                String[] array;
                if (counter!=null){
                    array = new String[counter.length+1];
                }
                else {
                    counter = new String[] {value};
                    continue;
                }
                array[array.length-1] = value;
                System.arraycopy(counter, 0, array, 0, counter.length);
                counter = array;
            }
            else if(key.toLowerCase().equals("buildsat"))
                this.buildsAt = value;
            else if(key.toLowerCase().equals("strongagainst")){
                String[] array;
                if (strongAgainst!=null){
                    array = new String[strongAgainst.length+1];
                }
                else {
                    strongAgainst = new String[] {value};
                    continue;
                }
                array[array.length-1] = value;
                System.arraycopy(strongAgainst, 0, array, 0, strongAgainst.length);
                strongAgainst = array;
            }
            else if(key.toLowerCase().equals("upgradeat")){
                String[] array;
                if (upgradeAt!=null){
                    array = new String[upgradeAt.length+1];
                }
                else {
                    upgradeAt = new String[] {value};
                    continue;
                }
                array[array.length-1] = value;
                System.arraycopy(upgradeAt, 0, array, 0, upgradeAt.length);
                upgradeAt = array;
            }
            else if(key.toLowerCase().equals("buildingtier"))
                this.buildingTier = value;
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

            else throw new ItemCardException("Couldn't interpret input string: '"+arguments[i]+"'");
            } catch(NumberFormatException e){
                System.out.println("Coudln't parse input value as int, not updating that field.");
                System.out.println(e.getMessage());
            }
                        
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
