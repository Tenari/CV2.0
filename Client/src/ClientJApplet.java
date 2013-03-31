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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ClientJApplet extends JApplet implements ActionListener {

private PrintWriter out;                    // The primary data stream along which messages are passed to the server.
private Socket socket;                      // The primary connector which the PrintWriter writes to.
private BufferedReader in;                  // The primary data stream along which messages are read from the server.

private CombatCanvas combatCanvas;          // The instantiation of the class for combat information feedback/message sending.
private Inventory inven;                    // The instantiation of the class for inventory display.
private StatusCanvas status;                // The instantiation of the class for health/stats display.
private Resources res;                      // The instantiation of the class for carried resource.

private int leftStatusTabsTopLeftX = 1;         // Top Left Bound X-coord for status tabs.
private int leftStatusTabsTopLeftY = 1;         // Top Left Bound Y-coord for status tabs.
private int leftStatusTabsBottomRightX = 230;   // Bottom Right Bound X-coord for status tabs.
private int leftStatusTabsBottomRightY = 400;   // Bottom Right Bound Y-coord for status tabs.
    
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

/*
 * The 'get' Accessor method for the StatusCanvas
 */
public StatusCanvas getChar() {
    return status;
}
/*
 * The 'get' Accessor method for the CombatCanvas
 */
public CombatCanvas getCombat() {
    return combatCanvas;
}
/*
 * The 'get' Accessor method for the Inventory
 */
public Inventory getInven() {
    return inven;
}
/*
 * The 'get' Accessor method for the OtherTrade
 */
public OtherTrade getOtherTrade() {
    return otrader;
}
/*
 * The 'get' Accessor method for the Resources
 */
public Resources getResources() {
    return res;
}
/*
 * The 'get' Accessor method for the Trade
 */
public Trade getTrade() {
    return trader;
}

/*
 * The method that is called at the beginning of the life of every applet.
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
        // JPanel declarations                                                                                   (StatusCanvas, Inventory, Resources)
        JPanel statusPanel = new JPanel();                  // The container panel for the StatusCanvas. Is added to 'leftStatusTabs' from above.
        JPanel inventoryPanel = new JPanel();               // The container panel for the Inventory.
        JPanel resourcesPanel = new JPanel();               // The container panel for the Resources.

        // JPanels initialization. 
        {    
            status = new StatusCanvas(arr,this);            // 'status' declared as a Field. Construct it with the image array 'arr'(which contains health images) and a pointer to this JApplet.
            ((Component)status).setFocusable(true);         // Cast 'status' as a Component so that we can call setFocusable(true), to make it visible.
            status.setBounds(leftStatusTabsTopLeftX,
                             leftStatusTabsTopLeftY,
                             leftStatusTabsBottomRightX,
                             leftStatusTabsBottomRightY);   // Set the size of the statusCanvas. Uses an inherited Canvas method.
            statusPanel.add(status);                        // Add the canvas to the statusPanel, which will then be added to the JTabbedPane 'leftStatusTabs'

            inven = new Inventory(armor,weapon,sheild,belt,boot,this);// 'inven' declared as a Field. Construct it with the images necessary and a pointer to this JApplet.
            ((Component)inven).setFocusable(true);          // Cast 'inven' as a Component so that we can call setFocusable(true), to make it visible.
            inven.setBounds(leftStatusTabsTopLeftX,
                            leftStatusTabsTopLeftY,
                            leftStatusTabsBottomRightX,
                            leftStatusTabsBottomRightY);    // Set the size of the Inventory. Uses an inherited Canvas method.
            inventoryPanel.add(inven);                      // Add the 'inven'tory to the 'inventoryPanel'

            res = new Resources();
            ((Component)res).setFocusable(true);
            res.setBounds(leftStatusTabsTopLeftX,
                          leftStatusTabsTopLeftY,
                          leftStatusTabsBottomRightX,
                          leftStatusTabsBottomRightY);      // Set the size of the Resources. Uses an inherited Canvas method.		
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
    
    Worker theGame = new Worker(socket,workerarr,this);
    ((Component)theGame).setFocusable(true);
    theGame.setBounds(230,1,320,320);			
    getContentPane().add(theGame);


    b=new JButton("Fight");
    //b.setBounds(250,350,40,40);
    b.setActionCommand("fight");
    b.addActionListener(this);
    b.setToolTipText("Click this button to begin combat.");
    //getContentPane().add(b);

    b1=new JButton("Run");
    //b1.setBounds(250,400,40,40);
    b1.setActionCommand("run");
    b1.addActionListener(this);
    b1.setToolTipText("Click this button to run from combat.");
    //getContentPane().add(b1);

    b2=new JButton("Trade");
    //b.setBounds(250,350,40,40);
    b2.setActionCommand("trade");
    b2.addActionListener(this);
    b2.setToolTipText("Click this button to begin trade.");



    JTabbedPane tabs2 = new JTabbedPane();

    JPanel pane = new JPanel();
    JPanel pane1 = new JPanel();

    combatCanvas = new CombatWorker(arr,this);
    ((Component)combatCanvas).setFocusable(true);
    combatCanvas.setSize(new Dimension(230,400));			
    //pane.add(combatCanvas);

    trader = new Trade();
    ((Component)trader).setFocusable(true);
    trader.setSize(new Dimension(230,200));

    otrader = new OtherTrade(this);
    ((Component)otrader).setFocusable(true);
    otrader.setSize(new Dimension(230,200));

    pane.add(trader);
    pane.add(otrader);

    tabs2.addTab("Combat",combatCanvas);
    tabs2.addTab("Trade",pane);

    ((Component)tabs2).setFocusable(true);
    tabs2.setPreferredSize(new Dimension(230,400));
    tabs2.setOpaque(true);
    getContentPane().add("East",tabs2);

    Panel bottom = new Panel();   // a Panel to hold the control buttons
    bottom.setLayout(new GridLayout(1,3));
    bottom.add(b);
    bottom.add(b1);
    bottom.add(b2);


    getContentPane().add("South",bottom);

    setVisible(true);
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
