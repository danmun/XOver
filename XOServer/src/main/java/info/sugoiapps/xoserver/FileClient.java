/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoserver;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;

/**
 * 
 * @author Daniel Munkacsi
 */

public class FileClient {
    
    private static final int PORT = 24999;
    private static String IP;

    /**
     * Create a new file client.
     * @param ip the address to connect to 
     */
    
    public FileClient(String ip){ // file client is only created once, when address is checked in XOverServer.java
        IP = ip;
    }
    
    /**
     * Send a file.
     * @param f the file to send
     * @throws IOException connection (type SocketException) and general File IO errors
     */
    public void sendFile(File f) throws IOException{
        Socket socket = new Socket(IP, PORT);  
   
        //Send file
        byte[] mybytearray = new byte[(int) f.length()];  
           
        FileInputStream fis = new FileInputStream(f);  
        BufferedInputStream bis = new BufferedInputStream(fis);  
        //bis.read(mybytearray, 0, mybytearray.length);  
           
        DataInputStream dis = new DataInputStream(bis);     
        dis.readFully(mybytearray, 0, mybytearray.length);  
           
        OutputStream os = socket.getOutputStream();  
           
        //Sending file name and file size to the server  
        DataOutputStream dos = new DataOutputStream(os);     
        dos.writeUTF(f.getName());     
        dos.writeLong(mybytearray.length);     
        dos.write(mybytearray, 0, mybytearray.length);     
        dos.flush();  
           
        //Sending file data to the server  
        os.write(mybytearray, 0, mybytearray.length); 
        os.flush();  
           
        //Closing socket
        os.close();
        dos.close();  
        socket.close();
    }
    
}
