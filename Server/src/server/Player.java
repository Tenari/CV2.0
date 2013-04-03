package server;

import java.util.ArrayList;

/**
 * @(#)Player.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2010/4/13
 */


public class Player extends homoSapien
{
	int tradingPartner;
	ArrayList<String> myofferings;
	boolean tradeIsGood;
    public Player(String n,int me) 
    {
    	super(n,me);
    	tradingPartner=me;
    	myofferings=new ArrayList<>();
    	tradeIsGood=false;
    }
    
    public void setTrader(int ha)
    {
    	tradingPartner =ha;
    }
    
    public int getTrader()
    {
    	return tradingPartner;
    }
    
    public String getOffer()
    {
    	String bele=""+tradeIsGood;
    	for(int i=0;i<myofferings.size();i++)
    	{
    		bele=bele+" "+myofferings.get(i);
    	}
    	return bele;
    }
    
    public void addOffer(String n)
    {
    	myofferings.add(n);
    }
    public void removeOffer(String n)
    {
    	myofferings.remove(n);
    }
    public void makeDealGood()
    {
    	tradeIsGood=true;
    }
    public void makeDealBad()
    {
    	tradeIsGood=false;
    }
    public boolean getTradeIsGood()
    {
    	return tradeIsGood;
    }
    
    public ArrayList<String> getOffers()
    {
    	return myofferings;
    }
}