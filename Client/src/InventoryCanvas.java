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
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class InventoryCanvas extends Canvas implements MouseListener{
    
    public InventoryCanvas (Image[] imgs, ClientJApplet c) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
