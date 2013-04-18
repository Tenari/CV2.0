/**
 * The server itself.
 * 
 * @author Daniel Zapata | djz24
 */

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private Map<Integer,ClientHandler> clientsMap;      // Records connected Clients and their IDs
    private int clientCount;                            // Number of currently connected Clients
    private ServerSocket server;                        // Connection to read/talk on some port
    private World staging;                              // Pointer to thread which contains game world state.
    
    /**
     * The constructor
     * @param port 
     */
    public Server(int port) {
        clientsMap = new HashMap<>();
        clientCount = 0;
        staging = new World(this);          //the instance of the game world where everything happens
        staging.start();
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
                ClientHandler client = new ClientHandler(this, socket, clientCount, staging);
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
            Server newServer = new Server(Integer.parseInt(args[0]));
        }
        else {
            System.out.println("Usage: java MplayServer port");
        }
    }
    
    // Remove a client
    public void removeMplayClient(int cid) {
        System.out.println("Client " + ((ClientHandler)clientsMap.get(cid)).getUsername() + "(" + cid + ") removed");
        clientsMap.remove(cid);
    }
    
    public void updateMoveScreensInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateMoveScreen();
            }
    }
    
    public void updateTradeInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateTrade();
            }
    }
    
    public void updateResourcesInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateResou();
            }
    }
    
    public void updateInventoryInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateInven();
            }
    }
    
    public void updateFightInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateFight();
            }
    }
    public void updateCharacterStatsInAllClients(){
        for (Object i : clientsMap.values()) {
                ((ClientHandler)i).updateCharacter();
            }
    }
}