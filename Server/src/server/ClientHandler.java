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
         Player asdf =new Player(getUsername(),1);  // we create a new character class for that user name
         if(world.charExist(asdf)==false) {         // this if/else thing is used to determine if this character already exists, 
            world.addCharacter(getUsername());      // if it doesnt, we add him to the world and say that a new char was created
            uid=world.getUID(asdf);                 // this sets the uid that this class knows to the same one as the one the world knows
            System.out.println("player created named "+getUsername());
            if(uid%2==0)
            {
                world.getCharacter(uid).setTeam(true);
            }
            else
            {
                world.getCharacter(uid).setTeam(false);
            }
         }
         else {         //if he does exist, we just set the uid, and say that he was already there
            uid=world.getUID(asdf);
            System.out.println("player " + getUsername()+" already there");
         }
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
        switch (msg) {
            case "USER":
                // the client is requesting for the username, and we oblige
                sendData(getUsername());
                System.out.println("MplayClient " + sessid + "> " +world.getCharacter(uid).getX()+" "+world.getCharacter(uid).getY());
                break;
            case "A":
                //client says move X direction, we tell the world to move the char "uid" X direction
                world.moveCharacter(uid, "west");
                break;
            case "S":
                world.moveCharacter(uid, "south");
                break;
            case "D":
                world.moveCharacter(uid, "east");
                break;
            case "W":
                world.moveCharacter(uid, "north");
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
                world.getCharacter(uid).addOffer(asdf.next());//read in next theing for what is being offered.add it to the list of that dude's offerings
                break;
            case "unoffer":
                world.getCharacter(uid).removeOffer(asdf.next());//read in next theing for what is being unoffered.remove it from the list of that dude's offerings
                break;
            case "acceptTrade":
                world.getCharacter(uid).makeDealGood();
                if(world.getCharacter(uid).getTradeIsGood() && world.getCharacter(world.getCharacter(uid).getTrader()).getTradeIsGood())
                {
                        world.finishTrade(uid,world.getCharacter(uid).getTrader());
                }
                break;
            case "unacceptTrade":
                world.getCharacter(uid).makeDealBad();
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

        updateMoveScreen();
        updateResou();
        updateInven();
        updateTrade();updateFight();updateCharacter();
    }

    public void updateMoveScreen()
    {
        String useme="";        //this string will hold the postitions of all characters needed to draw
        
        //Create an arrayList that has in it all the characters that we need to draw in the 10X10
        ArrayList<Organism> beee=world.getOthersWorld(world.getCharacter(uid).getX(),world.getCharacter(uid).getY(),world.getCharacter(uid).getWorld());
        for(int v=0;v<beee.size();v++)//loop throught the arrList and get all the locations in a String format--one big string
        {
                useme=useme+beee.get(v).getX()+" "+beee.get(v).getY()+" ";
        }
        beee=world.getOthersWorldNPC(world.getCharacter(uid).getX(),world.getCharacter(uid).getY(),world.getCharacter(uid).getWorld());
        for(int v=0;v<beee.size();v++)//loop throught the arrList and get all the locations in a String format--one big string
        {
                useme=useme+beee.get(v).getX()+" "+beee.get(v).getY()+" ";
        }
        useme=useme+"99999 ";
        beee=world.getOthersWorldMonster(world.getCharacter(uid).getX(),world.getCharacter(uid).getY(),world.getCharacter(uid).getWorld());
        for(int v=0;v<beee.size();v++)//loop throught the arrList and get all the locations in a String format--one big string
        {
                useme=useme+beee.get(v).getX()+" "+beee.get(v).getY()+" ";
        }
        sendData(""+world.getCharacter(uid).getLastMoveDirection()+" "+world.getCharacter(uid).getTeam1()+" "+world.getCharacter(uid).getWorld()+" "+world.getCharacter(uid).getX()+" "+world.getCharacter(uid).getY()+" "
                            +useme);//sends worldname followed by the char's X and Y followed by the positions of local characters to the applet, so it knows where to draw them.
    }

    public void updateFight()
    {
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

    public void updateTrade()
    {
           if(world.getCharacter(world.getCharacter(uid).getTrader()).getName().equals(world.getCharacter(uid).getName()))
           {
                   //need to wrap up trade/ reset stuff for further use
           }
           else
           {
                   sendData("tradeUpdate "+world.getCharacter(world.getCharacter(uid).getTrader()).getOffer());
           }
    }

    public void updateCharacter()
    {
           sendData("charstatus "+world.getCharacter(uid).getHead()+" "
                           +world.getCharacter(uid).getArms()+" "
                           +world.getCharacter(uid).getTorso()+" "
                           +world.getCharacter(uid).getLegs()+" "
                           +world.getCharacter(uid).determineMoveCost(2)+" "
                           +world.getCharacter(uid).getWeight()+" "
                           +world.getCharacter(uid).getEnergy()+" "
                           +world.getCharacter(uid).getMoney()+" "
                           +world.getCharacter(uid).getStrength()+" "
                           +world.getCharacter(uid).getAgility()+" "
                           +world.getCharacter(uid).getHandToHand()+" "
                           +world.getCharacter(uid).getSmallBlade()+" "
                           +world.getCharacter(uid).getLargeBlade()+" "
                           +world.getCharacter(uid).getAxe()+" "
                           +world.getCharacter(uid).getPolearm()+" "
                           +world.getCharacter(uid).getBow()+" "
                           +world.getCharacter(uid).getThrowing()+" "
                           +world.getCharacter(uid).getIntimidation()+" "
                           +world.getCharacter(uid).getDiplomacy()+" "
                           +world.getCharacter(uid).getEndurance()+" "
                           +world.getCharacter(uid).getHiding());
    }

     public void updateResou()
    {
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
        if (out.checkError()) {
         System.out.println("ERROR!\nCould not deliver message to client");
         kill();
        }
    }
}