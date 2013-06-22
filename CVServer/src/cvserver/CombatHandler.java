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
            int hitSpot1 = rollToHit(fasterGuyUID, slowerGuyUID);
            fightOneRound(hitSpot1, fasterGuyUID, slowerGuyUID);
            
            // Reslove the second guy's attack
            int hitSpot2 = rollToHit(slowerGuyUID, fasterGuyUID);
            fightOneRound(hitSpot2, slowerGuyUID, fasterGuyUID);
            
            // send these two their update info.
        }
        startTimeFight=System.currentTimeMillis();
      }
    }
    
    /**
     * Attempts to start a fight between UID and the opponent at x,y
     * @param agressorUID
     * @param opponentRelativeX
     * @param opponentRelativeY
     * @return boolean success of start. true = fight started.
     */
    public boolean startFight(int agressorUID, int opponentRelativeX, int opponentRelativeY){
        int opponentUID = communicate.selectUIDByXAndYAndWorld(lookup.movementTableName, opponentRelativeX, opponentRelativeY, org.getWorld(agressorUID));
        if (opponentUID == -1) {
            // do nothing, because the fight failed to start.
            return false;
        } else {
            fights.add(new NumberPair(agressorUID,opponentUID));
            return true;
        }
    }
    
    public void endFight(int attackerUID, int defenderUID){
        fights.remove(new NumberPair(attackerUID,defenderUID));
    }
    
    private void fightOneRound(int hitSpot, int attackerUID, int defenderUID){
        if (hitSpot != lookup.missCode){      // If he didn't miss. (blocks => auto-miss)
            if (blocked(hitSpot, defenderUID)) {
                stun(attackerUID);
            } else if (countered(hitSpot, attackerUID, defenderUID)) {
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
     * Given an attackerUID, uses probability/math to determine where the attack lands.
     *  Just because an org aims at the head, does not exclude the possibility 
     *  that it will hit arms instead.
     * @param attackerUID
     * @param defenderUID
     * @return lookpu.missCode: block, miss; "lookup.headCode:hit on head...
     */
    private int rollToHit(int attackerUID, int defenderUID) {
        if (org.getAttackTarget(attackerUID) == lookup.missCode) {  // then he was stunned
            org.setAttackTarget(lookup.headCode, attackerUID);      // un-stun him
            return lookup.missCode;                                 // do the actual stunning. 
        }
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
        double defSkill = org.getDefSkill(defenderUID) + hs.getItemMODTYPEBuffs(lookup.defSkillMod, attackerUID);
        // Other miscellaneous item bonuses
        int itemBuff = 0;
        double roll = Math.random() * 100;
        roll = ((roll * (defSkill/attSkill)) - (2*(Math.log((1+prof)/wepClass))));
        if (lookup.headCode == aim) {
            return littleHitOddsHelper(roll, 10, 20, 25, 30);
        } else if (lookup.armsCode == aim) {
            return littleHitOddsHelper(roll, 8, 22, 28, 32);
        } else if (lookup.torsoCode == aim) {
            return littleHitOddsHelper(roll, 8, 16, 28, 35);
        } else if (lookup.legsCode == aim) {
            return littleHitOddsHelper(roll, 2, 8, 15, 33);
        } else {
            return lookup.missCode;
        }
    }
    
    private int littleHitOddsHelper(double roll, int head, int arms, int torso, int legs){
        if (roll < head) {
            return lookup.headCode;
        } else if (roll < arms){
            return lookup.armsCode;
        } else if (roll < torso){
            return lookup.torsoCode;
        } else if (roll < legs){
            return lookup.legsCode;
        } else {
            return lookup.missCode;
        }
    }

    private boolean blocked(int hitSpot, int defenderUID) {
        if (hitSpot == org.getIntFromCombatTable("attackTarget", defenderUID)){
            if (lookup.blockCode == org.getIntFromCombatTable("attackStyle", defenderUID)){
                return true;
            }
        }
        return false;
    }

    private boolean countered(int hitSpot, int attackerUID, int defenderUID) {
        // Checks for correct combo of attack targets. (hitSpot is the attacker's target)
        if (hitSpot == ((org.getAttackTarget(defenderUID) + 1) % 4)){
            int attackerStyle = org.getIntFromCombatTable("attackStyle", attackerUID);
            int defenderStyle = org.getIntFromCombatTable("attackStyle", defenderUID);
            
            // Checks for correct combo of light/med/heavy attacks.
            if ((attackerStyle == (defenderStyle - 1)) ||
                ((attackerStyle == lookup.lightAttackCode) &&
                 (defenderStyle == lookup.heavyAttackCode)) &&
                (attackerStyle != lookup.blockCode) ){
                return true;
            }
        }
        return false;
    }

    private int determineDamage(int attackerUID, int defenderUID) {
        int attackerStyle = org.getIntFromCombatTable("attackStyle", attackerUID);
        double attStr = org.getAttStr(attackerUID) + hs.getItemMODTYPEBuffs(lookup.attStrMod, attackerUID);
        double defStr = org.getDefStr(defenderUID) + hs.getItemMODTYPEBuffs(lookup.defStrMod, defenderUID);
        return (int)((Math.random() * attStr * attackerStyle)
                      - defStr
                      + hs.getItemMODTYPEBuffs(lookup.damageBonusMod, attackerUID)
                      - hs.getItemMODTYPEBuffs(lookup.damageResistMod, defenderUID));
    }

    private void dealDamage(int damageAmount, int hitSpot, int defenderUID) {
        if (hitSpot == lookup.headCode){
            org.setHead((org.getHead(defenderUID)-damageAmount), defenderUID);
        } else if(hitSpot == lookup.armsCode) {
            org.setArms((org.getArms(defenderUID)-damageAmount), defenderUID);
        } else if(hitSpot == lookup.torsoCode) {
            org.setTorso((org.getTorso(defenderUID)-damageAmount), defenderUID);
        } else if(hitSpot == lookup.legsCode) {
            org.setLegs((org.getLegs(defenderUID)-damageAmount), defenderUID);
        }
    }

    private void applyHitNerf(int orgID) {
        org.setAttSkill(org.getAttSkill(orgID)*0.6, orgID);
    }

    private void applyMissBuff(int orgID) {
        org.setAttSkill(org.getAttSkill(orgID)*1.2, orgID);
    }

    private void stun(int orgID) {
        org.setAttackTarget(lookup.missCode, orgID);
    }

    private void applyCounterBuff(int orgID) {
        org.setAttSkill(org.getAttSkill(orgID)*1.2, orgID);
        org.setAttStr(org.getAttStr(orgID)*1.2, orgID);
    }
    
    public boolean isFighting(int orgUID){
        for(NumberPair i : fights) {
            if ((i.getNumOne() == orgUID) || (i.getNumTwo() == orgUID)){
                return true;
            }
        }
        return false;
    }
}
