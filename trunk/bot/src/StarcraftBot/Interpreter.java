/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import java.io.*;
import java.util.*;
/**
 *
 * @author jakob
 */
public class Interpreter {
    Query lastQuery;
    Response lastResponse;
    String database;
    TreeMap<String,Response> responseData;
    
    final String responseDatafile = "corpus/responseData.txt";
    String[] responseDataArray;
    
    String name = "Jeeves";
    
    // constr
    Interpreter()
    {
        // base constructor;
        this.database = "corpus/responseData.txt";
        createResponseDatabase();
    }
    Interpreter(String name){this.name=name;}
    
    // public methods
    public Query interpretTags(String[] tags, String[] inputs) throws SparseSpecException {
        // NOTE: Right now the generator just if-elses through the tags specified in the database (or corpus/wordVals.txt)
        // It may be done in a smoother way...
        
        // ALSO NOTE: Right now there is no double checking. It's the LAST action/ target pair that will be used!
        String action = null;
        String target = null;
        // assuming length(tags) == length(inputs) CHECK!
        for (int i = 0; i < tags.length; i++){
            // first find the action!
            if("action".equals(tags[i])) // not to be hard coded?
            {    action = inputs[i];
                if ("unit".equals(tags[i+1]))
                    target = inputs[i+1];
                else if ("unit".equals(tags[i+2])) // better check for the relationship between action and target?
                    target = inputs[i+2];
            }
                
        }
        if (action!=null && target != null){
            Query query = new Query(action, target);
            return query;
        }
        else throw new SparseSpecException("(at least) Action and Target must be defined to generate Query");
        
    }
    public String getReply(Query query){
        String reply;
        if(query.checkNotNull())
        {
            //String[] databaseEntry = match(query, this.responseDatafile); // change to some REAL database!
            Response response = match(query); // to the current database (knowledgebase)
            reply = generateReply(response);//generateReply(databaseEntry);
        }
        else
            reply = "Couldn't find all the relevant params in Query. Please enter new query. DEBUG: Check 'Interpreter'";
        return reply;
    }
    
    // private methods
    private String generateReply(Response r){
        return r.getStringResponse();
    }
//    private String generateReply(String[] data){
//        String reply = "";
//        for (int i = 0;i<data.length;i++)
//            reply = i==data.length-1 ? reply+"." : reply+" "+data[i];
//        
//        return reply;
//    }
    /* 
     * Method will be depricated once the plumbing for the token based answer finder is written.
     * 
     */
    private String generateReply(Query query, String database){
        
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(database));
            // tags2 = new TreeMap<String, String>();

            String strLine;
            ArrayList<String> responses = new ArrayList<String>();
            while((strLine = in.readLine())!= null)
            {
                if(!strLine.isEmpty())
                    if(!"#".equals(strLine.substring(0, 1)))
                        responses.add(strLine);
            }
            responseDataArray = responses.toArray(new String[responses.size()]); 
        // add code to get the appropriate reponse
        }
        catch(IOException e)
        {
            System.out.println("Couldn't load replies data");
        }
        return responseDataArray[0]; //new String[] {"Default","Data","Reply"};
    }
    private Response match(Query query) {
        String[] keywords = query.getMetadata();
        String kw = keywords[1].toUpperCase();
        Response r = new Response();
        //  try to find a matching response in the database
        try
        {
            r = responseData.get(kw);
            if (r == null) throw new IllegalArgumentException();
        }
        catch(IllegalArgumentException e)
        {
            r = new Response("No matching response to '"+kw+"' could be found in the database (was null)");
        }
        return r;
        // just match 1 keyword for now. what's a better way of matching?
        //String strategy = ""; // etc..
    }
    private String[] match(Query query, String database){
        // here shuold be the matching of relevant data (from database) to the generated Query.
        
        // database for now is a file with text string data replies.
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(database));
            // tags2 = new TreeMap<String, String>();

            String strLine;
            int count = 0;
            while((strLine = in.readLine())!= null)
            {
                //String[] pair = strLine.split("\t");
                //String key = pair[0].toLowerCase(); String value = pair[1].toLowerCase();

                //tags2.put(key, value);

                // use a simple String array for now
                responseDataArray[count]=strLine;
                count++;
            }
        
        // add code to get the appropriate reponse
        }
        catch(IOException e)
        {
            System.out.println("Couldn't load replies data");
        }
        String[] replydata = responseDataArray[0].split(" ");
        return replydata; //new String[] {"Default","Data","Reply"};
    }

    private TreeMap createResponseDatabase() {
        TreeMap map = new TreeMap<String,Response>();
        ArrayList<String> temp = Tools.ReadFile(database);
        String[] fileContents = temp.toArray(new String[temp.size()]);
        for (int i =0; i<fileContents.length; i++){
            String[] keyValuePair = fileContents[i].split("<-");
            if(keyValuePair.length==2)
                map.put(keyValuePair[0].trim(), new Response(keyValuePair[1]));
            // else //it's a plain line of text, ignore for now.
        }
        
        this.responseData = map;
        return map;
        
    }
    
    
    // exceptions
    class SparseSpecException extends Exception{
        public SparseSpecException(String message){
            super(message);
        }
    }
}
