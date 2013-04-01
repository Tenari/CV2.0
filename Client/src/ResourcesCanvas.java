/**
 * ResourcesCanvas is a Canvas, on which the various resources carried are displayed.
 * 
 * 
 * @author Daniel Zapata | djz24
 */

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

class ResourcesCanvas extends Canvas {
    
    int leather;
    int cloth;
    int tools;
    int wheat;
    int water;
    int meat;
    int stone;
    int wood;
    int metal;
    int buildingMaterial;
    
    Image leatherPic;
    Image clothPic;
    Image toolsPic;
    Image wheatPic;
    Image waterPic;
    Image meatPic;
    Image stonePic;
    Image woodPic;
    Image metalPic; 
    Image buildingMaterialPic;

    public ResourcesCanvas() {
    }
    /**
     * This method sets the amount of leather the user has
     * @param x - a positive integer
     */
    public void setLeather(int x)
	{
		leather=x;
	}
    /**
     * This method sets the amount of cloth the user has
     * @param x - a positive integer
     */
    public void setCloth(int x)
	{
		cloth=x;
	}
    /**
     * This method sets the amount of cloth the user has
     * @param x - a positive integer
     */
    public void setTools(int x)
	{
		tools=x;
	}
    /**
     * This method sets the amount of wheat the user has
     * @param x - a positive integer
     */
    public void setWheat(int x)
	{
		wheat=x;
	}
    /**
     * This method sets the amount of water the user has
     * @param x - a positive integer
     */
    public void setWater(int x)
	{
		water=x;
	}
    /**
     * This method sets the amount of meat the user has
     * @param x - a positive integer
     */
    public void setMeat(int x)
	{
		meat=x;
	}
    /**
     * This method sets the amount of stone the user has
     * @param x - a positive integer
     */
    public void setStone(int x)
	{
		stone=x;
	}
    /**
     * This method sets the amount of wood the user has
     * @param x - a positive integer
     */
    public void setWood(int x)
	{
		wood=x;
	}
    /**
     * This method sets the amount of metal the user has
     * @param x - a positive integer
     */
    public void setMetal(int x)
	{
		metal=x;
	}
    /**
     * This method sets the amount of buildingMaterial the user has
     * @param x - a positive integer
     */
    public void setBuildingMaterial(int x)
	{
		buildingMaterial=x;
		repaint();
	}
    /**
     * This method paints the resources screen of the status canvas
     * @param window - a Graphics object
     */
    @Override
    public void paint(Graphics window)
    {
        int x = 10;
        if(leather != 0)
        {
           window.drawImage( leatherPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + leather + " units", 30 , x);
        }
        if(cloth != 0)
        {
           window.drawImage( clothPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + cloth + " units", 30 , x);
        }
        if( tools != 0 )
        {
           window.drawImage( toolsPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + tools + " units", 30 , x);
        }
        if( wheat != 0 )
        {
           window.drawImage( wheatPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + wheat + " units", 30 , x);
        }
        if( meat != 0 )
        {
           window.drawImage( meatPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + meat + " units", 30 , x );
        }
        if( stone != 0 )
        {
           window.drawImage( stonePic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + stone + " units", 30 , x);
        }
        if( wood != 0 )
        {
           window.drawImage( woodPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + wood + " units", 30 , x);
        }
        if( metal != 0 )
        {
           window.drawImage( metalPic, 5 , x, 20 , 20, null  );
           x = x + 30;
           window.drawString("You have " + metal + " units", 30 , x);
        }
        if( buildingMaterial != 0 )
        {
           window.drawImage( buildingMaterialPic, 5 , x, 20 , 20 , null );
           x = x + 30;
           window.drawString("You have " + buildingMaterial + " units", 30 , x);
        }
        
        
    }
}