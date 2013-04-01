/**
 * CombatCanvas is a Canvas, on which the information relevant to combat is 
 * drawn.
 * 
 * It implements MouseListener so that the player can change combat preferences.
 * 
 * 
 * @author Daniel Zapata | djz24
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class CombatCanvas extends Canvas implements MouseListener{
    
    private ClientJApplet client;           // Pointer to parent JApplet.
    
    // Global Feild Images
    private final Image head;
    private final Image head1;
    private final Image head2;
    private final Image arms;
    private final Image arms1;
    private final Image arms2;
    private final Image torso;
    private final Image torso1;
    private final Image torso2;
    private final Image legs;
    private final Image legs1;
    private final Image legs2;
    private final Image bang;
    
    // HP counters
    private int headHp;
    private int armsHp;
    private int torsoHp;
    private int legsHp;

    // Hit? statuses per body part
    private boolean headBang;
    private boolean armsBang;
    private boolean legsBang;
    private boolean torsoBang;
    private int bangCount;
    private int bangMax = 5;
    
    // Detailed description texts
    private String aimText;
    private String fightTextA;
    private String fightTextB;
    
    // Bool is true when not fighting, false otherwise.
    private boolean nofight;

public CombatCanvas (Image[] arr, ClientJApplet c){
    client = c;         // Setup global pointer;
    
    bangCount=0;
    
    // Put imgs from parameter 'arr' into global variables.
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
    bang=arr[12];
    
    // Set all bang statuses to false (not hit)
    headBang = false;
    torsoBang= false;
    legsBang = false;
    armsBang = false;
    
    // Initialize blank, because no data has been read.
    aimText=" ";
    fightTextA=" ";
    fightTextB=" ";
    
    this.addMouseListener(this);        // Make the mouseListener work.
		
    setVisible(true);                   // Make the canvas visible.
}
    
@Override 
public void update(Graphics g) {
    
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
public void paint( Graphics window ) {
    
    window.setColor(Color.BLACK);
    window.drawString("Combat Information and Your Opponent's Health",555-532,390);
    
    if(!nofight) {

        // Head image drawing
        if(headHp<=0) {
            window.drawImage(head2,40,3,46,145,null);
        }
        else if(headHp<=12) {
            window.drawImage(head1,40,3,46,145,null);
        }
        else if(headHp>12) {
            window.drawImage(head,40,3,46,145,null);
        }
        
        // Arms image drawing
        if(armsHp<=0) {
            window.drawImage(arms2,40,3,46,145,null);
        }
        else if(armsHp<=12) {
            window.drawImage(arms1,40,3,46,145,null);
        }
        else if(armsHp>12) {
            window.drawImage(arms,40,3,46,145,null);
        }
        
        // Legs drawing
        if(legsHp<=0) {
            window.drawImage(legs2,40,3,46,145,null);
        }
        else if(legsHp<=12) {
            window.drawImage(legs1,40,3,46,145,null);
        }
        else if(legsHp>12) {
            window.drawImage(legs,40,3,46,145,null);
        }
        
        // Torso drawing
        if(torsoHp<=0) {
            window.drawImage(torso2,40,3,46,145,null );
        }
        else if(torsoHp==12) {
            window.drawImage(torso1,40,3,46,145,null );
        }
        else if(torsoHp>12) {
            window.drawImage(torso,40,3,46,145,null );
        }

        window.drawString(aimText,5,179);
        window.drawString(fightTextA,5,222);
        window.drawString(fightTextB,5,252);
        window.drawString("Head: "+headHp,660-532,22);
        window.drawString("Arms: "+armsHp,660-532,56);
        window.drawString("Torso: "+torsoHp,660-532,75);
        window.drawString("Legs: "+legsHp,660-532,113);

        if(headBang) {
            window.drawImage(bang,587-532,3,36,30,null);
            bangCount++;
            if(bangCount >= bangMax) {
                bangCount=0;
                headBang=false;
            }
        }
        if(armsBang) {
            window.drawImage(bang,564-532,32,86,58,null);
            bangCount++;
            if(bangCount >= bangMax)
            {
                bangCount=0;
                armsBang=false;
            }
        }
        if(legsBang) {
            window.drawImage(bang,578-532,89,55,58,null);
            bangCount++;
            if(bangCount >= bangMax)
            {
                bangCount=0;
                legsBang=false;
            }
        }
        if(torsoBang) {
            window.drawImage(bang,49,37,42,57,null );
            bangCount++;
            if(bangCount >= bangMax)
            {
                bangCount=0;
                torsoBang=false;
            }
        }
    }
}

public void goBlank(boolean a)
{
    nofight=a;
}

//this method checks for clicks on the body parts, and sets the aim spot accordingly
@Override
public void mouseClicked(MouseEvent e) {
    //the ifs check for an x greater than the leftmost edge of the box AND
    //lesser than the rightmost edge of the box
    //they also check the same for the y direction, and if all checks are met,
    //send a message to the server saying the new aim
    int xtemp=e.getX();
    int ytemp=e.getY();
    //the aim set for the head
    if((xtemp>=54 && xtemp<=70)&&(ytemp>=4 && ytemp<=14)){
        client.sendMessage("head");
    }
    //the aimset for the arms left arm
    else if((xtemp >= 38 && xtemp<= 48) && (ytemp >= 31 && ytemp <= 84))
    {
        client.sendMessage("arms");
    }
    //the aimset for the arms right arm
    else if((xtemp >= 76 && xtemp <= 86) && (ytemp >= 31 && ytemp <= 84))
    {
        client.sendMessage("arms");
    }
    //the aimset for the torso
    else if((xtemp >= 48 && xtemp <= 76) && (ytemp >= 31 && ytemp <= 69))
    {
        client.sendMessage("torso");
    }
    //the aimset for the legs
    else if((xtemp >= 48 && xtemp <= 76) && (ytemp >= 70 && ytemp <= 149))
    {
        client.sendMessage("legs");
    }
}

/*
 * Necessary overrides for 'implements' keyword on MouseListener.
 * 
 * They all do nothing, intentionally.
 */
@Override
public void mousePressed(MouseEvent e) {
}
@Override
public void mouseReleased(MouseEvent e) {
}
@Override
public void mouseEntered(MouseEvent e) {
}
@Override
public void mouseExited(MouseEvent e) {
}

public void setHeadHp(int x) {
    if(headHp!=x) {
        headHp=x;           // Change the head HP counter
        headBang=true;      // Head was hit, so flip it's bang status.
    }
    goBlank(false);
}

public void setArmsHp(int x) {
    if(armsHp != x) {
        armsHp = x;
        armsBang = true;
    }
}

public void setTorsoHp(int x) {
    if(torsoHp != x) {
        torsoHp = x;
        torsoBang = true;
    }
}

public void setLegsHp(int x) {
    if(legsHp != x) {
        legsHp = x;
        legsBang = true;
    }
}
public void setAimText(String te) {
        aimText=te;
}
public void setFightTextA(String te)
{
    fightTextA=te;
    for(int o=0;o<fightTextA.length()-2;o++)
    {
        if(fightTextA.charAt(o)=='|')
            if(fightTextA.charAt(o+1)=='|')
            {
                setFightTextB(fightTextA.substring(o+1));
                fightTextA=fightTextA.substring(0,o);
            }
    }
}
public void setFightTextB(String te)
{
        fightTextB=te;
        repaint();
}
    
}
