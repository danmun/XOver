/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.sugoiapps.xolauncher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniel Munkacsi
 */


public class Launcher implements Choice{

    
    /**
     * Planned additional features:
     * - updater
     * 
     * 
     * To set working directory for all the jars, do this:
     * - get location of XOLauncher.jar, call it jarLoc
     * - use ProcessBuilder to start other jars and set their working directory as such: 
     *      - ProcessBuilder pb = new ProcessBuilder(javaHome + pathToExe, "-jar", "XOverClient.jar").directory(new File(jarLoc))
     *      creates a new system process with working directory specified by .directory(new File(jarLoc)), this will be the directory that Launcher.jar is in
     *      - don't forget to find out the system's separator character by doing String s = File.separator; (which could return "\" for example)
     * NOTE: Linux would use "java" and not "java.exe", more generalised methodology needed
     */
    
    private static final String SEP = File.separator;
    private static final String CLIENT = "XOverClient.jar";
    private static final String SERVER = "XOverServer.jar";
    private static final String JARLOC = getJarLocation();
    private static final String pathToExe = SEP + "bin" + SEP + "javaw.exe";
    private static final String javaHome = System.getProperty("java.home");
    
    /**
     * @param args the command line arguments
     */
    private static int choice = -1;
    
    public static void main(String[] args) {
        
        new ProgramChooser(new Launcher()).setVisible(true);
        
        while(choice == -1){
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(choice == CLIENT_CHOICE){
            start(CLIENT);
        }else if(choice == SERVER_CHOICE){
            start(SERVER);
        }
        System.exit(0);
    }
    
    /**
     * Find the location of the Lancher file.
     * @return  the path where the file is
     */    
    private static String getJarLocation(){
        CodeSource codeSource = Launcher.class.getProtectionDomain().getCodeSource();
        File jarFile = null;
        try {
            jarFile = new File(codeSource.getLocation().toURI().getPath());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return jarFile.getParentFile().getPath();
    }
    
    /**
     * Start the selected program.
     * @param program the identifier of the program, see Choice interface for values
     */
    private static void start(String program){
        ProcessBuilder pb = new ProcessBuilder(javaHome + pathToExe, "-jar", program).directory(new File(JARLOC));
        try {
            Process p = pb.start();
        } catch (IOException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setChoice(int c){
        choice = c;
    }
    
}
