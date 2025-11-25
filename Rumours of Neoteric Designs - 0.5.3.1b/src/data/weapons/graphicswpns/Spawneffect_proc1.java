package data.weapons.graphicswpns;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class Spawneffect_proc1 implements EveryFrameWeaponEffectPlugin {

    public static final Color JITTER_COLOR = new Color(90, 255, 217,55);
    public static final Color JITTER_UNDER_COLOR = new Color(90, 255, 230,155);
    public static Color EXPLOSION = new Color(190, 247, 232, 150);
    public static Color LIGHTNING_FRINGE_COLOR = new Color(99, 255, 226, 175);


    private boolean active = false;

    boolean firstRun = true;
    IntervalUtil interval = new IntervalUtil(0f, 0.1f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();

        ShieldAPI shield = ship.getShield();

        MutableShipStatsAPI stats = ship.getMutableStats();



        if (!ship.isEngineBoostActive() || ship.isForceHideFFOverlay()) {
            if (!active) {
                active = true;
                ship.setSprite("Mayawati", "sprite7s");
                ship.getSpriteAPI().setCenter(48, 53);
                ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
                ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
                ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());
                ship.useSystem();
                Global.getCombatEngine().spawnExplosion(ship.getLocation(), ship.getVelocity(), EXPLOSION, 280f, 0.85f);
                Global.getCombatEngine().addSmoothParticle(ship.getLocation(), ship.getVelocity(), 140f, 0.4f, 0.5f, LIGHTNING_FRINGE_COLOR);
                Global.getSoundPlayer().playSound("Illusia_spawn", 1.0f,0.5f, ship.getLocation(), ship.getVelocity());


            }
        } else if(ship.isEngineBoostActive() || !ship.isForceHideFFOverlay()){
            active = false;
        }

    }
}







        


