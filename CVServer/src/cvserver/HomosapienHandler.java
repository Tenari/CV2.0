package cvserver;

/**
 *
 * @author Tenari
 */
public class HomosapienHandler extends OrganismHandler{
    
    private int teamCodeDefault           =   1;

    // Combat Proficencies Defaults
    private final double smallBladeDefault=   0.0;
    private final double largeBladeDefault=   0.0;
    private final double axeDefault       =   0.0;
    private final double polearmDefault   =   0.0;
    private final double shootingDefault  =   0.0;
    private final double throwingDefault  =   0.0;
    private final double magicDefault     =   0.0;
    
    // Other Skill Defaults
    private final double intimidateDefault=   0.0;
    private final double diplomacyDefault =   0.0;
    private final double hidingDefault    =   0.0;
    
    
    
   public HomosapienHandler(CustomCommunication c){
        super(c);       // Calls the OrganismHandler Constructor
   }
    
    // Uses Default values for everything--including organism values.
    public boolean createNewHomosapien(String name, int myUID, int classCode){
        boolean organismWorked = createNewOrganism(name, myUID, classCode);
        
        // Insert values for the homosapien's stats
        String[] initialValues = {
                                    ""+myUID,
                                    ""+teamCodeDefault,
                                    ""+smallBladeDefault,
                                    ""+largeBladeDefault,
                                    ""+axeDefault,
                                    ""+polearmDefault,
                                    ""+shootingDefault,
                                    ""+throwingDefault,
                                    ""+magicDefault,
                                    ""+intimidateDefault,
                                    ""+diplomacyDefault,
                                    ""+hidingDefault}; 
        organismWorked = organismWorked && communicate.insert(lookup.homosapienTableName, initialValues);  // true if the insert worked.
        
        return organismWorked;
    }
    
//----------------------ITEMS HANDLERS------------------------------------------
    // Adds a "Crappy Sword" and a "Crappy Shield". Returns true iff succeeded.
    public boolean addDefaultItems(int hsUID){
        boolean worked = addItem(hsUID, "Crappy Sword", 333, 5, false, 0);
        worked = worked && addItem(hsUID, "Crappy Sheild", 1000, 5, false, 0);
        return worked;
    }

    // Adds an item with passed stats to the hsitemsTableName. Returns true iff succeeded.
    public boolean addItem(int hsUID, String name, int code, int weight, boolean equipped, int slot, int mod1type, int mod1value, int mod2type, int mod2value, int mod3type, int mod3value){
        // Insert values for the homosapien's stats
        String[] values = {         ""+hsUID,
                                    "'"+name+"'",
                                    ""+code,
                                    ""+weight,
                                    ""+equipped,
                                    ""+slot,
                                    ""+mod1type,
                                    ""+mod1value,
                                    ""+mod2type,
                                    ""+mod2value,
                                    ""+mod3type,
                                    ""+mod3value}; 
       return communicate.insert(lookup.hsitemsTableName, values);
    }
    // Convinient overloads.
    public boolean addItem(int hsUID, String name, int code, int weight, boolean equipped, int slot, int mod1type, int mod1value, int mod2type, int mod2value){
        boolean worked = addItem(hsUID, name, code, weight, equipped, slot, mod1type, mod1value, mod2type, mod2value, 0, 0);
        
        return worked;
    }
    public boolean addItem(int hsUID, String name, int code, int weight, boolean equipped, int slot, int mod1type, int mod1value){
        boolean worked = addItem(hsUID, name, code, weight, equipped, slot, mod1type, mod1value, 0, 0, 0, 0);
        
        return worked;
    }
    public boolean addItem(int hsUID, String name, int code, int weight, boolean equipped, int slot){
        boolean worked = addItem(hsUID, name, code, weight, equipped, slot, 0, 0, 0, 0, 0, 0);
        return worked;
    }
    
    public boolean removeItem(int hsUID, String name, int slot){
        return communicate.deleteFromWhereUIDAndAnd(lookup.hsresourcesTableName, hsUID, "name", "'"+name+"'", "slot", ""+slot);
    }
//----------------------END ITEMS HANDLERS--------------------------------------
    
//----------------------RESOURCES HANDLERS--------------------------------------
    // Adds a resource with passed stats to the hsresourcesTableName. Returns true iff succeeded.
    public boolean addResource(int hsUID, String type, int amount){
        // Insert values for the homosapien's stats
        String[] values = {         ""+hsUID,
                                    "'"+type+"'",
                                    ""+amount}; 
       return communicate.insert(lookup.hsresourcesTableName, values);
    }
    
    /** Removes the passed amount of resources from the type, and deletes the row if the amount becomes 0.
     * If the amount is more than exists, returns false.
     * Returns true iff successfully removes resources.*/
    public boolean removeResource(int hsUID, String type, int amount){
        int currentAmt = communicate.selectIntByCustomQuery("SELECT `amount` FROM `"+lookup.hsresourcesTableName
                                        +"` WHERE `"+lookup.hsresourcesTableName+"`.`uid` = "+hsUID
                                        +" AND `"+lookup.hsresourcesTableName+"`.`type` = "+"'"+type+"'");
        int newAmt =(currentAmt - amount);
        if( newAmt < 0){     // There isn't enough resources.
            return false;
        } else if(newAmt == 0){
            // remove the row.
            return communicate.deleteFromWhereUIDAnd(lookup.hsresourcesTableName, hsUID, "type", type);
        } else {
            return communicate.updateSingleIntByUIDAndOther(lookup.hsresourcesTableName, "amount", newAmt, hsUID, "type", "'"+type+"'");
        }
    }
//----------------------END RESOURCES HANDLERS----------------------------------
    
    
    // MISSING RESOURCE APP LOGIC
    /**
     * Should return 0 if the ID passed is not a homosapien
     * @param hsUID
     * @return 
     */
    public int getWeight(int hsUID){
        int itemWeight = communicate.selectIntSumByUID("weight", lookup.hsitemsTableName, hsUID);
        int resourceWeight = 0;
        return itemWeight + resourceWeight;
    }
}
