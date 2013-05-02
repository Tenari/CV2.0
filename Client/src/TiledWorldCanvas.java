/**
 * Canvas on which the world is drawn. Utilizes the tiled message format.
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
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
    final String drawNewBottomRowMessageCode="s";
    final String drawNewLeftColMessageCode= "w";
    final String drawNewRightColMessageCode="e";
    final String drawOrganismsMsgCode   =   "o";
    final int numXTiles             =   11;
    final int numYTiles             =   11;
    final int tileDimension         =   32;
    final long waitSpeed            =   222;         // The time (in milliseconds) it takes before another move can be sent to the server.
    
    // Internally available 'tools'
    String          serverMessageToParseAndPaint    = "";
    Scanner         scan;
    ClientJApplet   client;
    long            startTime;
    ArrayList<ClientOrganismMapInfo> orgMem         = new ArrayList<>();
    int[][]         mapMem                          = new int[numYTiles][numXTiles];
    
    // Double Buffering stuff
    Dimension       offscreenDimension;
    Graphics        offscreenGraphicsContainer;
    Image           offscreenImage;
    
    // Declare variables for tile painting.
    Image[] images = new Image[200];                // Total number of images in the game
    int tileCode;
    int secondNumber;
    int classCode;
    int sureNegative1;
    
    public TiledWorldCanvas(ClientJApplet c){
        startTime=System.currentTimeMillis();           // Intialize the startTime counter.
        client = c;
        setVisible(true);                               // Make Canvas visible.
        this.addKeyListener(this);                      // Utilize the implemented KeyListener.
    }
    
    @Override 
    public void update(Graphics g) {
        Dimension d = getSize();
        
        // Create the offscreen graphics context
        if ((offscreenGraphicsContainer == null)
	 || (d.width != offscreenDimension.width)
	 || (d.height != offscreenDimension.height)) {
	    offscreenDimension = d;
	    offscreenImage = createImage(d.width, d.height);
	    offscreenGraphicsContainer = offscreenImage.getGraphics();
	}
        // Erase the previous image
	offscreenGraphicsContainer.setColor(getBackground());
	offscreenGraphicsContainer.fillRect(0, 0, d.width, d.height);
	offscreenGraphicsContainer.setColor(Color.black);

        // Do the actual painting of the canvas.
        paintFrame(offscreenGraphicsContainer);

        // And, finally, transfer the offscreen image to the visible window.
        g.drawImage(offscreenImage, 0, 0, null);
    }
    
    /**
     * Paint a frame of the world.
     * @param g 
     */
    public void paintFrame(Graphics g){
        // Check the first word of the message...
        scan = new Scanner(serverMessageToParseAndPaint);
        String firstWord = scan.next();
        // ...and decide what to do.
        
        // First parse and store the data
        switch (firstWord) {
            
            case drawFullWorldMessageCode:
                // Clear the organims arrayList
                orgMem = new ArrayList<>();
                
                // Iteratate left to right, then top to bottom through all the tiles we are supposed to be saving.
                for(int i=0; i<numYTiles; i++){
                    for(int j=0; j<numXTiles; j++){
                        boolean hasOrganism = parseToNegative1();
                        mapMem[i][j] = tileCode;
                        if (hasOrganism){
                            // record organisms memory stuff here
                            orgMem.add(new ClientOrganismMapInfo(classCode, secondNumber, j, i));
                        }
                    }
                }
                break;
                
            case drawNewTopRowMessageCode: 
                // Loop to shift the memMap old data
                for (int i=numYTiles-1; i>0; i--){
                    mapMem[i] = mapMem[i-1];
                }
                // Loop to store the new top row
                for(int r=0; r<numXTiles; r++){
                    mapMem[0][r] = scan.nextInt();                  // 0 implies fixed top row.
                }
                break;
                
            case drawNewBottomRowMessageCode: 
                // Loop to shift the memMap old data
                for (int i=0; i<numYTiles-1; i++){
                    mapMem[i] = mapMem[i+1];
                }
                // Loop to store the new bottom row
                for(int r=0; r<numXTiles; r++){
                    mapMem[10][r] = scan.nextInt();                 // 10 implies fixed bottom row.
                }
                break;
            case drawNewRightColMessageCode:
                // Loop to shift the memMap old data
                for (int i=0; i<numYTiles; i++){
                    for(int j=0; j<numXTiles-1; j++){
                        mapMem[i][j] = mapMem[i][j+1];
                    }
                }
                // Loop to store the new rightmost Col
                for(int r=0; r<numYTiles; r++){
                    mapMem[r][10] = scan.nextInt();                 // 10 implies fixed rightmost col.
                }
                break;
            
            case drawNewLeftColMessageCode: 
                // Loop to shift the memMap old data
                for (int i=0; i<numYTiles; i++){
                    for(int j=numXTiles-1; j> 0; j--){
                        mapMem[i][j] = mapMem[i][j-1];
                    }
                }
                // Loop to store the new leftmost col
                for(int r=0; r<numXTiles; r++){
                    mapMem[r][0] = scan.nextInt();                  // 0 implies fixed leftmost col.
                }
                break;
            
            case drawOrganismsMsgCode: 
                // Clear the organims arrayList
                orgMem = new ArrayList<>();
                
                while(scan.hasNext()){
                    int direction   =   scan.nextInt();
                    classCode       =   scan.nextInt();
                    int x           =   scan.nextInt();
                    int y           =   scan.nextInt();
                    orgMem.add(new ClientOrganismMapInfo(classCode, direction, x, y));
                }
                break;
        }
        if (offscreenGraphicsContainer != null){
            // Then draw the image from saved data.
            drawImageFromData(g);
        }
    }
    
    /**
     * Paint the previous frame, if any.
     * @param g 
     */
    @Override
    public void paint( Graphics g ){
        if (offscreenImage != null) {
	    g.drawImage(offscreenImage, 0, 0, null);
	}
    }
    
    void drawImageFromData(Graphics graphics){
        // Loop through the map and draw the background tiles.
        for (int i=0; i<numYTiles; i++){
            for(int j=0; j<numXTiles; j++){
                paintTile(graphics, mapMem[i][j], j, i);
            }
        }
        
        // Loop through the organisms and draw them.
        for(ClientOrganismMapInfo i : orgMem){
            paintOrganism(graphics, i.direction, i.classCode, i.x, i.y);
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
            
            case 10:
            case 11:
                return getImageLogic(10);           // Door
            default:
                return getImageLogic(tileNum);            // Blank, off-map
        }
    }
    
    Image getImageLogic(int x){
        if(images[x] == null){
            images[x] = client.getImage(client.getCodeBase(), ("" + x + ".png"));
        }
        return images[x];
    }
    
    public void sendMessage(String msg) {
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
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
