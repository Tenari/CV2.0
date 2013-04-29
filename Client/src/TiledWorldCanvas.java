/**
 * Canvas on which the world is drawn. Utilizes the tiled message format.
 */
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Scanner;

public class TiledWorldCanvas extends Canvas implements KeyListener {
    
    // Abstracted Constants
    final int bufferSideLength      =   (32*11);    // The size of one side of the buffer: 32px * 11 tiles
    final String drawFullWorldMessageCode=  "v";
    final String drawNewTopRowMessageCode=  "n";
    final int numXTiles             =   11;
    final int numYTiles             =   11;
    final int tileDimension         =   32;
    final long waitSpeed            =   222;         // The time (in milliseconds) it takes before another move can be sent to the server.
    
    // Internally available 'tools'
    String serverMessageToParseAndPaint = "";
    Scanner scan;
    ClientJApplet client;
    long startTime;
    int[][] mapMem = new int[numYTiles][numXTiles];
    ArrayList<ClientOrganismMapInfo> orgMem = new ArrayList<>();
    
    // Declare variables for tile painting.
    Image[] images = new Image[24];             // Total number of images in the game
    int tileCode;
    int secondNumber;
    int classCode;
    int sureNegative1;
    
    public TiledWorldCanvas(ClientJApplet c){
        
        startTime=System.currentTimeMillis();           // Intialize the startTime counter.
        client = c;
        setVisible(true);                           // Make Canvas visible.
        this.addKeyListener(this);// Utilize the implemented KeyListener.
    }
    
    @Override 
    public void update(Graphics g) {
        // Create Local variable needed for Double Buffering to work.
        Graphics offscreenGraphicsContainer;
        Image offscreenImage = null;

        // Create the offscreen buffer and associated Graphics
        offscreenImage = createImage(bufferSideLength, bufferSideLength);       // Make the image blank and square
        offscreenGraphicsContainer = offscreenImage.getGraphics();              // Use the graphics element from the blank image we just made.
        
        // Clear the exposed area.
        offscreenGraphicsContainer.setColor(getBackground());
        offscreenGraphicsContainer.fillRect(0, 0, bufferSideLength, bufferSideLength);
        offscreenGraphicsContainer.setColor(getForeground());
        
        // Do the actual painting of the canvas.
        paint(offscreenGraphicsContainer);
        
        // And, finally, transfer the offscreen image to the visible window.
        g.drawImage(offscreenImage, 0, 0, this);
    }
    
    @Override
    public void paint( Graphics bufferGraphics ){
        // Check the first word of the message...
        scan = new Scanner(serverMessageToParseAndPaint);
        String firstWord = scan.next();
        
        // ...and decide what to do.
        if(firstWord.equals(drawFullWorldMessageCode)){
            // Iteratate left to right, then top to bottom through all the tiles we are supposed to be drawing.
            for(int i=0; i<numYTiles; i++){
                for(int j=0; j<numXTiles; j++){
                    
                    boolean hasOrganism = parseToNegative1();
                    paintTile(bufferGraphics, tileCode, j, i);                  // Paint the background tile.
                    
                    mapMem[i][j] = tileCode;
                    
                    if (hasOrganism){
                        paintOrganism(bufferGraphics, secondNumber, classCode, j, i);// Paint the organism on top of the tile.
                        // record organisms memory stuff here
                    }
                }
            }
        }
        else if(firstWord.equals(drawNewTopRowMessageCode)){
            
            // Loops to paint all but the top row
            for(int i=0; i<numYTiles; i++){
                for(int j=0; j<numXTiles; j++){
                }
            }
            
            // Loop to paint the new top row
            for(int r=0; r<numXTiles; r++){
                boolean hasOrganism = parseToNegative1();
                    paintTile(bufferGraphics, tileCode, r, 0);                  // Paint the background tile.
                    
                    mapMem[0][r] = tileCode;
                    
                    if (hasOrganism){
                        paintOrganism(bufferGraphics, secondNumber, classCode, r, 0);// Paint the organism on top of the tile.
                        // record organisms memory stuff here
                    }
            }
        }
    }
    
    void setText(String fullServerMessage) {
        serverMessageToParseAndPaint = fullServerMessage;
        repaint();
    }
    
    boolean parseToNegative1(){
        // Scan the first two tokens. We know they are ints.
        tileCode = scan.nextInt();
        secondNumber = scan.nextInt();
        
        // secondNumber is either a '-1' or a directionCode for organism painting.
        // If it's NOT a -1, we need to scan in the classCode and the -1 delimiter.
        if(secondNumber != -1){
            classCode = scan.nextInt();
            sureNegative1 = scan.nextInt();
            return true;        // Have to return that we did indeed find an Organism's information.
        }
        
        // Otherwise, return that we didn not find an organism's info.
        return false;
    }
    
    void paintTile(Graphics gr, int tile, int xCoord, int yCoord){
        Image toDraw = getImage(tile);
        gr.drawImage(toDraw, (xCoord*tileDimension), (yCoord*tileDimension), tileDimension, tileDimension, null);
    }
    
    void paintOrganism(Graphics gr, int directionCode, int orgClass,int xCoord, int yCoord){
        Image toDraw = getImage(orgClass+directionCode);
        gr.drawImage(toDraw, (xCoord*tileDimension), (yCoord*tileDimension), tileDimension, tileDimension, null);
    }
    
    Image getImage(int tileNum){
        switch(tileNum){
            case 0:
                return getImageLogic(0);            // Blank, off-map
            case 1:
                return getImageLogic(1);            // Tree
            case 3:
                return getImageLogic(3);            // Road
            case 4:
                return getImageLogic(4);            // Grass
            case 10:
            case 11:
                return getImageLogic(10);           // Door
            case 21:
                return getImageLogic(21);           // Roman sword North
            case 22:
                return getImageLogic(22);           // Roman sword South
            case 23:
                return getImageLogic(23);           // Roman sword East
            case 24:
                return getImageLogic(24);           // Roman sword West
            default:
                return getImageLogic(0);            // Blank, off-map
        }
    }
    
    Image getImageLogic(int x){
        if(images[x] == null){
            images[x] = client.getImage(client.getCodeBase(), ("" + x + ".png"));
        }
        return images[x];
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

    @Override
    public void keyTyped(KeyEvent e) {}

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

    @Override
    public void keyReleased(KeyEvent e) {}
}
