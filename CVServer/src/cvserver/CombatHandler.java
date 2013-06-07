/**
 *
 * @author Tenari
 */
package cvserver;

import java.util.ArrayList;

public class CombatHandler {
    
    CustomCommunication communicate;
    OrganismHandler org;
    HomosapienHandler hs;
    LookupConfig lookup;
    
    ArrayList<NumberPair> fights = new ArrayList<>();
    
    long startTimeFight = System.currentTimeMillis();
    
    public CombatHandler(CustomCommunication c) {
        communicate = c;
        // Load the configuration manager class.
        lookup = new LookupConfig();
        
        // Initialize the org handler.
        org = new OrganismHandler(communicate);
        // Initialize the hs handler.
        hs = new HomosapienHandler(communicate);
    }
    
    /**
     * // player with fastest attackspeed should roll to hit
            // if he hit
                // see if opponent was blocking there --> do blocking things.
                        // or if opponent used counter --> do counter things.
                // determine damage
                // make it harder for opponent to make hit
            // else
                // make easier for opponent to hit
            // Then player with slowest attackspeed rolls to hit.
                // and rolls damage
     */
    public void updateAllFights(){
      if((System.currentTimeMillis()-startTimeFight) >= lookup.fightRoundLength) {
        for(NumberPair i : fights) {
            // Prep the fighters
            org.setRealSkills(i.getNumOne());
            org.setRealSkills(i.getNumTwo());
            
            // Determine combat order
            int fasterGuyUID = getFastestAttacker(i);
            int slowerGuyUID = i.getOther(fasterGuyUID);
            
            // Resolve the first guy's attack
            int hitSpot1 = rollToHit(fasterGuyUID);
            fightOneRound(hitSpot1, fasterGuyUID, slowerGuyUID);
            
            // Reslove the second guy's attack
            int hitSpot2 = rollToHit(slowerGuyUID);
            fightOneRound(hitSpot2, slowerGuyUID, fasterGuyUID);
            
            // send these two their update info.
        }
        startTimeFight=System.currentTimeMillis();
      }
    }
    
    public void startFight(int agressorUID, int opponentRelativeX, int opponentRelativeY){
        int opponentUID = communicate.selectSingleIntByXAndY("uid", lookup.movementTableName, opponentRelativeX, opponentRelativeY);
        fights.add(new NumberPair(agressorUID,opponentUID));
    }
    
    public void endFight(int attackerUID, int defenderUID){
        fights.remove(new NumberPair(attackerUID,defenderUID));
    }
    
    private void fightOneRound(int hitSpot, int attackerUID, int defenderUID){
        if (hitSpot != lookup.missCode){      // If he didn't miss. (blocks => auto-miss)
            if (blocked(hitSpot, defenderUID)) {
                stun(attackerUID);
            } else if (countered(hitSpot, defenderUID)) {
                applyCounterBuff(defenderUID);
                // Do we need to change attacker.hitStatus to "miss"?
            } else {
                dealDamage(determineDamage(attackerUID, defenderUID), hitSpot, defenderUID);
                applyHitNerf(defenderUID);
            }
        } else {
            applyMissBuff(defenderUID);
        }
    }

    /**
     * Given a NumberPair of a fight, returns the int UID of the combatant with
     * the fastest attack speed.
     * @param fight - The NumberPair containing the UIDs of two combatants
     * @return the UID of the fastest combatant.
     */
    private int getFastestAttacker(NumberPair fight) {
        // Use hs, b/c it will work on both org and hs UIDs
        double firstSpeed = hs.getAttackSpeed(fight.getNumOne());
        double secondSpeed = hs.getAttackSpeed(fight.getNumTwo());         
        
        if (firstSpeed > secondSpeed) {
            return fight.getNumOne();
        } else {
            return fight.getNumTwo();
        }
    }

    /**
     * Given an orgUID, uses probability/math to determine where the attack lands.
     *  Just because an org aims at the head, does not exclude the possibility 
     *  that it will hit arms instead.
     * @param attackerUID
     * @param defenderUID
     * @return "miss": block, miss "head":hit on head...
     */
    private int rollToHit(int attackerUID, int defenderUID) {
    // affected by:
        // attSkill of attacker + (equipped)itemBuffs.
        double attSkill = org.getAttSkill(attackerUID) + hs.getItemMODTYPEBuffs(lookup.attSkillMod, attackerUID);
        // aim location
        int aim = org.getAttackTarget(attackerUID);
        // usedWeaponClass
        int wepClass = hs.getWeaponClassSpeed(attackerUID); 
        // Raw weapon proficiency + proficiency buffs from items.
        double prof = hs.getCurrentProficiency(wepClass, attackerUID) + hs.getItemMODTYPEBuffs(lookup.getModCode(wepClass), attackerUID);
        // enemy defSkill
        double defSkill = org.getDefSkill(defenderUID);
        // Other miscellaneous item bonuses
        int itemBuff = 0;
        
        if () {
            
        } else {
            return lookup.missCode;
        }
    }

    private boolean blocked(int hitSpot, int defenderUID) {
        
    }

    private boolean countered(int hitSpot, int defenderUID) {
        
    }

    private int determineDamage(int attackerUID, int defenderUID) {
        
    }

    private void dealDamage(int damageAmount, int hitSpot, int defenderUID) {
        ()
    }

    private void applyHitNerf(int orgID) {
        ()
    }

    private void applyMissBuff(int orgID) {
        ()
    }

    private void stun(int orgID) {
        ()
    }

    private void applyCounterBuff(int defenderUID) {
        ()
    }
}
