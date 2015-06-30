/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xoclient;

import org.apache.commons.validator.routines.InetAddressValidator;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.net.InetAddress;
import java.io.File;


/**
 * 
 * @author Daniel Munkacsi
 */

// BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS //
// BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS //
/**
 * BUG 1
 * tl;dr - file client on remote machine can't rely on host machine's method of sending its IP.
 * The problem with sending the machine IP to the file client is that Java (or the system) might not pick the appropriate 
 * network interface (NIC) during getLocalAddress(). 
 * eg. there are 3 network interfaces, a wifi connection, ethernet connection and a virtual box network connection
 * Java could pick any one of these three. This means The getLocalAddress() method won't always return the correct IP address of the main machine 
 * (which is then sent to file client on the remote machine). This will result in a connection refused error and the main mouse client will crash.
 * 
 * Unfortunately, there is no way to choose a specific NIC without knowing its name and/or address, therefore this method should be deprecated 
 * and a different methodology used to identify the host (main) machine for the file client.
 * 
 * These may be of help: 
 * http://docs.oracle.com/javase/tutorial/networking/nifs/definition.html
 * http://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
 * 
 * BUG 2
 * When ran from the JAR file, the program doesn't seem to accept large files (or just MKV files (further experimenting needed)).
 * When ran from the IDE, the program accepts all files regardless of type or size.
 * 
 * BUG 3
 * When the server is not running, the client will not switch to the block screen and also can't exit.
 * There needs to be a way to exit the client in any case. 
 * Planned solution: implement JIntellitype to listen to a key combination to bring up an exit confirmation dialog.
 * 
 * ADDITIONAL:
 * Speed improvement can be achieved by using buffer, read more at
 * http://stackoverflow.com/questions/1169739/java-tcp-socket-data-transfer-is-slow
 * 
 * @author Daniel Munkacsi
 */
// BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS //
// BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS BUGS //


public class XOverClient {
    private static final String CONFIG_FILENAME = "config.txt";
    private static final boolean APPEND = true;
    private static String REMOTE_IP;
    private static String MACHINE_IP;
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // System.getProperty("user.dir")); gets working directory in the form: C:\Users\Munyosz\etc...\
        final String SEPARATOR = " - ";
        XOverClient client = new XOverClient();
        String ladrs = getLocalAddress();
        if(client.validAddress(ladrs)){
            MACHINE_IP = ladrs;
            
        }else{
            JOptionPane.showMessageDialog(null, "Your machine's internal IP couldn't be retreived, program will exit.");
            System.exit(0);
        }
        
        REMOTE_IP = null;
        if(!new File(CONFIG_FILENAME).exists()){
            while(REMOTE_IP == null || REMOTE_IP.equalsIgnoreCase("")) REMOTE_IP = JOptionPane.showInputDialog("Enter the internal IP of the machine you want to connect to.\n" +
                                                            "Must be in the format 192.168.xxx.xxx");
            String machinename = JOptionPane.showInputDialog("Now enter a name for the machine with internal IP " + "\"" + REMOTE_IP + "\"" + 
                                                              "\n" + "You will be able to select this machine from a list the next time you start the program.");
            new ListWriter(CONFIG_FILENAME).writeList(machinename + SEPARATOR + REMOTE_IP, APPEND);
        }else{
            MachineChooser mc = new MachineChooser(CONFIG_FILENAME,SEPARATOR,client);
            mc.setVisible(true);
            mc.setAddressInfo(MACHINE_IP);
            
            while(REMOTE_IP == null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    Logger.getLogger(XOverClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        new XOverClientGUI(REMOTE_IP,MACHINE_IP).setVisible(true);
        new FileServer().execute();
    }
    
    /**
     * Get the internal IP address of the client machine.
     * @return the address
     */
    private static String getLocalAddress(){
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();     
        }catch (UnknownHostException e) {
            e.printStackTrace();
            return "No address found";
        }
        return addr.getHostAddress();
    }    
    
    /**
     * Check the validity of the supplied IP address.
     * @param s the string to check
     * @return true if the supplied address is a valid IPv4 address
     */
    public boolean validAddress(String s){
        if(s != null){
            if(InetAddressValidator.getInstance().isValidInet4Address(s)) return true;
        }
        return false;
    }    
    
    public void setRemoteIP(String ip){
        REMOTE_IP = ip;
    }
    
    public void setMachineIP(String ip){
        MACHINE_IP = ip;
    }
    
}
