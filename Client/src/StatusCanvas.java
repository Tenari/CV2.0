/**
 * StatusCanvas is a Canvas, on which the basic health, energy, and stats are
 * displayed.
 * 
 * It implements KeyListener so that the player can change preferences.
 * 
 * 
 * @author Daniel Zapata | djz24
 */

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

class StatusCanvas extends Canvas implements KeyListener {
	Image head;
	Image head1;
	Image head2;
	Image arms;
	Image arms1;
	Image arms2;
	Image torso;
	Image torso1;
	Image torso2;
	Image legs;
	Image legs1;
	Image legs2;
        
        Frame frame = new Frame();
        Font test = new Font("Arial",Font.BOLD , 12);

        Button status = new Button("Main");
        Button inventory = new Button("Inventory");
        Button resources = new Button("Resources");
        
        ArrayList <Image> items = new ArrayList<>();
	int headHp;
	int armsHp;
	int torsoHp;
	int legsHp;
        int costToMove;
        int weight;
        int energy; 
        int money;
        int strength;
        int agility;
        int handToHand;
        int smallBlade;
        int largeBlade;
        int axe;
        int polearm;
        int bow;
        int throwing;
        int intimidation;
        int diplomacy;
        int endurance;
        int hiding;
	double energyHp;
	double energyMax=10000.0;
	double energyPercent;
	ClientJApplet client;

        
	public StatusCanvas (Image[] arr, ClientJApplet c)
	{
		client=c;

		head=arr[0];
 		head1=arr[1];
 		head2=arr[2];
 		arms=arr[3];
 		arms1=arr[4];
 		arms2=arr[5];
 		torso=arr[6];
 		torso1=arr[7];
 		torso2=arr[8];
 		legs=arr[9];
 		legs1=arr[10];
 		legs2=arr[11];
		headHp=25;

		this.addKeyListener(this);
		

		setVisible(true);
	}
        /**
        * This method sets the amount of headHp the user has
        * @param x - a positive integer
        */
	public void setHeadHp(int x)
	{
		headHp=x;
	}
        /**
        * This method sets the amount of armsHp the user has
        * @param x - a positive integer
        */
	public void setArmsHp(int x)
	{
		armsHp=x;
	}
        /**
        * This method sets the amount of torsoHp the user has
        * @param x - a positive integer
        */
	public void setTorsoHp(int x)
	{
		torsoHp=x;
	}
        /**
        * This method sets the amount of legsHp the user has
        * @param x - a positive integer
        */
	public void setLegsHp(int x)
	{
		legsHp=x;
	}
	public void setEnergy(int x)
	{
		energyHp=(double)x;
		repaint();
	}
        /**
        * This method sets the amount of costToMove the user has
        * @param x - a positive integer
        */
        public void setCostToMove(int x)
        {
            costToMove=x;
        }
        /**
        * This method sets the amount of weight the user has
        * @param x - a positive integer
        */
        public void setWeight(int x)
        {
            weight=x;
        }
        /**
        * This method sets the amount of money the user has
        * @param x - a positive integer
        */
        public void setMoney(int x)
        {
            money=x;
        }
        /**
        * This method sets the amount of strength the user has
        * @param x - a positive integer
        */
        public void setStrength(double x)
	{
		strength=(int)x;
	}
        /**
        * This method sets the amount of agility the user has
        * @param x - a positive integer
        */
        public void setAgility(double x)
	{
		agility=(int)x;
	}
        /**
        * This method sets the amount of toughness the user has
        * @param x - a positive integer
        */     
       /* public void setToughness(int x)
	{
		toughness=x;
	}*/
        /**
        * This method sets the amount of handToHand the user has
        * @param x - a positive integer
        */
        public void setHandToHand(double x)
	{
		handToHand=(int)x;
	}
        /**
        * This method sets the amount of smallBlade the user has
        * @param x - a positive integer
        */
        public void setSmallBlade(double x)
	{
		smallBlade=(int)x;
	}
        /**
        * This method sets the amount of largeBlade the user has
        * @param a positive integer
        */
        public void setLargeBlade(double x)
	{
		largeBlade=(int)x;
	}
        /**
        * This method sets the amount of axe the user has
        * @param x - a positive integer
        */
        public void setAxe(double x)
	{
		axe=(int)x;
	}
        /**
        * This method sets the amount of polearm the user has
        * @param x - a positive integer
        */
        public void setPolearm(double x)
	{
		polearm=(int)x;
	}
        /**
        * This method sets the amount of throwing the user has
        * @param x - a positive integer
        */
        public void setThrowing(double x)
	{
		throwing=(int)x;
	}
        /**
        * This method sets the amount of bow the user has
        * @param x - a positive integer
        */
        public void setBow(double x)
	{
		bow=(int)x;
	}
        /**
        * This method sets the amount of intimidation the user has
        * @param x - a positive integer
        */
        public void setIntimidation(double x)
	{
		intimidation=(int)x;
	}
        /**
        * This method sets the amount of diplomacy the user has
        * @param x - a positive integer
        */
        public void setDiplomacy(double x)
	{
		diplomacy=(int)x;
	}
        /**
        * This method sets the amount of endurance the user has
        * @param x - a positive integer
        */
        public void setEndurance(double x)
	{
		endurance=(int)x;
	}
        /**
        * This method sets the amount of hiding the user has
        * @param x - a positive integer
        */
        public void setHiding(double x)
	{
		hiding=(int)x;
	}

    @Override
        /**
         * This method paints the initial status canvas
         * @param window - a Graphics object
         */
	public void paint(Graphics window )
	{
		window.setColor(Color.BLACK);
		if(headHp<=0)
		{
			window.drawImage(head2,25,3,46,145,null);
		}
		else if(headHp<=12)
		{
			window.drawImage(head1,25,3,46,145,null);
		}
		else if(headHp>12)
		{
			window.drawImage(head,25,3,46,145,null);
		}
		if(armsHp<=0)
		{
			window.drawImage(arms2,25,3,46,145,null);
		}
		else if(armsHp<=12)
		{
			window.drawImage(arms1,25,3,46,145,null);
		}
		else if(armsHp>12)
		{
			window.drawImage(arms,25,3,46,145,null);
		}
		if(torsoHp<=0)
		{
			window.drawImage(torso2,25,3,46,145,null );
		}
		else if(torsoHp<=12)
		{
			window.drawImage(torso1,25,3,46,145,null );
		}
		else if(torsoHp>12)
		{
			window.drawImage(torso,25,3,46,145,null );
		}
		if(legsHp<=0)
		{
			window.drawImage(legs2,25,3,46,145,null);
		}
		else if(legsHp<=12)
		{
			window.drawImage(legs1,25,3,46,145,null);
		}
		else if(legsHp>12)
		{
			window.drawImage(legs,25,3,46,145,null);
		}
                window.setFont(test);
		window.drawString("Head: "+headHp,115,12);
		window.drawString("Arms: "+armsHp,115,42);
		window.drawString("Torso: "+torsoHp,115,73);
		window.drawString("Legs: "+legsHp,115,103);
                window.drawString("Cost to move: "+costToMove, 115,134);
                window.drawString("Weight: "+weight, 115 , 164);
		//window.drawImage(character,others.get(h)*32+64,others.get(h+1)*32+64,32,32,null);
		energyPercent= Math.ceil((energyHp/energyMax) * 215);
                energy =(int)energyPercent;
		window.drawRect(5,170,215, 10);
                window.setColor(Color.BLUE);
                window.fillRect(5, 170, energy, 10);
                window.setColor(Color.BLACK);
                window.drawString("Energy: " + energyHp, 10, 195);
                window.drawString("Money: " + money, 10, 209                             );
                window.drawLine(0 ,240, 230, 240);
                window.drawString("AttAverage: " + strength, 10 ,274);
                window.drawString("DefAverage: " + agility, 10, 288);
                //window.drawString("toughness " + toughness, 10, 302);
                window.drawString("Intimidation " + intimidation, 10, 316);
                window.drawString("Diplomacy " + diplomacy, 10, 330);
                window.drawString("Hiding " + hiding, 10, 344);
                window.drawString("Endurance " + endurance, 10, 358);
                window.drawString("Hand To Hand: " + handToHand, 115, 274);
                window.drawString("Small Blade: " + smallBlade, 115, 288);
                window.drawString("Large Blade: " + largeBlade, 115, 302);
                window.drawString("Axe: " + axe, 115, 316);
                window.drawString("Polearm: " + polearm, 115, 330);
                window.drawString("Bow: " + bow, 115, 344);
                window.drawString("throwing: " + throwing, 115, 358);
                
	}
        
    @Override
    public void update(Graphics g) 
    {
            Graphics offgc;
            Image offscreen = null;

            // create the offscreen buffer and associated Graphics
            offscreen = createImage(230, 400);
            offgc = offscreen.getGraphics();
            // clear the exposed area
            offgc.setColor(getBackground());
            offgc.fillRect(0, 0, 230, 400);
            offgc.setColor(getForeground());
            // do normal redraw
            paint(offgc);
            // transfer offscreen to window
            g.drawImage(offscreen, 0, 0, this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}

}