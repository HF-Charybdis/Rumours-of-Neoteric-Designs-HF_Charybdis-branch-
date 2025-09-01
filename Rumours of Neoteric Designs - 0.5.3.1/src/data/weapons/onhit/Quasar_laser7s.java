package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.combat.entities.Ship;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class Quasar_laser7s implements BeamEffectPlugin {

    /*static {
        fireInterval = new IntervalUtil(0.07f, 0.2f);
    }*/
    private IntervalUtil fireInterval = new IntervalUtil(0.25f, 1.75f);
    private boolean wasZero = true;
    boolean runOnce = false;

    protected Object STATUSKEY1 = new Object();

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {

        CombatEntityAPI target = beam.getDamageTarget();
        WeaponAPI weapon = beam.getWeapon();
        ShipAPI ship = weapon.getShip();
        ShipSystemAPI system = ship.getSystem();
        if (engine == null || engine.isPaused()) {
            return;
        }
        if (!ship.isAlive()) return;
        if (beam.getBrightness() > 0f) {
            int alpha_value1 = Math.round(system.getEffectLevel() * 15);

            if (alpha_value1 > 255); {
                alpha_value1 = 255;
            }
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);
            MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star1.png"), beam.getTo(), new Vector2f(),
                    new Vector2f(200, 100),
                    new Vector2f(2000 * system.getEffectLevel(), 1000 * system.getEffectLevel()),
                    weapon.getCurrAngle() - 45f,
                    0f,
                    new Color(100, 255, 178, alpha_value1),
                    true,
                    0.2f,
                    0f,
                    0.4f);

            int intensity = Math.round(system.getEffectLevel() * 7f);
            Color c = new Color(179, 255, 215,255);
            for (int x = 0; x < intensity; x++) {
                Global.getCombatEngine().addHitParticle(MathUtils.getRandomPointOnCircumference(beam.getTo(), 25f), Vector2f.sub(MathUtils.getRandomPointOnCircumference(beam.getTo(), 120f), beam.getTo(), null), 4.5f, 1f, 0.5f, c);

            }

        }

    }
}








