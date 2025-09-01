
package data.weapons.onhit;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;


public class RavonMuzzleFlash implements OnFireEffectPlugin {

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {


        CombatEntityAPI target = projectile.getSource();
        Vector2f point = projectile.getLocation();
        DamagingProjectileAPI e = engine.spawnDamagingExplosion(createExplosionSpec(projectile), projectile.getSource(), point);
        e.addDamagedAlready(target);
        // ||
        if (projectile.getWeapon().getId().equals("Repulser")) {
            engine.addSmokeParticle(point, weapon.getShip().getVelocity(), 20f, 0.5f, 0.2f, new Color(153, 255, 207, 80));
        }
        if (projectile.getWeapon().getId().equals("Slagga7s")) {
            engine.addSwirlyNebulaParticle(point,weapon.getShip().getVelocity(),55f,0.75f,0.5f,0.25f,0.4f,new Color(250, 179, 167, 202),true);
        }
    }
    public DamagingExplosionSpec createExplosionSpec(DamagingProjectileAPI projectile) {
        // "muzzle flash"
        float radius = 0;
        float particlesizemin = 0;
        float particlesizerange = 0;
        float duration = 0;
        int count = 0;
        int alpha1 = 0;
        int alpha2 = 0;
        int red = 0;
        int green = 0;
        int blue = 0;

        if (projectile.getWeapon().getId().equals("Repulser7s")) {
            radius = 20f;
            particlesizemin = 1f;
            particlesizerange = 4f;
            duration = 0.8f;
            count = 180;
            alpha1 = 90;
            alpha2 = 130;
            //blu
            red = 153;
            green = 255;
            blue = 207;
        }
        if (projectile.getWeapon().getId().equals("Slagga7s")) {
            radius = 25f;
            particlesizemin = 0.25f;
            particlesizerange = 3f;
            duration = 0.7f;
            count = 150;
            alpha1 = 85;
            alpha2 = 200;
            //250, 213, 167
            red = 250;
            green = 213;
            blue = 167;
        }
        /*
        else if (projectile.getWeapon().getId().equals("seven_redemoinho")) {
            radius = 8f;
            particlesizemin = 1f;
            particlesizerange = 2.5f;
            duration = 0.2f;
            count = 140;
            alpha1 = 40;
            alpha2 = 90;
            //yellow
            red = 250;
            green = 221;
            blue = 135;
        }

         */


        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.7f, // duration
                radius, // radius
                radius, // coreRadius
                0, // maxDamage
                0, // minDamage
                CollisionClass.NONE, // collisionClass
                CollisionClass.NONE, // collisionClassByFighter
                particlesizemin, // particleSizeMin
                particlesizerange, // particleSizeRange
                duration, // particleDuration
                count, // particleCount
                new Color(red, green, blue, alpha1), // particleColor
                new Color(red, green, blue, alpha2)  // explosionColor
        );

        spec.setDamageType(DamageType.HIGH_EXPLOSIVE);
        spec.setUseDetailedExplosion(false);
        spec.setSoundSetId("");
        return spec;
    }
}
