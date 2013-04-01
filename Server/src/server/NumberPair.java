package server;
/**
 * @(#)NumberPair.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2009/8/26
 */


public class NumberPair 
{
	private int num1;
	private int num2;
	boolean asd;
    public NumberPair(int n1, int n2,boolean m) 
    {
    	num1=n1;
    	num2=n2;
    	asd=m;
    }
    
    public int getNumOne()
    {
    	return num1;
    }
    public int getNumTwo()
    {
    	return num2;
    }
    
    public boolean getBool()
    {
    	return asd;
    }
    
    public boolean equals(Object obj)
    {
    	
    	if (num1==((NumberPair)obj).getNumOne())
    		return true;
    	else
    		return false;
    }
    
}