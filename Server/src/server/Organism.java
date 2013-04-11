package server;

import java.util.ArrayList;

/**
 * @(#)Organism.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2010/4/12
 */


public class Organism 
{
    // Variables to facilitate movement.
    String name;            // Organism's name
    int charUID;            // Organism's UID, used by ClientHandler
    String oldWorld = "world";        // The last world the Organism was in.
    String worldname = "world";       // The current world the Organism is in.
    
    // The initial values for the movement parameters.
    private final int startX    =   5;
    private final int startY    =   5;
    private final int startOldX =   5;
    private final int startOldY =   5;
    private final int startEnergy=  10000;
    
    // Maximum values
    private final int maxEnergy =   10000;
    
    // SQL variables
    CustomCommunication communicate;
    // SQL Tables
    String movementTableName = "organismsmovementinfo";
    
    // Other variables.
    int money;		//holds the amount of cash the character has on them.

    double attStr;		//the actual attacking Strength of this character
    double attSkill;	//the actual attacking Skill of this character
    double defStr;		//the actual defending Strength of this character
    double defSkill;	//the actual defending Skill of this character
    double attStrBase;	//the base(wont go down) attacking Strength of this character
    double attSkillBase;//the base(wont go down) attacking Skill of this character
    double defStrBase;	//the base(wont go down) defending Strength of this character
    double defSkillBase;//the base(wont go down) defending Skill of this character

    int headHealth;
    int armsHealth;
    int legsHealth;
    int torsoHealth;

    String aim;	
    boolean isFighting;
    int opponent;

    double handToHand;

    String attackStyle;

    boolean isConcious;
    boolean isDead;

    String fightStatus;

    boolean isMonster;

    String lastMoveDirection;
    
    public Organism(String n, int myUID, CustomCommunication c) 
    {
        // Create the communication
        communicate = c;
        // Create the intial values array for the momevment table
        String[] initialValues = {
                                    ""+myUID,  
                                    "'"+n+"'",   // name requires "'" around it because it's a varchar
                                    ""+startX,
                                    ""+startY,
                                    ""+startOldX,
                                    ""+startOldY,
                                    "'"+worldname+"'",
                                    "'"+oldWorld+"'",
                                    ""+startEnergy  };
        // Insert values for the organism's location
        communicate.insert(movementTableName, initialValues);
        
        money=10;
        charUID=myUID;
        name=n;
        worldname="world";
        oldWorld="world";

        attStr=3.0;
        attSkill=3.0;
        defStr=3.0;
        defSkill=3.0;
        attStrBase=3.0;
        attSkillBase=3.0;
        defStrBase=3.0;
        defSkillBase=3.0;
        headHealth=15;
        armsHealth=15;
        legsHealth=15;
        torsoHealth=15;

        handToHand=10.0;

        attackStyle="handToHand";

        fightStatus="";

        isFighting=false;
        aim="head";

        isMonster=false;

        isConcious=true;
        isDead=false;

        lastMoveDirection="south";
    }
    
//-----------------COMBAT METHODS-----------------------------------------------
    //returns the name of the spot hit or "miss"--only uses handToHand proficency
    public String getAttackSpot(double defendersDefSkill,String aim)
    {
    	setRealSkills();
    	double proficency=0;
    	if(attackStyle.equals("handToHand")){proficency=handToHand;}
    	double diff=  ((attSkill-defendersDefSkill)/2.0)+((3*proficency)/20.0);
    	double legs;double arms;
    	double torso;double head;
    	double miss;
    	if(aim.equals("torso"))
    	{
    		torso=(diff+10.0);
    		miss=  50.0-(diff*0.55);
    		arms=  30.0-(diff*0.35);
    		legs=  9.0-(diff*0.09);
    		head=  1.0-(diff*0.01);
    	}
    	else if(aim.equals("legs"))
    	{
    		legs=(diff+33.0);
    		miss=  50.0-(diff*0.66);
    		arms=  12.0-(diff*0.22);
    		torso= 4.0-(diff*0.08);
    		head=  1.0-(diff*0.04);
    	}
    	else if(aim.equals("arms"))
    	{
    		arms=(diff+33.0);
    		miss=  50.0-(diff*0.66);
    		torso= 12.0-(diff*0.22);
    		legs=  4.0-(diff*0.08);
    		head=  1.0-(diff*0.04);
    	}
    	else //if(aim.equals("head"))
    	{
    		head=(diff+15.0);
    		miss=  60.0-(diff*0.66);
    		arms=  15.0-(diff*0.20);
    		legs=  1.0-(diff*0.02);
    		torso= 9.0-(diff*0.12);
    	}
    	
    	double ra=Math.random()*100;
    	if(ra<=torso)
    	{
    		
    		attSkillBase+=.005;
    		attStrBase+=.01;
    		plusToProficency(.01);return "torso";
    	}
    	else if(ra<=torso+head)
    	{
    		
    		attSkillBase+=.005;
    		attStrBase+=.01;
    		plusToProficency(.01);return "head";
    	}
    	else if(ra<=torso+head+legs)
    	{
    		
    		attSkillBase+=.005;
    		attStrBase+=.01;
    		plusToProficency(.01);return "legs";
    	}
    	else if(ra<=torso+head+legs+arms)
    	{
    		
    		attSkillBase+=.005;
    		attStrBase+=.01;
    		plusToProficency(.01);return "arms";
    	}
    	else if(ra<=torso+head+legs+arms+miss)
    	{
    		
    		plusToProficency(.005);
    		attStrBase+=.01;return "miss";
    	}
    	
    	return "miss";
    }
    //DOES NOT TAKE INOT ACCOUNT WEAPON DAMAGE
    //DOES NOT TAKE INTO ACCOUNT ARMOR BONUS
    public int getDamageDone(double defendersDefStr)
    {
    	int gaaa=(int)(Math.random()*(((attStr-defendersDefStr)*0.25)+4));
    	return gaaa;
    }
    public void fight(int uid,boolean ismons)
    {
    	isMonster=ismons;
    	isFighting=true;
    	opponent=uid;
    	fightStatus="";
    	setRealSkills();
    }
    public boolean run(double defendersAttSkill)
    {
    	double bleh = (defSkill-defendersAttSkill)+(Math.random());
    	if(bleh > 0.33)
    	{
            isFighting  =   false;
            fightStatus =   "";
            opponent    =   charUID;
            isMonster   =   false;
            return true;
    	}
    	else
    	{
    		fightStatus="";
    		return false;
    	}
    	
    }
    public void autorun()
    {
    	isFighting=false;fightStatus="";opponent=charUID;isMonster=false;
    }
    public String getFightStatus()
    {
    	return fightStatus;
    }
    public void setFightStatus(String s)
    {
    	fightStatus=s;
    }
    public String getAim()
    {
    	return aim;
    }
    public void setAim(String a)
    {
    	aim=a;
    }
    public int getOpponent()
    {
    	if(isFighting)
    	{
    		return opponent;
    	}
    	else
    		return charUID;
    }
    public boolean getOpponentType()
    {
    	return isMonster;
    }
    
    public void setAttackStyle(String se)
    {
    	attackStyle=se;
    }
    public boolean isAbleToFight()
    {
    	if((isConcious)&&(!isDead))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
//-----------------END COMBAT METHODS-------------------------------------------
    
//---------------------HP ACCESSOR METHODS--------------------------------------
    public int getHead()
    {
    	return headHealth;
    }
    public int getArms()
    {
    	return armsHealth;
    }
    public int getTorso()
    {
    	return torsoHealth;
    }
    public int getLegs()
    {
    	return legsHealth;
    }
    
    public void setHead(int a)
    {
    	headHealth=a;
    	if(headHealth<=0)
    	{
    		isConcious=false;
    		headHealth=0;
    	}
    }
    public void setArms(int a)
    {
    	armsHealth=a;
    	if(armsHealth<=0)
    	{
    		armsHealth=0;
    	}
    }
    public void setTorso(int a)
    {
    	torsoHealth=a;
    	if(torsoHealth<=0)
    	{
    		isDead=true;
    		isConcious=false;
    		torsoHealth=0;
    	}
    }
    public void setLegs(int a)
    {
    	legsHealth=a;
    	if(legsHealth<=0)
    	{
    		legsHealth=0;
    	}
    }
//---------------------END HP ACCESSOR METHODS----------------------------------
    
//---------------------SKILL UPDATE/ACCESSOR METHODS----------------------------
     public void plusToProficency(double skillgain)
    {
        if(attackStyle.equals("handToHand")){handToHand+=skillgain;}
    }
    public void setRealSkills()
    {
    	
    	attStr=attStrBase*(.04*armsHealth);
    	attSkill=attSkillBase*(.04*headHealth);
    	defStr=defStrBase*(.04*torsoHealth);
    	defSkill=defSkillBase*(.04*legsHealth);
    	defSkillBase+=.0065;
    	defStrBase+=.0065;
    }
     public double getAttSkill()
    {
    	return attSkill;
    }
    public double getAttStr()
    {
    	return attStr;
    }
    public double getDefSkill()
    {
    	return defSkill;
    }
    public double getDefStr()
    {
    	return defStr;
    }//here is where armor bonus goes in
    
    public double getAttSkillBase()
    {
    	return attSkillBase;
    }
    public double getAttStrBase()
    {
    	return attStrBase;
    }
    public double getDefSkillBase()
    {
    	return defSkillBase;
    }
    public double getDefStrBase()
    {
    	return defStrBase;
    }
    
    public void setAttSkillBase(double x)
    {
    	attSkillBase=x;
    }
    public void setAttStrBase(double x)
    {
    	attStrBase=x;
    }
    public void setDefSkillBase(double x)
    {
    	defSkillBase=x;
    }
    public void setDefStrBase(double x)
    {
    	defStrBase=x;
    }
//---------------------END SKILL UPDATE/ACCESSOR METHODS------------------------
   
   
    
    public boolean moveNorth(int w)
    {
        boolean moved = false;
    	if(!isFighting && isConcious && getEnergy()>0)
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
    	if(isFighting==false && isConcious && getEnergy()>0)
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
    	if(isFighting==false && isConcious && getEnergy()>0)
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
    	if(isFighting==false && isConcious && getEnergy()>0)
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
        oldWorld=worldname;
        worldname="bar"+" "+(w-10);
        setX(3);        // The hardcoded entrance location for all bars
        setY(6);
    }
    public void updateLocationInfoForExitingABuilding() {
        worldname=oldWorld;
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
    
    public String getWorld()
    {
    	if(isDead)
    	{
    		return "dead";
    	}
    	else if(isConcious==false)
    	{
    		return "dead2";
    	}
    	else
    	{
    		return worldname;
    	}
    }
    public void setWorld(String w)
    {
    	worldname=w;
    }
    
    public String getName()
    {
    	return name;
    }
    
    /**
     * Returns the integer value of the energy stat stored in the database for 
     * a given organism, identified by unique uid int.
     * @return The amount of energy available to the organism.
     */
    public int getEnergy()
    {
        return communicate.selectSingleIntByUID("energy", movementTableName, charUID);
    }
    
    /**
     * Sets the energy stat in the database for a given organism, identified 
     * by unique uid int to the listed parameter.
     * @param e 
     */
    public void setEnergy(int e)
    {
        int energy = e;
        if (e > maxEnergy)
    	{
    		energy = maxEnergy;
    	}
        communicate.updateSingleIntByUID(movementTableName, "energy", energy, charUID);
    }
    
    public boolean equals(Object obj)
    {
    	String as=name;
    	if (as.equalsIgnoreCase(((Player)obj).getName()))
    		return true;
    	else
    		return false;
    }
    
    public void setMoney(int x)
        {
            money=x;
        }
    public void sethandToHand(double x)
	{
		handToHand=x;
	}
    
    public int getMoney()
        {
            return money;
        }
    public double getStrength()
{
	return (attStr+attSkill)/2;
}
    public double getAgility()
{
	return (defStr+defSkill)/2;
} 
    public double getHandToHand()
{
	return handToHand;
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
}