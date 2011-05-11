/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StarcraftBot;

import java.util.ArrayList;
import java.io.*;
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
    public static String mergeStrings(String[] array){
        return mergeStrings(array, ",");
    }
    public static String mergeStrings(String[] strarray, String separator){
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
