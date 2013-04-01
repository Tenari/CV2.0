/**
 * MainWorldCanvas is a stand-alone Canvas, on which the game world is displayed.
 * 
 * This handles player movement, updating to draw enemies movement, and 
 * background rendering.
 * 
 * 
 * @author Daniel Zapata | djz24
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Scanner;

class MainWorldCanvas extends Canvas implements KeyListener{
    
    private long startTime;         // The timer variable for limiting movespeed.
    private boolean firstFrameStill;// The switch to indicate not to draw the first frame (for double buffering purposes)
    private String paintInformation;// The string containing the info about what to draw.
    private long waitSpeed = 222;   // The time (in milliseconds) it takes before another move can be sent to the server.
    
    private final int worldImageWidth = 640;
    private final int worldImageHeight= 320;
    
    private ClientJApplet client;   // Pointer to Parent JApplet.
    
    // Image Feilds.
    private Image character;        // The pointer to the current representation of the player.
    private final Image rmlsouth;
    private final Image world;
    private final Image world1;
    private final Image wildi;
    private final Image bar;
    private final Image monster;
    private final Image rmlnorth;
    private final Image rmleast;
    private final Image rmlwest;
    private final Image cmlnorth;
    private final Image cmlsouth;
    private final Image cmlwest;
    private final Image cmleast;
    
// The constructor
public MainWorldCanvas (Image[] arr, ClientJApplet c) {
    
    startTime=System.currentTimeMillis();           // Intialize the startTime counter.
    paintInformation = "";                          // Blank intialization because no information received yet.
    
    // Initialize the images used by this class.
    character=arr[0];       // Initialize to roman medium sword facing south.
    
    rmlsouth=arr[0];        // The roman medium sword facing south.
    world=arr[1];           // The roman city background image.
    world1=arr[2];          // The chinese city background image.
    wildi=arr[3];           // The wilderness background image.
    bar=arr[4];             // The bar/interior of a building background image.
    monster=arr[5];         // The generic npc monster image.
    rmlnorth=arr[6];        // The roman medium sword facing north.
    rmleast=arr[7];         // The roman medium sword facing east.
    rmlwest=arr[8];         // The roman medium sword facing west.
    cmlnorth=arr[9];        // The chinese medium sword facing north.
    cmlsouth=arr[10];       // The chinese medium sword facing south.
    cmleast=arr[11];        // The chinese medium sword facing east.
    cmlwest=arr[12];        // The chinese medium sword facing west.
    
    client = c;             // Connect the global pointer to the parameter.
    
    // Closing things.
    firstFrameStill = true; // We are still involved in the first frame of painting.
    setVisible(true);       // Make Canvas visible.
    this.addKeyListener(this);// Utilize the implemented KeyListener.
}

/**
 * Necessary override for 'implements' keyword on KeyListener.
 * 
 * Does nothing, intentionally.
 */
@Override
public void keyTyped(KeyEvent e) {}

/**
 * Listens for specific keys being held down, and sends the appropriate message 
 * to the server.
 * 
 * Series of 'if's because they could all be held down, and just cause weird 
 * things--this is a desirable option for the players.
 */
@Override
public void keyPressed(KeyEvent e) {
    if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
        sendMessage("A");
    }
    if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D') {
        sendMessage("D");
    }
    if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
        sendMessage("W");
    }
    if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
        sendMessage("S");
    }
    if (e.getKeyChar() == 'e' || e.getKeyChar() == 'E') {
        sendMessage("E");
    }
}

/**
 * Necessary override for 'implements' keyword on KeyListener.
 * 
 * Does nothing, intentionally.
 */
@Override
public void keyReleased(KeyEvent e) {}

// Still gross.
public void paint( Graphics bufferGraphics )
{
    if(!firstFrameStill) {

        Scanner scan = new Scanner(paintInformation);
        String characterDirection = scan.next();    // Checks the first word to see which image to use to draw the main character
        String teamCode = scan.next();              // Checks to see which team the main character is on
        String locationCode = scan.next();          // Get locationCode from thrid word in scan.

        setCharacterImage(characterDirection, teamCode);// Resets the player's avater img appropriatley.
        
        // Choose action based on what location Code is read in.
        switch (locationCode) {
            case "bar": {
                scan.nextInt();
                int ex=scan.nextInt();//reading in the appropriate info based on leading word
                int ey=scan.nextInt();
                ArrayList<Integer> others =new ArrayList<Integer>();
                while(scan.hasNext())
                {
                        int ghj=scan.nextInt();
                        if(ghj==99999)
                        {

                        }
                        else
                        {
                                others.add(ghj);
                        }
                }
                bufferGraphics.drawImage(bar,32,32,192,192,null);
                bufferGraphics.drawImage(character,ex*32,ey*32,32,32,null);
                for(int h=0;h<others.size();h+=2)
                {
                        bufferGraphics.drawImage(character,others.get(h)*32,others.get(h+1)*32,32,32,null);
                }
                break;
            }
            case "dead":
                bufferGraphics.setColor(Color.BLACK);
                bufferGraphics.fillRect(0,0,320,320);
                bufferGraphics.setColor(Color.RED);
                bufferGraphics.drawString("You are DEAD!!! noob",100,100);
                break;
            case "dead2":
                bufferGraphics.setColor(Color.BLACK);
                bufferGraphics.fillRect(0,0,320,320);
                bufferGraphics.setColor(Color.RED);
                bufferGraphics.drawString("You are UNCONCIOUS!!! noob",100,100);
                break;
            default:
                {
                    Image temppic;
                    if(locationCode.equals("world"))
                    {
                            temppic=world;
                    }
                    else if(locationCode.equals("wildi"))
                    {
                            temppic=wildi;
                    }
                    else
                    {
                            temppic=world1;
                    }
                    int ex=scan.nextInt();//reading in the appropriate info based on leading word
                    int ey=scan.nextInt();
                    ArrayList<Integer> others =new ArrayList<>();
                    ArrayList<Integer> monsters =new ArrayList<>();
                    boolean gogo=true;
                    while(scan.hasNext()&&gogo)
                    {
                            int ghj=scan.nextInt();
                            if(ghj==99999)
                            {
                                    gogo=false;
                            }
                            else
                            {
                                    others.add(ghj);
                            }
                    }
                    while(scan.hasNext())
                    {
                            monsters.add(scan.nextInt());
                    }
                    if(ex<4)//so taht the map is always covering the entire screen, we need this series of ifs
                    {
                            if(ey<4)
                            {
                                    bufferGraphics.drawImage(temppic,32,32,worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,ex*32,ey*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,others.get(h)*32,others.get(h+1)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,monsters.get(h)*32,monsters.get(h+1)*32,32,32,null);
                                    }
                            }
                            else if(ey>=94)
                            {
                                    bufferGraphics.drawImage(temppic,32,32-(32*90),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,ex*32,(ey-90)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,others.get(h)*32,(others.get(h+1)-90)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,monsters.get(h)*32,(monsters.get(h+1)-90)*32,32,32,null);
                                    }
                            }
                            else
                            {
                                    bufferGraphics.drawImage(temppic,32,32-((ey-4)*32),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,ex*32,(4)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,others.get(h)*32,(others.get(h+1)-ey+4)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,monsters.get(h)*32,(monsters.get(h+1)-ey+4)*32,32,32,null);
                                    }
                            }

                    }
                    else if(ex>=94)
                    {
                            if(ey<4)
                            {
                                    bufferGraphics.drawImage(temppic,(32-(32*90)),32,worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,(ex-90)*32,ey*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-90)*32,others.get(h+1)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-90)*32,monsters.get(h+1)*32,32,32,null);
                                    }
                            }
                            else if(ey>=94)
                            {
                                    bufferGraphics.drawImage(temppic,(32-(32*90)),32-(32*90),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,(ex-90)*32,(ey-90)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-90)*32,(others.get(h+1)-90)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-90)*32,(monsters.get(h+1)-90)*32,32,32,null);
                                    }
                            }
                            else
                            {
                                    bufferGraphics.drawImage(temppic,(32-(32*90)),32-((ey-4)*32),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,(ex-90)*32,(4)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-90)*32,(others.get(h+1)-ey+4)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-90)*32,(monsters.get(h+1)-ey+4)*32,32,32,null);
                                    }
                            }
                    }
                    else
                    {
                            if(ey<4)
                            {
                                    bufferGraphics.drawImage(temppic,32-((ex-4)*32),32,worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,4*32,ey*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-ex+4)*32,others.get(h+1)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-ex+4)*32,monsters.get(h+1)*32,32,32,null);
                                    }
                            }
                            else if(ey>=94)
                            {
                                    bufferGraphics.drawImage(temppic,32-((ex-4)*32),32-(32*90),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,4*32,(ey-90)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-ex+4)*32,(others.get(h+1)-90)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-ex+4)*32,(monsters.get(h+1)-90)*32,32,32,null);
                                    }
                            }
                            else
                            {
                                    bufferGraphics.drawImage(temppic,32-((ex-4)*32),32-((ey-4)*32),worldImageWidth,worldImageHeight,null);
                                    bufferGraphics.drawImage(character,4*32,(4)*32,32,32,null);
                                    for(int h=0;h<others.size();h+=2)//drawing other chars
                                    {
                                            bufferGraphics.drawImage(rmlnorth,(others.get(h)-ex+4)*32,(others.get(h+1)-ey+4)*32,32,32,null);
                                    }
                                    for(int h=0;h<monsters.size();h+=2)//drawing monsters
                                    {
                                            bufferGraphics.drawImage(monster,(monsters.get(h)-ex+4)*32,(monsters.get(h+1)-ey+4)*32,32,32,null);
                                    }
                            }
                    }
                    break;
                }
        }
    }
}

public void sendMessage(String msg)
{
    long newTime=System.currentTimeMillis();        // Get the new time.
    if((newTime-startTime)>=waitSpeed)              // If the minimum time has elapsed.
    {
        client.sendMessage(msg);                    // Send the message to the server.
        startTime=System.currentTimeMillis();       // Move the time Marker.
    }
}

void setText(String fullServerMessage) {
    paintInformation = fullServerMessage;
    repaint();
}

private void setCharacterImage(String characterDirection, String teamCode) {
    if(teamCode.equals("r")) {
        if(characterDirection.equals("n")){ character=rmlnorth;}
        else if(characterDirection.equals("e")){character = rmleast;}
        else if(characterDirection.equals("w")){character = rmlwest;}
        else{character = rmlsouth;}
    }
    else {
        if(characterDirection.equals("n")){ character=cmlnorth;}
        else if(characterDirection.equals("e")){character = cmleast;}
        else if(characterDirection.equals("w")){character = cmlwest;}
        else{character = cmlsouth;}
    }
}

@Override 
public void update(Graphics g) {
    int bufferSideLength = 320;     // The size of one side of the buffer.
    // Creat Local variable needed for Double Buffering to work.
    Graphics offscreenGraphicsContainer;
    Image offscreenImage = null;

    // Create the offscreen buffer and associated Graphics
    offscreenImage = createImage(bufferSideLength, bufferSideLength);     // Make the image blank and 320px by 320px.
    offscreenGraphicsContainer = offscreenImage.getGraphics();  // Use the graphics element from the blank image we just made.
    // clear the exposed area
    offscreenGraphicsContainer.setColor(getBackground());
    offscreenGraphicsContainer.fillRect(0, 0, bufferSideLength, bufferSideLength);
    offscreenGraphicsContainer.setColor(getForeground());
    // do normal redraw
    paint(offscreenGraphicsContainer);
    // transfer offscreen to window
    g.drawImage(offscreenImage, 0, 0, this);
    
    //We are no longer in the first frame of painting.
    firstFrameStill=false;
}

}