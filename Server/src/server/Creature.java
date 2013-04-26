package server;
/**
 * @(#)Creature.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2010/4/12
 */
import java.util.Random;

//the only thing this class has to be able to do is take care of respawns. or nothing if that happens elsewhere.
public class Creature extends Organism 
{

	int level;
        Random generator;
    public Creature(String n,int me,int xI, int yI, String wldnm, int lvl, CustomCommunication c, int classCode)      
    {
        super(n,me,c, classCode);	
        setWorld(wldnm);
        
        setX(xI);
        setY(yI);
        
        level=lvl;
        setLevel(level);
        generator=new Random();
     }
     public void setLevel(int lvl)
     {
        int skill = 15;
        if(lvl==1)
        {
            skill = 15;
        }
        if(lvl==2)
        {
            skill = 30;
        }
        if(lvl==3)
        {
            skill = 45;
        }
        setDefStrBase(skill);
        setDefSkillBase(skill);
        setAttStrBase(skill);
        setAttSkillBase(skill);
     }
     
     // deactivated.
     private void executeRoutine(int level)
     {
         /*int moveValue =generator.nextInt(4) + 1;
		if(level==1)
		{
			if(getX()!=0 && getY()!=0 && getX()!=99 && getY()!=99)
			{
				if(moveValue==0)
				{
					moveNorth(3);
				}
				else if(moveValue==1)
				{
					moveWest(3);
				}
				else if(moveValue==2)
				{
					moveEast(3);
				}
				else if(moveValue==3)
				{
					moveSouth(3);
				}
			}
			else if(getX()==0 && getY()==0)
				moveSouth(3);
			else if(getX()==0 && getY()==99)
				moveEast(3);
			else if(getX()==99 && getY()==99)
				moveNorth(3);
			else if(getX()==99 && getY()==0)
				moveWest(3);
			else if(getX()==0)
				moveEast(3);
			else if(getX()==99)
				moveWest(3);
			else if(getY()==0)
				moveSouth(3);
			else if(getY()==99)
				moveNorth(3);
		}
		if(level==2)
		{
			if(getX()!=12 && getY()!=12 && getX()!=88 && getY()!=88)
			{
				if(moveValue==0)
				{
					moveNorth(3);
				}
				else if(moveValue==1)
				{
					moveWest(3);
				}
				else if(moveValue==2)
				{
					moveEast(3);
				}
				else if(moveValue==3)
				{
					moveSouth(3);
				}
			}
			else if(getX()==12 && getY()==12)
				moveSouth(3);
			else if(getX()==12 && getY()==88)
				moveEast(3);
			else if(getX()==88 && getY()==88)
				moveNorth(3);
			else if(getX()==88 && getY()==12)
				moveWest(3);
			else if(getX()==12)
				moveEast(3);
			else if(getX()==88)
				moveWest(3);
			else if(getY()==12)
				moveSouth(3);
			else if(getY()==88)
				moveNorth(3);
		}
		if(level==3)
		{
			if(getX()!=33 && getY()!=33 && getX()!=67 && getY()!=67)
			{
				if(moveValue==0)
				{
					moveNorth(3);
				}
				else if(moveValue==1)
				{
					moveWest(3);
				}
				else if(moveValue==2)
				{
					moveEast(3);
				}
				else if(moveValue==3)
				{
					moveSouth(3);
				}
			}
			else if(getX()==33 && getY()==33)
				moveSouth(3);
			else if(getX()==33 && getY()==67)
				moveEast(3);
			else if(getX()==67 && getY()==67)
				moveNorth(3);
			else if(getX()==67 && getY()==12)
				moveWest(3);
			else if(getX()==33)
				moveEast(3);
			else if(getX()==67)
				moveWest(3);
			else if(getY()==33)
				moveSouth(3);
			else if(getY()==67)
				moveNorth(3);
		}*/
    }
    public void act()
    {
    		executeRoutine(getLevel());
    }
    public int getLevel()
    {
    		return level;
    }
    
}