package data.shipsystems.scripts;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

public class Phase_Transition7s extends BaseShipSystemScript {

	private static Map mag = new HashMap();
	
	protected Object STATUSKEY1 = new Object();
	
	//public static final float INCOMING_DAMAGE_MULT = 0.6f;
	public static Color SMOKING = new Color(141, 157, 165, 2);
	public static Color SMOKING_2 = new Color(134, 174, 186, 90);
	public static Color JITTER = new Color(130, 180, 207, 160);
	public static Color CLOAK_HULL = new Color(117, 180, 146, 220);
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
		}
		if (effectLevel > 0) {
			Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.5f),
					new Vector2f(0f, 0f),
					ship.getCollisionRadius() / 3f,
					4f,
					-0.2f,
					0.5f,
					0.4f,
					SMOKING);
			Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.75f), new Vector2f(0f, 0f), ship.getCollisionRadius() / 5f, 8f, -0.2f, 0.5f, 0.7f, SMOKING_2);

			MagicRender.battlespace(
					Global.getSettings().getSprite("graphics/ships/Saltare7s_glow.png"), //sprite
					ship.getLocation(), //location vector2f
					new Vector2f(0f, 0f), //velocity vector2f
					new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
					new Vector2f(0f, 0f), //growth, vector2f, pixels/second
					ship.getFacing() - 90f, //angle, float
					0f, //spin, float
					CLOAK_HULL, //color Color
					true, //additive, boolean

					0f, //jitter range
					1f, //jitter tilt
					1f, // flicker range
					0f, //flicker median
					0.05f, //max delay

					0.07f * Global.getCombatEngine().getTimeMult().getMult(), //fadein, float, seconds
					0f * Global.getCombatEngine().getTimeMult().getMult(), //full, float, seconds
					0.14f * Global.getCombatEngine().getTimeMult().getMult(), //fadeout, float, seconds

					CombatEngineLayers.ABOVE_SHIPS_LAYER);

		}
		if (effectLevel > 0.25f) {
			ship.setPhased(true);
			ship.setApplyExtraAlphaToEngines(true);
			ship.setAlphaMult(1 + (0.25f - effectLevel));
			ship.setJitterUnder(ship, JITTER, 0.5f, 7, 10f, 15f);
		} if (effectLevel < 0.25f) {
			ship.setPhased(false);
			ship.setApplyExtraAlphaToEngines(false);
		}

		if (player && effectLevel > 0) {
			ShipSystemAPI system = ship.getSystem();
			if (system != null) {
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
					system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
					"ship is briefly phased", false);
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {

	}
	
	
//	public StatusData getStatusData(int index, State state, float effectLevel) {
//		float mult = (Float) mag.get(HullSize.CRUISER);
//		if (stats.getVariant() != null) {
//			mult = (Float) mag.get(stats.getVariant().getHullSize());
//		}
//		effectLevel = 1f;
//		float percent = (1f - INCOMING_DAMAGE_MULT) * effectLevel * 100;
//		if (index == 0) {
//			return new StatusData((int) percent + "% less damage taken", false);
//		}
//		return null;
//	}
}
