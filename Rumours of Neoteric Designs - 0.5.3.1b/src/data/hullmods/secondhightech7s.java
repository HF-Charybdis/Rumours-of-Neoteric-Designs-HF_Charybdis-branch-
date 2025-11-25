package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;


public class secondhightech7s extends BaseHullMod {

    public static Color LIGHTNING_CORE1 = new Color(200, 174, 252, 61);
    public static Color LIGHTNING_CORE2 = new Color(200, 174, 252, 153);
    public static Color NEBULA = new Color(200, 174, 252, 50);
    public static Color SHIELD = new Color(192, 125, 255, 75);

    private int shieldindex = 0;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        ShieldAPI shield = ship.getShield();
        float radius = shield.getRadius();

        String innersprite;
        String outersprite;


        if (radius >= 256.0F) {
            innersprite = "graphics/fx/seven_shields256.png";
            outersprite = "graphics/fx/shields256ringd.png";
        } else if (radius >= 128.0F) {
            innersprite = "graphics/fx/seven_shields128.png";
            outersprite = "graphics/fx/shields128ringc.png";
        } else {
            innersprite = "graphics/fx/seven_shields64.png";
            outersprite = "graphics/fx/shields64ring.png";
        }
        shield.setRadius(radius, innersprite, outersprite);


    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats = ship.getMutableStats();
        ShieldAPI shield = ship.getShield();
    }
}




