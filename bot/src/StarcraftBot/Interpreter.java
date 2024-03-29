/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;
import StarcraftBot.DatabaseAccessor.DatabaseException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Random random = new Random();
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
    public Query interpretTags(ArrayList<ArrayList> tags, String input, int exitcode) throws SparseSpecException {
        // NOTE: Right now the generator just if-elses through the tags specified in the database (or corpus/wordVals.txt)
        // It may be done in a smoother way...
        
       
        
        // Variables 
        String question = null;
        String action = null;
        String strobject = null;
        String actor = null;
        String in = input.toLowerCase();
        String lastinput;
        String yesno;
        boolean foundq = false;
        boolean founda = false;
        boolean foundobj = false;
        boolean foundactor = false;
        boolean objplural = false;
        boolean gotQuery = false;
        Query query = null;
        
        Scanner scan = new Scanner(System.in);
        
        int exit = exitcode;
        if(exit == 1)
            System.exit(0);
        
       
        // assuming length(tags) == length(inputs) CHECK!
//        for (int i = 0; i < tags.length; i++){
//            // first find the action!
//            if("action".equals(tags[i])) // not to be hard coded?
//            {   action = inputs[i];
//                if ("unit".equals(tags[i+1]))
//                    strobject = inputs[i+1];
//                else if ("unit".equals(tags[i+2])) // better check for the relationship between action and target?
//                    strobject = inputs[i+2];
//            }
//            if("question".equals(tags[i]))
//                question = inputs[i];     
 //            }
        ArrayList<ArrayList> taglist = tags;
        ListIterator<ArrayList> tagListitr = taglist.listIterator();
        // Find question first
       while(!gotQuery)
       {
           tagListitr = taglist.listIterator();
       while(tagListitr.hasNext() && !foundq)
       {
           ArrayList<String> temp = tagListitr.next();
          
           ListIterator<String> tempitr = temp.listIterator();
           while(tempitr.hasNext())
           {
               String tempstr = tempitr.next();
               if(in.indexOf(tempstr) >= 0 && temp.get(0).equals("Q"))
               {
                   question = temp.get(1);
                 //  System.out.println("question is: " + question);
                   foundq = true;
                   break;
               }
           }
       }
       // Then find the action
       tagListitr = taglist.listIterator();
       while(tagListitr.hasNext() && !founda)
       {
           ArrayList<String> temp = tagListitr.next();
         //  System.out.print(temp.get(0));
           ListIterator<String> tempitr = temp.listIterator();
           while(tempitr.hasNext())
           {
               String tempstr = tempitr.next();
               if(in.indexOf(tempstr) >= 0 && temp.get(0).equals("A"))
               {
                   action = temp.get(1);
             //     System.out.println(action);
                   founda = true;
                   break;
               }
           }
       }
       // And then the target
       tagListitr = taglist.listIterator();
       while(tagListitr.hasNext() && !foundobj)
       {
           ArrayList<String> temp = tagListitr.next();
         //  System.out.print(temp.get(0));
           ListIterator<String> tempitr = temp.listIterator();
           while(tempitr.hasNext())
           {
               String tempstr = tempitr.next();
               if(in.indexOf(tempstr) >= 0 && (temp.get(0).equals("U") || temp.get(0).equals("B")))
               {
                   strobject = temp.get(1);
                  // System.out.println(strobject);
                   foundobj = true;
                   break;
               }
           }
       }
       //Time to fix the actor as well
       tagListitr = taglist.listIterator();
       while(tagListitr.hasNext() && !foundactor)
       {
           ArrayList<String> temp = tagListitr.next();
         //  System.out.print(temp.get(0));
           ListIterator<String> tempitr = temp.listIterator();
           while(tempitr.hasNext())
           {
               String tempstr = tempitr.next();
               if(in.indexOf(tempstr) >= 0 && (temp.get(0).equals("U") || temp.get(0).equals("B")))
               {
                  // System.out.println("I'm here now");
                   if(in.indexOf("with " + tempstr) >= 0)
                   {
                       System.out.println("Just to clarify, you are the one controlling the " + temp.get(1) + "s, right?\n");
                       System.out.println("User: ");
                       yesno = scan.nextLine();
                       yesno = yesno.toLowerCase();
                       if(yesno.equals("yes") || yesno.equals("yeah"))
                       {
                           System.out.println("Alright, I understand.\n");
                           actor = temp.get(1);
                   
                            //System.out.println("actor is " + actor);
                            foundactor = true;
                            break;
                           // Need to manipulate query 
                       }
                       else if(yesno.equals("no") || yesno.equals("nope"))
                       {
                           System.out.println("Alright, I understand, you don't.\n");
                           actor = temp.get(1);
                   
                           //System.out.println("actor is " + actor);
                           foundactor = true;
                           break;
                       }
                   }
                   else if(!tempstr.equals(strobject) && !tempstr.equals(strobject + "s"))
                   {
                     
                           actor = temp.get(1);
                   
                           //System.out.println("actor is " + actor);
                           foundactor = true;
                           break;
                   }
               }
           }
       }
       // Query query = null;
        if (action!=null && strobject != null){
            if(action!=null && strobject != null && question!= null && actor == null)
            {
                query = new Query(action, strobject, question);
                return query;
                
            }
            else if(action!=null && strobject != null && question!= null && actor != null)
            {
                query = new Query(action, strobject, question, actor);
                                

                return query;
            }
        
        }
         //Strings for follow-up questions
        String[] noAction = new String[]{"You need to tell me what you want to do with " + strobject + "s.\n",
        "Ah, " + strobject + "s, gotcha. Now, please let me know what you want to do with it.\n",
        "You mention " + strobject + "s. What do you wish to know about " + strobject + "s?\n"
        };
        String[] onlyAction = new String[]{ "So you want to do some " +  action + "ing, eh? Please elaborate.\n",
        "You clearly want to " + action + " something. What would you like to " + action + "?\n",
        "It sure is possible to " + action + " things, but you'll have to be a bit more specific. \n"
        };
        String[] repeating = new String[]{"You're repeating yourself and it's not helping! \n",
        "Saying the same thing over and over won't produce the result you are looking for. \n",
        "Yes, I believe you just said that. Try entering something else this time. \n"
        };
        String[] noKeywords = new String[]{"I couldn't relate your question to Starcraft at all. Perhaps you phrased it badly? \n",
        "Nope, didn't understand that at all. Remember to keep the questions related to Starcraft!\n",
        "I'm afraid that has nothing to do with Starcraft, and my programming prohibits me from commenting further.\n"
        };
        String[] onlyQuestion = new String[]{question + " what? Please clarify.\n",
        "You said " + question + ", but " + question + " what?\n"            
        };
        String[] onlyObject = new String[]{"Ah, you're talking about " + strobject + "s. I know all about that! Ask me anything game related about " + strobject + ".\n",
        "You can ask me anything about " + strobject + "s!\n",
        "I'm a bit of an expert on " + strobject + "s, if I do say so myself. Ask me something about that!\n"
        };
        
        // Follow up questions. TODO! Right now the user has to ask
        // a completely new question. Make it so that the follow up is more natural!
        
        if(question == null && action == null && strobject == null && actor == null)
        {
            System.out.println(noKeywords[random.nextInt(3)]);
        }
        if(question == null && action != null && strobject == null )
        {
            System.out.println(onlyAction[random.nextInt(3)]);
            //System.out.println("You clearly want to " + action + " something. What would you like to " + action + "?\n");
            //question = "who";
        }
        else if(question != null && action == null && strobject == null)
        {
            System.out.println( onlyQuestion[random.nextInt(2)]);
        }
        else if(question == null && action == null && strobject != null)
        {
            System.out.println( onlyObject[random.nextInt(3)]);
        }
        else if(question == null && action != null && strobject != null)
        {
            return query = new Query( action, strobject,"how");
        }
        else if(question != null && action == null && strobject != null)
        {
           // System.out.println("You need to tell me what you want to do with " + strobject + ".");
            System.out.println(noAction[random.nextInt(3)]);
        }
        lastinput = in;
        System.out.println("User: ");
        in = scan.nextLine();
        while(in.equals(lastinput))
        {
            System.out.print(repeating[random.nextInt(3)]);
            System.out.println("User: ");
            lastinput = in;
            in = scan.nextLine();
        }
        try
        {
            exit = Integer.parseInt(in); 
        }catch (NumberFormatException e) {
                }
        finally {}
        if(exit == 1)
            System.exit(0);


        
        }
       return query;
       // else throw new SparseSpecException("(at least) Action and Target must be defined to generate Query");   
    }
    public String getReply(Query query, DatabaseAccessor dba){
        String reply;
        if(query.baseCheckNotNull())
        {
            try {
                //String[] databaseEntry = match(query, this.responseDatafile); // change to some REAL database!
                Response response = match2(query, dba); //generateReply(databaseEntry);
                reply = generateReply(response, query.isPlural);//generateReply(databaseEntry);
            } catch (DatabaseException ex) {
                reply = "Database-exception: "+ex.getMessage();
                Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLite.Exception ex) {
                reply = "Couldn't communicate with database, missing reply!";
                Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            reply = "Couldn't find all the relevant params in Query. Please enter new query. DEBUG: Check 'Interpreter'";
        return reply;
    }
    public String oldGetReply(Query query){
        String reply;
        if(query.baseCheckNotNull())
        {
            //String[] databaseEntry = match(query, this.responseDatafile); // change to some REAL database!
            Response response = match(query); // to the current database (knowledgebase)
            reply = oldGenerateReply(response);//generateReply(databaseEntry);
        }
        else
            reply = "Couldn't find all the relevant params in Query. Please enter new query. DEBUG: Check 'Interpreter'";
        return reply;
    }
    
    // private methods
    private String generateReply(Response r, boolean plural){
        return r.processCannedPhrase(plural);
    }
    private String oldGenerateReply(Response r){
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
    private Response match2(Query query, DatabaseAccessor dba) throws DatabaseException, SQLite.Exception{
        // now, the order is [question, action, object] which won't match legacy method match().
        String[] keywords = query.getMetadata();
        String q = keywords[0];
        String a = keywords[1];
        String ob = keywords[2];
        ItemCard card = dba.getItemCard(ob);

        // NOTE: the fields are private fields in dba, but currently contain:
        // id, question, action, actor, object, queryId
        String cPhrase = dba.getCannedPhrase(dba.getQid("question:"+q,"action:"+a,"object:"+ob));
        // this is super dumb. the order should be handled in a clever way.
        Response r = new Response(cPhrase, new String[] {a, ob, card.buildsAt});
        return r;
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
    
    
   
}
class SparseSpecException extends Exception{
        public SparseSpecException(String message){
            super(message);


        }
    }
