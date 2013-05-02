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
    private int uid;
    
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
    }

    // Decide what to do from input
    private void interpretData(String msg) {
        
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
}