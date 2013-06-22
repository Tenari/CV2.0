package cvserver;
/**
 *
 * @author Tenari
 */

public class WorldManager extends Thread{
    
    CustomCommunication communicate;            // Used to access the database.
    OrganismHandler org;
    HomosapienHandler hs;
    LookupConfig lookup;
    CombatHandler combat;
    
    int nextUID     =   0;
    /**
     * Initialize the world and its details.
     */
    public WorldManager (){
        // SQL Database Connection initialization
        communicate = new CustomCommunication();
        
        // Load the configuration manager class.
        lookup = new LookupConfig();
        
        // Load the combat helper methods
        combat = new CombatHandler(communicate);
        
        // Initialize the org handler.
        org = new OrganismHandler(communicate);
        // Initialize the hs handler.
        hs = new HomosapienHandler(communicate);
        
    }
    
    /**
     * The game's main infinite loop.
     *  Update fights.
     *  Act NPCs
     *  Spawn NPCs
     *  Regenerate resources
     *  Regenerate energy
     */
    @Override
    public void run(){
        System.out.println("World Threaded");
        while(true){
            combat.updateAllFights();   // includes timing constraints
        }
    }
//----------------------ORGANISM CREATION COMMANDS------------------------------
    public int addOrganism(String name, int classCode) {
        org.createNewOrganism(name, nextUID, classCode);
        //Increment UID and return.
        nextUID++;
        return nextUID-1;
    }
    
    /**
     * This method adds a Player to the game.
     * Returns the uid of added org.
     * @param name the name of the org
     */
    public int addPlayer(String name, int classCode) {
    	org.createNewOrganism(name, nextUID, classCode);
        hs.createNewHomosapien(name, nextUID, classCode);
            hs.addDefaultItems(nextUID);
        // Code to add player functionality to the uid:
        // player.createNewPlayer(STUFF);
        //Increment UID and return.
        nextUID++;
        return nextUID-1;
    }
    public int addPlayer(String name) {
    	return addPlayer(name, 20);
    }
    
    public int getID(String name){
        return communicate.selectUIDByName(name, lookup.movementTableName);
    }
    
//------------------------END ORGANISM CREATION---------------------------------

//-----------------MOVEMENT COMMANDS--------------------------------------------
    /**
     * Attempts to move the org orgID one tile towards directionCode.
     * Return true if the move happened, false otherwise.
     * @param orgID
     * @param directionCode 1=north, 2=south, 3=east, 4=west
     */
    public boolean moveOrganism(int orgID, int directionCode){
        boolean moved = false;
        // North or south
        if(directionCode == 1 || directionCode == 2){
            int y = org.getY(orgID);
            if (directionCode == 1){    // North
                moved = moveLogic(orgID, org.getX(orgID), y-1, false);
            } else if (directionCode == 2){    // South
                moved = moveLogic(orgID, org.getX(orgID), y+1, false);
            }
        }
        // East or west
        else if(directionCode == 3 || directionCode == 4){
            int x = org.getX(orgID);
            if (directionCode == 3){    // East
                moved = moveLogic(orgID, x-1, org.getY(orgID), true);
            } else if (directionCode == 4){    // West
                moved = moveLogic(orgID, x+1, org.getY(orgID), true);
            }
        }
        return moved;
    }
    
    // Moves an entity. Returns true if a move occurred, false otherwise.
    // Set horizontal true to move the x, and false to move the y.
    public boolean moveLogic(int orgID, int newX, int newY, boolean horizontal){
        String orgWorld = org.getWorld(orgID);         // Cache the world for efficiency.
        
        int nextTileType = getTileType(newX, newY, orgWorld);
        int currentTileType = getTileType(org.getX(orgID), org.getY(orgID), orgWorld);
        
        if (validMove(orgWorld, newX, newY, nextTileType, currentTileType, orgID) ){
            if (lookup.isDoor(nextTileType)) {
                String newWorld = lookup.getWorldFromDoor(nextTileType);
                
                org.setX(lookup.getEntranceX(newWorld, orgID), orgID);
                org.setY(lookup.getEntranceY(newWorld, orgID), orgID);
                org.setWorld(newWorld, orgID);
                return true;
            } else {
                // Decrease energy for moving
                org.setEnergy(org.getEnergy(orgID) - moveCost(currentTileType, orgID), orgID);
                // And change location.
                if (horizontal){
                    org.setX(newX, orgID);
                } else{
                    org.setY(newY, orgID);
                }
                // Update the Endurance skill for the guy who just moved.
                org.setEndurance(org.getEndurance(orgID)+lookup.enduranceGrowthConstant, orgID);
                return true;
            }
        }
        return false;
    }
    
    public int getTileType(int x, int y, String worldname){
        return communicate.selectSingleIntByXAndY("terrainType", worldname, x, y);
    }
    
    // Returns t/f depending on whether the moveTileType is valid, and whether the org can afford the moveCost
    public boolean validMove(String world, int x, int y, int moveTileType, int currentTileType, int orgUID){
        if ((lookup.invalidMoveCost != moveCost(moveTileType, orgUID)) &&       // If he has enough energy to move off current tile
            (org.getEnergy(orgUID)>=moveCost(currentTileType, orgUID)) &&       // and the moveCost of the next tile is not the invalidCode.
            (-1 == communicate.selectUIDByXAndYAndWorld(lookup.movementTableName, x, y, world)) &&// and there is no org at the location.
            (!combat.isFighting(orgUID) &&                                      // and he isn't fighting. You can't move when you're fighting.
            (!org.isLame(orgUID)))){                                            // and he isn't lame from leg HP ==0.
                return true;
        }
        return false;
    }
    
    public int moveCost(int tileType, int orgUID) {
        if (lookup.isOffMap(tileType) || lookup.isWall(tileType)){
            return lookup.invalidMoveCost;      // Return 10 times more than the maximum possible energy as an added safety measure to prevent movement from occuring there.
        } else if(lookup.isRoad(tileType)) {
            return moveCostLogic(lookup.baseRoadMoveCost, orgUID);
        } else if (lookup.isGround(tileType)) {
            return moveCostLogic(lookup.baseGroundMoveCost, orgUID);
        }
        
        return 5;   // default
    }
    
    private int moveCostLogic(int baseTileMoveCost, int orgUID){
        double endurance = org.getEndurance(orgUID);
        return lookup.getWeightModToCost(hs.getWeight(orgUID), endurance) + 
            (int)Math.round(baseTileMoveCost * 
                    (lookup.moveNormalizationConstant /
                        (lookup.moveNormalizationConstant + 
                           endurance )));
    }
    
//-----------------------END MOVEMENT STUFF-------------------------------------

//------------------------MESSAGE METHODS---------------------------------------
    
    /**
     * For given orgID, returns the header-less message indicating what tiles to draw.
     * Contains no information about other organisms.
     * Form: "[tileCode for 0,0] [tileCode for 1,0] [tileCode for 2,0] ...(for each tile...)"
     * Left to right, top to bottom.
     * @param orgID
     * @return 
     */
    public String getPlayerMapView(int orgID){
        String dataAsString = "";
        String orgWorld = org.getWorld(orgID);
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = org.getX(orgID) - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(orgWorld, true))){   // If this tile is NOT off the map...
                    int tempY = org.getY(orgID) - lookup.playerViewRow + i;// similar to above
                    if (!(tempY <= 0 || tempY >=lookup.getWorldDimension(orgWorld, false))){   // If this tile is NOT off the map...
                        dataAsString = dataAsString + getTileType(tempX, tempY, orgWorld) + " ";
                    }
                    else {  // The case for an off-the-map tile.
                        dataAsString = dataAsString + "0 "; // 0 represents "off the map"
                    }
                }
                else {  // The case for an off-the-map tile.
                    dataAsString = dataAsString + "0 "; // 0 represents "off the map"
                }
            }
        }
        return dataAsString;
    }
    
    /**
     * Returns the header-less message containing org painting information for the given orgID.
     * Form: "o [for each org found: [classCode] [directionCode] [relativeX] [relativeY]]"
     * ex: "o 101 2 4 4 1000 2 6 4"
     * @param orgID
     * @return 
     */
    public String getPlayerOrganismsView(int orgID){
        String dataAsString = "";                       // The string to return.
        // Some efficiency data caching
        String orgWorld = org.getWorld(orgID);
        int orgX = org.getX(orgID);
        int orgY = org.getY(orgID);
        
        // The logic.
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = orgX - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(orgWorld, true))){         // If this tile is NOT off the map...
                    int tempY = orgY - lookup.playerViewRow + i;// similar to above
                    if (!(tempY <= 0 || tempY >=lookup.getWorldDimension(orgWorld, false))){     // If this tile is NOT off the map...
                        int organismUID = communicate.selectUIDByXAndYAndWorld(lookup.movementTableName, tempX, tempY, orgWorld);   // Makes sure to only select orgs who are on same world
                        if(organismUID != -1 ){                                                  // If the select worked 
                            dataAsString = dataAsString + org.getDirection(organismUID)     // Append the message info.
                                    + " " + org.getClass(organismUID) 
                                    + " " + j + " " + i + " ";
                        }
                    }
                }
            }
        }
        return dataAsString;
    }
    
    
//-----------------------END MESSAGE STUFF--------------------------------------
}
