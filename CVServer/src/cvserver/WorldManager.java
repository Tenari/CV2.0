package cvserver;
/**
 *
 * @author Tenari
 */

import java.util.ArrayList;

public class WorldManager extends Thread{
    
    CustomCommunication communicate;            // Used to access the database.
    OrganismHandler organism;
    HomosapienHandler homosapien;
    LookupConfig lookup;
    
    ArrayList<NumberPair> fights = new ArrayList<>();
    
    int nextUID     =   0;
    /**
     * Initialize the world and its details.
     */
    public WorldManager (){
        // SQL Database Connection initialization
        communicate = new CustomCommunication();
        
        // Load the configuration manager class.
        lookup = new LookupConfig();
        
        // Initialize the organism handler.
        organism = new OrganismHandler(communicate);
        // Initialize the homosapien handler.
        homosapien = new HomosapienHandler(communicate);
        
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
        
        // 
        while(true){
            
        }
    }
//----------------------ORGANISM CREATION COMMANDS------------------------------
    /**
     * This method adds a Player to the game.
     * Returns the uid of added organism.
     * @param name the name of the organism
     */
    public int addPlayer(String name) {
    	organism.createNewOrganism(name, nextUID, 20);      // 20 is temporary hard-coded class code. need to programmatically determine this.
        homosapien.createNewHomosapien(name, nextUID, 20); // Ditto above
            homosapien.addDefaultItems(nextUID);
        // Code to add player functionality to the uid:
        // player.createNewPlayer(STUFF);
        //Increment UID and return.
        nextUID++;
        return nextUID-1;
    }
    
    public int getID(String name){
        return communicate.selectUIDByName(name, lookup.movementTableName);
    }
    
//------------------------END ORGANISM CREATION---------------------------------

//-----------------MOVEMENT COMMANDS--------------------------------------------
    /**
     * Attempts to move the organism orgID one tile towards directionCode.
     * Return true if the move happened, false otherwise.
     * @param orgID
     * @param directionCode 1=north, 2=south, 3=east, 4=west
     */
    public boolean moveOrganism(int orgID, int directionCode){
        boolean moved = false;
        // North or south
        if(directionCode == 1 || directionCode == 2){
            int y = organism.getY(orgID);
            if (directionCode == 1){    // North
                moved = moveLogic(orgID, organism.getX(orgID), y-1, false);
            } else if (directionCode == 2){    // South
                moved = moveLogic(orgID, organism.getX(orgID), y+1, false);
            }
        }
        // East or west
        else if(directionCode == 3 || directionCode == 4){
            int x = organism.getX(orgID);
            if (directionCode == 3){    // East
                moved = moveLogic(orgID, x-1, organism.getY(orgID), true);
            } else if (directionCode == 4){    // West
                moved = moveLogic(orgID, x+1, organism.getY(orgID), true);
            }
        }
        return moved;
    }
    
    // Moves an entity. Returns true if a move occurred, false otherwise.
    // Set horizontal true to move the x, and false to move the y.
    public boolean moveLogic(int orgID, int newX, int newY, boolean horizontal){
        String orgWorld = organism.getWorld(orgID);         // Cache the world for efficiency.
        
        int nextTileType = getTileType(newX, newY, orgWorld);
        int currentTileType = getTileType(organism.getX(orgID), organism.getY(orgID), orgWorld);
        
        if (validMove(orgWorld, newX, newY, nextTileType, currentTileType, orgID)){
            // Decrease energy for moving
            organism.setEnergy(organism.getEnergy(orgID) - moveCost(currentTileType, orgID), orgID);
            // And change location.
            if (horizontal){
                organism.setX(newX, orgID);
            } else{
                organism.setY(newY, orgID);
            }
            // Update the Endurance skill for the guy who just moved.
            organism.setEndurance(organism.getEndurance(orgID)+lookup.enduranceGrowthConstant, orgID);
            return true;
        }
        return false;
    }
    
    public int getTileType(int x, int y, String worldname){
        return communicate.selectSingleIntByXAndY("terrainType", worldname, x, y);
    }
    
    // Returns t/f depending on whether the moveTileType is valid, and whether the organism can afford the moveCost
    public boolean validMove(String world, int x, int y, int moveTileType, int currentTileType, int orgUID){
        if ((lookup.invalidMoveCost != moveCost(moveTileType, orgUID)) &&       // If he has enough energy to move off current tile
            (organism.getEnergy(orgUID)>=moveCost(currentTileType, orgUID)) &&  // and the moveCost of the next tile is not the invalidCode.
            (-1 == communicate.selectUIDByXAndYAndWorld(lookup.movementTableName, x, y, world))){ // and there is no organism at the location.
            // CHECK FOR BLOCKING ORGANISM && COMBAT ISSUES
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
        
        return 5;
    }
    
    private int moveCostLogic(int baseTileMoveCost, int orgUID){
        double endurance = organism.getEndurance(orgUID);
        return lookup.getWeightModToCost(homosapien.getWeight(orgUID), endurance) + 
            (int)Math.round(baseTileMoveCost * 
                    (lookup.moveNormalizationConstant /
                        (lookup.moveNormalizationConstant + 
                           endurance )));
    }
    
//-----------------------END MOVEMENT STUFF-------------------------------------
    
//-------------------------------COMBAT HANDLERS--------------------------------
    public void startFight(int agressorUID, int opponentRelativeX, int opponentRelativeY){
        int opponentUID = communicate.selectSingleIntByXAndY("uid", lookup.movementTableName, opponentRelativeX, opponentRelativeY);
        fights.add(new NumberPair(agressorUID,opponentUID));
    }
    
    public void endFight(int attackerUID, int defenderUID){
        fights.remove(new NumberPair(attackerUID,defenderUID));
    }
    
    public void fightOneRound(int fightIndex){
        fightOneRound(fights.get(fightIndex).getNumOne(), fights.get(fightIndex).getNumTwo());
    }
    public void fightOneRound(int attackerUID, int defenderUID){
        // Set the real Skills of both combatants, for accuracy of upcoming calculations
        organism.setRealSkills(attackerUID);
        organism.setRealSkills(defenderUID);
        
        // Determine where the attacker hit (or if he missed)
        String hitSpot = getAttackSpot(attackerUID, organism.getDefSkill(defenderUID));
    }
    public String getAttackSpot(int attackerUID, double defenderDefSkill){
        return "miss";
    }
//--------------------------END COMBAT STUFF------------------------------------

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
        String orgWorld = organism.getWorld(orgID);
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = organism.getX(orgID) - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(orgWorld, true))){   // If this tile is NOT off the map...
                    int tempY = organism.getY(orgID) - lookup.playerViewRow + i;// similar to above
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
     * Returns the header-less message containing organism painting information for the given orgID.
     * Form: "o [for each organism found: [classCode] [directionCode] [relativeX] [relativeY]]"
     * ex: "o 101 2 4 4 1000 2 6 4"
     * @param orgID
     * @return 
     */
    public String getPlayerOrganismsView(int orgID){
        String dataAsString = "";                       // The string to return.
        // Some efficiency data caching
        String orgWorld = organism.getWorld(orgID);
        int orgX = organism.getX(orgID);
        int orgY = organism.getY(orgID);
        
        // The logic.
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = orgX - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(orgWorld, true))){         // If this tile is NOT off the map...
                    int tempY = orgY - lookup.playerViewRow + i;// similar to above
                    if (!(tempY <= 0 || tempY >=lookup.getWorldDimension(orgWorld, false))){     // If this tile is NOT off the map...
                        int organismUID = communicate.selectUIDByXAndYAndWorld(lookup.movementTableName, tempX, tempY, orgWorld);   // Makes sure to only select orgs who are on same world
                        if(organismUID != -1 ){                                                  // If the select worked 
                            dataAsString = dataAsString + organism.getDirection(organismUID)     // Append the message info.
                                    + " " + organism.getClass(organismUID) 
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
