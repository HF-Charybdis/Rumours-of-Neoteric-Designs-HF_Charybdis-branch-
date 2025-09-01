package data.hullmods;

import com.fs.starfarer.api.Global;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import com.fs.starfarer.api.combat.BaseHullMod;

public class MayawatiSwitcher7s extends BaseHullMod
{
    private final Map<Integer, String> SWITCH1;
    public MayawatiSwitcher7s() {

        (this.SWITCH1 = new HashMap<Integer, String>()).put(0, "breakpoint7s");
        this.SWITCH1.put(1, "chasemode7s");

    }

    public void applyEffectsBeforeShipCreation(final ShipAPI.HullSize hullSize, final MutableShipStatsAPI stats, final String s) {
        ShipAPI ship = null;
        boolean player = false;
        int number = 0;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }

        boolean a = true;
        int hullmod1 = 0;
        for (int i = 0; i < this.SWITCH1.size(); ++i) {
            if (stats.getVariant().getHullMods().contains(this.SWITCH1.get(i))) {
                a = false;
                ++hullmod1;
            }
        }
        if (a) {
            if (!ship.getVariant().hasHullMod("breakpoint7s") && !ship.getVariant().hasHullMod("frame7s")) {
                if (ship.getVariant().hasHullMod("dummysynergy7s")) {
                    ship.getVariant().addMod("frame7s");
                    ship.getVariant().addMod("dummyframe7s");
                    ship.getVariant().removeMod("dummysynergy7s");
                } else if (ship.getVariant().hasHullMod("dummyframe7s")) {
                    ship.getVariant().addMod("breakpoint7s");
                    ship.getVariant().addMod("dummysynergy7s");
                    ship.getVariant().removeMod("dummyframe7s");
                }

            }
        }

        if (!ship.getVariant().hasHullMod("breakpoint7s") && !ship.getVariant().hasHullMod("frame7s") && !ship.getVariant().hasHullMod("dummysynergy7s") && !ship.getVariant().hasHullMod("dummyframe7s")) {
            ship.getVariant().addMod("breakpoint7s");
            ship.getVariant().addMod("dummysynergy7s");
        }
    }



    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats = ship.getMutableStats();
    }
    
    public boolean isApplicableToShip(final ShipAPI ship) {

        return ship.getHullSpec().getHullId().equals("Mayawati7s");
    }
}
