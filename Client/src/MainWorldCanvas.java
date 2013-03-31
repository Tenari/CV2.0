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
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;

class MainWorldCanvas extends Canvas implements KeyListener{
    
    // The constructor
    public MainWorldCanvas (Socket socket,Image[] arr,ClientJApplet c) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
