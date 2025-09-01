package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicAnim;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class SoulcrierEffect7s implements EveryFrameWeaponEffectPlugin {

    boolean firstRun = true;
    IntervalUtil interval = new IntervalUtil(0.015f, 0.015f);
    int pulsating = 0;
    private static Color PARTICLE = new Color(178, 255, 221);

    private static Color NEBULA = new Color(190, 247, 232, 150);

    public static Color JITTER = new Color(130, 207, 180, 160);
    public static Color SMOKING = new Color(141, 166, 156, 25);
    public static Color SMOKING_2 = new Color(134, 186, 158, 90);

    boolean boom = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        
        ShipAPI ship = weapon.getShip();
        
        ShieldAPI shield = ship.getShield();

        MutableShipStatsAPI stats = ship.getMutableStats();




        float influence = (ship.getHardFluxLevel() * 2.5f); //Simply don't question it my guy
        if (influence >= 1.75f) {
            influence = 1.75f;
        }
        float firerate = 2.25f - influence;

            int intensity = Math.round(0f + (ship.getFluxLevel() * 4f));
            for (int x = 0; x < intensity; x++) {
                Global.getCombatEngine().addHitParticle(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 30f), Vector2f.sub(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 20f), ship.getLocation(), null), 5f, 1f, 2f, PARTICLE);
                //Global.getCombatEngine().addSmoothParticle(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 30f), Vector2f.sub(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 150f), ship.getLocation(), null), 5f, 0.25f, 0.5f, NEBULA);
            }
      if (ship.getVariant().hasHullMod("breakpoint7s")) {
          weapon.setRefireDelay(firerate);
      }

            /*
        if (ship.getFluxLevel() <= 0.05f) {

        }
        else if (ship.getFluxLevel() >= 0.05f) {
            weapon.setRefireDelay(firerate);
            if (weapon.getCooldownRemaining() >= 1f) {
                if (!ship.hasTag("ACTIVE7S") || ship.getFluxTracker().isOverloaded()) {
                    ship.setHitpoints(ship.getHitpoints() - (ship.getFluxLevel() * 1.15f));
                }
                if (ship.getHitpoints() <= 0) {
                    Global.getCombatEngine().applyDamage (ship, ship.getLocation(), 1, HIGH_EXPLOSIVE, 9999, true, false, ship);

                }
                /*
                if (ship.getTags().contains("ACTIVE")) {
                    weapon.setRefireDelay(12f);

                }


            }
        }
        */

        if (!ship.isAlive()) {
            if (!boom) {
                boom = true;
                Global.getCombatEngine().getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
                engine.spawnExplosion(ship.getLocation(), ship.getVelocity(), NEBULA, 700f, 5f);
                engine.addSmoothParticle(ship.getLocation(), ship.getVelocity(), 500f, 0.4f, 3f, NEBULA);
                Global.getSoundPlayer().playSound("custom_explosion_sevens", 1.05f, 0.8f, ship.getLocation(), ship.getVelocity());

                for (int c = 0; c < 6; c++) {
                    Global.getCombatEngine().getFleetManager(ship.getOriginalOwner()).spawnShipOrWing("Mayawati_wing", MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 2f), MathUtils.getRandomNumberInRange(0,360));
                }
                for (int i = 0; i < 400; i++) {
                    Global.getCombatEngine().addHitParticle(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 30f), Vector2f.sub(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 150), ship.getLocation(), null), MathUtils.getRandomNumberInRange(5, 10), 1f, MathUtils.getRandomNumberInRange(2, 4), PARTICLE);
                }
                Global.getCombatEngine().getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);
            }
        }

        if (ship.isPhased()) {

            ship.setJitterUnder(ship,JITTER,1f, 7, 10f,15f);
            Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.5f),
                    new Vector2f(0f, 0f),
                    ship.getCollisionRadius() / 3f,
                    8f,
                    -0.2f,
                    0.5f,
                    0.4f,
                    SMOKING);
            Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.75f), new Vector2f(0f, 0f), ship.getCollisionRadius() / 12f, 8f, -0.2f, 0.5f, 0.7f, SMOKING_2);

        }


    }
}







        


