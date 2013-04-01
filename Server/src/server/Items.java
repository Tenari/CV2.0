package server;
/**
 * @(#)Items.java
 *
 *
 * @author Daniel Zapata
 * @version 1.00 2009/10/6
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import java.applet.*;
public class Items {
	
	public int weight;
	public int bonus;
	public String type;
	public String name;
	public String style;
	boolean equipped;
    public Items( int b, int c, String d, String e, String f ) 
    {
    	weight = b;
    	bonus = c;
    	type = d;
		name = e;
		style =f;
		equipped=false;
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
    public String getStyle()
    {
    	return style;
    }
	public String getName()
	{
		return name;
	}
	public boolean getEquipped()
	{
		return equipped;
	}
   
    public void setWeight(int b)
    {
    	weight = b;
    }
    public void setBonus(int c)
    {
    	bonus = c;
    }
    public void setType(String d)
    {
    	type = d;
    }
    public void setStyle(String d)
    {
    	style = d;
    }
	public void setName(String e)
	{
		name = e;
	}
	public void setEquipped(boolean e)
	{
		equipped = e;
	}
}