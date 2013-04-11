/**
 * This class serves as the ongoing 'world' in which the game data is altered.
 * 
 * @author Daniel Zapata
 */
package server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class World extends Thread{
    
    private ArrayList<Organism> organisms;   // list that hold all of the characters in the game
    private String[][] fullWorld;
    
    private int smallCityYLength = 12;
    private int smallCityXLength = 22;
    
    private int barYLength = 8;
    private int barXLength = 8;
    
    private int nextOrganismID;
    private int levelOne;
    private int levelTwo;
    private int levelThree;	
    private ArrayList<NumberPair> fights;
    
    // SQL Database Feilds
    String dbURL = "jdbc:mysql://localhost/game";   // URL of the database.
    String dbUsername = "root";                     
    String dbPassword = "";
    
    CustomCommunication communicate;
        
    private Server server;
    
    private boolean playerIsCreated = false;    //shitty stopgap measure
    
    public World(Server s) {
        server=s;                   // Connect the pointer to the server thread
        
        // SQL Database Connection initialization
        communicate = new CustomCommunication();      
        
        
        fullWorld= new String[][] {{"world","wildi","world1"}};
		
        organisms=new ArrayList<>();
        
        nextOrganismID=0;
        
        levelOne=0;
        levelTwo=0;
        levelThree=0;
        fights=new ArrayList<>();

        addNPC("one", 6, 6,"world");
        addMonster("squirell",7,7,"world",1);
    }
    
    /**
     * This method is called by the Thread execution.
     */
    @Override
    public void run()
    {
        System.out.println("World Threaded");
        infiniteLoop();
    }
    
    /**
     * This method adds a Character to the characters ArrayList
     * Returns the uid of added character.
     * @param name the name of the character
     */
    public int addCharacter(String name)
    {
    	Player asef=new Player(name, nextOrganismID, communicate);
    	organisms.add(asef);//putting our new character into our arrList, so it is actually in the game
    	nextOrganismID++;
        playerIsCreated = true;
        return nextOrganismID-1; // return the uid of the character we just created.
    }
    public void addNPC(String name,int xNPC, int yNPC, String wname)
    {
    	HumanNPC asef=new HumanNPC(name,nextOrganismID,xNPC,yNPC,wname, communicate);
    	organisms.add(asef);//putting our new character into our arrList, so it is actually in the game
    	nextOrganismID++;
    }
    public void addMonster(String name,int xNPC, int yNPC, String wname, int lvl)
    {
    	Creature asef=new Creature(name,nextOrganismID,xNPC,yNPC,wname,lvl, communicate);
    	if(lvl==1)
    	{
    		organisms.add(asef);//putting our new character into our arrList, so it is actually in the game
    		levelOne++;
    	}
    	if(lvl==2)
    	{
        	organisms.add(asef);//putting our new character into our arrList, so it is actually in the game
        	levelTwo++;
    	}
     	if(lvl==3)
     	{
     		organisms.add(asef);//putting our new character into our arrList, so it is actually in the game
     		levelThree++;
     	}
        nextOrganismID++;
        
    }
    
    /**
     * This method checks to see that the specified charater actually exists
     * @param name the name of the Human being checked
     * @return boolean value indicating existence
     */
    public boolean charExist(String name)
    {
    	if(organisms.isEmpty())
    	{return false;}
    	else
    	{
	    	for (Organism i : organisms){
                    if(i.getName().equals(name)){
	    		return true;
                    }
                }
	    	return false;
    	}
    	
    }
    
    // Keeps monster NPC population at appropriate levels
    // deactivated
    public void populate()
    {
        /*if(levelOne<500)
        {
            int xCord = (int)(Math.random()*100);
            int yCord = (int)(Math.random()*100);
            String name="squirel"+levelOne;
            addMonster(name,xCord,yCord,"wildi",1);
        }
        if(levelTwo<250)
        {
            int xCord = (int)(Math.random()*67)+17;
            int yCord = (int)(Math.random()*67)+17;
            addMonster("wolf"+levelTwo+"",xCord,yCord,"wildi",2);
        }
        if(levelThree<100)
        {
            int xCord = (int)(Math.random()*33)+33;
            int yCord = (int)(Math.random()*33)+33;
            addMonster("bear"+levelThree+"",xCord,yCord,"wildi",3);
        }*/
    }	
    
    /**
     * The primary infinite loop which updates the state of the game world.
     * Handles:
     *  Fights
     *  Energy Regeneration
     *  NPC AI actions
     *  Monster NPC spawns
     *  Trades
     */
    public void infiniteLoop()
    {
    	long startTimeFight=System.currentTimeMillis();
    	long startTimeEnergy=System.currentTimeMillis();
    	long startTimeNPC=System.currentTimeMillis();
    	long startTimeSpawn=System.currentTimeMillis();
        long startTimeUpdate=System.currentTimeMillis();
    	while(true)
    	{
    		long newTime=System.currentTimeMillis();
    		//loop for rounds of combat
	 		if((newTime-startTimeFight)>=1000)
			{
				for(int i=0; i<fights.size(); i++)
				{
					if(fights.get(i).getBool())//if the fight is happening between two characters
					{
						organisms.get(fights.get(i).getNumOne()).setFightStatus("");
	    				organisms.get(fights.get(i).getNumTwo()).setFightStatus("");
						fightOneRound(fights.get(i).getNumOne(),fights.get(i).getNumTwo(),true,true);
				    	fightOneRound(fights.get(i).getNumTwo(),fights.get(i).getNumOne(),true,true);
				    	if((organisms.get(fights.get(i).getNumTwo()).isAbleToFight()==false)||(organisms.get(fights.get(i).getNumOne()).isAbleToFight()==false))
				    	{
				    		organisms.get(fights.get(i).getNumOne()).autorun();
				    		organisms.get(fights.get(i).getNumTwo()).autorun();
				    		fights.remove(new NumberPair(fights.get(i).getNumOne(),1,true));
				    	}
					}
					else
					{
						organisms.get(fights.get(i).getNumOne()).setFightStatus("");
	    				organisms.get(fights.get(i).getNumTwo()).setFightStatus("");
						fightOneRound(fights.get(i).getNumOne(),fights.get(i).getNumTwo(),false,false);
				    	fightOneRound(fights.get(i).getNumTwo(),fights.get(i).getNumOne(),false,true);
				    	if((organisms.get(fights.get(i).getNumTwo()).isAbleToFight()==false)||(organisms.get(fights.get(i).getNumOne()).isAbleToFight()==false))
				    	{
				    		organisms.get(fights.get(i).getNumOne()).autorun();
				    		organisms.get(fights.get(i).getNumTwo()).autorun();
				    		fights.remove(new NumberPair(fights.get(i).getNumOne(),1,true));
				    	}
					}
					
				}
				startTimeFight=System.currentTimeMillis();
			}
			//loop for adding monsters
			populate();
                
			//loop for adding energy
			if((newTime-startTimeEnergy)>=60000)
			{
				for(int i=0; i<organisms.size(); i++)
				{
					organisms.get(i).setEnergy(organisms.get(i).getEnergy()+7);
				}
				startTimeEnergy=System.currentTimeMillis();
			}
			//loop for acting NPCs
			if((newTime-startTimeNPC)>=700 && playerIsCreated)
			{
				for(int a=0;a<organisms.size();a++)
				{
					organisms.get(a).act();
				}
				startTimeNPC=System.currentTimeMillis();
                                server.updateMoveScreensInAllClients();
			}
			if((newTime-startTimeSpawn)>=7200000)
			{
				for(int a=0;a<organisms.size();a++)
				{
					if(organisms.get(a).getWorld().equals("dead")||organisms.get(a).getWorld().equals("dead2"))
					{
						int b=organisms.get(a).getLevel();
						if(b==1)
						{
							levelOne--;
						}
						else if(b==2)
						{
							levelTwo--;
						}
						else
						{
							levelThree--;
						}
						organisms.remove(a);
						
					}
				}
				startTimeSpawn=System.currentTimeMillis();
			}
                        //loop for updating Trade information in clients
			if((newTime-startTimeUpdate)>=35 && playerIsCreated)
			{
				server.updateInventoryInAllClients();
                                server.updateResourcesInAllClients();
                                server.updateTradeInAllClients();
				startTimeUpdate=System.currentTimeMillis();
			}
			
    	}
    }
    
    /**
     * this is used to get the user id of a given human object
     * @param as the Human object that is beign identified
     * @return and int that represents the uid of the Human
     */
    public int getUID(String name)
    {
        for (Organism i : organisms){
                    if(i.getName().equals(name)){
	    		return organisms.indexOf(i);
                    }
                }
    	return -1;   //failed
    }
    
    /**
     * This method is used to get the Player object of a specific uid
     * @param uid the int used to find a Player
     * @return a Player object
     */
    public Player getCharacter(int uid)
    {
    	return (Player)organisms.get(uid);
    }
    public Creature getMonster(int uid)
    {
    	return (Creature)organisms.get(uid);
    }
    
    /**
     * This method moves the specified character in the specified direction
     * @param uid the int user id for the character to be moved
     * @param dir the direction code for which way the character is suppoed to go.
     */
    public void moveCharacter(int uid, String dir)
    {
    	if(dir.equals("north"))
    	{
		if(organisms.get(uid).getY()>0)
		{
			int w=nextSpotType(organisms.get(uid).getX(),organisms.get(uid).getY()-1,organisms.get(uid).getWorld());//a param to pass saying what type of spot we are trying to move to
			organisms.get(uid).moveNorth(w);//moves our actuall character North
		}
		else if(organisms.get(uid).getY()==0)
		{
			String next=changeWorld("north",uid,organisms.get(uid).getWorld());
			if(next.equals(organisms.get(uid).getWorld())==false)
			{
				
				organisms.get(uid).setWorld(next);
				organisms.get(uid).setY(99);
			}
		}
    	}
    	if(dir.equals("south"))
    	{
    		if(organisms.get(uid).getY()<smallCityYLength-1)
    		{
    			int w=nextSpotType(organisms.get(uid).getX(),organisms.get(uid).getY()+1,organisms.get(uid).getWorld());
    			organisms.get(uid).moveSouth(w);
    		}
    		else if(organisms.get(uid).getY()==smallCityYLength-1)
			{
				String next=changeWorld("south",uid,organisms.get(uid).getWorld());
				if(next.equals(organisms.get(uid).getWorld())==false)
			{
				
				organisms.get(uid).setWorld(next);
				organisms.get(uid).setY(0);
			}
			}
	    	}
    	
    	if(dir.equals("east"))
    	{
		if(organisms.get(uid).getX()<smallCityXLength-1)
		{
			int w=nextSpotType(organisms.get(uid).getX()+1,organisms.get(uid).getY(),organisms.get(uid).getWorld());
			organisms.get(uid).moveEast(w);
		}
		else if(organisms.get(uid).getX()==smallCityXLength-1)
		{
			String next=changeWorld("east",uid,organisms.get(uid).getWorld());
			System.out.print(next);
			if(next.equals(organisms.get(uid).getWorld())==false)
			{
				
				organisms.get(uid).setWorld(next);
				organisms.get(uid).setX(0);
			}
		}
    	}
    	
    	if(dir.equals("west"))
    	{
		if(organisms.get(uid).getX()>0)
		{
			int w=nextSpotType(organisms.get(uid).getX()-1,organisms.get(uid).getY(),organisms.get(uid).getWorld());
			organisms.get(uid).moveWest(w);
		}
		else if(organisms.get(uid).getX()==0)
		{
			String next=changeWorld("west",uid,organisms.get(uid).getWorld());
			if(next.equals(organisms.get(uid).getWorld())==false)
			{
				
				organisms.get(uid).setWorld(next);
				organisms.get(uid).setX(99);
			}
		}
    	}
        server.updateCharacterStatsInAllClients();
        server.updateMoveScreensInAllClients();
    }
    
    /**
     * This method starts a fight between two characters
     * @param uid the user to start fighting
     */
    public void startFight(int uid)
    {
    	for(int i=0;i<organisms.size();i++)
    	{
    		if(organisms.get(i).getWorld().equals(organisms.get(uid).getWorld()))
    		{
    			if(organisms.get(i).getX()==organisms.get(uid).getX())
	    		{
	    			if(organisms.get(i).getY()==organisms.get(uid).getY())
		    		{
		    			if(organisms.get(i).equals(organisms.get(uid))==false)
		    			{
		    				if(organisms.get(i).isAbleToFight())
		    				{
		    					
		    					fights.add(new NumberPair(uid,i,true));//lists these chars ids in a fights map so whe know who is currently fighting
		    					organisms.get(i).fight(uid,false);//alters some human side variables to initiate fighting.
			    				organisms.get(uid).fight(i,false);//alters some human side variables to initiate fighting.
			    				break;
		    				}
		    			}
		    		}
	    		}
    		}
    	}
    	if(organisms.get(uid).getOpponent()==uid)
    	{
            for(int i=0;i<organisms.size();i++)
            {
                if(organisms.get(i).getWorld().equals(organisms.get(uid).getWorld()))
                {
                    if(organisms.get(i).getX()==organisms.get(uid).getX())      // same X check
                    {
                        if(organisms.get(i).getY()==organisms.get(uid).getY())  // same Y check
                        {
                            if(organisms.get(i).equals(organisms.get(uid).getName())==false)    // Different name check
                            {
                                if(organisms.get(i).isAbleToFight())
                                {
                                        fights.add(new NumberPair(uid,i,false));//lists these chars ids in a fights map so whe know who is currently fighting
                                        organisms.get(i).fight(uid,false);//alters some human side variables to initiate fighting.
                                        organisms.get(uid).fight(i,true);//alters some human side variables to initiate fighting.
                                        break;
                                }
                            }
                        }
                    }
                }
            }
    	}
    }
    
    /**
     * This method ends a fight between two characters
     * @param uid the user to stop fighting
     */
    public void endFight(int uid)//this has a bug with out of bounds exception for the arraylist here. fix it
    {
    	fights.remove(new NumberPair(uid,1,true));
    }
    
    /**
     * This method causes one round of combat to occur
     * @param uid the first user to fight
     * @param uid2 the second user to fight
     */
    public void fightOneRound(int uid, int uid2, boolean pvp,boolean mthenp)
    {
    	if(pvp)//if both are players
    	{
    		String aimed=organisms.get(uid).getAttackSpot(organisms.get(uid2).getDefSkill(),organisms.get(uid).getAim());
	    	if(aimed.equals("miss")==false)
	    	{
	    		int damage=organisms.get(uid).getDamageDone(organisms.get(uid2).getDefStr());
	    		if(aimed.equals("head"))
	    		{
	    			organisms.get(uid2).setHead(organisms.get(uid2).getHead()-damage);
	    		}
	    		else if(aimed.equals("arms"))
	    		{
	    			organisms.get(uid2).setArms(organisms.get(uid2).getArms()-damage);
	    		}
	    		else if(aimed.equals("torso"))
	    		{
	    			organisms.get(uid2).setTorso(organisms.get(uid2).getTorso()-damage);
	    		}
	    		else if(aimed.equals("legs"))
	    		{
	    			organisms.get(uid2).setLegs(organisms.get(uid2).getLegs()-damage);
	    		}
	    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You did "
	    											+ damage+ " to your opponent's "+ aimed+"|");
	    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()
	    											+"|You were hit in the "+aimed+" for "+damage+"|");
	    		organisms.get(uid).setAttStrBase(organisms.get(uid).getAttStrBase()+(.01*damage));
	    		organisms.get(uid2).setDefStrBase(organisms.get(uid2).getDefStrBase()+(.01*damage));
	    	}
	    	else
	    	{
	    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You missed your opponent|");
	    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()+"|Your opponent missed|");
	    	}
	    	organisms.get(uid).setAttSkillBase(organisms.get(uid).getAttSkillBase()+(.01));
	    	organisms.get(uid2).setDefSkillBase(organisms.get(uid2).getDefSkillBase()+(.01));
    	}
    	else
    	{
	     if(mthenp)//if monster=uid
	     {
	     	String aimed=organisms.get(uid).getAttackSpot(organisms.get(uid2).getDefSkill(),organisms.get(uid).getAim());
		    	if(aimed.equals("miss")==false)
		    	{
		    		int damage=organisms.get(uid).getDamageDone(organisms.get(uid2).getDefStr());
		    		if(aimed.equals("head"))
		    		{
		    			organisms.get(uid2).setHead(organisms.get(uid2).getHead()-damage);
		    		}
		    		else if(aimed.equals("arms"))
		    		{
		    			organisms.get(uid2).setArms(organisms.get(uid2).getArms()-damage);
		    		}
		    		else if(aimed.equals("torso"))
		    		{
		    			organisms.get(uid2).setTorso(organisms.get(uid2).getTorso()-damage);
		    		}
		    		else if(aimed.equals("legs"))
		    		{
		    			organisms.get(uid2).setLegs(organisms.get(uid2).getLegs()-damage);
		    		}
		    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You did "
		    											+ damage+ " to your opponent's "+ aimed+"|");
		    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()
		    											+"|You were hit in the "+aimed+" for "+damage+"|");
		    		organisms.get(uid).setAttStrBase(organisms.get(uid).getAttStrBase()+(.01*damage));
		    		organisms.get(uid2).setDefStrBase(organisms.get(uid2).getDefStrBase()+(.01*damage));
		    	}
		    	else
		    	{
		    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You missed your opponent|");
		    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()+"|Your opponent missed|");
		    	}
		    	organisms.get(uid).setAttSkillBase(organisms.get(uid).getAttSkillBase()+(.01));
		    	organisms.get(uid2).setDefSkillBase(organisms.get(uid2).getDefSkillBase()+(.01));
	     }
	     else//uid2=monster
	     {
	     	String aimed=organisms.get(uid).getAttackSpot(organisms.get(uid2).getDefSkill(),organisms.get(uid).getAim());
	    	if(aimed.equals("miss")==false)
	    	{
	    		int damage=organisms.get(uid).getDamageDone(organisms.get(uid2).getDefStr());
	    		if(aimed.equals("head"))
	    		{
	    			organisms.get(uid2).setHead(organisms.get(uid2).getHead()-damage);
	    		}
	    		else if(aimed.equals("arms"))
	    		{
	    			organisms.get(uid2).setArms(organisms.get(uid2).getArms()-damage);
	    		}
	    		else if(aimed.equals("torso"))
	    		{
	    			organisms.get(uid2).setTorso(organisms.get(uid2).getTorso()-damage);
	    		}
	    		else if(aimed.equals("legs"))
	    		{
	    			organisms.get(uid2).setLegs(organisms.get(uid2).getLegs()-damage);
	    		}
	    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You did "
	    											+ damage+ " to your opponent's "+ aimed+"|");
	    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()
	    											+"|You were hit in the "+aimed+" for "+damage+"|");
	    		organisms.get(uid).setAttStrBase(organisms.get(uid).getAttStrBase()+(.01*damage));
	    		organisms.get(uid2).setDefStrBase(organisms.get(uid2).getDefStrBase()+(.01*damage));
	    	}
	    	else
	    	{
	    		organisms.get(uid).setFightStatus(organisms.get(uid).getFightStatus()+"|You missed your opponent|");
	    		organisms.get(uid2).setFightStatus(organisms.get(uid2).getFightStatus()+"|Your opponent missed|");
	    	}
	    	organisms.get(uid).setAttSkillBase(organisms.get(uid).getAttSkillBase()+(.01));
	    	organisms.get(uid2).setDefSkillBase(organisms.get(uid2).getDefSkillBase()+(.01));
	     }
    	}
    	
        server.updateFightInAllClients();       // Because the state of at least one fight has changed.
        server.updateCharacterStatsInAllClients();
    }
    
    /**
     * This method is used to get the type of spot that the specified x/y are (ie a 1, a 2, a 3 and so on)
     * @param x the x value for the location for the spot
     * @param y the x value for the location for the spot
     * @param worldname the name of the mini-world
     * @return an int representing a type of ground
     */
    public int nextSpotType(int x,int y,String worldnam)
    {
    	Scanner had = new Scanner(worldnam);
    	String worldname=had.next();
        if(worldname.equals("world")) {
            return communicate.selectSingleIntByXAndY("terrainType", "smallcity", x, y);
        }
        if(worldname.equals("bar")) {
            return communicate.selectSingleIntByXAndY("terrainType", "bar", x, y);
        }
        return 1;
    }
    
    public String changeWorld(String dire, int uid, String current)
    {
    	String yay=current;
    	int spX=-2;
    	int spY=-2;
    	for(int i=0;i<fullWorld.length;i++)
    	{
    		for(int a=0;a<fullWorld[i].length;a++)
    		{
    			if(fullWorld[i][a].equals(current))
    			{
    				spX=i;
    				spY=a;
    			}
    		}
    	}
    	if(spX!=-2)
    	{
    		
	    	if(dire.equals("north"))
	    	{
	    		if(spX>0)
	    		{
	    			yay= fullWorld[spX-1][spY];
	    		}
	    		
	    	}
	    	else if(dire.equals("south"))
	    	{
	    		if(spX<fullWorld.length-1)
	    		{
	    			yay= fullWorld[spX+1][spY];
	    		}
	    	}
	    	else if(dire.equals("east"))
	    	{
	    		
	    		if(spY<fullWorld[spX].length-1)
	    		{
	    			
	    			yay= fullWorld[spX][spY+1];
	    		}
	    	}
	    	else if(dire.equals("west"))
	    	{
	    		if(spY>0)
	    		{
	    			yay= fullWorld[spX][spY-1];
	    		}
	    	}
    	}
    	if(yay.equals("world1")&&organisms.get(uid).getTeam1())
    	{
    		return current;
    	}
    	else if(yay.equals("world")&&(organisms.get(uid).getTeam1()==false))
    	{
    		return current;
    	}
    	else
    		return yay;
    		
    }
    
    /**
     * this methodretuns the arrList of all the humans within the 10X10 whose origin is that of int x, int y
     * @param x an integer representing the x coordinate
     * @param y an integer representing the y coordinate
     * @param wor a String representing the world to search
     */
    public ArrayList<Organism> getOthersWorld(int x,int y,String wor)
    {
    	ArrayList<Organism> end=new ArrayList<>();
    	for(int i=0;i<organisms.size();i++)
    	{
    		if((organisms.get(i).getX()<=x+8)&&(organisms.get(i).getX()>=x-8))
    		{
    			if((organisms.get(i).getY()<=y+8)&&(organisms.get(i).getY()>=y-8))
    			{
	    			if((organisms.get(i).getX()==x)&&(organisms.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(organisms.get(i).getWorld().equals(wor))
	    				{
	    					end.add(organisms.get(i));
	    				}
	    			}
    			}
    		}
    	}
    	return end;
    }
    public ArrayList<Organism> getOthersWorldNPC(int x,int y,String wor)
    {
    	ArrayList<Organism> end=new ArrayList<>();
    	for(int i=0;i<organisms.size();i++)
    	{
    		if((organisms.get(i).getX()<=x+8)&&(organisms.get(i).getX()>=x-8))
    		{
    			if((organisms.get(i).getY()<=y+8)&&(organisms.get(i).getY()>=y-8))
    			{
	    			if((organisms.get(i).getX()==x)&&(organisms.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(organisms.get(i).getWorld().equals(wor))
	    				{
	    					end.add(organisms.get(i));
	    				}
	    			}
    			}
    		}
    	}
    	return end;
    }
    public ArrayList<Organism> getOthersWorldMonster(int x,int y,String wor)
    {
    	ArrayList<Organism> end=new ArrayList<>();
    	for(int i=0;i<organisms.size();i++)
    	{
    		if((organisms.get(i).getX()<=x+8)&&(organisms.get(i).getX()>=x-8))
    		{
    			if((organisms.get(i).getY()<=y+8)&&(organisms.get(i).getY()>=y-8))
    			{
	    			if((organisms.get(i).getX()==x)&&(organisms.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(organisms.get(i).getWorld().equals(wor))
	    				{
	    					end.add(organisms.get(i));
	    				}
	    			}
    			}
    		}
    	}
    	return end;
    }
    
    public void startTrade(int uid)
    {
    	for(int i=0;i<organisms.size();i++)
    	{
    		if(organisms.get(i).getWorld().equals(organisms.get(uid).getWorld()))
    		{
    			if(organisms.get(i).getX()==organisms.get(uid).getX())
	    		{
	    			if(organisms.get(i).getY()==organisms.get(uid).getY())
		    		{
		    			if(organisms.get(i).equals(organisms.get(uid))==false)
		    			{
		    				if(organisms.get(i).isAbleToFight())
		    				{
		    					organisms.get(uid).setTrader(i);
		    					organisms.get(i).setTrader(uid);
		    				}
		    			}
		    		}
	    		}
    		}
    	}
    }
    //trades only take place between items. not money or resources
    public void finishTrade(int uid, int otherbro)
    {
    	ArrayList<String> temp=organisms.get(uid).getOffers();
    	ArrayList<Items> temp2=organisms.get(uid).getInventory().getItems();
    	for(int i=0;i<temp.size();i++)
    	{
    		for(int a=0;a<temp2.size();a++)
    		{
    			if(temp.get(i).equals(temp2.get(a).getName()))
    			{
    				Items use =temp2.get(a);
    				organisms.get(uid).getInventory().removeItem(a);
    				organisms.get(otherbro).getInventory().addItems(use);
    			}
    		}
    	}
    	
    	temp=organisms.get(otherbro).getOffers();
    	temp2=organisms.get(otherbro).getInventory().getItems();
    	for(int i=0;i<temp.size();i++)
    	{
    		for(int a=0;a<temp2.size();a++)
    		{
    			if(temp.get(i).equals(temp2.get(a).getName()))
    			{
    				Items use =temp2.get(a);
    				organisms.get(otherbro).getInventory().removeItem(a);
    				organisms.get(uid).getInventory().addItems(use);
    			}
    		}
    	}
    	organisms.get(otherbro).setTrader(otherbro);
    	organisms.get(uid).setTrader(uid);
    }
}