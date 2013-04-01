/**
 * This class serves as the ongoing 'world' in which the game data is altered.
 * 
 * @author Daniel Zapata
 */
package server;

import java.util.ArrayList;
import java.util.Scanner;

class World extends Thread{
    
    private ArrayList<Player> characters;   // list that hold all of the characters in the game
    private ArrayList<HumanNPC> humanNPCs;
    private ArrayList<Creature> monsters;
    private String[][] fullWorld;
    private int[][] world;                  // main world map array defining the right side city
    private int[][] world1;                 // wildi
    private int[][] wildi;                  // main world map array defining the left side city
    private int[][] bar;                    // bar map array defining the walls and such
    private int nextChar;
    private int nextNPCChar;
    private int nextMonster;
    private int levelOne;
    private int levelTwo;
    private int levelThree;	
    private ArrayList<NumberPair> fights;
    
    private Server server;
    
    private boolean playerIsCreated = false;    //shitty stopgap measure
    
    public World(Server s) {
        server=s;
        
        world = new int[][] {
            {1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
            {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
            {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
            {1,1,10,1,1,3,1,11,1,1,4,4,4,1,1,1,1,4,4,4,4,1},
            {1,3,3, 3,3,3,3,3, 3,3,3,3,3,3,3,3,3,3,4,4,4,1},
            {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
            {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
            {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
            {1,4,1, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
            {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
            {1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1}            
        };
        
        world1=wildi=world;
        
        bar = new int[][]  {{1,1,1,1,1,1,1,1},
                            {1,3,3,3,3,3,3,1},							//5=door to main	this is the bar building
                            {1,3,3,1,1,1,1,1},
                            {1,3,3,3,3,3,3,1},
                            {1,1,3,3,3,3,3,1},
                            {1,3,3,3,3,3,3,1},
                            {1,3,3,3,3,3,3,1},
                            {1,1,1,5,5,1,1,1}};
        fullWorld= new String[][] {{"world","wildi","world1"}};
		
        characters=new ArrayList<>();
        humanNPCs=new ArrayList<>();
        monsters=new ArrayList<>();
        
        nextChar=0;
        nextNPCChar=0;
        nextMonster=0;
        
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
     * @param name the name of the character
     */
    public void addCharacter(String name)
    {
    	Player asef=new Player(name,nextChar);
    	characters.add(asef);//putting our new character into our arrList, so it is actually in the game
    	nextChar++;
        playerIsCreated = true;
    }
    public void addNPC(String name,int xNPC, int yNPC, String wname)
    {
    	HumanNPC asef=new HumanNPC(name,nextNPCChar,xNPC,yNPC,wname);
    	humanNPCs.add(asef);//putting our new character into our arrList, so it is actually in the game
    	nextNPCChar++;
    }
    public void addMonster(String name,int xNPC, int yNPC, String wname, int lvl)
    {
    	Creature asef=new Creature(name,nextMonster,xNPC,yNPC,wname,lvl);
    	if(lvl==1)
    	{
    		monsters.add(asef);//putting our new character into our arrList, so it is actually in the game
    		levelOne++;
    	}
    	if(lvl==2)
    	{
        	monsters.add(asef);//putting our new character into our arrList, so it is actually in the game
        	levelTwo++;
    	}
     	if(lvl==3)
     	{
     		monsters.add(asef);//putting our new character into our arrList, so it is actually in the game
     		levelThree++;
     	}
        nextMonster++;
        
    }
    //this method adds monsters to the wildi
    //deactivated
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
     * This method is an infinite loop that is used to restrain the speed of fights. It is intended to cause computer controlled events to occur.
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
						characters.get(fights.get(i).getNumOne()).setFightStatus("");
	    				characters.get(fights.get(i).getNumTwo()).setFightStatus("");
						fightOneRound(fights.get(i).getNumOne(),fights.get(i).getNumTwo(),true,true);
				    	fightOneRound(fights.get(i).getNumTwo(),fights.get(i).getNumOne(),true,true);
				    	if((characters.get(fights.get(i).getNumTwo()).isAbleToFight()==false)||(characters.get(fights.get(i).getNumOne()).isAbleToFight()==false))
				    	{
				    		characters.get(fights.get(i).getNumOne()).autorun();
				    		characters.get(fights.get(i).getNumTwo()).autorun();
				    		fights.remove(new NumberPair(fights.get(i).getNumOne(),1,true));
				    	}
					}
					else
					{
						characters.get(fights.get(i).getNumOne()).setFightStatus("");
	    				monsters.get(fights.get(i).getNumTwo()).setFightStatus("");
						fightOneRound(fights.get(i).getNumOne(),fights.get(i).getNumTwo(),false,false);
				    	fightOneRound(fights.get(i).getNumTwo(),fights.get(i).getNumOne(),false,true);
				    	if((monsters.get(fights.get(i).getNumTwo()).isAbleToFight()==false)||(characters.get(fights.get(i).getNumOne()).isAbleToFight()==false))
				    	{
				    		characters.get(fights.get(i).getNumOne()).autorun();
				    		monsters.get(fights.get(i).getNumTwo()).autorun();
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
				for(int i=0; i<characters.size(); i++)
				{
					characters.get(i).setEnergy(characters.get(i).getEnergy()+7);
				}
				startTimeEnergy=System.currentTimeMillis();
			}
			//loop for acting NPCs
			if((newTime-startTimeNPC)>=700 && playerIsCreated)
			{
				for(int a=0;a<monsters.size();a++)
				{
					monsters.get(a).act();
				}
				for(int a=0;a<humanNPCs.size();a++)
				{
					humanNPCs.get(a).act();
				}
				startTimeNPC=System.currentTimeMillis();
                                server.updateMoveScreensInAllClients();
			}
			if((newTime-startTimeSpawn)>=7200000)
			{
				for(int a=0;a<monsters.size();a++)
				{
					if(monsters.get(a).getWorld().equals("dead")||monsters.get(a).getWorld().equals("dead2"))
					{
						int b=monsters.get(a).getLevel();
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
						monsters.remove(a);
						
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
    public int getUID(Player as)
    {
    	return characters.indexOf(as);
    }
    /**
     * This method checks to see that the specified charater actually exists
     * @param name the name of the Human being checked
     * @return boolean value indicating existence
     */
    public boolean charExist(Player name)
    {
    	if(characters.isEmpty())
    	{return false;}
    	else
    	{
	    	if(characters.contains(name))
	    		return true;
	    	else
	    		return false;
    	}
    	
    }
    /**
     * This method is used to get the Human object of a specific uid
     * @param uid the int used to find a Human
     * @return a Human object
     */
    public Player getCharacter(int uid)
    {
    	return characters.get(uid);
    }
    public Creature getMonster(int uid)
    {
    	return monsters.get(uid);
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
		if(characters.get(uid).getY()>0)
		{
			int w=nextSpotType(characters.get(uid).getX(),characters.get(uid).getY()-1,characters.get(uid).getWorld());//a param to pass saying what type of spot we are trying to move to
			characters.get(uid).moveNorth(w);//moves our actuall character North
		}
		else if(characters.get(uid).getY()==0)
		{
			String next=changeWorld("north",uid,characters.get(uid).getWorld());
			if(next.equals(characters.get(uid).getWorld())==false)
			{
				
				characters.get(uid).setWorld(next);
				characters.get(uid).setY(99);
			}
		}
    	}
    	if(dir.equals("south"))
    	{
    		if(characters.get(uid).getY()<world.length-1)
    		{
    			int w=nextSpotType(characters.get(uid).getX(),characters.get(uid).getY()+1,characters.get(uid).getWorld());
    			characters.get(uid).moveSouth(w);
    		}
    		else if(characters.get(uid).getY()==world.length-1)
			{
				String next=changeWorld("south",uid,characters.get(uid).getWorld());
				if(next.equals(characters.get(uid).getWorld())==false)
			{
				
				characters.get(uid).setWorld(next);
				characters.get(uid).setY(0);
			}
			}
	    	}
    	
    	if(dir.equals("east"))
    	{
		if(characters.get(uid).getX()<world[0].length-1)
		{
			int w=nextSpotType(characters.get(uid).getX()+1,characters.get(uid).getY(),characters.get(uid).getWorld());
			characters.get(uid).moveEast(w);
		}
		else if(characters.get(uid).getX()==world[0].length-1)
		{
			String next=changeWorld("east",uid,characters.get(uid).getWorld());
			System.out.print(next);
			if(next.equals(characters.get(uid).getWorld())==false)
			{
				
				characters.get(uid).setWorld(next);
				characters.get(uid).setX(0);
			}
		}
    	}
    	
    	if(dir.equals("west"))
    	{
		if(characters.get(uid).getX()>0)
		{
			int w=nextSpotType(characters.get(uid).getX()-1,characters.get(uid).getY(),characters.get(uid).getWorld());
			characters.get(uid).moveWest(w);
		}
		else if(characters.get(uid).getX()==0)
		{
			String next=changeWorld("west",uid,characters.get(uid).getWorld());
			if(next.equals(characters.get(uid).getWorld())==false)
			{
				
				characters.get(uid).setWorld(next);
				characters.get(uid).setX(99);
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
    	for(int i=0;i<characters.size();i++)
    	{
    		if(characters.get(i).getWorld().equals(characters.get(uid).getWorld()))
    		{
    			if(characters.get(i).getX()==characters.get(uid).getX())
	    		{
	    			if(characters.get(i).getY()==characters.get(uid).getY())
		    		{
		    			if(characters.get(i).equals(characters.get(uid))==false)
		    			{
		    				if(characters.get(i).isAbleToFight())
		    				{
		    					
		    					fights.add(new NumberPair(uid,i,true));//lists these chars ids in a fights map so whe know who is currently fighting
		    					characters.get(i).fight(uid,false);//alters some human side variables to initiate fighting.
			    				characters.get(uid).fight(i,false);//alters some human side variables to initiate fighting.
			    				break;
		    				}
		    			}
		    		}
	    		}
    		}
    	}
    	if(characters.get(uid).getOpponent()==uid)
    	{
    		for(int i=0;i<monsters.size();i++)
	    	{
	    		if(monsters.get(i).getWorld().equals(characters.get(uid).getWorld()))
	    		{
	    			if(monsters.get(i).getX()==characters.get(uid).getX())
		    		{
		    			if(monsters.get(i).getY()==characters.get(uid).getY())
			    		{
			    			if(monsters.get(i).equals(characters.get(uid))==false)
			    			{
			    				if(monsters.get(i).isAbleToFight())
			    				{
			    					fights.add(new NumberPair(uid,i,false));//lists these chars ids in a fights map so whe know who is currently fighting
			    					monsters.get(i).fight(uid,false);//alters some human side variables to initiate fighting.
				    				characters.get(uid).fight(i,true);//alters some human side variables to initiate fighting.
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
    		String aimed=characters.get(uid).getAttackSpot(characters.get(uid2).getDefSkill(),characters.get(uid).getAim());
	    	if(aimed.equals("miss")==false)
	    	{
	    		int damage=characters.get(uid).getDamageDone(characters.get(uid2).getDefStr());
	    		if(aimed.equals("head"))
	    		{
	    			characters.get(uid2).setHead(characters.get(uid2).getHead()-damage);
	    		}
	    		else if(aimed.equals("arms"))
	    		{
	    			characters.get(uid2).setArms(characters.get(uid2).getArms()-damage);
	    		}
	    		else if(aimed.equals("torso"))
	    		{
	    			characters.get(uid2).setTorso(characters.get(uid2).getTorso()-damage);
	    		}
	    		else if(aimed.equals("legs"))
	    		{
	    			characters.get(uid2).setLegs(characters.get(uid2).getLegs()-damage);
	    		}
	    		characters.get(uid).setFightStatus(characters.get(uid).getFightStatus()+"|You did "
	    											+ damage+ " to your opponent's "+ aimed+"|");
	    		characters.get(uid2).setFightStatus(characters.get(uid2).getFightStatus()
	    											+"|You were hit in the "+aimed+" for "+damage+"|");
	    		characters.get(uid).setAttStrBase(characters.get(uid).getAttStrBase()+(.01*damage));
	    		characters.get(uid2).setDefStrBase(characters.get(uid2).getDefStrBase()+(.01*damage));
	    	}
	    	else
	    	{
	    		characters.get(uid).setFightStatus(characters.get(uid).getFightStatus()+"|You missed your opponent|");
	    		characters.get(uid2).setFightStatus(characters.get(uid2).getFightStatus()+"|Your opponent missed|");
	    	}
	    	characters.get(uid).setAttSkillBase(characters.get(uid).getAttSkillBase()+(.01));
	    	characters.get(uid2).setDefSkillBase(characters.get(uid2).getDefSkillBase()+(.01));
    	}
    	else
    	{
	     if(mthenp)//if monster=uid
	     {
	     	String aimed=monsters.get(uid).getAttackSpot(characters.get(uid2).getDefSkill(),monsters.get(uid).getAim());
		    	if(aimed.equals("miss")==false)
		    	{
		    		int damage=monsters.get(uid).getDamageDone(characters.get(uid2).getDefStr());
		    		if(aimed.equals("head"))
		    		{
		    			characters.get(uid2).setHead(characters.get(uid2).getHead()-damage);
		    		}
		    		else if(aimed.equals("arms"))
		    		{
		    			characters.get(uid2).setArms(characters.get(uid2).getArms()-damage);
		    		}
		    		else if(aimed.equals("torso"))
		    		{
		    			characters.get(uid2).setTorso(characters.get(uid2).getTorso()-damage);
		    		}
		    		else if(aimed.equals("legs"))
		    		{
		    			characters.get(uid2).setLegs(characters.get(uid2).getLegs()-damage);
		    		}
		    		monsters.get(uid).setFightStatus(monsters.get(uid).getFightStatus()+"|You did "
		    											+ damage+ " to your opponent's "+ aimed+"|");
		    		characters.get(uid2).setFightStatus(characters.get(uid2).getFightStatus()
		    											+"|You were hit in the "+aimed+" for "+damage+"|");
		    		monsters.get(uid).setAttStrBase(monsters.get(uid).getAttStrBase()+(.01*damage));
		    		characters.get(uid2).setDefStrBase(characters.get(uid2).getDefStrBase()+(.01*damage));
		    	}
		    	else
		    	{
		    		monsters.get(uid).setFightStatus(monsters.get(uid).getFightStatus()+"|You missed your opponent|");
		    		characters.get(uid2).setFightStatus(characters.get(uid2).getFightStatus()+"|Your opponent missed|");
		    	}
		    	monsters.get(uid).setAttSkillBase(monsters.get(uid).getAttSkillBase()+(.01));
		    	characters.get(uid2).setDefSkillBase(characters.get(uid2).getDefSkillBase()+(.01));
	     }
	     else//uid2=monster
	     {
	     	String aimed=characters.get(uid).getAttackSpot(monsters.get(uid2).getDefSkill(),characters.get(uid).getAim());
	    	if(aimed.equals("miss")==false)
	    	{
	    		int damage=characters.get(uid).getDamageDone(monsters.get(uid2).getDefStr());
	    		if(aimed.equals("head"))
	    		{
	    			monsters.get(uid2).setHead(monsters.get(uid2).getHead()-damage);
	    		}
	    		else if(aimed.equals("arms"))
	    		{
	    			monsters.get(uid2).setArms(monsters.get(uid2).getArms()-damage);
	    		}
	    		else if(aimed.equals("torso"))
	    		{
	    			monsters.get(uid2).setTorso(monsters.get(uid2).getTorso()-damage);
	    		}
	    		else if(aimed.equals("legs"))
	    		{
	    			monsters.get(uid2).setLegs(monsters.get(uid2).getLegs()-damage);
	    		}
	    		characters.get(uid).setFightStatus(characters.get(uid).getFightStatus()+"|You did "
	    											+ damage+ " to your opponent's "+ aimed+"|");
	    		monsters.get(uid2).setFightStatus(monsters.get(uid2).getFightStatus()
	    											+"|You were hit in the "+aimed+" for "+damage+"|");
	    		characters.get(uid).setAttStrBase(characters.get(uid).getAttStrBase()+(.01*damage));
	    		monsters.get(uid2).setDefStrBase(monsters.get(uid2).getDefStrBase()+(.01*damage));
	    	}
	    	else
	    	{
	    		characters.get(uid).setFightStatus(characters.get(uid).getFightStatus()+"|You missed your opponent|");
	    		monsters.get(uid2).setFightStatus(monsters.get(uid2).getFightStatus()+"|Your opponent missed|");
	    	}
	    	characters.get(uid).setAttSkillBase(characters.get(uid).getAttSkillBase()+(.01));
	    	monsters.get(uid2).setDefSkillBase(monsters.get(uid2).getDefSkillBase()+(.01));
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
    	if(worldname.equals("world"))
    	{
    		return world[y][x];//goes [row][col] so, y comes first--yes its weird
    	} 
    	else if(worldname.equals("bar"))
    	{
    		return bar[y][x];
    	}
    	else if(worldname.equals("world1"))
    	{
    		return world1[y][x];
    	}
    	else if(worldname.equals("wildi"))
    	{
    		return wildi[y][x];
    	}
    	
    	else
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
    	if(yay.equals("world1")&&characters.get(uid).getTeam1())
    	{
    		return current;
    	}
    	else if(yay.equals("world")&&(characters.get(uid).getTeam1()==false))
    	{
    		return current;
    	}
    	else
    		return yay;
    		
    }
    
    //
    /**
     * this methodretuns the arrList of all the humans within the 10X10 whose origin is that of int x, int y
     * @param x an integer representing the x coordinate
     * @param y an integer representing the y coordinate
     * @param wor a String representing the world to search
     */
    public ArrayList<Organism> getOthersWorld(int x,int y,String wor)
    {
    	ArrayList<Organism> end=new ArrayList<>();
    	for(int i=0;i<characters.size();i++)
    	{
    		if((characters.get(i).getX()<=x+8)&&(characters.get(i).getX()>=x-8))
    		{
    			if((characters.get(i).getY()<=y+8)&&(characters.get(i).getY()>=y-8))
    			{
	    			if((characters.get(i).getX()==x)&&(characters.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(characters.get(i).getWorld().equals(wor))
	    				{
	    					end.add(characters.get(i));
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
    	for(int i=0;i<humanNPCs.size();i++)
    	{
    		if((humanNPCs.get(i).getX()<=x+8)&&(humanNPCs.get(i).getX()>=x-8))
    		{
    			if((humanNPCs.get(i).getY()<=y+8)&&(humanNPCs.get(i).getY()>=y-8))
    			{
	    			if((humanNPCs.get(i).getX()==x)&&(humanNPCs.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(humanNPCs.get(i).getWorld().equals(wor))
	    				{
	    					end.add(humanNPCs.get(i));
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
    	for(int i=0;i<monsters.size();i++)
    	{
    		if((monsters.get(i).getX()<=x+8)&&(monsters.get(i).getX()>=x-8))
    		{
    			if((monsters.get(i).getY()<=y+8)&&(monsters.get(i).getY()>=y-8))
    			{
	    			if((monsters.get(i).getX()==x)&&(monsters.get(i).getY()==y))
	    			{}
	    			else
	    			{
	    				if(monsters.get(i).getWorld().equals(wor))
	    				{
	    					end.add(monsters.get(i));
	    				}
	    			}
    			}
    		}
    	}
    	return end;
    }
    
    
    public void startTrade(int uid)
    {
    	for(int i=0;i<characters.size();i++)
    	{
    		if(characters.get(i).getWorld().equals(characters.get(uid).getWorld()))
    		{
    			if(characters.get(i).getX()==characters.get(uid).getX())
	    		{
	    			if(characters.get(i).getY()==characters.get(uid).getY())
		    		{
		    			if(characters.get(i).equals(characters.get(uid))==false)
		    			{
		    				if(characters.get(i).isAbleToFight())
		    				{
		    					characters.get(uid).setTrader(i);
		    					characters.get(i).setTrader(uid);
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
    	ArrayList<String> temp=characters.get(uid).getOffers();
    	ArrayList<Items> temp2=characters.get(uid).getInventory().getItems();
    	for(int i=0;i<temp.size();i++)
    	{
    		for(int a=0;a<temp2.size();a++)
    		{
    			if(temp.get(i).equals(temp2.get(a).getName()))
    			{
    				Items use =temp2.get(a);
    				characters.get(uid).getInventory().removeItem(a);
    				characters.get(otherbro).getInventory().addItems(use);
    			}
    		}
    	}
    	
    	temp=characters.get(otherbro).getOffers();
    	temp2=characters.get(otherbro).getInventory().getItems();
    	for(int i=0;i<temp.size();i++)
    	{
    		for(int a=0;a<temp2.size();a++)
    		{
    			if(temp.get(i).equals(temp2.get(a).getName()))
    			{
    				Items use =temp2.get(a);
    				characters.get(otherbro).getInventory().removeItem(a);
    				characters.get(uid).getInventory().addItems(use);
    			}
    		}
    	}
    	characters.get(otherbro).setTrader(otherbro);
    	characters.get(uid).setTrader(uid);
    }
}