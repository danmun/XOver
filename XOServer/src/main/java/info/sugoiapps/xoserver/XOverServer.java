/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoserver;

import org.apache.commons.validator.routines.InetAddressValidator;
import java.io.InputStreamReader;
import java.net.SocketException;
import javax.swing.SwingWorker;
import java.io.BufferedReader;
import javax.swing.JTextField;
import java.awt.AWTException;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.awt.Robot;
import java.io.File;



/**
 * 
 * @author Daniel Munkacsi
 */
public class XOverServer extends SwingWorker<Void, String>{
    
    private static final String CONNECTED_MESSAGE = "Server started! Port: ";
    private static final String DISCONNECTED_MESSAGE = "Server stopped!";
    private static final String MOUSE_RELEASED = "RELEASE";
    private static final String MOUSE_PRESSED = "PRESS";
    private static ServerSocket server = null;
    private static String[] mouseparts = null;
    private static boolean addressReceived;
    private static final int PORT = 25000;
    private static JTextField statusField;
    private static String hostAddress;
    private static FileClient fc;
    private static File file;
    private static Robot rbt;

    /**
     * @param args the command line arguments
     */

    
    /**
     * Create new MouseServer.
     * Initiate connection to a listen port and create a robot for mouse imitation.
     * @param sf JTextField to modify
     */
    public XOverServer(JTextField sf){
        //switcher = sw;
        hostAddress = null;
        addressReceived = false;
        statusField = sf;
        file = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            rbt = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Inform the user that the server has been stopped.
     * @param ex the potential SocketException to handle
     */    
    private void serverClosedMessage(IOException ex){
        if(ex instanceof SocketException){
            System.out.println(DISCONNECTED_MESSAGE);
        }
        ex.printStackTrace();
    }
    
    /**
     * The main background task.
     * Accept connection from the server socket and read incoming messages.
     * Pass messages to mouse event handler.
     */
    private void listen() {
        Socket socket = null;
        InputStream in = null;
        publish(CONNECTED_MESSAGE + PORT);
        System.out.println("about to enter server loop");
        while(!Thread.interrupted()){ //!Thread.interrupted()
            try {
                socket = server.accept(); // stop is clicked, this is still waiting for incoming connections, 
                                          // therefore once the server is closed, it will produce an error
                //socket.setKeepAlive(false);
                System.out.println("started accepting from socket");
            } catch (IOException ex) {
                serverClosedMessage(ex);
                break;
            } 
            
            try {
                in = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while((line = br.readLine()) != null){
                    if(isCancelled()){
                        in.close();
                        isr.close();
                        br.close();
                        socket.close();
                        break;
                    }
                    if(!addressReceived && isValidAddress(line)){
                        System.out.println("address check entered");
                        addressReceived = true;
                        hostAddress = line;
                        startFileClient();
                    } else {
                        imitateMouse(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                serverClosedMessage(ex);
            }
        }
    }
    
    
    /**
     * Start the file client.
     */
    private void startFileClient(){
        SwingWorker fileclient = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                System.out.println("File client about to start with host IP set to " + hostAddress);
                fc = new FileClient(hostAddress);
                return null;
            }
        };
        fileclient.execute();             
        
    }
    
    /**
     * Check if supplied string is a valid IPv4 address.
     * @param s the string to check
     * @return true - if the string represents a valid IPv4 address
     */
    private boolean isValidAddress(String s){
        return InetAddressValidator.getInstance().isValidInet4Address(s);
    }
    
    /**
     * Imitate mouse behaviour.
     * @param s the mouse info received from the client
     */
    private void imitateMouse(String s){
        mouseparts = s.split(" ");
        if(rbt == null){System.out.println("robot is null, not supported; program will exit"); System.exit(0);}
        rbt.mouseMove(Integer.parseInt(mouseparts[0]), Integer.parseInt(mouseparts[1]));
        if(mouseparts.length == 4){
            if(mouseparts[2].equals(MOUSE_PRESSED)){
                rbt.mousePress(Integer.parseInt(mouseparts[3]));
            }else if(mouseparts[2].equals(MOUSE_RELEASED)){
                rbt.mouseRelease(Integer.parseInt(mouseparts[3]));
            }
        }
    }
    
    /**
     * Fetch the file client associated with this object.
     * @return the file client object
     */
    public FileClient getFileClient(){
        return fc;
    }
    
    /**
     * Carry out background task.
     * @return null
     */
    @Override
    protected Void doInBackground() {
        listen();
        return null;
    }
    
    /**
     * Update GUI.
     * @param chunks 
     */
    @Override
    protected void process(List<String> chunks) {
        statusField.setText(chunks.get(chunks.size()-1));
    }
    
    /**
     * Close the server.
     */
    @Override
    protected void done(){
        try {
            server.close();
            System.out.println("server closed");
        } catch (IOException ex) {
            serverClosedMessage(ex);
        }
        publish(DISCONNECTED_MESSAGE);
        return;
    }
    
}
