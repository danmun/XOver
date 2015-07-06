/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoserver;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.io.InputStream;

/**
 * 
 * @author Daniel Munkacsi
 */

public class FileClient {
    
    private static final int BUFFER_SIZE = 65536;
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
        socket.setTcpNoDelay(true);
        
        InputStream is = new FileInputStream(f);
        OutputStream os = socket.getOutputStream();  
           
        //Sending file name and file size to the server  
        DataOutputStream dos = new DataOutputStream(os);     
        dos.writeUTF(f.getName());  
        dos.writeLong(f.length());
        dos.flush();  
           
        //Sending file data to the server
        int read;
        byte[] buffer = new byte[BUFFER_SIZE];
        while((read = is.read(buffer)) != -1){
            os.write(buffer, 0, read);
        }
        os.flush(); 
        dos.flush();
        
        //Closing socket
        os.close();
        dos.close();  
        socket.close();
    }
}