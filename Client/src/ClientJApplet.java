/**
 * The primary class through which the client runs. 
 * This extends Japplet, making this an Applet as well. Applets can be embedded 
 * in webpages--which is how this one will be delivered to players.
 * Due to this embedding thing, the goal will be to keep this as short as
 * possible, to prevent using up too much bandwidth when players load the game.
 * 
 * All of the other classes in this project/package, are just data handlers
 * and managers/workers that this class calls. This is the class you embed on 
 * the web-page. The rest just have to be accessible in the /www/ folder.
 * 
 * @author Daniel Zapata | djz24
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ClientJApplet extends JApplet implements ActionListener {

private PrintWriter out;                    // The primary data stream along which messages are passed to the server.
private Socket socket;                      // The primary connector which the PrintWriter writes to.
private BufferedReader in;                  // The primary data stream along which messages are read from the server.

private CombatCanvas combatCanvas;          // The instantiation of the class for combat information feedback/message sending.
private InventoryCanvas inven;              // The instantiation of the class for inventory display.
private StatusCanvas status;                // The instantiation of the class for health/stats display.
private ResourcesCanvas res;                // The instantiation of the class for carried resource.
private TradeCanvas trader;
private TradeActionsPanel bottomTradePanel;

private MainWorldCanvas theGameWorld;           // The global instantiation of the main world viewport canvas class.

private int leftStatusTabsTopLeftX = 1;         // Top Left Bound X-coord for status tabs.
private int leftStatusTabsTopLeftY = 1;         // Top Left Bound Y-coord for status tabs.
private int leftStatusTabsBottomRightX = 230;   // Bottom Right Bound X-coord for status tabs.
private int leftStatusTabsBottomRightY = 400;   // Bottom Right Bound Y-coord for status tabs.

private Image[] statusCanvasImageArray;         // Used to hold the images needed for the statusCanvas (health body parts)
private Image[] inventoryImageArray;            // Holds the images needed for the inventoryCanvas (item images)
private Image[] worldImageArray;                // Hold the images for the MainWorldViewCanvas thing. (backgrounds, player sprites, enemy sprites)
    
/*
 * The required interface override to make CLientJApplet implement ActionListener
 * Listens for events.
 * On event, looks up the ActionCommand (a string), and
 * tries to match it to one of the codes. (listed above sendMessage()
 */
@Override
public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
        case "f":
            sendMessage("f");               //"f" = tryToStartFight()
            break;
        case "r":
            sendMessage("r");               //"r" = runFromFight()
            break;
        case "t":
            sendMessage("t");               //"t" = tryToTrade()
            break;
    }
}

/**
 * The 'get' Accessor method for the StatusCanvas
 */
public StatusCanvas getChar() {
    return status;
}
/**
 * The 'get' Accessor method for the CombatCanvas
 */
public CombatCanvas getCombat() {
    return combatCanvas;
}
/**
 * The 'get' Accessor method for the InventoryCanvas
 */
public InventoryCanvas getInven() {
    return inven;
}
/**
 * The 'get' Accessor method for the TradeActionsPanel
 */
public TradeActionsPanel getOtherTrade() {
    return bottomTradePanel;
}
/**
 * The 'get' Accessor method for the ResourcesCanvas
 */
public ResourcesCanvas getResources() {
    return res;
}
/**
 * The 'get' Accessor method for the TradeCanvas
 */
public TradeCanvas getTrade() {
    return trader;
}

/**
 * The method that is called at the beginning of the life of every JApplet.
 * Serves as the constructor:
 *      Sets up communication channels to the Server.
 */
@Override
public void init() {
    
    // Applet parameter handling
    String host = getParameter("host");                                         // Get the name of the server. (i.e. localhost, IP address, ...)
    Integer port = Integer.parseInt(getParameter("port"));                      // Get the port the server is listening on.
    String username= getParameter("username");                                  // Get the name of the player, so we know who's data to be displaying.
    
    // Try/catch chunk to connect to the server and setup the read and write channels
    try {
        socket = new Socket(host, port);                                        // Construct a new socket with passed info from above.
        out = new PrintWriter(socket.getOutputStream(), true);                  // Construct a new PrintWriter data stream with the socket we just made.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// Finally, make the bufferedReader withe the socket we just made.
    }
    catch(UnknownHostException e) {
        System.out.println("Unknown or unreachable host " + host + " on port " + port);
        e.printStackTrace();
    }
    catch(IOException e) {
        System.out.println("I/O error");
        e.printStackTrace();
    }
    
//--------------- Component compilation/setup --------------------------------\\
    // The following block creates a JTabbedPane, which contains 3 Panels, which
    // each contain a Canvas of some sort (Status, inventory, resources), and 
    // adds said JTabbedPane to the JApplet's contentPane.
    {
        JTabbedPane leftStatusTabs = new JTabbedPane();     // 'leftStatusTabs' is the JTabbedPane which holds the left sidebar of the game's 3 main components
        // JPanel declarations                                                                                   (StatusCanvas, InventoryCanvas, ResourcesCanvas)
        JPanel statusPanel = new JPanel();                  // The container panel for the StatusCanvas. Is added to 'leftStatusTabs' from above.
        JPanel inventoryPanel = new JPanel();               // The container panel for the InventoryCanvas.
        JPanel resourcesPanel = new JPanel();               // The container panel for the ResourcesCanvas.

        // JPanels initialization. 
        {    
            status = new StatusCanvas(statusCanvasImageArray,this); // 'status' declared as a Field. Construct it with the image array 'arr'(which contains health images) and a pointer to this JApplet.
            ((Component)status).setFocusable(true);         // Cast 'status' as a Component so that we can call setFocusable(true), to make it visible.
            status.setBounds(leftStatusTabsTopLeftX,
                             leftStatusTabsTopLeftY,
                             leftStatusTabsBottomRightX,
                             leftStatusTabsBottomRightY);   // Set the size of the statusCanvas. Uses an inherited Canvas method.
            statusPanel.add(status);                        // Add the canvas to the statusPanel, which will then be added to the JTabbedPane 'leftStatusTabs'

            inven = new InventoryCanvas(inventoryImageArray,this);// 'inven' declared as a Field. Construct it with the images necessary and a pointer to this JApplet.
            ((Component)inven).setFocusable(true);          // Cast 'inven' as a Component so that we can call setFocusable(true), to make it visible.
            inven.setBounds(leftStatusTabsTopLeftX,
                            leftStatusTabsTopLeftY,
                            leftStatusTabsBottomRightX,
                            leftStatusTabsBottomRightY);    // Set the size of the InventoryCanvas. Uses an inherited Canvas method.
            inventoryPanel.add(inven);                      // Add the 'inven'tory to the 'inventoryPanel'

            res = new ResourcesCanvas();
            ((Component)res).setFocusable(true);
            res.setBounds(leftStatusTabsTopLeftX,
                          leftStatusTabsTopLeftY,
                          leftStatusTabsBottomRightX,
                          leftStatusTabsBottomRightY);      // Set the size of the ResourcesCanvas. Uses an inherited Canvas method.		
            resourcesPanel.add(res);                        // Add the 'res'ources to the 'resourcesPanel'
        }

        // Add the JPanels to the JTabbedPane.
        leftStatusTabs.addTab("Main",statusPanel);          // Name the statusPanel "Main", and add it to the JTabbedPane.
        leftStatusTabs.addTab("Inventory",inventoryPanel);  // Name the inventoryPanel, and add it to the JTabbedPane.
        leftStatusTabs.addTab("Resources",resourcesPanel);  // Name the resourcesPanel, and add it to the JTabbedPane.

        // Add the JTabbedPane to the JApplet.
        ((Component)leftStatusTabs).setFocusable(true);
        leftStatusTabs.setPreferredSize(new Dimension(230,400));
        leftStatusTabs.setOpaque(true);
        getContentPane().add("West",leftStatusTabs);
    }
    
    //----- MainWorldCanvas Setup ----\\
        // 'theGameWorld' is a canvas that goes in the JApplet, and lets the player see the world.
        theGameWorld = new MainWorldCanvas(socket,worldImageArray,this);      // Construct it with the array of images it needs.
        ((Component)theGameWorld).setFocusable(true);                   // Make it visible.
        theGameWorld.setBounds(230,1,320,320);                          // Set it's bounds with the Canvas-inhereited method.
        getContentPane().add(theGameWorld);                             // Add it to the JApplet.
//  \\---MainWorldCanvas Setup Done---//
    
    //------- JButtons Setup ---------\\ 
        // The setup for the JButton which starts fights.
        JButton fightJButton=new JButton("Fight");                      // Make the button with the name/Label "Fight"
        fightJButton.setActionCommand("f");                             // Message code "f" = tryToStartFight()
        fightJButton.addActionListener(this);                           // Make the JApplet ActionListener listen to events fired by this button.
        fightJButton.setToolTipText("Click this button to begin combat.");

        // The setup for the JButton which makes the player attempt to run away from fights.
        JButton runJButton=new JButton("Run Away");                     // Make the button with the name/Label "Run Away"
        runJButton.setActionCommand("r");                               // Message code "r" = runFromFight()
        runJButton.addActionListener(this);                             // Make the JApplet ActionListener listen to events fired by this button.
        runJButton.setToolTipText("Click this button to run from combat.");

        // The setup for the JButton which makes the player attempt to trade with players on the same tile as him.
        JButton tradeJButton=new JButton("Trade");                      // Make the button with the name/Label "TradeCanvas"
        tradeJButton.setActionCommand("t");                             // Message code "t" = tryToTrade()
        tradeJButton.addActionListener(this);                           // Make the JApplet ActionListener listen to events fired by this button.
        tradeJButton.setToolTipText("Click this button to begin trade.");
//  \\---- JButton Setup Complete ----//
    
    //------ RightStatusTabs Setup ----\\
        JTabbedPane rightStatusTabs = new JTabbedPane();            // 'rightStatusTabs' is the JTabbedPane which holds the right sidebar of the game's 3 main components
                                                                        // It contains the TradeCanvas and Combat components.
        JPanel tradePanel = new JPanel();                           // JPanel container for the two tradeCanvases

        trader = new TradeCanvas();
        ((Component)trader).setFocusable(true);
        trader.setSize(new Dimension(230,200));

        bottomTradePanel = new TradeActionsPanel(this);
        ((Component)bottomTradePanel).setFocusable(true);
        bottomTradePanel.setSize(new Dimension(230,200));

        tradePanel.add(trader);
        tradePanel.add(bottomTradePanel);

        combatCanvas = new CombatCanvas(statusCanvasImageArray,this);   // Uses same images as statusCanvas.
        ((Component)combatCanvas).setFocusable(true);
        combatCanvas.setSize(new Dimension(230,400));

        rightStatusTabs.addTab("Combat",combatCanvas);
        rightStatusTabs.addTab("Trade",tradePanel);

        ((Component)rightStatusTabs).setFocusable(true);
        rightStatusTabs.setPreferredSize(new Dimension(230,400));
        rightStatusTabs.setOpaque(true);
        getContentPane().add("East",rightStatusTabs);
//  \\--- RightStatusTabs Setup Done---//

    //--------- BottomPanel Setup ---------\\
        Panel bottomPanel = new Panel();            // A Panel to hold the control buttons.
        bottomPanel.setLayout(new GridLayout(1,3)); // Deine the layout to be 1 row with 3 columns.
        bottomPanel.add(fightJButton);              // Throw the buttons in the panel.
        bottomPanel.add(runJButton);
        bottomPanel.add(tradeJButton);

        getContentPane().add("South",bottomPanel);  // Add the panel (now containing the buttons) to the JApplet's contentPane.
//  \\------ BottomPanel Steup Done -------//

    setVisible(true);       // Set the whole applet visible.
}

/* 
   Sends a given string message (codes as follows) through the 'out' PrintWriter
   Which was initialized in init() and is a private global variable
   "f" = tryToStartFight()
   "r" = runFromFight()
   "t" = tryToTrade()
*/
public void sendMessage(String msg) {
    out.println(msg);
}

}
