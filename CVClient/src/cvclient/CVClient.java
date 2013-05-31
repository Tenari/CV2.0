/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JApplet;

/**
 *
 * @author Tenari
 */
public class CVClient extends JApplet implements ActionListener {
    
    private PrintWriter out;                    // The primary data stream along which messages are passed to the server.
    private Socket socket;                      // The primary connector which the PrintWriter writes to.
    private BufferedReader in;                  // The primary data stream along which messages are read from the server.
    
    @Override
    public void init(){
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
