package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

public class RavonCombatHandler extends BaseEveryFrameCombatPlugin {

    /**
     * Yeah, a spriter can mess with this as well (although that I just did what others already did after all)
     **/

    public static final Map<String, ProjData> PROJ_IDS = new HashMap<>();

    static {
        PROJ_IDS.put("Repulser7s_shot", new ProjData(null));
        PROJ_IDS.put("Slagga7s_shot", new ProjData(null));
        PROJ_IDS.put("Plasma_accelerator7s_projectile_shot", new ProjData(null));
    }

    private Map<DamagingProjectileAPI, Vector2f> Repulser_shot = new HashMap<>();
    private Map<DamagingProjectileAPI, Vector2f> Slagga7s_shot = new HashMap<>();
    private Map<DamagingProjectileAPI, Vector2f> Plasma_accelerator7s_projectile_shot = new HashMap<>();


    public static class ProjData {

        public String newWeaponId; // can be null, in which case projectile is not replaced
        public boolean muzzleFlash; // only needed if true (for flash)
        public int particles; // only needed for flash
        public Color pColor; // only needed for flash
        public float pMaxSize; // only needed for flash, min size = 20%
        public float pMaxSpeed; // only needed for flash, min speed = 50%
        public float pMaxAngle; // only needed for flash, full arc width = this * 2
        public float pDur; // only needed for flash, in seconds
        public float pBright; // only needed for flash, 0.0 to 1.0

        public ProjData(String newWeaponId) {
            add(newWeaponId, false, null, null, null, null, null, null, null);
        }

        public ProjData(String newWeaponId, boolean flash, Integer particles, Color pColor, Float pMaxSize, Float pMaxSpeed, Float pMaxAngle, Float pDur, Float pBright) {
            add(newWeaponId, flash, particles, pColor, pMaxSize, pMaxSpeed, pMaxAngle, pDur, pBright);
        }

        private void add(String newWeaponId, boolean flash, Integer particles, Color pColor, Float pMaxSize, Float pMaxSpeed, Float pMaxAngle, Float pDur, Float pAlpha) {
            this.newWeaponId = newWeaponId;
            this.particles = particles != null ? particles : 0;
            this.pColor = pColor != null ? pColor : new Color(255, 255, 255, 255);
            this.pMaxSize = pMaxSize != null ? pMaxSize : 0f;
            this.pMaxSpeed = pMaxSpeed != null ? pMaxSpeed : 0f;
            this.pMaxAngle = pMaxAngle != null ? pMaxAngle : 0f;
            this.pDur = pDur != null ? pDur : 0f;
            this.pBright = pAlpha != null ? pAlpha : 0f;
        }
    }

    private int count;

    @Override
    public void init(CombatEngineAPI engine) {
        count = 0;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null || engine.isPaused()) {
            return;
        }

        Color Just_for_picking = new Color(255, 222, 216,255);

        float dur = engine.getTimeMult().getMult();

        List<DamagingProjectileAPI> projectiles = engine.getProjectiles();
        int size = projectiles.size();
        for (int i = 0; i < size; i++) {
            DamagingProjectileAPI proj = projectiles.get(i);
            String projId = proj.getProjectileSpecId();

            // handle all the special stuff for special projs we caught
            if (engine.isEntityInPlay(proj) && PROJ_IDS.containsKey(projId)) {

                // setup stuff
                ProjData data = PROJ_IDS.get(projId);
                ShipAPI source = proj.getSource();
                WeaponAPI weapon = proj.getWeapon();
                Vector2f loc = proj.getLocation();
                Vector2f vel = proj.getVelocity();
                float angle = proj.getFacing();
                Vector2f shipVel = proj.getSource().getVelocity();
                Vector2f zero = new Vector2f(0, 0);

                // general proj replacement handler
                if (data.newWeaponId != null) {
                    engine.removeEntity(proj);
                    engine.spawnProjectile(source, weapon, data.newWeaponId, loc, angle, shipVel);
                }

                // special case-by-case switcher
                switch (projId) {
                    case "Repulser7s_shot":
                        if (weapon == null) {
                            break;
                        }

                        float Fuse_range = 25f;
                        count += 1;
                        if (count >= (4 / dur)) {
                            count = 0;
                        }
                        float spin = 0f;
                        float spin_direction = MathUtils.getRandomNumberInRange(1, 2);
                        if (spin_direction == 1) spin = 1800f;
                        if (spin_direction == 2) spin = -1800f;
                        float Range = weapon.getRange();
                        float Random = (float) Math.random();
                        float Random_value = Random * 10f;
                        if (Random_value >= 0.95f) Random_value = 0.95f;
                        float Range_from_gun = MathUtils.getDistance(proj, weapon.getLocation());
                        boolean Repulse = false;

                        int Threats = 0;
                        for (CombatEntityAPI stuff_in_map : CombatUtils.getEntitiesWithinRange(proj.getLocation(), Fuse_range)) {
                            if (stuff_in_map.getOwner() != proj.getOwner()) {
                                if (stuff_in_map instanceof ShipAPI || stuff_in_map instanceof AsteroidAPI || stuff_in_map instanceof MissileAPI) {
                                    //Phased targets, and targets with no collision, are ignored
                                    if (stuff_in_map instanceof ShipAPI) {
                                        if (((ShipAPI) stuff_in_map).isPhased()) {
                                            continue;
                                        }
                                    }
                                    if (stuff_in_map.getCollisionClass().equals(CollisionClass.NONE)) {
                                        continue;
                                    }
                                    Threats++;
                                }
                            }
                        }
                        if (Range_from_gun >= Range * Random_value || Threats > 0) {
                            Repulse = true;
                        }

                        if (Repulse) {
                            // handle visuals
                            engine.spawnExplosion(loc, zero, new Color(153, 255, 207, 255), 75f, 0.5f);
                            Global.getSoundPlayer().playSound("shock_repeater_fire", 0.7f, 0.5f, loc, zero);
                            MagicRender.battlespace(Global.getSettings().getSprite("graphics/fx/seven_aura4.png"), loc, zero,
                                    new Vector2f(50f, 50f),
                                    new Vector2f(400f, 400f),
                                    MathUtils.getRandomNumberInRange(0f, 720f),
                                    spin,
                                    new Color(154, 243, 255, 255),
                                    true,
                                    0.07f,
                                    0f,
                                    0.14f);
                            if (!CombatUtils.getEntitiesWithinRange(proj.getLocation(), 999f).isEmpty()) {
                                for (CombatEntityAPI stuff : CombatUtils.getEntitiesWithinRange(proj.getLocation(), 150f)) {
                                    if (MathUtils.getDistance(proj, stuff) <= 50f) {
                                        if (stuff.getOwner() != proj.getOwner() || stuff.getCollisionClass() == CollisionClass.SHIP) {
                                            DamagingProjectileAPI e = engine.spawnDamagingExplosion(createExplosionSpec(), source, loc);
                                            e.addDamagedAlready(stuff);
                                            if (stuff.getCollisionClass() != CollisionClass.SHIP) {
                                                CombatUtils.applyForce(stuff, VectorUtils.getAngle(stuff.getLocation(), proj.getLocation()), -50f);
                                            }
                                        }
                                    }
                                }
                            }
                            engine.removeEntity(proj);

                        }
                        break;

                }

                // special case-by-case switcher
                switch (projId) {
                    case "Plasma_accelerator7s_projectile_shot":
                        if (weapon == null) {
                            break;
                        }
                        //float direction1 = proj.getFacing() - 55f;
                        //float direction2 = proj.getFacing() + 55f;
                        float Range = weapon.getRange();
                        float Range_from_gun = MathUtils.getDistance(proj, weapon.getLocation());

                        if (Range_from_gun >= Range * 1.1f) {
                            engine.spawnExplosion(loc, zero, new Color(153, 255, 240, 208), 55f, 0.3f);
                            engine.removeEntity(proj);
                        }


                        break;

                }

                // special case-by-case switcher
                switch (projId) {
                    case "Slagga7s_shot":
                        if (weapon == null) {
                            break;
                        }
                        Vector2f vel_flares = new Vector2f(vel.x * 0.05f,vel.y * 0.05f);
                        float spread1 = proj.getFacing() + MathUtils.getRandomNumberInRange(-80, 80);
                        float Range = weapon.getRange();
                        float Random = (float) Math.random();
                        float Random_value = Random * 5f;
                        if (Random_value >= 0.95f) Random_value = 0.95f;
                        float Range_from_gun = MathUtils.getDistance(proj, weapon.getLocation());
                        boolean flare = false;
                        if (Range_from_gun * 0.7f >= Range * Random_value) {
                            flare = true;
                        }

                        if (flare) {
                            for (int g = 0; g < MathUtils.getRandomNumberInRange(1,1); g++) {
                                engine.spawnProjectile(weapon.getShip(), weapon, "Slagga_flarer7s", proj.getLocation(), spread1, vel_flares);
                            }
                        }
                        if (Range_from_gun >= Range * 1.1f) {
                            engine.spawnExplosion(loc, zero, new Color(255, 165, 153, 208), 40f, 0.2f);
                            engine.removeEntity(proj);
                        }

                        }
                        break;

                }
            }
        }


    public DamagingExplosionSpec createExplosionSpec() {
        float damage = 0;
        float damage2 = 0;
        float radius = 0;
        float radius2 = 0;
        float particlesizemin = 0;
        float particlesizerange = 0;
        float duration = 0;
        int count = 0;
        int alpha1 = 0;
        int alpha2 = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        List<DamagingProjectileAPI> projectiles = Global.getCombatEngine().getProjectiles();
        int size = projectiles.size();
        for (int i = 0; i < size; i++) {
            DamagingProjectileAPI proj = projectiles.get(i);
            WeaponAPI weapon = proj.getWeapon();
            String projId = proj.getProjectileSpecId();
            if (Global.getCombatEngine().isEntityInPlay(proj) && PROJ_IDS.containsKey(projId)) {
                ProjData data = PROJ_IDS.get(projId);
                if (data.newWeaponId != null) {
                    Global.getCombatEngine().removeEntity(proj);
                    Global.getCombatEngine().spawnProjectile(proj.getSource(), weapon, data.newWeaponId, proj.getLocation(), proj.getFacing(), proj.getSource().getVelocity());
                }

                switch (projId) {
                    case "Repulser7s_shot":
                        if (weapon == null) {
                            break;
                        }
                        damage = 40;
                        damage2 = 20;
                        radius = 100;
                        radius2 = 25;
                        particlesizemin = 2f;
                        particlesizerange = 2f;
                        duration = 0.1f;
                        count = 100;
                        alpha1 = 1;
                        alpha2 = 1;
                        //blu
                        red = 154;
                        green = 243;
                        blue = 255;
                        break;
                }
                DamagingExplosionSpec spec = new DamagingExplosionSpec(
                        duration, // duration
                        radius, // radius
                        radius2, // coreRadius
                        damage, // maxDamage
                        damage2, // minDamage
                        CollisionClass.PROJECTILE_FF, // collisionClass
                        CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                        particlesizemin, // particleSizeMin
                        particlesizerange, // particleSizeRange
                        duration, // particleDuration
                        count, // particleCount
                        new Color(red, green, blue, alpha1), // particleColor
                        new Color(red, green, blue, alpha2)  // explosionColor
                );

                spec.setDamageType(weapon.getDamageType());
                spec.setUseDetailedExplosion(false);
                spec.setSoundSetId("");
                return spec;
            }
        }
        return null;
    }


}
