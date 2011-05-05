/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package postagger1;

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
    public static ArrayList<String> ReadFile(String filename) {
        ArrayList<String> fileContents = new ArrayList<String>();
        try
        {
            
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String strLine;
            int count = 0;
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
}
