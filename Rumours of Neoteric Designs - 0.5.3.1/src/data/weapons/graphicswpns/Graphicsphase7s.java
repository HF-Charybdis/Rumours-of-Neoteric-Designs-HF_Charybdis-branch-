package data.weapons.graphicswpns;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import java.awt.*;

public class Graphicsphase7s implements EveryFrameWeaponEffectPlugin {

    float index = 0;
    public static Color JITTER = new Color(130, 207, 180, 160);
    public static Color SMOKING = new Color(141, 166, 156, 25);
    public static Color SMOKING_2 = new Color(134, 186, 158, 90);

    boolean firstRun = true;
    IntervalUtil interval = new IntervalUtil(0f, 0.1f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        ShipAPI ship = weapon.getShip();
        if (!ship.isAlive()) return;



        if (ship.getEngineController().isAccelerating() || ship.getEngineController().isStrafingLeft() || ship.getEngineController().isStrafingRight()) {
            for (ShipEngineControllerAPI.ShipEngineAPI w : ship.getEngineController().getShipEngines()) {
                //  engine.addNegativeSwirlyNebulaParticle(w.getLocation(),new Vector2f(0f,0f), w.getEngineSlot().getWidth() * 0.18f + 5f ,w.getEngineSlot().getWidth() * 0.18f + 5f , 1f, 0.2f, 0.45f+w.getEngineSlot().getLength()*0.0045f, FRINGE_COLOR2);

                if (!ship.isPhased()) {
                    if (!ship.isFighter() || !ship.isDrone()) {
                        float time = Math.round(ship.getMutableStats().getTimeMult().getMult());
                        //engine.addNebulaParticle(new Vector2f(w.getLocation().getX(), w.getLocation().getY() - 5f), new Vector2f(0f, 0f), w.getEngineSlot().getWidth() * 0.18f + 5f, w.getEngineSlot().getWidth() * 0.18f + 5f, -0.2f, 0.5f, 0.75f + w.getEngineSlot().getLength() * 0.002f / time, new Color(161, 84, 77, 21));
                        //engine.addNebulaParticle(new Vector2f(w.getLocation().getX(), w.getLocation().getY() - 5f), new Vector2f(0f, 0f), w.getEngineSlot().getWidth() * 0.18f + 5f, w.getEngineSlot().getWidth() * 0.18f + 5f, -0.2f, 0.5f, 0.75f + w.getEngineSlot().getLength() * 0.01f / time, new Color(119, 24, 140, 10));
                    }
                }
            }
        }

        if (ship.isPhased()) {
            if (interval.intervalElapsed()) {
                Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.5f),
                        new Vector2f(0f, 0f),
                        ship.getCollisionRadius() / 3f,
                        4f,
                        -0.2f,
                        0.5f,
                        0.4f,
                        SMOKING);
                Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.75f), new Vector2f(0f, 0f), ship.getCollisionRadius() / 12f, 8f, -0.2f, 0.5f, 0.7f, SMOKING_2);

            }
        }
    }
}
