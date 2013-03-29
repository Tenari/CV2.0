/**
 *The primary class through which the client runs. 
 * This extends Japplet, making this an Applet as well. Applets can be embedded 
 * in webpages--which is how this one will be delivered to players.
 * Due to this embedding thing, the goal will be to keep this as short as
 * possible, to prevent using up too much bandwidth when players load the game.
 * 
 * All of the other classes in this project/package, are just data handlers
 * and managers/workers that this class calls. This is the class you embed on 
 * the web-page. The rest just have to be accessible in the /www/ folder.
 * 
 * @author Daniel Zapata
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JApplet;

public class ClientJApplet extends JApplet implements ActionListener {

private PrintWriter out = null;             // The primary data stream along which messages are passed to the server.
private Socket socket = null;               // The primary connector which the PrintWriter writes to.
private BufferedReader in = null;           // The primary data stream along which messages are read from the server.

    
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
 * The method that is called at the beginning of the life of every applet.
 * Serves as the constructor:
 *      Sets up communication channels to the Server.
 */
@Override
public void init() {
    
    //parameter handling
    String host = getParameter("host");                                         // get the name of the server (i.e. localhost, IP address, ...)
    Integer port = Integer.parseInt(getParameter("port"));                      // get the port the server is listening on
    String username= getParameter("username");                                  // get the name of the player, so we know who's data to be displaying
    
    // Try/catch chunk to connect to the server and setup the read and write channels
    try {
        socket = new Socket(host, port);                                        // construct a new socket with passed info from above
        out = new PrintWriter(socket.getOutputStream(), true);                  // construct a new PrintWriter data stream with the socket we just made.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// finally, make the bufferedReader withe the socket we just made.
    }
    catch(UnknownHostException e) {
        System.out.println("Unknown or unreachable host " + host + " on port " + port);
        e.printStackTrace();
    }
    catch(IOException e) {
        System.out.println("I/O error");
        e.printStackTrace();
    }
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
