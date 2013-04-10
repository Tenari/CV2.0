/**
 * This class just provides some all-purpose methods for talking to the database.
 * 
 * @author Daniel Zapata
 */
package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
public class CustomCommunication {
    // SQL variables
    Connection dbConnection;
    Statement dbStmt;
    ResultSet dbResultSet;
    // SQL Database Feilds
    String dbURL = "jdbc:mysql://localhost/game";   // URL of the database.
    String dbUsername = "root";                     
    String dbPassword = "";
    
    public CustomCommunication() {
        // SQL Database Connection initialization
        try {
            // The driver is connected in the IDE build settings. FYI.
            
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.dbUsername);
            connectionProps.put("password", this.dbPassword);
            dbConnection = DriverManager.getConnection(dbURL, connectionProps);
            dbStmt = dbConnection.createStatement();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
        } 
    }
    
    /**
     * This method is used to insert data into a given table.
     * For real, though, make sure you put ' ' around varchar things. This doesn't do that for you.
     * @return true if worked, false otherwise
     */
    public boolean insert(String table, String[] vals) {
        // Make the statement.
        String stmt = "INSERT INTO `"+table+"` VALUES (";   // The intial part of the query
        for (String s : vals) {
            stmt = stmt + s + ", ";                          // The list of values to insert in the table
        }
        stmt = stmt.substring(0,stmt.length()-2);           // Pull the last comma off for syntax.
        stmt = stmt + ")";                                  // Add the last parenthase for syntax.
                
        // Do the statement.
        try {
            dbStmt.execute(stmt);
            return true;        
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return false;
        }
    }
    
    /**
     * Takes a columnName, a tableName and a userID, and does "SELECT colName FROM 
     *      table WHERE table.`uid` = userID".
     * 
     * @param colName
     * @param table
     * @param uid
     * @return The ResultSet of the query.
     */
    public ResultSet selectSingleByUID(String colName, String table, int uid) {
        // Make the statement.
        String stmt = 
                "SELECT `"+colName+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`uid` = "+uid;   
                
        // Do the query.
        try {
            if (dbStmt.execute(stmt)) {  
                dbResultSet = dbStmt.getResultSet();
            } 
            else {
                System.err.println("CustomCommunication.selectSingleByUID fialed");
            }
            
            return dbResultSet;
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return null;
        }
    }
}
