package data.weapons.graphicswpns;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import java.awt.*;

public class Pusher_plate_smoke7s implements EveryFrameWeaponEffectPlugin {

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

        if (ship.getVelocity().x > 350 || ship.getVelocity().y > 350) {
            Color start = new Color(117, 157, 143, 200);
            Color end = new Color(24, 140, 74, 120);
            float time = Math.round(ship.getMutableStats().getTimeMult().getMult());
            Vector2f location = VectorUtils.rotate(new Vector2f(-50,0),weapon.getCurrAngle());
            Color c = new Color(179, 255, 215,255);
            MagicRender.battlespace(
                    Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                    new Vector2f(location.getX()+ship.getLocation().getX(),location.getY()+ship.getLocation().getY()),
                    new Vector2f(0,0),
                    new Vector2f(ship.getSpriteAPI().getWidth(),ship.getSpriteAPI().getHeight()),
                    new Vector2f(0,0),
                    ship.getFacing()-90f,
                    0,
                    new Color(171, 255, 239, Math.round(5 * time)),
                    true,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0.1f * time,
                    0.1f * time,
                    1f * time,
                    CombatEngineLayers.ABOVE_SHIPS_LAYER
            );

        }
    }
}
