package cvserver;

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
    int[] doorTileCodes     =   {10,11};
    
    // Map base move cost config
    int moveNormalizationConstant=24;
    int invalidMoveCost     =   100000;
    int baseGroundMoveCost  =   20;
    int baseRoadMoveCost    =   10;
    
    // Skill Growth constants
    double enduranceGrowthConstant  =   0.002;
    
    
    public LookupConfig(){
        
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
//---------------------------End TileType Lookup Methods------------------------
}
