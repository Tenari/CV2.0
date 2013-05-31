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
        
        // Load the configuration manager class.
        lookup = new LookupConfig();
        
        // Initialize the organism handler.
        organism = new OrganismHandler(communicate);
        // Initialize the homosapien handler.
        homosapien = new HomosapienHandler(communicate);
        
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
        return communicate.selectUIDByName(name, movementTableName);
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
        int nextTileType = getTileType(newX, newY, organism.getWorld(orgID));
        int currentTileType = getTileType(organism.getX(orgID), organism.getY(orgID), organism.getWorld(orgID));
        if (validMove(nextTileType, currentTileType, orgID)){
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
    public boolean validMove(int moveTileType, int currentTileType, int orgUID){
        // If guy has enough energy to move off current tile, and the moveCost of the next tile is not the invalidCode.
        if ((lookup.invalidMoveCost != moveCost(moveTileType, orgUID)) &&
            (organism.getEnergy(orgUID)>=moveCost(currentTileType, orgUID))){
            // CHECK FOR BLOCKING ORGANISM && COMBAT ISSUES
            return true;
        }
        return false;
    }
    
    // NEED TO ADD ENDURANCE APPLICATION LOGIC HERE. CURRENTLY IS FIXED MOVECOST
    public int moveCost(int tileType, int orgUID) {
        if (lookup.isOffMap(tileType) || lookup.isWall(tileType)){
            return lookup.invalidMoveCost;      // Return 10 times more than the maximum possible energy as an added safety measure to prevent movement from occuring there.
        } else if(lookup.isRoad(tileType)) {
            return (int)Math.round(lookup.baseRoadMoveCost*(lookup.moveNormalizationConstant/(lookup.moveNormalizationConstant+organism.getEndurance(orgUID))));
        } else if (lookup.isGround(tileType)) {
            return (int)Math.round(lookup.baseGroundMoveCost*(lookup.moveNormalizationConstant/(lookup.moveNormalizationConstant+organism.getEndurance(orgUID))));
        }
        
        return 5;
    }
    
//-----------------------END MOVEMENT STUFF-------------------------------------
    
//-------------------------------COMBAT HANDLERS--------------------------------
    public void startFight(int agressorUID, int opponentRelativeX, int opponentRelativeY){
        int opponentUID = communicate.selectSingleIntByXAndY("uid", movementTableName, opponentRelativeX, opponentRelativeY);
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
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = organism.getX(orgID) - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(organism.getWorld(orgID), true))){   // If this tile is NOT off the map...
                    int tempY = organism.getY(orgID) - lookup.playerViewRow + i;// similar to above
                    if (!(tempY <= 0 || tempY >=lookup.getWorldDimension(organism.getWorld(orgID), false))){   // If this tile is NOT off the map...
                        dataAsString = dataAsString + getTileType(tempX, tempY, organism.getWorld(orgID)) + " ";
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
        String dataAsString = "";
        for(int i=0; i<lookup.playerViewYSize; i++){    // The y length
            for(int j=0; j<lookup.playerViewXSize; j++){// The x length
                int tempX = organism.getX(orgID) - lookup.playerViewCol + j;      
                if (!(tempX <= 0 || tempX >=lookup.getWorldDimension(organism.getWorld(orgID), true))){         // If this tile is NOT off the map...
                    int tempY = organism.getY(orgID) - lookup.playerViewRow + i;// similar to above
                    if (!(tempY <= 0 || tempY >=lookup.getWorldDimension(organism.getWorld(orgID), false))){     // If this tile is NOT off the map...
                        int organismClass = communicate.selectSingleIntByXAndY("class", "organismsmovementinfo", tempX, tempY);
                        if(!(organismClass == -1)){   // If the organismClass did NOT fail (did succeed) to get the clasCode 
                            dataAsString = dataAsString + organism.getDirection(orgID)
                                    + " " + organismClass 
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
