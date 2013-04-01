/**
 * @(#)Items.java
 *
 *
 * @author Daniel Zapata | djz24
 * 
 */

import java.awt.*;
public class Items {
	
	public int weight;
	public Image pic;
	public int bonus;
	public String type;
	public String name;
	public String equipped;
	public int place;
    public Items(Image a, int b, int c, String d, String e, String f, int g) 
    {
    	pic = a;
    	weight = b;
    	bonus = c;
    	type = d;
		name = e;
		equipped=f;
		place=g;
    }
    public Items(Image a) 
    {
    	pic = a;
    	weight = 0;
    	bonus = 0;
    	type = "asshole";
		name = "fuck you";
    }
    public Image getPic()
    {
    	return pic;
    }
    public int getWeight()
    {
    	return weight;
    }
    public int getBonus()
    {
    	return bonus;
    }
    public String getType()
    {
    	return type;
    }
    public String getName()
    {
            return name;
    }
    public int getPlace()
    {
            return place;
    }
    public boolean getEquipped()
    {
        if(equipped.equals("true")) {
            return true;
        }
        else {
            return false;
        }
    }
	
    public void setPic(Image a)
    {
    	pic = a;
    }
    public void setWeight(int b)
    {
    	weight = b;
    }
    public void setbonus(int c)
    {
    	bonus = c;
    }
    public void setType(String d)
    {
    	type = d;
    }
    public void setName(String e)
    {
        name = e;
    }
    
    public boolean equals(Items x)
    {
    	if(x.getName().equals(this.getName()))
    	{
    		return true;
    	}
    	else
    		return false;
    }
}