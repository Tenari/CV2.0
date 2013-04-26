/**
 * This grand class is responsible for creating the illustrious Database and
 *  associated tables upon which the entire game relies. It's intended to be run
 *  the first time the server is set-up, and it'll work fine. However, running 
 *  this class on a machine with the Database already set-up is a bad idea.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
public class CreateCustomDatabase {
    // SQL variables
    Connection dbConnection;
    // SQL Database Feilds
    String dbURL = "jdbc:mysql://localhost/?user=root&password=";   // URL of the server
    String dbURL2 = "jdbc:mysql://localhost/game";// URL of the database.
    String dbUsername = "root";                     
    String dbPassword = "";
    
    int[][] bar = { {1,1,1,1,1,1,1,1},
                    {1,3,3,3,3,3,3,1},
                    {1,3,3,1,1,1,1,1},
                    {1,3,3,3,3,3,3,1},
                    {1,1,3,3,3,3,3,1},
                    {1,3,3,3,3,3,3,1},
                    {1,3,3,3,3,3,3,1},
                    {1,1,1,5,5,1,1,1}   };
    
    int[][] smallcity ={
        {1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1},  //22 X 12
        {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
        {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
        {1,3,3, 3,1,3,1,3, 3,1,4,4,4,1,4,4,1,4,4,4,4,1},
        {1,1,10,1,1,3,1,11,1,1,4,4,4,1,1,1,1,4,4,4,4,1},
        {1,3,3, 3,3,3,3,3, 3,3,3,3,3,3,3,3,3,3,4,4,4,1},
        {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
        {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
        {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
        {1,4,1, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
        {1,4,4, 4,4,4,4,4, 4,4,4,4,4,4,4,4,4,4,4,4,4,1},
        {1,1,1, 1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1}   };
            
    
    public CreateCustomDatabase() {
        // SQL Database Connection initialization
        try {
            // The driver is connected in the IDE build settings. FYI.
            dbConnection = DriverManager.getConnection(dbURL);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
        }
        
        String stmt = "CREATE DATABASE game";
        // Do the query.
        try {
            Statement dbStmt = dbConnection.createStatement();
            dbStmt.execute(stmt);
            dbStmt.close();
            
            //Connect to the newly created Database
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.dbUsername);
            connectionProps.put("password", this.dbPassword);
            dbConnection = DriverManager.getConnection(dbURL2, connectionProps);
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
        }
    }
    
    public void addTables(){
        String stmt = "CREATE TABLE bar(x int, y int, terrainType int, CONSTRAINT pairBar PRIMARY KEY (x,y))";
        doStatement(stmt);
        stmt = "CREATE TABLE smallcity(x int, y int, terrainType int, CONSTRAINT pairSmallCity PRIMARY KEY (x,y))";
        doStatement(stmt);
        stmt = "CREATE TABLE combatstats(uid int PRIMARY KEY,"
                + " attStr double, attSkill double,"
                + " defStr double, defSkill double,"
                + " headHP int, armsHP int, torsoHP int, legsHP int,"
                + " opponentUID int)";
        doStatement(stmt);
        stmt = "CREATE TABLE detailedstats(uid int PRIMARY KEY,"
                + " attStrBase double, attSkillBase double,"
                + " defStrBase double, defSkillBase double,"
                + " handToHand double, "
                + " money int)";
        doStatement(stmt);
        stmt = "CREATE TABLE organismsmovementinfo(uid int PRIMARY KEY,"
                + " name varchar(35),"
                + " x int, y int,"
                + " oldx int, oldy int,"
                + " world varchar(9), oldworld varchar(9),"
                + " energy int,"
                + " class int)";
        doStatement(stmt);
    }
    
    public void addDataToTables(){
        // Add the bar map.
        for(int i=0; i<bar.length; i++){
            for(int j=0; j<bar[0].length; j++){
                int a = bar[i][j];
                String stmt = "INSERT INTO bar VALUES ("
                        + j +", "
                        + i +", "
                        + a +");";
                doStatement(stmt);
            }
        }
        // Add the smallcity map.
        for(int i=0; i<smallcity.length; i++){
            for(int j=0; j<smallcity[0].length; j++){
                int a = smallcity[i][j];
                String stmt = "INSERT INTO smallcity VALUES ("
                        + j +", "
                        + i +", "
                        + a +");";
                doStatement(stmt);
            }
        }
    }
    
    public void doStatement(String stmt){
        // Do the query.
        try {
            Statement dbStmt = dbConnection.createStatement();
            dbStmt.execute(stmt);
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
        }
    }
}
