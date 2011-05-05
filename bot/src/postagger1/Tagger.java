/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package postagger1;
import java.util.*;
import java.io.*;
/**
 *
 * @author jakob
 */
public class Tagger {
    private String[] lastChunks;
    AbstractMap.SimpleEntry<String,String>[] tags; // try different approach
    TreeMap<String, String> tags2;
    final String corpusFile = "corpus/datafile.txt";
    final String tagsFile = "corpus/wordVals.txt";
    
    // constr;
    public Tagger()
    {
        // create the tagger object
        try {
            createCorpus();
            createDatabase(); // here I just read some file of hard coded tags
        }
        catch (IOException e){
            System.out.println("Couldn't initialize the Tagger object. Check code!");
            System.out.println(e);
        }
    }
    
    // public methods
    public String[] chopString(String input){
        String[] chunks = input.split(" ");
        this.lastChunks = chunks;
        return chunks;
    }
    
    public String[] getTagsFromDatabase() throws ExecutionOrderException, IOException {// returns tags, use presaved vars
        
        // - - - - - -
        createDatabase(); // recreate on each call for now.
        // - - - - - - 
        
        String[] tags = new String[lastChunks.length];
        if (lastChunks.length == 0)
            throw new ExecutionOrderException("didn't get an input string, please run 'chopString' to set 'lastChunks'.");
        for(int i = 0; i<lastChunks.length; i++)
        {
            String item = lastChunks[i].toLowerCase();
            if(tags2.get(item)!=null)
                tags[i] = tags2.get(item);
            else if(item.endsWith("s") && tags2.get(item.substring(0,item.length()-1)) != null) //simple check for plural
                tags[i] = tags2.get(item.substring(0,item.length()-1));
            else
                tags[i] = "[null]";
        }
        return tags;
         
    }
    // private methods
    private void createDatabase() throws IOException { 
        
        BufferedReader in = new BufferedReader(new FileReader(tagsFile));
        tags2 = new TreeMap<String, String>();
        String strLine;
        while((strLine = in.readLine())!= null)
        {
            if(!strLine.isEmpty())
                if(!"#".equals(strLine.substring(0, 1)))
                {
                    String[] pair = strLine.split("\t");
                    String key = pair[0].toLowerCase(); String value = pair[1].toLowerCase();
                    tags2.put(key, value);
                }
        }
    }
    private void createCorpus(){
        // create the corpus to derive the probabilities for the text tagger.
        // read from file
        
    }
    
    // Exceptions
    class ExecutionOrderException extends Exception{public ExecutionOrderException(String message){super(message);}}
    
    // get-set nightmare.
    public String[] getLastChunks(){return this.lastChunks;}
}
