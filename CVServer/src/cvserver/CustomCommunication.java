/**
 * This class just provides some all-purpose methods for talking to the database.
 * 
 * @author Daniel Zapata
 */
package cvserver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
public class CustomCommunication {
    // SQL variables
    Connection dbConnection;
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
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
        } 
    }
    
    /**
     * This method is used to insert data into a given table.
     * For real, though, make sure you put ' ' around varchar things. This doesn't do that for you.
     * @param table
     * @param vals 
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
        return doNonSelect(stmt);
    }
    
    /**
     * Takes a columnName, a tableName and a userID, and does "SELECT colName FROM 
     *      table WHERE table.`uid` = userID".
     * 
     * @param colName
     * @param table
     * @param uid
     * @return The first double from the ResultSet of the query. -1.0 if failed.
     */
    public double selectSingleDoubleByUID(String colName, String table, int uid) {
        // Make the statement.
        String stmt = 
                "SELECT `"+colName+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`uid` = "+uid+";";   
                
        // Do the query.
        try {
            Statement dbStmt = dbConnection.createStatement();
            ResultSet dbResultSet = null;
            if (dbStmt.execute(stmt)) {  
                dbResultSet = dbStmt.getResultSet();
                dbResultSet.last();
                return dbResultSet.getDouble(1);
            } 
            else {
                System.err.println("CustomCommunication.selectSingleByUID failed");
            }
            
            return -1.0;
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return -1.0;
        }
    }
    
    /**
     * Takes a columnName, a tableName and a userID, and does the query:
     *      "SELECT colName FROM table WHERE table.`uid` = userID".
     * 
     * @param colName
     * @param table
     * @param uid
     * @return The last string from the ResultSet of the query. null if failed.
     */
    public String selectSingleStringByUID(String colName, String table, int uid) {
        // Make the statement.
        String stmt = 
                "SELECT `"+colName+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`uid` = "+uid+";";   
                
        // Do the query.
        try {
            Statement dbStmt = dbConnection.createStatement();
            ResultSet dbResultSet = null;
            if (dbStmt.execute(stmt)) {  
                dbResultSet = dbStmt.getResultSet();
                dbResultSet.last();
                return dbResultSet.getString(1);
            } 
            else {
                System.err.println("CustomCommunication.selectSingleStringByUID failed");
            }
            
            return null;
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return null;
        }
    }
    
    /**
     * Takes a name, and a tableName, and does the query:
     *      "SELECT `uid` FROM table WHERE table.`name` = name".
     * 
     * @param name
     * @param table
     * @return The last int from the ResultSet of the query. -1 if failed.
     */
    public int selectUIDByName(String name, String table) {
        // Make the statement.
        String stmt = 
                "SELECT `uid"+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`name` = "+name+";";
                
        // Do the query.
        return doSelectInt(stmt);
    }
    
    /**
     * Takes a columnName, a tableName and a userID, and does "SELECT colName FROM 
     *      table WHERE table.`uid` = userID".
     * 
     * @param colName
     * @param table
     * @param uid
     * @return The first int from the ResultSet of the query. -1 if failed.
     */
    public int selectSingleIntByUID(String colName, String table, int uid) {
        // Make the statement.
        String stmt = 
                "SELECT `"+colName+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`uid` = "+uid+";";   
                
        // Do the query.
        return doSelectInt(stmt);
    }
    
    /**
     * Takes a columnName, a tableName, a x, and a y, and does "SELECT colName 
     *      FROM table WHERE table.`x` = x AND table.`y` = y".
     * 
     * @param colName
     * @param table
     * @param uid
     * @return The first int from the ResultSet of the query. -1 if failed.
     */
    public int selectSingleIntByXAndY(String colName, String table, int x, int y) {
        // Make the statement.
        String stmt = 
                "SELECT "+colName+
                " FROM "+table+
                " WHERE "+table+".x = "+x+
                "   AND "+table+".y = "+y;   
                
        // Do the query.
        return doSelectInt(stmt);
    }
    
    public int selectUIDByXAndYAndWorld(String table, int x, int y, String world){
        // Make the statement.
        String stmt = 
                "SELECT uid"+
                " FROM "+table+
                " WHERE "+table+".x = "+x+
                "   AND "+table+".y = "+y+
                "   AND "+table+".world = "+world;   
                
        // Do the query.
        return doSelectInt(stmt);
    }
    
    public int selectIntByCustomQuery(String query){
        // Make the statement.
        String stmt = query;   
                
        // Do the query.
        return doSelectInt(stmt);
    }
    
    public int selectIntSumByUID(String colName, String table, int uid) {
        // Make the statement.
        String stmt = 
                "SELECT `"+colName+
                "` FROM `"+table+
                "` WHERE `"+table+"`.`uid` = "+uid+";";
                
        // Do the query.
        try {
            int currentSum = 0;
            Statement dbStmt = dbConnection.createStatement();
            if (dbStmt.execute(stmt)) {  
                ResultSet dbResultSet = dbStmt.getResultSet();
                while (dbResultSet.next()){
                    currentSum += dbResultSet.getInt(1);
                }
                return currentSum;
            } 
            else {
                System.err.println("CustomCommunication.selectSingleByUID failed");
            }
            
            return -1;
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return -1;
        }
    }
    
    /**
     * Does the SQL to set a double to a new value, based on a given uid key.
     * @param table
     * @param doubleName
     * @param newValue
     * @param uid
     * @return true on success, false otherwise.
     */
    public boolean updateSingleDoubleByUID(String table, String doubleName, double newValue, int uid) {
        // Make the statement string.
        String stmt =   "UPDATE `"+table+
                        "` SET "+doubleName+"="+newValue+
                        " WHERE uid="+uid;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
    /**
     * Does the SQL to set an int to a new value, based on a given uid key.
     * @param table
     * @param intName
     * @param newValue
     * @param uid
     * @return true on success, false otherwise.
     */
    public boolean updateSingleIntByUID(String table, String intName, int newValue, int uid) {
        // Make the statement string.
        String stmt =   "UPDATE `"+table+
                        "` SET "+intName+"="+newValue+
                        " WHERE uid="+uid;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
    public boolean updateSingleIntByUIDAndOther(String table, String intName, int newValue, int uid, String otherName, String otherValue) {
        // Make the statement string.
        String stmt =   "UPDATE `"+table+
                        "` SET "+intName+"="+newValue+
                        " WHERE uid="+uid+
                        " AND "+otherName+"="+otherValue;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
    /**
     * Does the SQL to set string to a new value, based on a given uid key.
     * @param table
     * @param stringName
     * @param newValue
     * @param uid
     * @return true on success, false otherwise.
     */
    public boolean updateSingleStringByUID(String table, String stringName, String newValue, int uid) {
        // Make the statement string.
        String stmt =   "UPDATE `"+table+
                        "` SET "+stringName+"='"+newValue+"'"+
                        " WHERE uid="+uid;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
    public boolean deleteFromWhereUIDAnd(String table, int uid, String andName, String andValue){
        // Make the statement string.
        String stmt =   "DELETE FROM `"+table+
                        "` WHERE uid="+uid+
                        "  AND "+andName+"="+andValue;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
    public boolean deleteFromWhereUIDAndAnd(String table, int uid, String and1Name, String and1Value, String and2Name, String and2Value){
        // Make the statement string.
        String stmt =   "DELETE FROM `"+table+
                        "` WHERE uid="+uid+
                        "  AND "+and1Name+"="+and1Value+
                        "  AND "+and2Name+"="+and2Value;
        
        // Do the statement.
        return doNonSelect(stmt);
    }
    
//-------------------------Private Helpers------------------------------------\\
    private boolean doNonSelect(String stmt){
        try {
            Statement dbStmt = dbConnection.createStatement();
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
    
    private int doSelectInt(String stmt){
        try {
            Statement dbStmt = dbConnection.createStatement();
            ResultSet dbResultSet = dbStmt.executeQuery(stmt);
            // If there are actually results to handle, we'll just return the int
            while (dbResultSet.next()) {
                dbResultSet.last();
                return dbResultSet.getInt(1);
            } 
            // Otherwise, return the failure value.
            return -1;
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode()); 
            return -1;
        }
    }
}
