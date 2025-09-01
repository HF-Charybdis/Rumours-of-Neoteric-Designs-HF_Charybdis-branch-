package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class PlasmaAcceleratorEffect7s implements BeamEffectPlugin {

    /*static {
        fireInterval = new IntervalUtil(0.07f, 0.2f);
    }*/
    private IntervalUtil fireInterval = new IntervalUtil(0.25f, 1.75f);
    private boolean wasZero = true;
    boolean runOnce = false;


    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        //CombatEntityAPI target = beam.getDamageTarget();
        WeaponAPI weapon = beam.getWeapon();
        float range = beam.getBrightness() * 800f;
        int alpha_value1 = Math.round(beam.getBrightness() * 50f);

        if (alpha_value1 > 255); {
            alpha_value1 = 255;
        }

        MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star1.png"), beam.getFrom(), new Vector2f(),
                new Vector2f(50f, 25f),
                new Vector2f(range * 2f, range),
                weapon.getCurrAngle() - 90f,
                0f,
                new Color(154, 243, 255, alpha_value1),
                true,
                0.07f,
                0f,
                0.14f);

        if (beam.getBrightness() >= 1f) {
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);

                //boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
                Vector2f point = beam.getRayEndPrevFrame();

                    if (!runOnce) {
                        runOnce = true;
                        Global.getSoundPlayer().playSound("Plasma_accelerator7s_fire", 0.9f, 1.35f, beam.getFrom(), new Vector2f());
                        engine.spawnProjectile(weapon.getShip(), weapon, "Plasma_accelerator7s_projectile", beam.getFrom(), weapon.getCurrAngle(), new Vector2f());
                        if (weapon.getChargeLevel() >= 0.5f) {
                            beam.setWidth(beam.getWidth() * (-1f + (weapon.getChargeLevel() * 2.5f)));
                        }
                    }
        } else {
            runOnce = false;
        }
    }
}





