package server;

import java.util.ArrayList;

/**
 * @(#)Inventory.java
 *
 *
 * @author Daniel Zapata
 */



public class Inventory {
	public ArrayList<Items> its; 
	public ArrayList<Resourc> res; 
		int weight;
    public Inventory() 
    {
    	its=new ArrayList<>();
    	res=new ArrayList<>();
    	res.add(new Resourc("leather",0));
    	res.add(new Resourc("cloth",0));
    	res.add(new Resourc("tools",0));
    	res.add(new Resourc("wheat",0));
    	res.add(new Resourc("water",0));
    	res.add(new Resourc("meat",0));
    	res.add(new Resourc("stone",0));
    	res.add(new Resourc("wood",0));
    	res.add(new Resourc("metal",0));
    	res.add(new Resourc("buildingMaterial",0));
    }
     
     public void addResource(String type, int amount)
     {
     	for(int a=0;a<res.size();a++)
    	{
    		if(type.equals(res.get(a).getType()))
    		{
    			res.get(a).setAmount(res.get(a).getAmount()+amount);
    		}
    	}
     }
     	public int getLeather()
    {
        return res.get(0).getAmount();
    }
    public int getCloth()
    {
        return res.get(1).getAmount();
    }
    public int getTools()
    {
        return res.get(2).getAmount();
    }
    public int getWheat()
    {
        return res.get(3).getAmount();
    }
    public int getWater()
    {
        return res.get(4).getAmount();
    }
    public int getMeat()
    {
        return res.get(5).getAmount();
    }
    public int getStone()
    {
        return res.get(6).getAmount();
    }
    public int getWood()
    {
        return res.get(7).getAmount();
    }
    public int getMetal()
    {
        return res.get(8).getAmount();
    }
    public int getBuildingMaterial()
    {
        return res.get(9).getAmount();
    }
     
     public void addItems( Items i)
     {
     	if(its.size()<25)
     	{
     		its.add(i);
     	}
     	
     }
     public void removeItem(int x)
     {
     	its.remove(x);
     	
     
     }
     public Items getItem(int place)
     {
     	return its.get(place);
     }
     public int hugenessInven()
     {
     	return its.size();
     }
     public int getWepDmg()
     {
     	int ret=0;
     	for(int i=0;i<its.size();i++)
     	{
     		if(its.get(i).getType().equals("weapon"))
     		{
     			if(its.get(i).getEquipped())
     			{
     				ret= its.get(i).getBonus();
     			}
     		}
     	}
     	return ret;
     }
     public int getArmBon()
     {
     	int ret=0;
     	for(int i=0;i<its.size();i++)
     	{
     		if(its.get(i).getType().equals("armor"))
     		{
     			if(its.get(i).getEquipped())
     			{
     				ret= its.get(i).getBonus();
     			}
     		}
     	}
     	return ret;
     }
    
    public int determineWeight()
    {
    	weight=0;
    	for(int i=0;i<its.size();i++)
    	{
    		weight=weight + its.get(i).getWeight();
    	}
    	
    	for(int a=0;a<res.size();a++)
    	{
    		weight=weight +(res.get(a).getAmount());
    	}
    	return weight;
    }
    
    public ArrayList<Items> getItems()
    {
    	return its;
    }
    public void setItems(ArrayList<Items> b)
    {
    	its=b;
    }
}