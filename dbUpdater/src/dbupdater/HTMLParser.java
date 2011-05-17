/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbupdater;

import SQLite.Exception;
import StarcraftBot.DatabaseAccessor;
import StarcraftBot.DatabaseAccessor.DatabaseException;
import StarcraftBot.ItemCard;
import StarcraftBot.ItemCardException;
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import StarcraftBot.Tools;

//import javax.swing.text.AttributeSet;
//import javax.swing.text.MutableAttributeSet;
//import javax.swing.text.html.HTML;
//import javax.swing.text.html.HTMLEditorKit;



/**
 *
 * @author jakob
 */
public class HTMLParser {

    // fields:
    String dbFile = "../bot/corpus/database";

    public static void main(String[] args) {
        
        try {
            
            HTMLParser p = new HTMLParser();
            p.updateItemCardsOnline("itemsList.txt");
            
        } catch (java.lang.Exception ex) {
            System.err.println("Caught exception when doing HTMLParser dbUpdate!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private static void printRaw(URLConnection c) throws IOException{
        BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    c.getInputStream()));
            String inputline;
            while((inputline = in.readLine()) != null)
                System.out.println(inputline);
            in.close();
    }
    private void listTree(Element base){
        int depth = base.getDepth();
        
        Element cp = base;
        for(int k = 0;k<depth;k++){ // for every level
                List<Element> childElements = cp.getChildElements();
                //if()
                for(int i = 0;i<childElements.size();i++){
                    System.out.printf("\nelement (at level %d): %s",k, childElements.get(i).getName());
                    cp = childElements.get(i);
                }
            }

    }
    private void test1() throws MalformedURLException, IOException{
        String itemname="Probe";
            String sourceUrlString="http://wiki.teamliquid.net/starcraft/"+itemname;
//		if (args.length==0)
//		  System.err.println("Using default argument of \""+sourceUrlString+'"');
//		else
//		sourceUrlString=args[0];

		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
                // is this stuff needed?
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

                // my calls:
                String baseId = "bodyContent";
                String titleClass = "infobox";
                Element bod = source.getElementById(baseId);
                List<Element> title = bod.getAllElementsByClass(titleClass); // should be just 1

                int depth = bod.getDepth();
                System.out.printf("depht of the %s element is : %d\n", baseId, depth );

                // List<Element> childElements = bod.getChildElements();

                // first try getting the first one!
                Element element = title.get(0);

                //System.out.printf("\nFound data: %s", data);
                TextExtractor extractor = new TextExtractor(element) {
			public boolean includeElement(StartTag startTag) {
				return startTag.getName()==HTMLElementName.B ;//|| "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
			}
		};

//                System.out.println("data: "+extractor.toString());
//                String[] blocks = extractor.toString().split(":");

                HTMLParser parser = new HTMLParser();
                ArrayList<String> updates = parser.getItemCardUpdates(itemname);
                Tools.printArray(updates.toArray(new String[updates.size()]));

//                System.out.println("\n METHODS FROM INITIAL EXAMPLE: ");
//                System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");
//		System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString());
//
//		System.out.println("\nSame again but this time extend the TextExtractor class to also exclude text from P elements and any elements with class=\"control\":\n");
//		TextExtractor textExtractor = new TextExtractor(source) {
//			public boolean excludeElement(StartTag startTag) {
//				return startTag.getName()==HTMLElementName.P || "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
//			}
//		};
    }
    public void updateItemCardsOnline(String urlEndingsFile) throws IOException, IllegalArgumentException, Exception, ItemCardException{
        ArrayList<String> fromFile = Tools.ReadFile(urlEndingsFile);

        String[] endings = fromFile.toArray(new String[fromFile.size()]);
        for (int i = 0;i<endings.length;i++){
            
            // do this once per update to aviod loosing the whole batch!
            DatabaseAccessor dba = new DatabaseAccessor(dbFile);
            // update each of the cards, creating them if they don't exist!
            ArrayList<String> updates = getItemCardUpdates(endings[i]);
            String name = updates.get(updates.size()-1).split(":")[1].trim(); // assumes that name is last entry!
            try{
                ItemCard c = dba.getItemCard(name);
                System.out.println("Found existing item card: "+c.name);
            } catch(DatabaseException e){
                // card wasn't found: create it!
                dba.write(new ItemCard(name)); // should take name and not type - this sets type to "name" right now...
            }
            try{
                int row = dba.updateItemCard(name, updates.toArray(new String[updates.size()]));
                System.out.printf(" - updated itemCard '%s' on line %d \n",name, row);
            } catch(DatabaseException e){
                System.err.printf("Couldn't update database for %s!\n", name);
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            dba.close();
        }
        System.out.println("Finished updating database!");
    }
    public ArrayList<String> getItemCardUpdates(String itemName) throws IOException{

        ArrayList<String> updates = new ArrayList<String>();

        String sourceUrlString="http://wiki.teamliquid.net/starcraft/"+itemName;
//		if (args.length==0)
//		  System.err.println("Using default argument of \""+sourceUrlString+'"');
//		else
//		sourceUrlString=args[0];

        if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
        // is this stuff needed?
        MicrosoftConditionalCommentTagTypes.register();
        PHPTagTypes.register();
        PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
        MasonTagTypes.register();
        Source source = new Source(new URL(sourceUrlString));

        // Call fullSequentialParse manually as most of the source will be parsed.
        source.fullSequentialParse();

        // my calls (NOTE: Unique to liquipedia)
        String baseId = "bodyContent";
        String titleClass = "infobox";
        Element bod = source.getElementById(baseId);
        List<Element> title = bod.getAllElementsByClass(titleClass); // should be just 1

        // List<Element> childElements = bod.getChildElements();

        // first try getting the first one!
        Element element = title.get(0);

        //System.out.printf("\nFound data: %s", data);
        TextExtractor extractor = new TextExtractor(element) {
                public boolean includeElement(StartTag startTag) {
                        return startTag.getName()==HTMLElementName.B ;//|| "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
                }
        };

        System.out.println("FOUND DATA: "+extractor.toString());
        String[] blocks = extractor.toString().split(":");

        // best string operation EVER!!
        // String name = extractor.toString().split(":")[0].trim().replace(" ", "_").toLowerCase();
        // fields to get:

        String name = blocks[0].substring(0, blocks[0].lastIndexOf(" ")).replace(" ", "_");//.toLowerCase();

        // can also do e.g.: cooldown, range, attack
        boolean setType = false;
        for (int i = 1;i<blocks.length;i++){
            if(blocks[i].toLowerCase().endsWith("type"))
            {
                String[] data = blocks[i+1].split(" ");
                int index = 0;
                if(data[index].isEmpty())
                    index++;
                updates.add("size:"+data[index].trim());
                //airground = data[2];
                updates.add("type:"+data[index+2].trim());
                setType = true;
            }
            if(blocks[i].toLowerCase().endsWith("cost"))
            {
                String[] data = blocks[i+1].split(" ");
                int index = 0;
                if(data[index].isEmpty())
                    index++;
                updates.add("mineralCost:"+data[index].trim());
                updates.add("gasCost:"+data[index+1].trim());
                updates.add("buildTime:"+data[index+2].trim());
            }
            if(blocks[i].toLowerCase().endsWith("defence"))
            {
                String[] data = blocks[i+1].split(" ");
                int index = 0;
                if(data[index].isEmpty())
                    index++;
                updates.add("health:"+data[index].trim());
                //updates.add("shield:"+data[1].trim());
                updates.add("armour:"+data[index+2].trim());
            }
//            if(blocks[i].equalsIgnoreCase("sight"))
//            {
//                String[] data = blocks[i+1].split(" ");
//            }
        }
        if(!setType) // assumes type is a building!
            updates.add("type: building");
        updates.add("name:"+name); // NEEDS TO BE LAST ELEMENT FOR NOW!
        return updates;
    }
}

