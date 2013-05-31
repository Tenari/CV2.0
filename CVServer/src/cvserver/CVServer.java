/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tenari
 */
public class CVServer {

    Map<Integer,ClientHandler> clientsMap;      // Records connected Clients and their IDs
    int clientCount;                            // Number of currently connected Clients
    ServerSocket server;                        // Connection to read/talk on some port
    WorldManager gameMaster;                    // Pointer to thread which contains game world state.
    
    /**
     * The constructor
     * @param port 
     */
    public CVServer(int port) {
        clientsMap = new HashMap<>();
        clientCount = 0;
        gameMaster = new WorldManager();           // Construct the instance of the worldManager, which updates and calculates everything.
        gameMaster.start();                        // Start it...
        try {
            System.out.print("Starting server on port " + port + "...");
            server = new ServerSocket(port);
            System.out.println("Done!");
            System.out.println("Now handling incoming communications");
            handleIncoming();
        }
        catch(IOException e) {
            System.out.println("ERROR!\nCould not start server. Shutting down...");
            kill();
        }
    }
    
    // Handle incoming communications
    private void handleIncoming()
    {
        try {
            while (true) {
                Socket socket = server.accept();
                clientCount++;
                ClientHandler client = new ClientHandler(this, socket, clientCount, gameMaster);
                clientsMap.put(clientCount, client);
                System.out.println("MplayClient " + clientCount + " joined");
                client.start();
            }
        }
        catch(Exception e) {
            System.out.println("ERROR!\nFailed to handle incoming communication. Shutting down...");
            kill();
        }
    }

    /**
     * Shutdown the server.
     */
    private void kill() {
        try {
            for (Object i : clientsMap.values()) {
                ((ClientHandler)i).kill();
            }
        }
        catch(Exception e) {
            System.out.println("WARNING! Server could not be killed reliably");
        }
    }
    
    /**
     * Read in the port and either fail, or construct the server.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Multiplayer Server\n");
        
        // Create the database. Comment this out if it's already made.
        CreateCustomDatabase c = new CreateCustomDatabase();
        c.addTables();      // Add the needed tables to the DB
        c.addDataToTables();// Add the data to the tabels just created.
        
        if (args.length == 1) {
            CVServer newServer = new CVServer(Integer.parseInt(args[0]));
        }
        else {
            System.out.println("Usage: java CVServer port");
        }
    }
    
    // Remove a client
    public void removeMplayClient(int cid) {
        System.out.println("Client " + ((ClientHandler)clientsMap.get(cid)).getUsername() + "(" + cid + ") removed");
        clientsMap.remove(cid);
    }
    
    
    public void updateMoveScreensInAllClients(){
        for (Object i : clientsMap.values()) {
            ((ClientHandler)i).updateMoveScreenOrgs();
        }
    }
    
    public void updateMoveScreensForWorld(String worldname){
        for (Object i : clientsMap.values()) {
            if ( gameMaster.organism.getWorld(gameMaster.getID(((ClientHandler)i).getUsername())).equals(worldname) ){
                ((ClientHandler)i).updateMoveScreenOrgs();
            }
        }
    }
}
