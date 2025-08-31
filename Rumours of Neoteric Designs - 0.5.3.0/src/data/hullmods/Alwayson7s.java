package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class Alwayson7s extends BaseHullMod {

    public static Color JITTER = new Color(151, 207, 130, 160);
    private int index7 = 0;
    public static Color SMOKING_2 = new Color(134, 186, 166, 90);
    public static Color SMOKING = new Color(141, 165, 141, 2);
    private IntervalUtil Interval = new IntervalUtil(10f, 12f);

    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats = ship.getMutableStats();

        if (ship.isAlive()) {
            if (!ship.getSystem().isStateActive()) {
                ship.getSystem().forceState(ShipSystemAPI.SystemState.ACTIVE,0f);
            }


            if (ship.getFluxLevel() >= 0.99f) {
                Global.getCombatEngine().removeEntity(ship);
            }


        }
    }
}
