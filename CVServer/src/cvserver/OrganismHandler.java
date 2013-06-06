package cvserver;

/**
 *
 * @author Tenari
 */
public class OrganismHandler {
//-------------------Initializing constants-------------------------------------
    // The initial values for the movement parameters.
    private final int startX    =   5;
    private final int startY    =   5;
    private final String startWorldName = "smallcity";
    
    // Initial HP values
    private final int headHealthStart   =   15;
    private final int armsHealthStart   =   15;
    private final int legsHealthStart   =   15;
    private final int torsoHealthStart  =   15;
    
    // Initial combat stats
    private final double attStrStart    =   3.0;	//the initialized attacking Strength of this character
    private final double attSkillStart  =   3.0;	//the initialized attacking Skill of this character
    private final double defStrStart    =   3.0;	//the initialized defending Strength of this character
    private final double defSkillStart  =   3.0;	//the initialized defending Skill of this character
    
    // Initial detailed stats
    private final double handToHandStart=   0.0;
    private final int moneyStart        =   10;
    private final double enduranceStart =   0.0;
    
    // Maximum values
    private final int maxEnergy         =   10000;
//-------------------End Initializing constants---------------------------------
    
    // SQL variables
    CustomCommunication communicate;
    
    LookupConfig lookup;
    
    public OrganismHandler(CustomCommunication c) {
        // Create the communication
        communicate = c;
        // Load the configuration manager class.
        lookup = new LookupConfig();
    }
    
    // Create a basic organism, utilizing default values.
    public boolean createNewOrganism(String name, int myUID, int classCode){
        try{
            boolean worked = false;
            // Insert values for the organism's location
            String[] initialMoveValues = {
                                        ""+myUID,  
                                        "'"+name+"'",   // name requires "'" around it because it's a varchar
                                        ""+startX,
                                        ""+startY,
                                        "'"+startWorldName+"'",
                                        ""+maxEnergy,
                                        ""+classCode,
                                        ""+2};          // 1= North. this is DirectionCode for the way the organism is facing.
            worked = communicate.insert(lookup.movementTableName, initialMoveValues);  // true if the insert worked.
            if (!worked) {return worked;}

            // Insert values for the organism's combat Stats
            String[] initialFightValues = {
                                        ""+myUID,  
                                        ""+attStrStart,   
                                        ""+attSkillStart,
                                        ""+defStrStart,
                                        ""+defSkillStart,
                                        ""+headHealthStart,
                                        ""+armsHealthStart,
                                        ""+torsoHealthStart,
                                        ""+legsHealthStart,
                                        ""+myUID };//opponent
            worked = communicate.insert(lookup.combatTableName, initialFightValues);
            if (!worked) {return worked;}

            // Insert values for the organism's detailed Stats
            String[] initialStatsValues = {
                                        ""+myUID,  
                                        ""+attStrStart,   
                                        ""+attSkillStart,
                                        ""+defStrStart,
                                        ""+defSkillStart,
                                        ""+headHealthStart,
                                        ""+armsHealthStart,
                                        ""+torsoHealthStart,
                                        ""+legsHealthStart,
                                        ""+handToHandStart,
                                        ""+moneyStart,
                                        ""+enduranceStart};
            worked = communicate.insert(lookup.statsTableName, initialStatsValues);
            
            return worked;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        
    }
    
//---------------------HP ACCESSOR METHODS------------------------------------\\
    public int getHead(int orgUID) {
        return communicate.selectSingleIntByUID("headHP", lookup.combatTableName, orgUID);
    }
    public int getArms(int orgUID) {
    	return communicate.selectSingleIntByUID("armsHP", lookup.combatTableName, orgUID);
    }
    public int getTorso(int orgUID) {
    	return communicate.selectSingleIntByUID("torsoHP", lookup.combatTableName, orgUID);
    }
    public int getLegs(int orgUID) {
    	return communicate.selectSingleIntByUID("legsHP", lookup.combatTableName, orgUID);
    }
    
    public void setHead(int newHP, int orgUID) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(lookup.combatTableName, "headHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(lookup.combatTableName, "headHP", newHP, orgUID);
        }
    }
    public void setArms(int newHP, int orgUID) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(lookup.combatTableName, "armsHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(lookup.combatTableName, "armsHP", newHP, orgUID);
        }
    }
    public void setTorso(int newHP, int orgUID) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(lookup.combatTableName, "torsoHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(lookup.combatTableName, "torsoHP", newHP, orgUID);
        }
    }
    public void setLegs(int newHP, int orgUID) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(lookup.combatTableName, "legsHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(lookup.combatTableName, "legsHP", newHP, orgUID);
        }
    }
//---------------------END HP ACCESSOR METHODS--------------------------------//
    
//---------------------SKILL UPDATE/ACCESSOR METHODS--------------------------\\
     
    public void setRealSkills(int orgUID) {
        // Set the current stat to the base stat times the normalized relevant HP stat.
        // curStat = baseStat * (curHP/HPmax)
    	setAttStr(  getAttStrBase(orgUID)   *   (getArms(orgUID)),  orgUID);
    	setAttSkill(getAttSkillBase(orgUID) *   (getHead(orgUID)),  orgUID);
    	setDefStr(  getDefStrBase(orgUID)   *   (getTorso(orgUID)), orgUID);
    	setDefSkill(getDefSkillBase(orgUID) *   (getLegs(orgUID)),  orgUID);
    }
    
    public double getAttSkill(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attSkill", lookup.combatTableName, orgUID);
    }
    public double getAttStr(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attStr", lookup.combatTableName, orgUID);
    }
    public double getDefSkill(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defSkill", lookup.combatTableName, orgUID);
    }
    public double getDefStr(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defStr", lookup.combatTableName, orgUID);
    }
    
    public void setAttStr(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.combatTableName, "attStr", newSkillValue, orgUID);
    }
    public void setAttSkill(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.combatTableName, "attSkill", newSkillValue, orgUID);
    }
    public void setDefStr(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.combatTableName, "defStr", newSkillValue, orgUID);
    }
    public void setDefSkill(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.combatTableName, "defSkill", newSkillValue, orgUID);
    }
    
    public double getAttSkillBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attSkillBase", lookup.statsTableName, orgUID);
    }
    public double getAttStrBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attStrBase", lookup.statsTableName, orgUID);
    }
    public double getDefSkillBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defSkillBase", lookup.statsTableName, orgUID);
    }
    public double getDefStrBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defStrBase", lookup.statsTableName, orgUID);
    }
    
    /**
     * These setskillBase methods work similarly to other setters--just put in 
     *  the new double value you want the skill to have. 
     * @param x 
     */
    public void setAttSkillBase(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.statsTableName, "attSkillBase", newSkillValue, orgUID);
    }
    public void setAttStrBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(lookup.statsTableName, "attStrBase", newSkillValue, orgUID);
    }
    public void setDefSkillBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(lookup.statsTableName, "defSkillBase", newSkillValue, orgUID);
    }
    public void setDefStrBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(lookup.statsTableName, "defStrBase", newSkillValue, orgUID);
    }
    
    public double getEndurance(int orgUID) {
    	return communicate.selectSingleDoubleByUID("endurance", lookup.statsTableName, orgUID);
    }
    public void setEndurance(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(lookup.statsTableName, "endurance", newSkillValue, orgUID);
    }
//---------------------END SKILL UPDATE/ACCESSOR METHODS----------------------//

//--------------------LOCATION ACCESSOR METHODS-------------------------------\\
    public int getX(int orgUID) {
        return communicate.selectSingleIntByUID("x", lookup.movementTableName, orgUID);
    }
    public int getY(int orgUID) {
    	return communicate.selectSingleIntByUID("y", lookup.movementTableName, orgUID);
    }
    
    /**
     * Sets the X column of the uid matched row to newID in the 
     * movementTableName database table.
     * @param newX the new X value
     */
    public void setX(int newX, int orgUID) {
    	communicate.updateSingleIntByUID(lookup.movementTableName, "x", newX, orgUID);
    }
    /**
     * Sets the Y column of the uid matched row to newID in the 
     * movementTableName database table.
     * @param newY the new Y value
     */
    public void setY(int newY, int orgUID) {
    	communicate.updateSingleIntByUID(lookup.movementTableName, "y", newY, orgUID);
    }
    
    public String getWorld(int orgUID) {
        return communicate.selectSingleStringByUID("world", lookup.movementTableName, orgUID);
    }
    public void setWorld(String newWorldName, int orgUID) {
    	communicate.updateSingleStringByUID(lookup.movementTableName, "world", newWorldName, orgUID);
    }
    
    public int getDirection(int orgUID) {
        return communicate.selectSingleIntByUID("direction", lookup.movementTableName, orgUID);
    }
    public void setDirection(int newDirection, int orgUID){
        communicate.updateSingleIntByUID(lookup.movementTableName, "direction", newDirection, orgUID);
    }
//--------------------------END LOCATION ACCESSORS----------------------------//
    
//----------------------------COMBAT HELPERS----------------------------------\\
    public boolean isFighting(int orgUID){
        if (getOpponentUID(orgUID) == orgUID) {
            return false;
        }
        return true;
    }
    
    public int getOpponentUID(int orgUID){
        return communicate.selectSingleIntByUID("opponentUID", lookup.combatTableName, orgUID);
    }
    public boolean setOpponentUID(int opponentUID, int orgUID){
        return communicate.updateSingleIntByUID(lookup.combatTableName, "opponentUID", opponentUID, orgUID);
    }
//---------------------------END COMBAT HELPERS-------------------------------//

//--------------------------MISC ACCESSORS------------------------------------\\
    public int getEnergy(int orgUID) {
        return communicate.selectSingleIntByUID("energy", lookup.movementTableName, orgUID);
    }
    public void setEnergy(int e, int orgUID) {
        int energy = e;
        if (e > maxEnergy)
    	{
    		energy = maxEnergy;
    	}
        communicate.updateSingleIntByUID(lookup.movementTableName, "energy", energy, orgUID);
    }
    
    public int getMoney(int orgUID) {
        return communicate.selectSingleIntByUID("money", lookup.statsTableName, orgUID);
    }
    public void setMoney(int cash, int orgUID) {
        communicate.updateSingleIntByUID(lookup.statsTableName, "money", cash, orgUID);
    }
    
    public int getClass(int orgUID) {
        return communicate.selectSingleIntByUID("class", lookup.movementTableName, orgUID);
    }
    public void setClass(int classCode, int orgUID) {
        communicate.updateSingleIntByUID(lookup.movementTableName, "class", classCode, orgUID);
    }
}
