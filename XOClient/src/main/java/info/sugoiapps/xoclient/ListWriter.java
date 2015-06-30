/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoclient;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

/**
 * 
 * @author Daniel Munkacsi
 */
public class ListWriter{
    
    private String path;
    private static final boolean WRITE = false;
    private static final boolean APPEND = true;
    
    /**
     * Create a new list writer.
     * @param p the path to the file to create /or/ a file name
     */
    public ListWriter(String p){
        path = p;
    }
    
    /**
     * Write data to file.
     * @param s the data to be written to file (NOTE: a newline character is appended to the string supplied)
     * @param mode the mode for the process. WRITE (false) or APPEND (true)
     */
    public void writeList(String s, boolean mode){
        try{
            String data = s +"\r\n";
 
            File file = new File(path);
            //if file doesnt exists, then create it
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getName(),mode);
            BufferedWriter br = new BufferedWriter(fw);
            br.write(data);
            br.close();
    	}catch(IOException e){
            e.printStackTrace();
    	} 
    }
    
    /**
     * Rewrite the list elements when user edits the address in machine chooser.
     * (NOT YET IMPLEMENTED)
     * @param lineToFind the line to find in the file
     * @param newData the new data to be written in the supplied line
     */
    public void modifyList(String lineToFind, String newData){
        ArrayList<String> filelines = (new ListReader(path).readList());
        for(int i = 0; i < filelines.size(); i++){
            if(filelines.get(i).startsWith(lineToFind)){
                filelines.set(i, lineToFind + newData);
            }
        }
        String newcontent = "";
        for(int k = 0; k < filelines.size(); k++){
            if(k == filelines.size() - 1){
                newcontent += filelines.get(k);
                break;
            }
            newcontent += filelines.get(k) + "\n";
        }
        writeList(newcontent, WRITE);
    
    }
    
    /**
     * Set the path for the file associated with this object.
     * @param p the path to be set
     */
    public void setPath(String p){
        path = p;
    }
}
