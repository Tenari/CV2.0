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

    public boolean isHomosapien(int uid) {
        return (-1 != communicate.selectSingleIntByUID("team", lookup.homosapienTableName, uid));
    }
    /**
     * Returns the attack speed of any uid, including those that are just organisms.
     * @param uid
     * @return 
     */
    @Override
    public double getAttackSpeed(int uid) {
        if (isHomosapien(uid)) {
            // depends on attack style, weapon class&proficiency, specific weapon
            int attStyle = getAttackStyle(uid);
            // Returns the speed of the weaponClass from lookup
            int wepClass = getWeaponClassSpeed(uid);
            // get proficiency
            double prof = getProficiency(wepClass, uid);
            // Calculate and return the attackSpeed
            return lookup.maxAttackSpeed /
                    ((attStyle * (wepClass /
                                 (1 + prof))) + getItemMODTYPEBuffs(lookup.attackSpeedMod, uid));  // getItemAttSpeedBuffs includes all buffs/debuff from items.
        } else {
            return super.getAttackSpeed(uid);
        }
    }

    /**
     * Returns the lookup.weaponClass Speed value of the equipped item.
     * @param uid
     * @return defaults to lookup.handsSpeed if no equipped weapon found.
     */
    public int getWeaponClassSpeed(int uid) {
        int[] equipped = getEquippedItemIDs(uid);
        if (equipped[0] == 0){      // there are no equipped items.
            // Default return handToHand
            return lookup.handsSpeed;
        } else {
            for (int i : equipped){
                int code = getItemDetail("code", equipped[i]);
                if (lookup.isWeapon(code)){
                    return lookup.getWeaponClassSpeed(code);
                }
            }
            // If no weapon class speed was returned so far, they must not have a weapon on
            // So we return handToHand
            return lookup.handsSpeed;
        }
    }
    
//----------------------------Proficiency Handlers----------------------------\\
    /**
     * Uses equipped weapon to determine which proficiency to return.
     * @param uid
     * @return double proficiency value
     */
    public double getCurrentProficiency(int uid){
        return getProficiency(getWeaponClassSpeed(uid), uid);
    }
    
    public double getProficiency(int wepClassSpeed, int uid) {
        if (wepClassSpeed == lookup.smallBladeSpeed) {
            return communicate.selectSingleDoubleByUID("smallblade", lookup.homosapienTableName, uid);
        } else if (wepClassSpeed == lookup.largeBladeSpeed) {
            return communicate.selectSingleDoubleByUID("largeblade", lookup.homosapienTableName, uid);
        } else if (wepClassSpeed == lookup.axeSpeed) {
            return communicate.selectSingleDoubleByUID("axe", lookup.homosapienTableName, uid);
        } else {  // wepClassSpeed == lookup.handsSpeed
            return getDoubleFromStatsTable("handToHand", uid);
        }
    }
    
    public boolean setProficiency(double newValue, int wepClassSpeed, int uid){
        if (wepClassSpeed == lookup.smallBladeSpeed) {
            return communicate.updateSingleDoubleByUID(lookup.homosapienTableName, "smallblade", newValue, uid);
        } else if (wepClassSpeed == lookup.largeBladeSpeed) {
            return communicate.updateSingleDoubleByUID(lookup.homosapienTableName, "largeblade", newValue, uid);
        } else if (wepClassSpeed == lookup.axeSpeed) {
            return communicate.updateSingleDoubleByUID(lookup.homosapienTableName, "axe", newValue, uid);
        } else {  // wepClassSpeed == lookup.handsSpeed
            return communicate.updateSingleDoubleByUID(lookup.statsTableName, "handToHand", newValue, uid);
        }
    }
//\\----------------------End Proficiency Handlers----------------------------//
    
    int getItemMODTYPEBuffs(int modType, int uid) {
        int buff = 0;
        int[] equipped = getEquippedItemIDs(uid);
        for (int i : equipped){
            if (lookup.attackSpeedMod == modType){
                buff += attackSpeedModVal(i);
            } else if (lookup.attSkillMod == modType) {
                buff += attackSkillModVal(i);
            }
            
        }
        return buff;
    }

    private int[] getEquippedItemIDs(int uid) {
        return communicate.selectIntArrayByUIDAnd("id", lookup.hsitemsTableName, "equipped", "1", uid); // 1=> true
    }

    private int attackSpeedModVal(int itemID) {
        if (lookup.attackSpeedMod == getItemDetail("mod1type", itemID)){
            return getItemDetail("mod1value",itemID);
        } else if (lookup.attackSpeedMod == getItemDetail("mod2type", itemID)){
            return getItemDetail("mod2value",itemID);
        } else if (lookup.attackSpeedMod == getItemDetail("mod3type", itemID)){
            return getItemDetail("mod3value",itemID);
        } else {
            return 0;
        }
    }
    private int attackSkillModVal(int itemID) {
        if (lookup.attSkillMod == getItemDetail("mod1type", itemID)){
            return getItemDetail("mod1value",itemID);
        } else if (lookup.attSkillMod == getItemDetail("mod2type", itemID)){
            return getItemDetail("mod2value",itemID);
        } else if (lookup.attSkillMod == getItemDetail("mod3type", itemID)){
            return getItemDetail("mod3value",itemID);
        } else {
            return 0;
        }
    }
    
    private int getItemDetail(String detailName, int itemID){
        return communicate.selectIntByCustomQuery(
                "SELECT "+detailName
                +" FROM "+lookup.hsitemsTableName
                +" WHERE id = "+itemID);
    }
}
