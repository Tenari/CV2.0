package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    int x;                  // Organism's x coord
    int y;                  // Organism's y coord
    String oldWorld = "world";        // The last world the Organism was in.
    String worldname = "world";       // The current world the Organism is in.
    int leavespotX;         // The x coord of the position the Organism left the outisde world.
    int leavespotY;         // The y coord of the position the Organism left the outisde world.
    
    // The initial values for the movement parameters.
    private final int startX    =   5;
    private final int startY    =   5;
    private final int startOldX =   5;
    private final int startOldY =   5;
    private final int startEnergy=  10000;
    
    // SQL variables
    Connection dbConnection;
    Statement dbStmt;
    ResultSet dbResultSet;
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
    
    public Organism(String n, int me, Connection dbConnection, Statement dbStmt, ResultSet dbResultSet) 
    {
        // Connect SQL stuff.
        this.dbConnection   =   dbConnection;
        this.dbStmt         =   dbStmt;
        this.dbResultSet    =   dbResultSet;
        
        // Create the communication
        communicate = new CustomCommunication();
        // Create the intial values array for the momevment table
        String[] initialValues = {
                                    ""+me,  // uid
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
        charUID=me;
        x=5;y=5;		//initial starting spot(5,5)
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
    
    public void moveNorth(int w)
    {
    	if(!isFighting && isConcious && getEnergy()>0)
    	{
	    	if(w==3)
	    	{
	    		y-=1;
	    	}
	    	else if(w==4)
	    	{
	    		y-=1;
	    	}
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=2;y=5;
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
	    	setLastMoveDirection("north");
    	}
    	
    }
    public void moveSouth(int w)
    {
    	if(isFighting==false && isConcious && getEnergy()>0)
    	{
	    	if(w==3)
	    	{
	    		y+=1;
	    	}
	    	if(w==4)
	    	{
	    		y+=1;
	    	}
	    	
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=2;y=5;
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
                setLastMoveDirection("south");
    	}
    	
    }
    public void moveEast(int w)
    {
    	if(isFighting==false && isConcious && getEnergy()>0)
    	{
	    	if(w==3)
	    	{
	    		x+=1;
	    	}
	    	if(w==4)
	    	{
	    		x+=1;
	    	}
	    	
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=2;y=5;
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
                setLastMoveDirection("east");
    	}
    	
    }
    public void moveWest(int w)
    {
    	if(isFighting==false && isConcious && getEnergy()>0)
    	{
    		if(w==3)
	    	{
	    		x-=1;
	    	}
	    	if(w==4)
	    	{
	    		x-=1;
	    	}
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=2;y=5;//entrance point
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
                setLastMoveDirection("west");
    	}
    	
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
    	return x;
    }
    public int getY()
    {
    	return y;
    }
    public void setX(int ad)
    {
    	x=ad;
    }
    public void setY(int ab)
    {
    	y=ab;
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
    public int getEnergy()
    {
        dbResultSet = communicate.selectSingleByUID("energy", movementTableName, charUID);
        try {
            while (dbResultSet.next()) {
                    return dbResultSet.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return 0;
        }
    }
    public void setEnergy(int e)
    {
        int energy = e;
        if (e >10000)
    	{
    		energy=10000;
    	}
        try {
            if (dbStmt.execute("UPDATE `organismsmovementinfo` SET energy="+energy+" WHERE uid="+charUID)) {  
                // Should return false, because there are no results to get on an update.
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 

        }
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