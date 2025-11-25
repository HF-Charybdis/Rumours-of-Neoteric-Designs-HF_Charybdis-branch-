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

public class PlasmaAcceleratorSound7s implements EveryFrameWeaponEffectPlugin {

    //Why the hell is required a fucking everyframe for the game stop crashing with looping sounds? Fuck...

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }
        int alpha_value1 = Math.round(weapon.getChargeLevel() * 50f);

        if (alpha_value1 > 255); {
            alpha_value1 = 255;
        }
        if (weapon.getChargeLevel() > 0.975f) {
            MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star1.png"), weapon.getFirePoint(0), new Vector2f(),
                    new Vector2f(50f, 25f),
                    new Vector2f(800f * weapon.getChargeLevel(), 800f * 2f * weapon.getChargeLevel()),
                    weapon.getCurrAngle() - 90f,
                    0f,
                    new Color(154, 243, 255, alpha_value1),
                    true,
                    0.07f,
                    0f,
                    0.14f);

        }

        if (weapon.getCooldownRemaining() == 0 && weapon.getChargeLevel() > 0.2f) {
            float pitch = weapon.getChargeLevel();
            Global.getSoundPlayer().playLoop("Plasma_accelerator7s_loop", weapon.getId(), pitch, 0.7f * weapon.getChargeLevel(), weapon.getLocation(), new Vector2f(0, 0), 0.5f, 0.2f);

        }

    }
}







        


