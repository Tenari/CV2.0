/**
 *
 * @author Tenari
 */
package cvserver;

import java.util.ArrayList;

public class CombatHandler {
    
    CustomCommunication communicate;
    OrganismHandler org;
    LookupConfig lookup;
    
    ArrayList<NumberPair> fights = new ArrayList<>();
    
    long startTimeFight = System.currentTimeMillis();
    
    public CombatHandler(CustomCommunication c) {
        communicate = c;
        // Load the configuration manager class.
        lookup = new LookupConfig();
        
        // Initialize the org handler.
        org = new OrganismHandler(communicate);
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
            int fasterGuyUID = getFastestAttackSpeed(i);
            int slowerGuyUID = i.getOther(fasterGuyUID);
            
            String hitSpot1 = rollToHit(fasterGuyUID);
            if (!hitSpot1.equals(lookup.missCode)){
                if (blocked(hitSpot1,i.getOther(fasterGuyUID))) {
                    // do blocking things
                } else if (countered(hitSpot1, slowerGuyUID)) {
                    // do counter things
                } else {
                    dealDamage(determineDamage(i), hitSpot1, slowerGuyUID);
                    applyHitNerf(slowerGuyUID);
                }
            }
            
            applyMissBuff(slowerGuyUID);
            
            String hitSpot2 = rollToHit(slowerGuyUID);
            if (!hitSpot2.equals(lookup.missCode)){
                if (blocked(hitSpot2, fasterGuyUID)) {
                    // do blocking things
                } else if (countered(hitSpot2, fasterGuyUID)) {
                    // do counter things
                } else {
                    dealDamage(determineDamage(i), hitSpot1, fasterGuyUID);
                }
            }
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
    
    public void fightOneRound(int fightIndex){
        fightOneRound(fights.get(fightIndex).getNumOne(), fights.get(fightIndex).getNumTwo());
    }
    public void fightOneRound(int attackerUID, int defenderUID){
        // Set the real Skills of both combatants, for accuracy of upcoming calculations
        org.setRealSkills(attackerUID);
        org.setRealSkills(defenderUID);
        
        // Determine where the attacker hit (or if he missed)
        String hitSpot = getAttackSpot(attackerUID, org.getDefSkill(defenderUID));
    }
    public String getAttackSpot(int attackerUID, double defenderDefSkill){
        return "miss";
    }

    private int getFastestAttackSpeed(NumberPair i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String rollToHit(int orgUID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean blocked(String hitSpot, int defenderUID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private boolean countered(String hitSpot, int defenderUID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int determineDamage(NumberPair i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void dealDamage(int damageAmount, String hitSpot, int defenderUID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void applyHitNerf(int orgID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void applyMissBuff(int orgID) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
