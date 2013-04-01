/**
 * InventoryCanvas is a Canvas, on which the inventory and equipped items are 
 * displayed.
 * 
 * It implements MouseListener so that the player can change what is equipped.
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
import java.util.ArrayList;
import java.util.Scanner;

class InventoryCanvas extends Canvas implements MouseListener{
    
    private ClientJApplet client;
    
    // Images.
    private Image armor;
    private Image weapon; 
    private Image sheild;
    private Image belt;
    private Image boot;
    
    // Items variables.
    private Items[][] inven = new Items[5][5];
    private Items[] equipped= new Items[6];
    
    int a;
    int b;
    int x;
    int y;
    int z=0;
    
    ArrayList<Items> mycrap = new ArrayList<>();
    String type;
    Items moving;
    
public InventoryCanvas (Image[] imgs, ClientJApplet c) {
        client = c;
        
        // Initialize images from array.
        armor   =   imgs[0];
        weapon  =   imgs[1];
        sheild  =   imgs[2];
        belt    =   imgs[3];
        boot    =   imgs[4];
        
        this.addMouseListener(this);
    }
    
@Override
public void paint(Graphics window) {
    window.setColor(Color.WHITE);
    window.fillRect(0, 0, 230, 370);
    window.setColor(Color.BLACK);
    window.drawRect(0,0,230,173);
    window.drawLine(115,0,115,173);
    window.drawRect(39,58,38,58); //Spot for armor
            if(equipped[0]!=null){window.drawImage(equipped[0].getPic(), 42, 61, 32,52,null);} 
    window.drawRect(5,68,29,48);//Left arm
            if(equipped[1]!=null){window.drawImage(equipped[1].getPic(), 8, 71, 23, 42,null );}
    window.drawRect(82,68 ,29,48);//Right arm
            if(equipped[2]!=null){window.drawImage(equipped[2].getPic(), 85,71, 23,42,null);}
    window.drawRect(39,117,38,15);//Belt
            if(equipped[3]!=null){window.drawImage(equipped[3].getPic(),42,120,32,10,null);}
    window.drawRect(39,135,38,30);//Boots
            if(equipped[4]!=null){window.drawImage(equipped[4].getPic(),42,138,32,24,null);}
    //bottom grid for items
    window.drawRect(5,180,220,180);
    window.drawLine(5, 216, 225, 216);
    window.drawLine(5, 252, 225, 252);
    window.drawLine(5, 288, 225, 288);
    window.drawLine(5, 324, 225, 324);
    window.drawLine(49, 180, 49, 360);
    window.drawLine(93, 180, 93, 360);
    window.drawLine(137, 180, 137, 360);
    window.drawLine(181, 180, 181, 360);
    a=7;
    for(x=0; x<5; x++)
    {
            b=182;
            for(y=0; y<5; y++)
            {
                    if(!(inven[x][y]==null))
                    {
                            window.drawImage(inven[x][y].getPic(), a, b, 40 , 32, null);
                    }

                    a+=36;
            }
            b+=44;
    }
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
public void mouseClicked(MouseEvent e) {
    int xCord = e.getX();
    int yCord = e.getY();
    System.out.println(" "+xCord+yCord);
    moveItem(xCord , yCord);
}

@Override
public void mouseExited(MouseEvent e) {}

@Override
public void mouseEntered(MouseEvent e) {}

@Override
public void mouseReleased(MouseEvent e) {}

@Override
public void mousePressed(MouseEvent e) {}

public void moveItem(int xCord , int yCord) {
    
    if (xCord<49) {
            x=0;
    }
    else if(xCord<93) {
            x=1;
    }
    else if(xCord<137) {
            x=2;
    }
    else if(xCord<181) {
            x=3;
    }
    else if(xCord<225) {
            x=4;
    }
    else {
            x=0;
    }
    
    if(yCord<180+36) {
            y=0;
    }
    else if(yCord<216+36) {
            y=1;
    }
    else if(yCord<252+36) {
            y=2;
    }
    else if(yCord<288+36) {
            y=3;
    }
    else if(yCord<324+36) {
            y=4;
    }
    else {
            y=0;
    }

    if(inven[y][x]!=null) {
            moving = inven[y][x];
            type =moving.getType();	   
            System.out.println("equip "+moving.getPlace());
            client.sendMessage("equip "+moving.getPlace());
    }
}
public void clearInventory()
{
        inven = new Items[5][5];
}
public void update(String inv)
{
        clearInventory();
        Scanner scan =new Scanner(inv);
        scan.next();
        int num= scan.nextInt();

        for(int i=0;i<num;i++)
        {
                int firstInt=scan.nextInt();
                int secondInt=scan.nextInt();
                String c=scan.next();
                String d=scan.next();
                String f=scan.next();
                int g=scan.nextInt();
                if(c.equals("armor"))
                {
                        mycrap.add(new Items(armor,firstInt,secondInt,c,d,f,g));
                }
                else if(c.equals("weapon"))
                {
                        mycrap.add(new Items(weapon,firstInt,secondInt,c,d,f,g));
                }
                else if(c.equals("sheild"))
                {
                        mycrap.add(new Items(sheild,firstInt,secondInt,c,d,f,g));
                }
                else if(c.equals("belt"))
                {
                        mycrap.add(new Items(belt,firstInt,secondInt,c,d,f,g));
                }
                else if(c.equals("boot"))
                {
                        mycrap.add(new Items(boot,firstInt,secondInt,c,d,f,g));
                }
        }

        for(int d=0;d<mycrap.size();d++)
        {
                if(mycrap.get(d).getEquipped())
                {
                        if(mycrap.get(d).getType().equals("armor"))
                        {
                                equipped[0]=mycrap.get(d);
                        }
                        else if(mycrap.get(d).getType().equals("weapon"))
                        {
                                equipped[1]=mycrap.get(d);
                        }
                        else if(mycrap.get(d).getType().equals("shield"))
                        {
                                equipped[2]=mycrap.get(d);
                        }
                        else if(mycrap.get(d).getType().equals("belt"))
                        {
                                equipped[3]=mycrap.get(d);
                        }
                        else if(mycrap.get(d).getType().equals("boots"))
                        {
                                equipped[4]=mycrap.get(d);
                        }
                }
                else
                {
                        if(d<5)
                        {
                                inven[0][d]=mycrap.get(d);
                        }
                        else if(d<10)
                        {
                                inven[1][d-5]=mycrap.get(d);
                        }
                        else if(d<15)
                        {
                                inven[2][d-10]=mycrap.get(d);
                        }
                        else if(d<20)
                        {
                                inven[3][d-15]=mycrap.get(d);
                        }
                        else 
                        {
                                inven[4][d-20]=mycrap.get(d);
                        }
                }


        }
        mycrap.clear();
        repaint();
}

public String[] getMycrap()
{
    int count=0;
    ArrayList<String> happy=new ArrayList<>();
    
    for(int i=0;i<5;i++) {
        for(int j=0;j<5;j++) {
            if(inven[i][j]!=null) {
                    happy.add(inven[i][j].getName());
                    count++;
            }
        }
    }
    
    String[] arr = new String[count];
    for(int g=0;g<happy.size();g++) {
        arr[g]=happy.get(g);
    }
    return arr;
}

}