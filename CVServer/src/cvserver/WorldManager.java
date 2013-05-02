/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvserver;

/**
 *
 * @author Tenari
 */
public class WorldManager extends Thread{
    
    CustomCommunication communicate;            // Used to access the database.
    OrganismHandler organism;
    
    /**
     * Initialize the world and its details.
     */
    public WorldManager (){
        // SQL Database Connection initialization
        communicate = new CustomCommunication();
        
        // Initialize the organism handler.
        organism = new OrganismHandler(communicate);
    }
    
    @Override
    public void run(){
        System.out.println("World Threaded");
        
        // The game's main infinite loop.
        while(true){
            // Update fights.
            // Act NPCs
            // Spawn NPCs
            // Regenerate resources
            // Regenerate energy
        }
    }
    
    /**
     * Attempts to move the organism orgID one tile towards directionCode.
     * Return true if the move happened, false otherwise.
     * @param orgID
     * @param directionCode - 1=north, 2=south, 3=east, 4=west
     */
    public boolean moveOrganism(int orgID, int directionCode){
        return false;
    }
    
}
