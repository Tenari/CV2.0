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
    
    // Combat Codes
    final int missCode          =   0;      // Doubles as stun-code for attackTargets.
    final int headCode          =   1;
    final int armsCode          =   2;
    final int torsoCode         =   3;
    final int legsCode          =   4;
    
    final int blockCode         =   1;
    final int lightAttackCode   =   2;
    final int medAttackCode     =   3;
    final int heavyAttackCode   =   4;
    
    final int maxAttackSpeed    =   9001;   // get it?
    
    // Weapon Class Codes. Double as attackSpeed for class.
    final int handsSpeed        =   100;
    final int smallBladeSpeed   =   200;
    final int axeSpeed          =   300;
    final int largeBladeSpeed   =   400;
    
    // Item constants
    final int noEffectMod       =   0;
    final int attStrMod         =   1;
    final int attSkillMod       =   2;
    final int defStrMod         =   3;
    final int defSkillMod       =   4;
    final int handsMod          =   5;
    final int smallBladeMod     =   6;
    final int largeBladeMod     =   7;
    final int axeMod            =   8;
    final int polearmMod        =   9;
    final int shootingMod       =   10;
    final int throwingMod       =   11;
    final int attackSpeedMod    =   12;
    final int damageBonusMod    =   13;
    final int damageResistMod   =   14;
    
    // Timing constants
    int fightRoundLength    =   3000;   // In milliseconds
    
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

    public boolean isWeapon(int itemCode) {
        return ((itemCode >= 100) && (itemCode <= 999));
    }
    private boolean isSmallBlade(int code) {
        return ((code >= 200) && (code <= 299));
    }
    private boolean isLargeBlade(int code) {
        return ((code >= 300) && (code <= 399));
    }
    private boolean isAxe(int code) {
        return ((code >= 400) && (code <= 499));
    }
    /**
     * Takes an itemCode and returns the attack speed of it's weapon class.
     * @param code The itemCode.
     * @return 0 if code is not a weapon.
     */
    public int getWeaponClassSpeed(int code) {
        if (isSmallBlade(code)) {
            return smallBladeSpeed;
        } else if (isLargeBlade(code)) {
            return largeBladeSpeed;
        } else if (isAxe(code)) {
            return axeSpeed;
        } else {
            return 0;
        }
    }
    /**
     * Takes the wepClass (actually is attackspeed#) and returns the item 
     * ModCode for the corresponding weapon proficiency.
     * @param wepClass
     * @return itemModCode for relevant proficiency buff.
     */
    int getModCode(int wepClass) {
        switch (wepClass) {
            case smallBladeSpeed:
                return smallBladeMod;
            case largeBladeSpeed:
                return largeBladeMod;
            case axeSpeed:
                return axeMod;
            case handsSpeed:
            default:
                return handsMod;
        }
    }
}
