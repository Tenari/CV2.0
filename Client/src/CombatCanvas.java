/**
 * CombatCanvas is a Canvas, on which the information relevant to combat is 
 * drawn.
 * 
 * It implements MouseListener so that the player can change combat preferences.
 * 
 * 
 * @author Daniel Zapata | djz24
 */

import java.awt.Canvas;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class CombatCanvas extends Canvas implements MouseListener{

public CombatCanvas (Image[] arr, ClientJApplet c){
    
}
    
/*
 * Necessary override.
 */
@Override
public void mouseClicked(MouseEvent e) {
    throw new UnsupportedOperationException("Not supported yet.");
}

/*
 * Necessary overrides for 'implements' keyword on MouseListener.
 * 
 * They all do nothing, intentionally.
 */
@Override
public void mousePressed(MouseEvent e) {
}
@Override
public void mouseReleased(MouseEvent e) {
}
@Override
public void mouseEntered(MouseEvent e) {
}
@Override
public void mouseExited(MouseEvent e) {
}
    
}
