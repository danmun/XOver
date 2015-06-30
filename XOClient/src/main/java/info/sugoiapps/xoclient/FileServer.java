/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoclient;

import java.io.FileOutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.DataInputStream;
import javax.swing.SwingWorker;
import java.net.ServerSocket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


/**
 * 
 * @author Daniel Munkacsi
 */
public class FileServer extends SwingWorker<Void,Void> {
    
    private static final int PORT = 24999;
    private static ServerSocket server;
    private ProgressGUI progress;
    private int filesize;
    
    /**
     * BUG
     *  Filesize is an int, but the received actual size is a long, casting that to int is not possible beyond a size of 100 Gigabytes (would be too large a number for int to represent)
     *  This is a minor bug for users who wish to transfer 100 Gb at a time
     */
    
    
    /**
     * Create a new file server.
     */
    public FileServer(){
        try {
            server = new ServerSocket(PORT);
        } catch (IOException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = new ProgressGUI();
    }
     
    /**
     * Start the server.
     * @throws IOException can be thrown during connection failures and write errors
     */
    public void download() throws IOException {
        int bytesRead;
        while(true){
            Socket clientSocket = null;  
            clientSocket = server.accept();  
           
            InputStream in = clientSocket.getInputStream();  
           
            DataInputStream clientData = new DataInputStream(in);   
           
            String filename = clientData.readUTF();
            OutputStream output = new FileOutputStream(filename);     
            long size = clientData.readLong();
            filesize = (int) size;
            System.out.println("filesize is " + filesize + ", and the long version received is " + size);
            displayProgress(filename);
            System.out.println("filesize received is " + filesize + " bytes");
            int downloaded = 0;
            byte[] buffer = new byte[1024];     
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
                progress.setProgress(getProgress(downloaded));
                output.write(buffer, 0, bytesRead);     
                size -= bytesRead;  
                downloaded += bytesRead;
            }
            disposeProgress();
            
            // Closing the FileOutputStream handle
            in.close();
            clientData.close();
            output.close();
        }  
    }    
    
    /**
     * Display the progress of the current download.
     * @param filename the filename to be displayed in the progressbar
     */
    private void displayProgress(String filename){
        progress.setInfoText("Downloading " + filename + "");
        progress.pack();
        progress.setLocationRelativeTo(null);
        progress.setVisible(true);
    }
    
    /**
     * Dispose the progress bar gui.
     */
    private void disposeProgress(){
        progress.setVisible(false);
        progress.setProgress(0);
        progress.dispose();
    }
    
    /**
     * Get the progress of the current download.
     * @param downloaded the current size of the download
     * @return the percentage progress of the download
     */
    private int getProgress(int downloaded) {
        return (int) (((float)downloaded / (float)filesize) * 100);
    }
    
    @Override
    protected Void doInBackground() {
        try{
            download();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void done(){
        try {
            server.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
