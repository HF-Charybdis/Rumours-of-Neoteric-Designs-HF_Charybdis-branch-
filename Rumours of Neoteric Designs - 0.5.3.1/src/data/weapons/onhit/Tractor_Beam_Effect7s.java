package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class Tractor_Beam_Effect7s implements BeamEffectPlugin {

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
        int alpha_value1 = Math.round(beam.getBrightness() * 50f);

        if (alpha_value1 > 255); {
            alpha_value1 = 255;
        }
        if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);
            MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star1.png"), beam.getTo(), new Vector2f(),
                    new Vector2f(50f, 25f),
                    new Vector2f(200, 100),
                    weapon.getCurrAngle() - 90f,
                    0f,
                    new Color(154, 243, 255, alpha_value1),
                    true,
                    0.07f,
                    0f,
                    0.14f);
            float power = 0;
            if (MathUtils.getDistance(target,weapon.getShip()) >= 125f) {
                power = 15f;
            }
            else if (MathUtils.getDistance(target,weapon.getShip()) < 125f) {
                power = -10f;
            }

            float force = 1f * (((power) * (1f + (dur * 0.2f))) - (0.00025f * (MathUtils.getDistance(weapon.getLocation(), target.getLocation()))));
            if (force < 0) force = 0;
            CombatUtils.applyForce(target, VectorUtils.getAngle(target.getLocation(), weapon.getLocation()), force);

            if (Global.getCombatEngine().getPlayerShip() == target) {
                Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
                        "graphics/icons/hullsys/damper_field.png", "Tractor beam",
                        "Ship is being pulled in!", true);
            }

        }

    }
}








