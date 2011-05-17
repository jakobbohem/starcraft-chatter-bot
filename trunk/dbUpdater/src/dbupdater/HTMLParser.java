/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbupdater;

import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;

//import javax.swing.text.AttributeSet;
//import javax.swing.text.MutableAttributeSet;
//import javax.swing.text.html.HTML;
//import javax.swing.text.html.HTMLEditorKit;



/**
 *
 * @author jakob
 */
public class HTMLParser {

    public static void main(String[] args) {
        
        try {
            
            String sourceUrlString="http://wiki.teamliquid.net/starcraft/Probe";
//		if (args.length==0)
//		  System.err.println("Using default argument of \""+sourceUrlString+'"');
//		else
//		sourceUrlString=args[0];
            
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();
            
            
                System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE elements):\n");
		System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString());

		System.out.println("\nSame again but this time extend the TextExtractor class to also exclude text from P elements and any elements with class=\"control\":\n");
		TextExtractor textExtractor = new TextExtractor(source) {
			public boolean excludeElement(StartTag startTag) {
				return startTag.getName()==HTMLElementName.P || "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
			}
		};
            
        } catch (java.lang.Exception ex) {
            System.err.println("Caught exception in main!");
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
    
    
}

