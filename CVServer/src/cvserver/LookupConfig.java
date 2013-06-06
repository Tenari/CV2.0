package cvserver;

import java.util.HashMap;

/**
 *
 * @author Tenari
 */
class LookupConfig {
    // Map Codes Configuration
    int offMapTileCode      =   0;
    int[] wallTileCodes     =   {1,2};
    int[] roadTileCodes     =   {3};
    int[] groundTileCodes   =   {4};        // Essentially grass or dirt. => passable ground
    int[] doorTileCodes     =   {5,10,11};
    
    // Map base move cost config
    int moveNormalizationConstant   =   24;
    int invalidMoveCost     =   100000;
    int baseGroundMoveCost  =   20;
    int baseRoadMoveCost    =   10;
    
    // Skill Growth constants
    double enduranceGrowthConstant  =   0.002;
    
    // The world dimension constants
    int smallCityYLength    =   12;
    int smallCityXLength    =   22;
    int barYLength          =   8;
    int barXLength          =   8;
    
    // Worldnames
    final String smallcity  =   "smallcity";
    final String bar10      =   "bar 10";
    final String bar11      =   "bar 11";
    
    
    // Player view constants
    int playerViewXSize     =   11;
    int playerViewYSize     =   11;
    int playerViewRow       =   5;
    int playerViewCol       =   5;
    
    // Table names
    String movementTableName        =   "organismsmovementinfo";
    String combatTableName          =   "combatstats";
    String statsTableName           =   "detailedstats";
    String homosapienTableName      =   "homosapiendata";
    String hsitemsTableName         =   "hsitems";
    String hsresourcesTableName     =   "hsresources";
    
    // Math/formulas constants and factors.
    final int enduranceWeightFactor   =   2;
    final int weightReductionFactor   =   5;
    
    HashMap<Integer, String> doorToWorldMap;
    
    
    public LookupConfig(){
        doorToWorldMap = new HashMap();
        mapDoorsToWorld();
    }
    
//-----------------------------TileType Lookup Methods--------------------------
    public boolean isOffMap(int tileType){
        if (offMapTileCode == tileType){
            return true;
        }
        return false;
    }
    public boolean isWall(int tileType){
        for (int i : wallTileCodes){
            if (i == tileType){
                return true;
            }
        }
        return false;
    }
    public boolean isRoad(int tileType){
        for (int i : roadTileCodes){
            if (i == tileType){
                return true;
            }
        }
        return false;
    }
    public boolean isGround(int tileType){
        for (int i : groundTileCodes){
            if (i == tileType){
                return true;
            }
        }
        return false;
    }
    public boolean isDoor(int tileType){
        for (int i : doorTileCodes){
            if (i == tileType){
                return true;
            }
        }
        return false;
    }
    
    public String getWorldFromDoor(int doorNumber){
        
        return doorToWorldMap.get(new Integer(doorNumber));
    }
//---------------------------End TileType Lookup Methods------------------------
    
    // Add the return value to the already calculated moveCost to get true moveCost
    public int getWeightModToCost(int weightCarried, double endurance){
        
        return (int)( (weightCarried/weightReductionFactor) / (1 + (endurance/enduranceWeightFactor)) );
    }
    
    /** Set x true for X dimension of worldName. 
     *   Returns int length of given dimension for world.*/
    public int getWorldDimension(String worldName, boolean x){
        if (x){
            switch(worldName){
                case smallcity:
                    return smallCityXLength;
                case bar10:
                case bar11:
                    return barXLength;
                default:
                    return smallCityXLength;
            }
        } else {
            switch(worldName){
                case smallcity:
                    return smallCityYLength;
                case bar10:
                case bar11:
                    return barYLength;
                default:
                    return smallCityYLength;
            }
        }
        
    }
    
    private void mapDoorsToWorld(){
        doorToWorldMap.put(new Integer(5),smallcity);
        doorToWorldMap.put(new Integer(10),bar10);
        doorToWorldMap.put(new Integer(11),bar11);
    }

    public int getEntranceX(String worldName, int orgID) {
        switch(worldName){
            case bar10:
            case bar11:
                return 3;
            case smallcity:     // WRONG!!! ONLY WORKS FOR ONE BAR
            default:
                return 2;
        }
    }

    public int getEntranceY(String worldName, int orgID) {
        switch(worldName){
            case bar10:
            case bar11:
                return 6;
            case smallcity:     // WRONG!!! ONLY WORKS FOR ONE BAR
            default:
                return 5;
        }
    }
}
