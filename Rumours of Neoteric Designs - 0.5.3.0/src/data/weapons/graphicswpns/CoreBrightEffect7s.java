package data.weapons.graphicswpns;

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

public class CoreBrightEffect7s implements EveryFrameWeaponEffectPlugin {


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused()) {
            return;
        }

        ShipAPI ship = weapon.getShip();

        if (ship.isPhased()) {
            if (weapon.getId().equals("Core_bright_big7s")) {
                MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star2.png"), weapon.getFirePoint(0), new Vector2f(),
                        new Vector2f(120, 120),
                        new Vector2f(480, 480),
                        weapon.getCurrAngle(),
                        0f,
                        new Color(117, 180, 120, 20),
                        true,
                        0.07f * engine.getTimeMult().getMult(),
                        0f,
                        0.14f * engine.getTimeMult().getMult());

            }
            if (weapon.getId().equals("Core_bright_small7s")) {
                MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/star2.png"), weapon.getFirePoint(0), new Vector2f(),
                        new Vector2f(70, 70),
                        new Vector2f(280, 280),
                        weapon.getCurrAngle(),
                        0f,
                        new Color(117, 180, 120, 20),
                        true,
                        0.07f * engine.getTimeMult().getMult(),
                        0f,
                        0.14f * engine.getTimeMult().getMult());

            }
        }

    }
}







        


