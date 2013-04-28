/**
 * Canvas on which the world is drawn. Utilizes the tiled message format.
 */
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

public class TiledWorldCanvas extends Canvas implements KeyListener {
    
    // Abstracted Constants
    final int bufferSideLength      =   (32*11);    // The size of one side of the buffer: 32px * 11 tiles
    final String drawWorldMessageCode=   "v";
    final int numXTiles             =   11;
    final int numYTiles             =   11;
    final int tileDimension         =   32;
    final long waitSpeed            =   222;         // The time (in milliseconds) it takes before another move can be sent to the server.
    
    // Internally available 'tools'
    String serverMessageToParseAndPaint = "";
    Scanner scan;
    ClientJApplet client;
    long startTime;
    
    // Declare variables for tile painting.
    Image[] images = new Image[24];
    int tileCode;
    int secondNumber;
    int classCode;
    int sureNegative1;
    
    public TiledWorldCanvas(ClientJApplet c){
        
        startTime=System.currentTimeMillis();           // Intialize the startTime counter.
        client = c;
        setVisible(true);                           // Make Canvas visible.
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
        if(firstWord.equals(drawWorldMessageCode)){
            // Iteratate left to right, then top to bottom through all the tiles we are supposed to be drawing.
            for(int i=0; i<numYTiles; i++){
                for(int j=0; j<numXTiles; j++){
                    
                    boolean hasOrganism = parseToNegative1();
                    paintTile(bufferGraphics, tileCode, j, i);                  // Paint the background tile.
                    if (hasOrganism){
                        paintOrganism(bufferGraphics, secondNumber, classCode, j, i);// Paint the organism on top of the tile.
                    }
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
        
    }
    
    Image getImage(int tileNum){
        switch(tileNum){
            case 0:
                if(images[0] == null){
                    images[0] = client.getImage(client.getCodeBase(), ("" + 0 + ".png"));
                }
                return images[0];
            case 1:
                if(images[1] == null){
                    images[1] = client.getImage(client.getCodeBase(), ("" + 1 + ".png"));
                }
                return images[1];
            case 3:
                if(images[3] == null){
                    images[3] = client.getImage(client.getCodeBase(), ("" + 3 + ".png"));
                }
                return images[3];
            case 4:
                if(images[4] == null){
                    images[4] = client.getImage(client.getCodeBase(), ("" + 4 + ".png"));
                }
                return images[4];
            case 10:
            case 11:
                if(images[10] == null){
                    images[10] = client.getImage(client.getCodeBase(), ("" + 10 + ".png"));
                }
                return images[10];
            default:
                if(images[0] == null){
                    images[0] = client.getImage(client.getCodeBase(), ("" + 0 + ".png"));
                }
                return images[0];
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
