/**
 * @(#)Trade.java
 *
 *
 * @author 
 * @version 1.00 2010/2/16
 */

import java.awt.*;
import java.util.*;

public class TradeCanvas extends Canvas {
	ArrayList<String> opponentsOffer = new ArrayList<>();
	String tradeIsGood;
    public TradeCanvas() 
    {
    	tradeIsGood="a no-go.";
    }
    
    public void addOppOffer(String s)
    {
    	opponentsOffer.clear();
    	Scanner scan =new Scanner(s);
    	tradeIsGood=scan.next();
    	while(scan.hasNext())
    	{
    		opponentsOffer.add(scan.next());
    	}
    	
    	if(tradeIsGood.equals("true"))
    	{
    		tradeIsGood="good to go!";
    	}
    	else
    	{
    		tradeIsGood="bad, you cheap-o!";
    	}
    }
    
    @Override
    public void paint( Graphics window )
	{
		window.setColor(Color.BLACK);
		window.drawString("Other Player's Offer",15,35);
		window.drawString("Deal is "+tradeIsGood,15,19);
		
		for(int i=0;i<opponentsOffer.size();i++)
		{
			window.drawString(opponentsOffer.get(i), 20,(20*i)+50 );
		}
		
	}
}