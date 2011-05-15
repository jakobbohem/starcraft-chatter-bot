/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.ArrayList;
import java.io.*;
import java.util.regex.Pattern;
/**
 *
 * @author jakob
 */
public class Tools {
    
    
    public static void printArray(String[] ar)
    {
        for (int i = 0; i<ar.length; i++) {
            System.out.println(i+" "+ar[i]);
        }
        System.out.println("fin.\n\n");
    
    }
    public static void printCard(ItemCard card){
        System.out.println(" --- ITEM CARD ---");
        System.out.println(" Name: "+card.name);
        System.out.println(" Tier: "+card.tier);
        System.out.println(" Builds at: "+ card.buildsAt);
    }
    /** method returns the columns to insert into for use in SQL queries,
     * stripping the keywords used in the schema for table creation.
     * Another way of doing the same thing might be to pass a "Schema" command
     * to sqlite through the {@link SQLite.Database}.
     * 
     * @param schema is the schema on the form {"name1 integer", "name2 text" } 
     * etc. to parse.
     * 
     * @return a string that can be used in a query, i.e. 
     *  String q = "insert into tableName("+parseColumns(schema)+" values('234', 'string value')";
     * 
     * @
     */
    public static String parseColumns(String[] schema){
        String[] columns = new String[schema.length];
        
        String[] keywords = new String[] {"text","blob","key","integer","primary"};
        for (int i = 0;i<schema.length;i++)
        {
            // flush out keywords:
            String[] keys = schema[i].split(" ");
            for (int j = 0;j<keys.length;j++){
                // compare to every keyword:
                boolean isKeyword = false;
                for (int k =0;k<keywords.length;k++){
                    if (keywords[k].equals(keys[j].toLowerCase()))
                        isKeyword = true;
                }
                if (!isKeyword){
                    columns[i] = keys[j];
                    break;
                }
            }
        }
        return mergeKeys(columns, ",");
    }
    public static ArrayList<String> ReadFile(String filename) {
        ArrayList<String> fileContents = new ArrayList<String>();
        try
        {
            
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String strLine;
            while((strLine = in.readLine())!= null)
            {
                // remove comments and empty lines from input
                if(strLine.length() != 0)
                    if(!strLine.startsWith("#"))
                        fileContents.add(strLine);
            }
        }
        catch(IOException e)
        {
            System.out.println("Couldn't read in datafile. Check Tools Code!");
            e.printStackTrace();
        }
        return fileContents;
    }
    public static String[] tokenise(String str){
        String[] tokens = str.split(" ");
        return tokens;
    }
    /** merges strings with quotes around string arguments.
     * This is appropriate for passing VALUES() to database
     * 
     * This version of the method defaults to a comma separator
     * 
     * @param array is the string array of [values]
     * @returns a string of arguments that go into a SQL query.
     */ 
    public static String mergeValues(String[] array){
        return mergeValues(array, ",");
    }
    /** Merges strings for various uses, quoting the 'string' arguments.
     * Integer arguments are passed without quotes.
     * 
     * @param strarray the array of arguments to be merged
     * @param separator the separator character which is inserted inbetween the 
     * contents of strarray
     * @return 
     */
    public static String mergeValues(String[] strarray, String separator){
        String output = "";
        for (int i = 0;i<strarray.length;i++)
        {
            if(i==strarray.length-1)
                try{
                    output = output+Integer.parseInt(strarray[i]);
                } catch(NumberFormatException e){output = output+"'"+strarray[i]+"'";}
                    
            else
                try{
                    output = output+Integer.parseInt(strarray[i])+separator;
                } catch(NumberFormatException e){output = output+"'"+strarray[i]+"'"+separator;}              
        }
        return output;
    }
    /** A mergeStrings method that doesn't quote the arguments as opposed to mergeStrings2
     * This is suitable for specifying columns to write to in the database i.e. to
     * pass to the [tablename](column1, column2, ...) section of a SQL query.
     * 
     * @param strarray the strign array of arguments
     * @param separator the separator character, usually one wants a comma.
     * @return a merged string of arguments
     */
    public static String mergeKeys(String[] strarray, String separator){
        String output = "";
        for (int i = 0;i<strarray.length;i++)
        {
            if(i==strarray.length-1)
                try{
                    output = output+Integer.parseInt(strarray[i]);
                } catch(NumberFormatException e){output = output+strarray[i];}
                    
            else
                try{
                    output = output+Integer.parseInt(strarray[i])+separator;
                } catch(NumberFormatException e){output = output+strarray[i]+separator;}              
        }
        return output;
    }

    // Exapmle Dumb insert for canned phrases:
    public static String DumbInsert(String cPhrase, String[] inserts, boolean plural){

        // this assumes the second insert is the object.
        if(plural) inserts[1] = inserts[1]+"s";

        for (int i = 0;i<inserts.length;i++){ 
            // Note: the backslash escape-characters
            // in themselves need to be escaped.
            cPhrase = cPhrase.replaceFirst("\\[\\]", inserts[i]);
        }
        return cPhrase;
    }
    
    // Object serialiser for save to database
    public static byte[] Serialise(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(baos);
		oout.writeObject(obj);
		oout.close();
                byte[] serialisedObj = baos.toByteArray();
                
                return serialisedObj;
    }
    public static Object DeSerialise(byte[] ar) throws IOException, java.lang.ClassNotFoundException {
		if (ar != null) {
                    ObjectInputStream objectIn = new ObjectInputStream(
                                    new ByteArrayInputStream(ar));
                    Object returnObject = objectIn.readObject();
                    return returnObject;
                }
                return null;
    }
}
