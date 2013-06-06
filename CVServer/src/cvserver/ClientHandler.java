/**
 * The thread spawned by the server to handle communications with a client.
 * 
 * @author Daniel Zapata | djz24
 */
package cvserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


class ClientHandler extends Thread{
    private int sessid;
    private String username;
    private CVServer server;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private WorldManager gameMaster;
    private int uid     =   -1;
    
    final String northKey    =    "W";
    final String southKey    =    "S";
    final String eastKey     =    "A";
    final String westKey     =    "D";
    final String nameKey     =    "N";
    final String moveKey     =    "m";
    
    int northCode   =   1;
    int southCode   =   2;
    int eastCode    =   3;
    int westCode    =   4;
    
    final String mapMsgHeader   =   "v ";   //v => view => map-view
    final String orgMsgHeader   =   "o ";   //o => organisms
    final String skillMsgHeader =   "s ";   //s => skills
    final String healthMsgHeader=   "hp ";
    final String miscMsgHeader  =   "r ";   //r => random => misc
    
    // Constructor
    public ClientHandler(CVServer server, Socket socket, int sessid, WorldManager gm) {
        // Setup global Fields
        this.server = server;           //"this" used because variable names are same
        this.socket = socket;
        this.sessid = sessid;
        gameMaster=gm;                  //Gives this access to the gameMaster
        
        // Try to set upt the communication line between the server and the client.
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("New client created");
        }
        catch(IOException e) {
         System.out.println("ERROR!\nCould not create client");
         kill();
        }
    }
 
    public void run() {
        System.out.println("MplayClient " + sessid + " threaded. Listening to incoming");
        handleIncoming();
    }

    // Handle communications from client
    private void handleIncoming()
    {
        try {
            String receivedMessage;
            
            // This while fails/exits if the communication line fails/does not exist.
            while ((receivedMessage = in.readLine()) != null) {
                interpretData(receivedMessage);    // waits until in.readLine returns something... 
                                                   // ...and sends that something to interpretData
            }
        }   
        catch(IOException e) {
           System.out.println("ERROR!\nCould not read data from client");
           kill();
        }
    }

//---------------ACCESSOR METHODS-----------------------------------------------
    // Kill the client connection/object
    public void kill() {
        server.removeMplayClient(sessid);
    }
    // Return client ID
    public int getID() {
        return this.sessid;
    }
    // Change client ID
    public void setID(int sessid) {
        this.sessid = sessid;
    }
    // Return username
    public String getUsername() {
        return this.username;
    }
    // Change username
    public void setUsername(String username) {
        this.username = username;
        uid = gameMaster.getID(this.username);
    }
//-------------------END ACCESSOR METHODS---------------------------------------

    // Decide what to do from input
    private void interpretData(String msg) {
        Scanner scan = new Scanner(msg);
        String firstWord =scan.next();
       switch (firstWord){
        case nameKey:                   // Client is alerting server to the name of the character connecting.
            String name = scan.next();  // Msg of form: "N [name]" so scan.next is username.
            if(gameMaster.getID(name) == -1){
                gameMaster.addPlayer(name);
            }
            setUsername(name);   
            break;
        case moveKey:
            movePlayer(scan.nextInt());     // due to "m [directionCode]" form, scan.nextInt() is the direction code to pass.
            break;
       }
    }

    // Send data to the client
    public void sendData(String msg) {
        out.println(msg);
        
        // Debugging line
            System.out.println(msg);
        // Debugging line
            
        if (out.checkError()) {
         System.out.println("ERROR!\nCould not deliver message to client");
         kill();
        }
    }
    
    // Tells the gameMaster to move the player in the directionCode
    void movePlayer(int directionCode){
        boolean moved = false;
        moved = gameMaster.moveOrganism(uid, directionCode);
        // If a move occured, we need to inform the other players.
        if (moved){
            updateWholeMoveScreen();
            server.updateMoveScreensForWorld(gameMaster.organism.getWorld(uid));
        }
    }
    
    public void updateWholeMoveScreen() {
        // the use of uid as passed param below is not perfectly safe. uid might be -1. if this is a problem, fix later.
        String updateInfo = mapMsgHeader + gameMaster.getPlayerMapView(uid);
        sendData(updateInfo);
        updateInfo = orgMsgHeader + gameMaster.getPlayerOrganismsView(uid);
        sendData(updateInfo);
    }
    
    public void updateMoveScreenOrgs() {
        // the use of uid as passed param below is not perfectly safe. uid might be -1. if this is a problem, fix later.
        String updateInfo = orgMsgHeader + gameMaster.getPlayerOrganismsView(uid);
        sendData(updateInfo);
    }
    
    public void updateAllStats() {
        updateHealthStats();
        updateSkillStats();
        updateMiscStats();
        // Other stats go here
    }
    // Head, Arms, Torso, Legs
    public void updateHealthStats() {
        String updateInfo = healthMsgHeader 
                + gameMaster.organism.getHead(uid) + " "
                + gameMaster.organism.getArms(uid) + " "
                + gameMaster.organism.getTorso(uid) + " "
                + gameMaster.organism.getLegs(uid);
        sendData(updateInfo);
    }
    /**
     * Includes:
     *  attStr, attSkill
     *  defStr, defSkill
     *  attStrBase, attSkillBase
     *  defStrBase, defSkillBase
     *  endurance
     */
    public void updateSkillStats() {
        String updateInfo = skillMsgHeader 
                + gameMaster.organism.getAttStr(uid) + " "
                + gameMaster.organism.getAttSkill(uid) + " "
                + gameMaster.organism.getDefStr(uid) + " "
                + gameMaster.organism.getDefSkill(uid) + " "
                + gameMaster.organism.getAttStrBase(uid) + " "
                + gameMaster.organism.getAttSkillBase(uid) + " "
                + gameMaster.organism.getDefStrBase(uid) + " "
                + gameMaster.organism.getDefSkillBase(uid) + " "
                + gameMaster.organism.getEndurance(uid);
        sendData(updateInfo);
    }
    /**
     * Includes:
     *  money
     *  energy
     */
    public void updateMiscStats() {
        String updateInfo = miscMsgHeader 
                + gameMaster.organism.getMoney(uid) + " "
                + gameMaster.organism.getEnergy(uid);
        sendData(updateInfo);
    }
}