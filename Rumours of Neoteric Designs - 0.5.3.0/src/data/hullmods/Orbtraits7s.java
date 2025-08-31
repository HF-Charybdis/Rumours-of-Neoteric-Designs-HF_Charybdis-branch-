package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.EngineSlot;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class Orbtraits7s extends BaseHullMod {
    public static final Color JITTER_COLOR = new Color(90, 255, 217,55);
    public static Color EXPLOSION = new Color(190, 247, 232, 150);
    public static Color LIGHTNING_FRINGE_COLOR = new Color(99, 255, 226, 175);

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEngineHealthBonus().modifyFlat(id,10000000f);
        stats.getWeaponHealthBonus().modifyFlat(id,10000000f);
    }

	public void advanceInCombat(ShipAPI ship, float amount){
            ship.getMutableStats().getTimeMult().modifyMult(ship.getId(),2f);
            ship.blockCommandForOneFrame(ShipCommand.VENT_FLUX);
            ship.getFluxTracker().setCurrFlux(ship.getMaxFlux() * 0f);
            ship.setCollisionClass(CollisionClass.FIGHTER);
            ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0,0,0,0), 1f, 0.5f);
            ship.getEngineController().extendFlame(this, -0.25f, -0.25f, 0.5f);
            //ship.clearDamageDecals();

        MagicRender.battlespace(
                Global.getSettings().getSprite("graphics/ships/Mayawati7s_jitter.png"), //sprite
                ship.getShieldCenterEvenIfNoShield(), //location vector2f
                new Vector2f(0f, 0f), //velocity vector2f
                new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
                new Vector2f(0f, 0f), //growth, vector2f, pixels/second
                ship.getFacing() - 90f, //angle, float
                0f, //spin, float
                EXPLOSION, //color Color
                true, //additive, boolean

                0f, //jitter range
                1f, //jitter tilt
                1f, // flicker range
                0f, //flicker median
                0.05f, //max delay

                0.2f, //fadein, float, seconds
                0f, //full, float, seconds
                0.2f, //fadeout, float, seconds

                CombatEngineLayers.BELOW_SHIPS_LAYER);


        if (ship.isAlive()) {
            ship.getSpriteAPI().setCenter(53, 48);
        }

        if (ship.getEngineController().isAcceleratingBackwards() || ship.getEngineController().isAccelerating() || ship.getEngineController().isTurningLeft() || ship.getEngineController().isTurningRight()) {
                ship.setSprite("Mayawati", "sprite7s");
                ship.getSpriteAPI().setCenter(53, 48);
                ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
                ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
                ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());
            }
            if (ship.getHullLevel() <= 0.99f) {
                Global.getCombatEngine().spawnExplosion(ship.getLocation(), ship.getVelocity(), EXPLOSION, 150f, 0.65f);
                Global.getCombatEngine().addSmoothParticle(ship.getLocation(), ship.getVelocity(), 105f, 0.4f, 0.5f, LIGHTNING_FRINGE_COLOR);
                Global.getSoundPlayer().playSound("mote_attractor_impact_damage", 1.25f,0.8f, ship.getLocation(), ship.getVelocity());
                Global.getCombatEngine().removeEntity(ship);
                if (ship.getHitpoints() == 0) Global.getCombatEngine().removeEntity(ship);
                if (!ship.isAlive()) {
                    Global.getCombatEngine().removeEntity(ship);
                }
        }
	}

        



}
