package server;
/**
 * @(#)resourc.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2009/10/6
 */


public class Resourc 
{
	String type;
	int amount;
    public Resourc(String t,int amnt) 
    {
    	type=t;
    	amount=amnt;
    }
    
    public String getType()
    {
    	return type;
    }
    
    public int getAmount()
    {
    	return amount;
    }
    
    public void setAmount(int a)
    {
    	amount=a;
    }
}