package server;

import java.util.ArrayList;

/**
 * @(#)Organism.java
 *
 *
 * @author Daniel Zapata
 * @version 1.50 4/12/2013
 */


public class Organism 
{
    // Important variables.
    int charUID;            // Organism's UID, used by ClientHandler & DB
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

    String aim;	
    boolean isFighting;

    String attackStyle;

    String fightStatus;

    boolean isMonster;

    String lastMoveDirection;
    
    public Organism(String name, int myUID, CustomCommunication c, int classCode) 
    {
        // Create the communication
        communicate = c;
        // Create the intial values array for the momevment table
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
        // Insert values for the organism's location
        communicate.insert(movementTableName, initialMoveValues);
        
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
        
        // Insert values for the organism's combat Stats
        communicate.insert(combatTableName, initialFightValues);
        
        String[] initialStatsValues = {
                                    ""+myUID,  
                                    ""+attStrStart,   
                                    ""+attSkillStart,
                                    ""+defStrStart,
                                    ""+defSkillStart,
                                    ""+handToHandStart,
                                    ""+moneyStart };
        
        // Insert values for the organism's combat Stats
        communicate.insert(statsTableName, initialStatsValues);
       
        charUID=myUID;

        attackStyle="handToHand";

        fightStatus="";

        isFighting=false;
        aim="head";

        isMonster=false;

       lastMoveDirection="south";
    }
    
//-----------------COMBAT METHODS-----------------------------------------------
    //returns the name of the spot hit or "miss"--only uses handToHand proficency
    public String getAttackSpot(double defendersDefSkill,String aim) {
    	setRealSkills();
    	double proficency=0;
    	if(attackStyle.equals("handToHand")){proficency=getHandToHand();}
    	double diff=  ((getAttSkill()-defendersDefSkill)/2.0)+((3*proficency)/20.0);
    	double legs;double arms;
    	double torso;double head;
    	double miss;
    	if(aim.equals("torso")) {
            torso=(diff+10.0);
            miss=  50.0-(diff*0.55);
            arms=  30.0-(diff*0.35);
            legs=  9.0-(diff*0.09);
            head=  1.0-(diff*0.01);
    	}
    	else if(aim.equals("legs")) {
            legs=(diff+33.0);
            miss=  50.0-(diff*0.66);
            arms=  12.0-(diff*0.22);
            torso= 4.0-(diff*0.08);
            head=  1.0-(diff*0.04);
    	}
    	else if(aim.equals("arms")) {
            arms=(diff+33.0);
            miss=  50.0-(diff*0.66);
            torso= 12.0-(diff*0.22);
            legs=  4.0-(diff*0.08);
            head=  1.0-(diff*0.04);
    	}
        else {
            head=(diff+15.0);
            miss=  60.0-(diff*0.66);
            arms=  15.0-(diff*0.20);
            legs=  1.0-(diff*0.02);
            torso= 9.0-(diff*0.12);
    	}
    	
        // Make the stat bases increase.
    	double ra=Math.random()*100;
    	if(ra<=torso) {
            setAttSkillBase(getAttSkillBase()+.005);
            setAttStrBase(getAttStrBase()+.01);
            plusToProficency(.01);
            return "torso";
    	}
    	else if(ra<=torso+head) {
            setAttSkillBase(getAttSkillBase()+.005);
            setAttStrBase(getAttStrBase()+.01);
            plusToProficency(.01);
            return "head";
    	}
    	else if(ra<=torso+head+legs) {
            setAttSkillBase(getAttSkillBase()+.005);
            setAttStrBase(getAttStrBase()+.01);
            plusToProficency(.01);
            return "legs";
    	}
    	else if(ra<=torso+head+legs+arms) {
            setAttSkillBase(getAttSkillBase()+.005);
            setAttStrBase(getAttStrBase()+.01);
            plusToProficency(.01);
            return "arms";
    	}
    	else if(ra<=torso+head+legs+arms+miss)
        {
            plusToProficency(.005);
            setAttStrBase(getAttStrBase()+.01);
            return "miss";
    	}
    	return "miss";
    }
    //DOES NOT TAKE INOT ACCOUNT WEAPON DAMAGE
    //DOES NOT TAKE INTO ACCOUNT ARMOR BONUS
    public int getDamageDone(double defendersDefStr) {
    	int dmg=(int)(Math.random()*(((getAttStr()-defendersDefStr)*0.25)+4));
    	return dmg;
    }
    public void fight(int uid, boolean ismons) {
    	isMonster=ismons;
    	isFighting=true;
    	setOpponent(uid);
    	fightStatus="";
    	setRealSkills();
    }
    public boolean run(double defendersAttSkill) {
    	double bleh = (getDefSkill()-defendersAttSkill)+(Math.random());
    	if(bleh > 0.33)
    	{
            isFighting  =   false;
            fightStatus =   "";
            setOpponent(charUID);
            isMonster   =   false;
            return true;
    	}
    	else
    	{
    		fightStatus="";
    		return false;
    	}
    	
    }
    public void autorun() {
    	isFighting=false;
        fightStatus="";
        setOpponent(charUID);
        isMonster=false;
    }
    public String getFightStatus() {
    	return fightStatus;
    }
    public void setFightStatus(String s) {
    	fightStatus=s;
    }
    public String getAim() {
    	return aim;
    }
    public void setAim(String a) {
    	aim=a;
    }
    public int getOpponent() {
    	if(isFighting)
    	{
    		return communicate.selectSingleIntByUID("opponentUID", combatTableName, charUID);
    	}
    	else
    		return charUID;
    }
    public boolean getOpponentType() {
    	return isMonster;
    }
    
    public void setAttackStyle(String se) {
    	attackStyle=se;
    }
    public boolean isAbleToFight() {
    	if((isConcious())
                &&(!isDead())) {
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
//-----------------END COMBAT METHODS-------------------------------------------
    
//---------------------HP ACCESSOR METHODS--------------------------------------
    public int getHead() {
        return communicate.selectSingleIntByUID("headHP", combatTableName, charUID);
    }
    public int getArms() {
    	return communicate.selectSingleIntByUID("armsHP", combatTableName, charUID);
    }
    public int getTorso() {
    	return communicate.selectSingleIntByUID("torsoHP", combatTableName, charUID);
    }
    public int getLegs() {
    	return communicate.selectSingleIntByUID("legsHP", combatTableName, charUID);
    }
    
    public void setHead(int newHP) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "headHP", 0, charUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "headHP", newHP, charUID);
        }
    }
    public void setArms(int newHP) {
    	if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "armsHP", 0, charUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "armsHP", newHP, charUID);
        }
    }
    public void setTorso(int newHP) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "torsoHP", 0, charUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "torsoHP", newHP, charUID);
        }
    }
    public void setLegs(int newHP) {
        if(newHP<=0) {
            communicate.updateSingleIntByUID(combatTableName, "legsHP", 0, charUID);
    	}
        else {
            communicate.updateSingleIntByUID(combatTableName, "legsHP", newHP, charUID);
        }
    }
//---------------------END HP ACCESSOR METHODS----------------------------------
    
//---------------------SKILL UPDATE/ACCESSOR METHODS----------------------------
     public void plusToProficency(double skillgain)
    {
        if(attackStyle.equals("handToHand")){setHandToHand(getHandToHand()+skillgain);}
    }
    public void setRealSkills()
    {
    	setAttStr(getAttStrBase()*(.04*getArms()));
    	setAttSkill(getAttSkillBase()*(.04*getHead()));
    	setDefStr(getDefStrBase()*(.04*getTorso()));
    	setDefSkill(getDefSkillBase()*(.04*getLegs()));
        
    	setDefSkillBase(getDefSkillBase()+.0065);
    	setDefStrBase(getDefStrBase()+.0065);
    }
    public double getAttSkill() {
    	return communicate.selectSingleDoubleByUID("attSkill", combatTableName, charUID);
    }
    public double getAttStr() {
    	return communicate.selectSingleDoubleByUID("attStr", combatTableName, charUID);
    }
    public double getDefSkill() {
    	return communicate.selectSingleDoubleByUID("defSkill", combatTableName, charUID);
    }
    public double getDefStr() {
    	return communicate.selectSingleDoubleByUID("defStr", combatTableName, charUID);
    }
    
    public void setAttStr(double newSkillValue) {
        communicate.updateSingleDoubleByUID(combatTableName, "attStr", newSkillValue, charUID);
    }
    public void setAttSkill(double newSkillValue) {
        communicate.updateSingleDoubleByUID(combatTableName, "attSkill", newSkillValue, charUID);
    }
    public void setDefStr(double newSkillValue) {
        communicate.updateSingleDoubleByUID(combatTableName, "defStr", newSkillValue, charUID);
    }
    public void setDefSkill(double newSkillValue) {
        communicate.updateSingleDoubleByUID(combatTableName, "defSkill", newSkillValue, charUID);
    }
    
    public double getAttSkillBase() {
    	return communicate.selectSingleDoubleByUID("attSkillBase", statsTableName, charUID);
    }
    public double getAttStrBase() {
    	return communicate.selectSingleDoubleByUID("attStrBase", statsTableName, charUID);
    }
    public double getDefSkillBase() {
    	return communicate.selectSingleDoubleByUID("defSkillBase", statsTableName, charUID);
    }
    public double getDefStrBase() {
    	return communicate.selectSingleDoubleByUID("defStrBase", statsTableName, charUID);
    }
    
    /**
     * These setskillBase methods work similarly to other setters--just put in 
     *  the new double value you want the skill to have. 
     * @param x 
     */
    public void setAttSkillBase(double newSkillValue) {
        communicate.updateSingleDoubleByUID(statsTableName, "attSkillBase", newSkillValue, charUID);
    }
    public void setAttStrBase(double newSkillValue) {
    	communicate.updateSingleDoubleByUID(statsTableName, "attStrBase", newSkillValue, charUID);
    }
    public void setDefSkillBase(double newSkillValue) {
    	communicate.updateSingleDoubleByUID(statsTableName, "defSkillBase", newSkillValue, charUID);
    }
    public void setDefStrBase(double newSkillValue) {
    	communicate.updateSingleDoubleByUID(statsTableName, "defStrBase", newSkillValue, charUID);
    }
//---------------------END SKILL UPDATE/ACCESSOR METHODS------------------------
   
   
    
    public boolean moveNorth(int w)
    {
        boolean moved = false;
    	if(!isFighting && isConcious() && getEnergy()>0)
    	{
            if(w==3 || w==4)
            {
                setY(getY()-1);
                moved = true;
            }
            else if(w>=10)
            {
                updateLocationInfoForEnteringABuilding(w);
            }
            else if(w==5)
            {
                updateLocationInfoForExitingABuilding();
            }
            setLastMoveDirection("north");
    	}
    	return moved;
    }
    public boolean moveSouth(int w)
    {
        boolean moved = false;
    	if(isFighting==false && isConcious() && getEnergy()>0)
    	{
            if(w==3 || w==4)
            {
                setY(getY()+1);
                moved = true;
            }

            else if(w>=10)
            {
                updateLocationInfoForEnteringABuilding(w);
            }
            else if(w==5)   // if the tile was a building exit tile.
            {
                updateLocationInfoForExitingABuilding();
            }
            setLastMoveDirection("south");
        }
        return moved;
    }
    public boolean moveEast(int w)
    {
        boolean moved = false;
    	if(isFighting==false && isConcious() && getEnergy()>0)
    	{
            if(w==3 || w==4)
            {
                setX(getX()+1);
                moved = true;
            }
            else if(w>=10)
            {
                updateLocationInfoForEnteringABuilding(w);
            }
            else if(w==5)
            {
                updateLocationInfoForExitingABuilding();
            }
            setLastMoveDirection("east");
    	}
        return moved;
    }
    public boolean moveWest(int w)
    {
        boolean moved = false;
    	if(isFighting==false && isConcious() && getEnergy()>0)
    	{
            if(w==3 || w==4)
            {
                setX(getX()-1);
                moved = true;
            }
            else if(w>=10)
            {
                updateLocationInfoForEnteringABuilding(w);
            }
            else if(w==5)
            {
                updateLocationInfoForExitingABuilding();
            }
            setLastMoveDirection("west");
    	}
        return moved;
    }
    
    public void updateLocationInfoForEnteringABuilding(int w) {
        setOldX(getX());
        setOldY(getY());
        setOldWorld(getWorld());
        setWorld("bar"+" "+(w-10));
        setX(3);        // The hardcoded entrance location for all bars
        setY(6);
    }
    public void updateLocationInfoForExitingABuilding() {
        setWorld(getOldWorld());
        setX(getOldX());   // use the oldX/oldY as the new locations
        setY(getOldY());
    }
    public void setLastMoveDirection(String gu)
    {
        lastMoveDirection=gu;
    }
    public String getLastMoveDirection()
    {
        return lastMoveDirection;
    }
    
    public int getX()
    {
        return communicate.selectSingleIntByUID("x", movementTableName, charUID);
    }
    public int getY()
    {
    	return communicate.selectSingleIntByUID("y", movementTableName, charUID);
    }
    public int getOldX() {
        return communicate.selectSingleIntByUID("oldx", movementTableName, charUID);
    }
    public int getOldY() {
        return communicate.selectSingleIntByUID("oldy", movementTableName, charUID);
    }
    
    /**
     * Sets the X column of the uid matched row to newID in the 
     * movementTableName database table.
     * @param newX the new X value
     */
    public void setX(int newX) {
    	communicate.updateSingleIntByUID(movementTableName, "x", newX, charUID);
    }
    /**
     * Sets the Y column of the uid matched row to newID in the 
     * movementTableName database table.
     * @param newY the new Y value
     */
    public void setY(int newY) {
    	communicate.updateSingleIntByUID(movementTableName, "y", newY, charUID);
    }
    public void setOldX(int newOldX) {
    	communicate.updateSingleIntByUID(movementTableName, "oldx", newOldX, charUID);
    }
    public void setOldY(int newOldY) {
    	communicate.updateSingleIntByUID(movementTableName, "oldy", newOldY, charUID);
    }
    
    public String getWorld() {
    	if(isDead()) {
            return "dead";
    	}
    	else if(!isConcious()) {
            return "dead2";
    	}
        else {
            return communicate.selectSingleStringByUID("world", movementTableName, charUID);
    	}
    }
    public void setWorld(String newWorldName) {
    	communicate.updateSingleStringByUID(movementTableName, "world", newWorldName, charUID);
    }
    public String getOldWorld() {
    	return communicate.selectSingleStringByUID("oldworld", movementTableName, charUID);
    }
    public void setOldWorld(String newWorldName)
    {
    	communicate.updateSingleStringByUID(movementTableName, "oldworld", newWorldName, charUID);
    }
    
    public String getName()
    {
    	return communicate.selectSingleStringByUID("name", movementTableName, charUID);
    }
    public void setName(String newName) {
        communicate.updateSingleStringByUID(movementTableName, "name", newName, charUID);
    }
    
    /**
     * Returns the integer value of the energy stat stored in the database for 
     * a given organism, identified by unique uid int.
     * @return The amount of energy available to the organism.
     */
    public int getEnergy() {
        return communicate.selectSingleIntByUID("energy", movementTableName, charUID);
    }
    
    /**
     * Sets the energy stat in the database for a given organism, identified 
     * by unique uid int to the listed parameter.
     * @param e 
     */
    public void setEnergy(int e) {
        int energy = e;
        if (e > maxEnergy)
    	{
    		energy = maxEnergy;
    	}
        communicate.updateSingleIntByUID(movementTableName, "energy", energy, charUID);
    }
    
    public boolean equals(Object obj) {
    	String as=getName();
    	if (as.equalsIgnoreCase(((Player)obj).getName()))
    		return true;
    	else
    		return false;
    }
    
    public void setMoney(int money) {
        communicate.updateSingleIntByUID(statsTableName, "money", money, charUID);
    }
    
    
    public int getMoney() {
        return communicate.selectSingleIntByUID("money", statsTableName, charUID);
    }
    public double getStrength() {
	return (getAttStr()+getAttSkill())/2;
    }
    public double getAgility() {
	return (getDefStr()+getDefSkill())/2;
    } 
    public double getHandToHand() {
        return communicate.selectSingleDoubleByUID("handToHand", statsTableName, charUID);
    }
    public void setHandToHand(double newProficencyDouble) {
        communicate.updateSingleDoubleByUID(statsTableName, "handToHand", newProficencyDouble, charUID);
    }
    void act() {
        
    }

    int getLevel() {
        return 0;
        
    }

    boolean getTeam1() {
        return false;
        
    }

    void setTrader(int i) {
        
    }

    ArrayList<String> getOffers() {
        return null;
        
    }

    Inventory getInventory() {
        return null;
        
    }

    private void setOpponent(int uid) {
        communicate.updateSingleIntByUID(combatTableName, "opponentUID", uid, charUID);
    }
    
    public boolean isDead(){
        if( getTorso() <= 0 ){
            return true;
        }
        return false;
    }
    public boolean isConcious(){
        if( getHead() <= 0 || isDead()){
            return false;
        }
        return true;
    }
}