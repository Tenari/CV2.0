/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private final int startOldX =   5;
    private final int startOldY =   5;
    private final String startWorldName = "world";
    
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
    private final double handToHandStart=   10.0;
    private final int moneyStart        =   10;
    
    // Maximum values
    private final int maxEnergy         =   10000;
//-------------------End Initializing constants---------------------------------
    
    // SQL variables
    CustomCommunication communicate;
    // SQL Tables
    String movementTableName = "organismsmovementinfo";
    String combatTableName = "combatstats";
    String statsTableName = "detailedstats";
    
    public OrganismHandler(CustomCommunication c) 
    {
        // Create the communication
        communicate = c;
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
                                        ""+startOldX,
                                        ""+startOldY,
                                        "'"+startWorldName+"'",
                                        "'"+startWorldName+"'",
                                        ""+maxEnergy,
                                        ""+classCode};
            worked = communicate.insert(movementTableName, initialMoveValues);  // true if the insert worked.
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
                                        ""+myUID };
            worked = communicate.insert(combatTableName, initialFightValues);
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
                                        ""+moneyStart };
            worked = communicate.insert(statsTableName, initialStatsValues);
            
            return worked;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        
    }
    
    //---------------------HP ACCESSOR METHODS--------------------------------------
    public int getHead(int orgUID) {
        return communicate.selectSingleIntByUID("headHP", combatTableName, orgUID);
    }
    public int getArms(int orgUID) {
    	return communicate.selectSingleIntByUID("armsHP", combatTableName, orgUID);
    }
    public int getTorso(int orgUID) {
    	return communicate.selectSingleIntByUID("torsoHP", combatTableName, orgUID);
    }
    public int getLegs(int orgUID) {
    	return communicate.selectSingleIntByUID("legsHP", combatTableName, orgUID);
    }
    
    public void setHead(int newHP, int orgUID) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "headHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "headHP", newHP, orgUID);
        }
    }
    public void setArms(int newHP, int orgUID) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "armsHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "armsHP", newHP, orgUID);
        }
    }
    public void setTorso(int newHP, int orgUID) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "torsoHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "torsoHP", newHP, orgUID);
        }
    }
    public void setLegs(int newHP, int orgUID) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "legsHP", 0, orgUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "legsHP", newHP, orgUID);
        }
    }
//---------------------END HP ACCESSOR METHODS----------------------------------
    
//---------------------SKILL UPDATE/ACCESSOR METHODS----------------------------
     
    public void setRealSkills(int orgUID) {
        // Set the current stat to the base stat times the normalized relevant HP stat.
        // curStat = baseStat * (curHP/HPmax)
    	setAttStr(  getAttStrBase(orgUID)   *   (getArms(orgUID)),  orgUID);
    	setAttSkill(getAttSkillBase(orgUID) *   (getHead(orgUID)),  orgUID);
    	setDefStr(  getDefStrBase(orgUID)   *   (getTorso(orgUID)), orgUID);
    	setDefSkill(getDefSkillBase(orgUID) *   (getLegs(orgUID)),  orgUID);
    }
    
    public double getAttSkill(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attSkill", combatTableName, orgUID);
    }
    public double getAttStr(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attStr", combatTableName, orgUID);
    }
    public double getDefSkill(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defSkill", combatTableName, orgUID);
    }
    public double getDefStr(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defStr", combatTableName, orgUID);
    }
    
    public void setAttStr(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(combatTableName, "attStr", newSkillValue, orgUID);
    }
    public void setAttSkill(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(combatTableName, "attSkill", newSkillValue, orgUID);
    }
    public void setDefStr(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(combatTableName, "defStr", newSkillValue, orgUID);
    }
    public void setDefSkill(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(combatTableName, "defSkill", newSkillValue, orgUID);
    }
    
    public double getAttSkillBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attSkillBase", statsTableName, orgUID);
    }
    public double getAttStrBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("attStrBase", statsTableName, orgUID);
    }
    public double getDefSkillBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defSkillBase", statsTableName, orgUID);
    }
    public double getDefStrBase(int orgUID) {
    	return communicate.selectSingleDoubleByUID("defStrBase", statsTableName, orgUID);
    }
    
    /**
     * These setskillBase methods work similarly to other setters--just put in 
     *  the new double value you want the skill to have. 
     * @param x 
     */
    public void setAttSkillBase(double newSkillValue, int orgUID) {
        communicate.updateSingleDoubleByUID(statsTableName, "attSkillBase", newSkillValue, orgUID);
    }
    public void setAttStrBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(statsTableName, "attStrBase", newSkillValue, orgUID);
    }
    public void setDefSkillBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(statsTableName, "defSkillBase", newSkillValue, orgUID);
    }
    public void setDefStrBase(double newSkillValue, int orgUID) {
    	communicate.updateSingleDoubleByUID(statsTableName, "defStrBase", newSkillValue, orgUID);
    }
//---------------------END SKILL UPDATE/ACCESSOR METHODS------------------------
 
}
