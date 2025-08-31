package data.hullmods;

import com.fs.starfarer.api.Global;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import com.fs.starfarer.api.combat.BaseHullMod;

public class ElipsionSwitcher7s extends BaseHullMod
{
    private final Map<Integer, String> SWITCH1;
    public ElipsionSwitcher7s() {

        (this.SWITCH1 = new HashMap<Integer, String>()).put(0, "defensemode7s");
        this.SWITCH1.put(1, "attackmode7s");
        this.SWITCH1.put(2, "chasemode7s");

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
            if (!ship.getVariant().hasHullMod("defensemode7s") && !ship.getVariant().hasHullMod("attackmode7s") && !ship.getVariant().hasHullMod("chasemode7s")) {
                if (ship.getVariant().hasHullMod("dummydefense7s")) {
                    ship.getVariant().addMod("attackmode7s");
                    ship.getVariant().addMod("dummyattack7s");
                    ship.getVariant().removeMod("dummydefense7s");
                } else if (ship.getVariant().hasHullMod("dummyattack7s")) {
                    ship.getVariant().addMod("chasemode7s");
                    ship.getVariant().addMod("dummychase7s");
                    ship.getVariant().removeMod("dummyattack7s");
                } else if (ship.getVariant().hasHullMod("dummychase7s")) {
                    ship.getVariant().addMod("defensemode7s");
                    ship.getVariant().addMod("dummydefense7s");
                    ship.getVariant().removeMod("dummychase7s");
                }

            }
        }

        if (!ship.getVariant().hasHullMod("defensemode7s") && !ship.getVariant().hasHullMod("attackmode7s") && !ship.getVariant().hasHullMod("chasemode7s") && !ship.getVariant().hasHullMod("dummydefense7s") && !ship.getVariant().hasHullMod("dummyattack7s") && !ship.getVariant().hasHullMod("dummychase7s")) {
            ship.getVariant().addMod("defensemode7s");
            ship.getVariant().addMod("dummydefense7s");
        }
    }



    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats = ship.getMutableStats();
    }
    
    public boolean isApplicableToShip(final ShipAPI ship) {

        return ship.getHullSpec().getHullId().equals("elipsion7s");
    }
}
