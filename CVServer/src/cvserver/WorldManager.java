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
    
    
    String movementTableName = "organismsmovementinfo";
    String combatTableName = "combatstats";
    String statsTableName = "detailedstats";
    
    int nextUID     =   0;
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
//----------------------ORGANISM CREATION COMMANDS------------------------------
    /**
     * This method adds a Player to the game.
     * Returns the uid of added organism.
     * @param name the name of the organism
     */
    public int addPlayer(String name) {
    	organism.createNewOrganism(name, nextUID, 20);  // 20 is temporary hard-coded class code. need to programmatically determine this.
        // Code to add player functionality to the uid:
        // player.createNewPlayer(STUFF);
        //Increment UID and return.
        nextUID++;
        return nextUID-1;
    }
    
    public int getID(String name){
        return communicate.selectUIDByName(name, movementTableName);
    }
    
//------------------------END ORGANISM CREATION---------------------------------
    
//-----------------MOVEMENT COMMANDS--------------------------------------------
    /**
     * Attempts to move the organism orgID one tile towards directionCode.
     * Return true if the move happened, false otherwise.
     * @param orgID
     * @param directionCode - 1=north, 2=south, 3=east, 4=west
     */
    public boolean moveOrganism(int orgID, int directionCode){
        boolean moved = false;
        int nextTileType;
        // North or south
        if(directionCode == 1 || directionCode == 2){
            int y = organism.getY(orgID);
            if (directionCode == 1){    // North
                nextTileType = getTileType(organism.getX(orgID), y-1, organism.getWorld(orgID));
                if (validMove(nextTileType, orgID)){
                    // Decrease energy for moving
                    organism.setEnergy(organism.getEnergy(orgID)-moveCost(nextTileType, orgID), orgID);
                    // And change location.
                    organism.setY(y-1, orgID);
                    moved = true;
                }
            }
            if (directionCode == 2){    // South
                nextTileType = getTileType(organism.getX(orgID), y+1, organism.getWorld(orgID));
                if (validMove(nextTileType, orgID)){
                    // Decrease energy for moving
                    organism.setEnergy(organism.getEnergy(orgID)-moveCost(nextTileType, orgID), orgID);
                    // And change location.
                    organism.setY(y+1, orgID);
                    
                    moved = true;
                }
            }
        }
        // East or west
        else if(directionCode == 3 || directionCode == 4){
            int x = organism.getX(orgID);
            if (directionCode == 3){    // East
                nextTileType = getTileType(x-1, organism.getY(orgID), organism.getWorld(orgID));
                if (validMove(nextTileType, orgID)){
                    // Decrease energy for moving
                    organism.setEnergy(organism.getEnergy(orgID)-moveCost(nextTileType, orgID), orgID);
                    // And change location.
                    organism.setX(x-1, orgID);
                    moved = true;
                }
            }
            if (directionCode == 4){    // West
                nextTileType = getTileType(x+1, organism.getY(orgID), organism.getWorld(orgID));
                if (validMove(nextTileType, orgID)){
                    // Decrease energy for moving
                    organism.setEnergy(organism.getEnergy(orgID)-moveCost(nextTileType, orgID), orgID);
                    // And change location.
                    organism.setX(x+1, orgID);
                    
                    moved = true;
                }
            }
        }
        return moved;
    }
    
    public int getTileType(int x, int y, String worldname){
        return communicate.selectSingleIntByXAndY("terrainType", worldname, x, y);
    }
    
    public boolean validMove(int moveTileType, int orgUID){
        if (organism.getEnergy(orgUID)>=moveCost(moveTileType, orgUID)){
            if (moveTileType > 2 ){ // >2 because 0,1,2 are off-map and walls. Should also check for 'occupied' status of tile.
                return true;
            }
        }
        return false;
    }
    
    /**
     * NEED TO ADD ENDURANCE APPLICATION LOGIC HERE. CURRENTLY IS FIXED MOVECOST
     * @param tileType
     * @param orgUID
     * @return 
     */
    public int moveCost(int tileType, int orgUID) {
        return 5;
    }
    
//-----------------------END MOVEMENT STUFF-------------------------------------

//------------------------MESSAGE METHODS---------------------------------------
    public String getPlayerMapView(int orgID){
        String dataAsString = "";
        //TODO: LOGIC GOES HERE
        return dataAsString;
    }
//-----------------------END MESSAGE STUFF--------------------------------------
}
