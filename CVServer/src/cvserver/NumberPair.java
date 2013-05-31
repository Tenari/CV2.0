package cvserver;
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
    
    public NumberPair(int n1, int n2) {
    	num1=n1;
    	num2=n2;
    }
    
    public int getNumOne() {
    	return num1;
    }
    public int getNumTwo() {
    	return num2;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
    	if (num1==((NumberPair)obj).getNumOne() && num2==((NumberPair)obj).getNumTwo()) {
            return true;
        } else {
            return false;
        }
    }
}