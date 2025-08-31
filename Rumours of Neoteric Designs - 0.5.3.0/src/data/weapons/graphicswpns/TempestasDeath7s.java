package data.weapons.graphicswpns;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.shipsystems.scripts.Tempestas_cloak7s;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import java.awt.*;
import data.shipsystems.scripts.Tempestas_cloak7s;

public class TempestasDeath7s implements EveryFrameWeaponEffectPlugin {

    boolean firstRun = true;
    IntervalUtil interval = new IntervalUtil(0f, 0.1f);
    boolean boom = false;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();

        ShieldAPI shield = ship.getShield();

        MutableShipStatsAPI stats = ship.getMutableStats();


        if (!ship.isAlive() && ship.getHardFluxLevel() != 0f) {
            if (!boom) {
                boom = true;

                Global.getCombatEngine().spawnExplosion(ship.getLocation(), ship.getVelocity(), Tempestas_cloak7s.JITTER, 3000, 7f);
                Global.getCombatEngine().addSmoothParticle(ship.getLocation(), ship.getVelocity(), 2000, 0.4f, 4f, Tempestas_cloak7s.JITTER);
                Global.getSoundPlayer().playSound("custom_explosion_sevens", 0.95f, 1.25f, ship.getLocation(), ship.getVelocity());
                for (int i = 0; i < 700; i++) {
                    Global.getCombatEngine().addHitParticle(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 90f), Vector2f.sub(MathUtils.getRandomPointOnCircumference(ship.getLocation(), 300), ship.getLocation(), null), MathUtils.getRandomNumberInRange(6, 12), 30f, MathUtils.getRandomNumberInRange(4, 8), Tempestas_cloak7s.FRINGE_COLOR);
                }
            }
        }
    }
}
