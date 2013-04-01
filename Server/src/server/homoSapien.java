package server;
/**
 * @(#)homoSapien.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2010/4/12
 */

//this class adds to organism the ability to fight using different weapons/proficencieys and bonus weapon damage/armor help
//it also implements trade, an inventoryDONE, resourcesDONE, teamsDONE and move speedsDONE!!!!!!!!!!!!!!these not done yet!!!!!!
public class homoSapien extends Organism
{
	
	boolean team1;
	String tribe;
	boolean created;
	
	int moveCost;
	int weightCarried;
	int spaceAvailable; //the amount of space that a character can hold on their person
	Inventory mystuff;
	
	double smallBlade;
    double largeBlade;
    double axe;
    double polearm;
    double bow;
    double throwing;
    double intimidation;
    double diplomacy;
    double endurance;
    double hiding;
    
    public homoSapien(String n,int me) 
    {
    	super(n,me);
    	
    	smallBlade=10.0;
    	largeBlade=10.0;
    	axe=10.0;
    	polearm=10.0;
    	bow=10.0;
    	throwing=10.0;
    	intimidation=10.0;
    	diplomacy=10.0;
    	endurance=10.0;
    	hiding=10.0;
    	
    	mystuff=new Inventory();
		mystuff.addItems(new Items(3,3,"armor","suckySheild", "none"));
		mystuff.addItems(new Items(3,3,"weapon","suckySword","smallBlade"));
		mystuff.addResource("tools",2);
		mystuff.addResource("water",5);
		mystuff.addResource("wheat",5);
		
		created=false;
    }
    //COMBAT________________________________________________________________
    public String getAttackSpot(double defendersDefSkill,String aim)
    {
    	setRealSkills();
    	double proficency=0;
    	if(attackStyle.equals("handToHand")){proficency=handToHand;}
    	else if(attackStyle.equals("smallBlade")){proficency=smallBlade;}
    	else if(attackStyle.equals("largeBlade")){proficency=largeBlade;}
    	else if(attackStyle.equals("axe")){proficency=axe;}
    	else if(attackStyle.equals("polearm")){proficency=polearm;}
    	else if(attackStyle.equals("bow")){proficency=bow;}
    	else if(attackStyle.equals("throwing")){proficency=throwing;}
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
    public int getDamageDone(double defendersDefStr)
    {
    	int gaaa=(int)(Math.random()*(((attStr-defendersDefStr)*0.25)+4+getWeaponDamage()));
    	return gaaa;
    }// here is where weapon bonus goes in
    public int getWeaponDamage()
    {
    		return mystuff.getWepDmg();
    }
    public int getArmorBonus()
    {
    		return mystuff.getArmBon();
    }
    public void plusToProficency(double skillgain)
    {
		if(attackStyle.equals("handToHand")){handToHand+=skillgain;}
    	else if(attackStyle.equals("smallBlade")){smallBlade+=skillgain;}
    	else if(attackStyle.equals("largeBlade")){largeBlade+=skillgain;}
    	else if(attackStyle.equals("axe")){axe+=skillgain;}
    	else if(attackStyle.equals("polearm")){polearm+=skillgain;}
    	else if(attackStyle.equals("bow")){bow+=skillgain;}
    	else if(attackStyle.equals("throwing")){throwing+=skillgain;}
    }
    public double getDefStr()
    {
    	return defStr+getArmorBonus();
    }
    //COMBAT________________________________________________________________
    
    public int determineMoveCost(int w)
    {
    	weightCarried=mystuff.determineWeight();
    	int a=weightCarried-(int)endurance;
    	if(a>0)
    	{
    		return a+(3*w);
    	}
    	if(a<=0 && w-Math.abs(a)>0)
    	{
    		return (3*w)+a;
    	}
    	else
    		return 1;
    }
    public Inventory getInventory()
    {
    	return mystuff;
    }
    public void setTeam(boolean nub)
    {
    	team1=nub;
    	if(team1)
    	{
    		worldname="world";
    		x=4;y=5;
    	}
    	else
    	{
    		worldname="world1";
    		x=4;y=5;
    	}
    }
    public boolean getTeam1()
    {
    	return team1;
    }
    
    //  SET METHODS  SET METHODS  SET METHODS  SET METHODS  SET METHODS
    public void setWeight(int x)
        {
            weightCarried=x;
        }
    public void setSmallBlade(double x)
	{
		smallBlade=x;
	}
    public void setLargeBlade(double x)
	{
		largeBlade=x;
	}
    public void setAxe(double x)
	{
		axe=x;
	}
	public void setPolearm(double x)
		{
		polearm=x;
		}
    public void setThrowing(double x)
	{
		throwing=x;
	}
    public void setBow(double x)
	{
		bow=x;
	}
    public void setIntimidation(double x)
	{
		intimidation=x;
	}
    public void setDiplomacy(double x)
	{
		diplomacy=x;
	}
    public void setEndurance(double x)
	{
		endurance=x;
	}
    public void setHiding(double x)
	{
		hiding=x;
	}
	//  GET METHODS  GET METHODS  GET METHODS  GET METHODS  GET METHODS
	public int getWeight()
        {
            return weightCarried;
        }
    public double getSmallBlade()
	{
		return smallBlade;
	}
    public double getLargeBlade()
	{
		return largeBlade;
	}
    public double getAxe()
	{
		return axe;
	}
	public double getPolearm()
		{
		return polearm;
		}
    public double getThrowing()
	{
		return throwing;
	}
    public double getBow()
	{
		return bow;
	}
    public double getIntimidation()
	{
		return intimidation;
	}
    public double getDiplomacy()
	{
		return diplomacy;
	}
    public double getEndurance()
	{
		return endurance;
	}
    public double getHiding()
	{
		return hiding;
	}
	
    public void moveNorth(int w)
    {
    	if(isFighting==false && isConcious && energy>0)
    	{
    		int mov=determineMoveCost(w);
    		
	    	if(w==3)
	    	{
	    		y-=1;
	    		energy-=mov;
	    	}
	    	else if(w==4)
	    	{
	    		y-=1;
	    		energy-=mov;
	    	}
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=3;y=6;//entrance point in the bar array
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
	    	endurance+=0.005;
    	}
    	setLastMoveDirection("north");
    }
    public void moveSouth(int w)
    {
    	if(isFighting==false && isConcious && energy>0)
    	{
    		int mov=determineMoveCost(w);
	    	if(w==3)
	    	{
	    		y+=1;
	    		energy-=mov;
	    	}
	    	if(w==4)
	    	{
	    		y+=1;
	    		energy-=mov;
	    	}
	    	
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=3;y=6;//entrance point in the bar array
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
	    	endurance+=0.005;
    	}
    	setLastMoveDirection("south");
    }
    public void moveEast(int w)
    {
    	if(isFighting==false && isConcious && energy>0)
    	{
    		int mov=determineMoveCost(w);
	    	if(w==3)
	    	{
	    		x+=1;
	    		energy-=mov;
	    	}
	    	if(w==4)
	    	{
	    		x+=1;
	    		energy-=mov;
	    	}
	    	
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=3;y=6;//entrance point in the bar array
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
	    	endurance+=0.005;
    	}
    	setLastMoveDirection("east");
    }
    public void moveWest(int w)
    {
    	if(isFighting==false && isConcious && energy>0)
    	{
    		int mov=determineMoveCost(w);
    		if(w==3)
	    	{
	    		x-=1;
	    		energy-=mov;
	    	}
	    	if(w==4)
	    	{
	    		x-=1;
	    		energy-=mov;
	    	}
	    	else if(w>=10)
	    	{
	    		leavespotX=x;
				leavespotY=y;
				oldWorld=worldname;
	    		worldname="bar"+" "+(w-10);
	    		x=3;y=6;//entrance point
	    	}
	    	else if(w==5)
	    	{
	    		worldname=oldWorld;
	    		x=leavespotX;y=leavespotY;
	    	}
	    	endurance+=0.005;
    	}
    	setLastMoveDirection("west");
    }
}