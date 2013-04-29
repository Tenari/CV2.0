/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tenari
 */
public class ClientOrganismMapInfo {
    
    int classCode;
    int direction;
    int x;
    int y;
    
    public ClientOrganismMapInfo(int classCodeNum, int directionCode, int xCoord, int yCoord){
        classCode   =   classCodeNum;
        direction   =   directionCode;
        x           =   xCoord;
        y           =   yCoord;
    }
    
}
