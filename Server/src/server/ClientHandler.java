/**
 * The thread spawned by the server to handle communications with a client.
 * 
 * @author Daniel Zapata | djz24
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


class ClientHandler extends Thread{
    private int sessid;
    private String username;
    private Server server;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private World world;
    private int uid;
    
    // Constructor
    public ClientHandler(Server server, Socket socket, int sessid, World s) {
        this.server = server;   //"this" used because variable names are same
        this.socket = socket;
        this.sessid = sessid;
        world=s;                //making "world" point to the instance of the World class that was created in the Server class
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("New client created");
        }
        catch(IOException e) {
         System.out.println("ERROR!\nCould not create client");
         kill();
        }
    }
 
    public void run() {
        System.out.println("MplayClient " + sessid + " threaded. Listening to incoming");
        handleIncoming();
    }

    // Handle communications from client
    private void handleIncoming()
    {
        try {
         String firstMessageRead=in.readLine();     // reads in the first line that the applet sends
         setUsername(firstMessageRead);             // the first thing will always be the username, so we set the user name here
         if(world.charExist(getUsername())==false) {         // this if/else thing is used to determine if this character already exists, 
            uid=world.addCharacter(getUsername());      // if it doesnt, we add him to the world and say that a new char was created
            // Also sets the uid that this class knows to the same one as the one the world knows
            System.out.println("player created named "+getUsername());
            if(uid%2==0)
            {
                ((Player)world.getCharacter(uid)).setTeam(true);
            }
            else
            {
                ((Player)world.getCharacter(uid)).setTeam(false);
            }
         }
         else {         //if he does exist, we just set the uid, and say that he was already there
            uid=world.getUID(getUsername());
            System.out.println("player " + getUsername()+" already there");
         }
         updateMoveScreen();updateMoveScreen();updateMoveScreen();updateMoveScreen();updateMoveScreen();
         while ((firstMessageRead = in.readLine()) != null) interpretData(firstMessageRead);//"thismsg=in.ReadLine()"means that the loop just waits until in.readLine returns something
        }													//it will return null in the case that the "in" bit is dead.  then we just send the inread String off to our interpretData method
        catch(IOException e) {
           System.out.println("ERROR!\nCould not read data from client");
           kill();
        }
    }
 
    // Kill the client connection/object
    public void kill() {
        server.removeMplayClient(sessid);
    }
 
    // Return client ID
    public int getID() {
     return this.sessid;
    }

    // Change client ID
    public void setID(int sessid) {
     this.sessid = sessid;
    }

    // Return username
    public String getUsername() {
     return this.username;
    }
 
    // Change username
    public void setUsername(String username)
    {
     this.username = username;
    }

    // Decide what to do from input
    private void interpretData(String msgg) {
        Scanner asdf=new Scanner(msgg);
        String msg=asdf.next();
        String useme="";             //this string will hold the postitions of all characters needed to draw
        boolean moved; // used to determine what kind of update to send ot the movement screen.
        switch (msg) {
            case "USER":
                // the client is requesting for the username, and we oblige
                sendData(getUsername());
                System.out.println("MplayClient " + sessid + "> " +world.getCharacter(uid).getX()+" "+world.getCharacter(uid).getY());
                break;
            case "A":
                //client says move X direction, we tell the world to move the char "uid" X direction
                moved = world.moveCharacter(uid, "west");
                if (moved)
                    updateMoveScreen("w");      // do the specialized update for a movement
                updateOrganisms();
                break;
            case "S":
                moved = world.moveCharacter(uid, "south");
                if (moved)
                    updateMoveScreen("s");      // do the specialized update for a movement
                updateOrganisms();
                break;
            case "D":
                moved = world.moveCharacter(uid, "east");
                if (moved)
                    updateMoveScreen("e");      // do the specialized update for a movement
                updateOrganisms();
                break;
            case "W":
                moved = world.moveCharacter(uid, "north");
                if (moved)
                    updateMoveScreen("n");      // do the specialized update for a movement
                updateOrganisms();
                break;
            case "f":
                world.startFight(uid);
                break;
            case "r":
                world.getCharacter(uid).autorun();//this was edited to make the run() method of organism unecessary.
                world.endFight(uid);
                world.endFight(world.getCharacter(uid).getOpponent());
                world.getCharacter(world.getCharacter(uid).getOpponent()).autorun();
                break;
            case "t":
                world.startTrade(uid);
                break;
            case "offer":
                ((Player)world.getCharacter(uid)).addOffer(asdf.next());//read in next theing for what is being offered.add it to the list of that dude's offerings
                break;
            case "unoffer":
                ((Player)world.getCharacter(uid)).removeOffer(asdf.next());//read in next theing for what is being unoffered.remove it from the list of that dude's offerings
                break;
            case "acceptTrade":
                ((Player)world.getCharacter(uid)).makeDealGood();
                if(((Player)world.getCharacter(uid)).getTradeIsGood() && ((Player)world.getCharacter(((Player)world.getCharacter(uid)).getTrader())).getTradeIsGood())
                {
                        world.finishTrade(uid,((Player)world.getCharacter(uid)).getTrader());
                }
                break;
            case "unacceptTrade":
                ((Player)world.getCharacter(uid)).makeDealBad();
                break;
            case "head":
                world.getCharacter(uid).setAim(msg);
                break;
            case "arms":
                world.getCharacter(uid).setAim(msg);
                break;
            case "torso":
                world.getCharacter(uid).setAim(msg);
                break;
            case "legs":
                world.getCharacter(uid).setAim(msg);
                break;
            case "equip":
                int uh=asdf.nextInt();
                world.getCharacter(uid).getInventory().getItem(uh).setEquipped(true);
                world.getCharacter(uid).setAttackStyle(world.getCharacter(uid).getInventory().getItem(uh).getStyle());
                break;
            case "E":
                break;
        }
    }

    public void updateMoveScreen() {
        String updateInfo = "v " + world.getPlayerMapView(world.getCharacter(uid));
        sendData(updateInfo);
    }
    
    public void updateOrganisms() {
        String updateInfo = "o " + world.getPlayerOrganismsView(world.getCharacter(uid));
        sendData(updateInfo);
    }

    public void updateFight() {
           if(world.getCharacter(uid).getOpponentType())//if the opponent is a monster
           {
                   sendData("fighting "+world.getMonster(world.getCharacter(uid).getOpponent()).getHead()+" "
                           +world.getMonster(world.getCharacter(uid).getOpponent()).getArms()+" "
                           +world.getMonster(world.getCharacter(uid).getOpponent()).getTorso()+" "
                           +world.getMonster(world.getCharacter(uid).getOpponent()).getLegs()+" "
                           +"You aimed for "+ world.getMonster(world.getCharacter(uid).getOpponent()).getName()
                           +"'s "+world.getCharacter(uid).getAim());
           }
           else
           {
                   if(world.getCharacter(world.getCharacter(uid).getOpponent()).getName().equals(world.getCharacter(uid).getName()))
                   {
                           sendData("fighting 12345");
                   }
                   else
                   {
                           sendData("fighting "+world.getCharacter(world.getCharacter(uid).getOpponent()).getHead()+" "
                           +world.getCharacter(world.getCharacter(uid).getOpponent()).getArms()+" "
                           +world.getCharacter(world.getCharacter(uid).getOpponent()).getTorso()+" "
                           +world.getCharacter(world.getCharacter(uid).getOpponent()).getLegs()+" "
                           +"You aimed for "+ world.getCharacter(world.getCharacter(uid).getOpponent()).getName()
                           +"'s "+world.getCharacter(uid).getAim());
                   }
           }
           sendData("fightstatus "+world.getCharacter(uid).getFightStatus());

    }

    public void updateTrade() {
           if(world.getCharacter(((Player)world.getCharacter(uid)).getTrader()).getName().equals(world.getCharacter(uid).getName()))
           {
                   //need to wrap up trade/ reset stuff for further use
           }
           else
           {
                   sendData("tradeUpdate "+((Player)world.getCharacter(((Player)world.getCharacter(uid)).getTrader())).getOffer());
           }
    }

    public void updateCharacter() {
           sendData("charstatus "+world.getCharacter(uid).getHead()+" "
                           +world.getCharacter(uid).getArms()+" "
                           +world.getCharacter(uid).getTorso()+" "
                           +world.getCharacter(uid).getLegs()+" "
                           +((Player)world.getCharacter(uid)).determineMoveCost(2)+" "
                           +((Player)world.getCharacter(uid)).getWeight()+" "
                           +world.getCharacter(uid).getEnergy()+" "
                           +world.getCharacter(uid).getMoney()+" "
                           +world.getCharacter(uid).getStrength()+" "
                           +world.getCharacter(uid).getAgility()+" "
                           +world.getCharacter(uid).getHandToHand()+" "
                           +((Player)world.getCharacter(uid)).getSmallBlade()+" "
                           +((Player)world.getCharacter(uid)).getLargeBlade()+" "
                           +((Player)world.getCharacter(uid)).getAxe()+" "
                           +((Player)world.getCharacter(uid)).getPolearm()+" "
                           +((Player)world.getCharacter(uid)).getBow()+" "
                           +((Player)world.getCharacter(uid)).getThrowing()+" "
                           +((Player)world.getCharacter(uid)).getIntimidation()+" "
                           +((Player)world.getCharacter(uid)).getDiplomacy()+" "
                           +((Player)world.getCharacter(uid)).getEndurance()+" "
                           +((Player)world.getCharacter(uid)).getHiding());
    }

    public void updateResou() {
           sendData("resou "+world.getCharacter(uid).getInventory().getLeather()+" "
                           +world.getCharacter(uid).getInventory().getCloth()+" "
                           +world.getCharacter(uid).getInventory().getTools()+" "
                           +world.getCharacter(uid).getInventory().getWheat()+" "
                           +world.getCharacter(uid).getInventory().getWater()+" "
                           +world.getCharacter(uid).getInventory().getMeat()+" "
                           +world.getCharacter(uid).getInventory().getStone()+" "
                           +world.getCharacter(uid).getInventory().getWood()+" "
                           +world.getCharacter(uid).getInventory().getMetal()+" "
                           +world.getCharacter(uid).getInventory().getBuildingMaterial());
    }

    public void updateInven() {
        String asshat="";
        for(int i=0;i<world.getCharacter(uid).getInventory().hugenessInven();i++) {
            asshat= asshat+world.getCharacter(uid).getInventory().getItem(i).getWeight()+" "
                            +world.getCharacter(uid).getInventory().getItem(i).getBonus()+" "
                            +world.getCharacter(uid).getInventory().getItem(i).getType()+" "
                            +world.getCharacter(uid).getInventory().getItem(i).getName()+" "
                            +world.getCharacter(uid).getInventory().getItem(i).getEquipped()+" "
                            +i+" ";
        }
        sendData("addItem "+world.getCharacter(uid).getInventory().hugenessInven()+" "+asshat);
    }

    // Send data to the client
    public void sendData(String msg) {
        out.println(msg);
        System.out.println(msg);
        if (out.checkError()) {
         System.out.println("ERROR!\nCould not deliver message to client");
         kill();
        }
    }

    private void updateMoveScreen(String direction) {
        String updateInfo = direction + " " + world.getPlayerMapView(world.getCharacter(uid), direction);
        sendData(updateInfo);
    }
}