package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class Fluxdrive7s extends BaseShipSystemScript {

	protected Object STATUSKEY1 = new Object();

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		int number = 0;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
		} else {
			return;
		}
		stats.getCombatEngineRepairTimeMult().modifyMult(id,0.01f);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, 0.01f);

		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
		} else {
			if (!ship.isEngineBoostActive()) {
				stats.getMaxSpeed().modifyFlat(id, 50f);
				stats.getAcceleration().modifyPercent(id, 150f * effectLevel);
				stats.getDeceleration().modifyPercent(id, 150f * effectLevel);
				stats.getTurnAcceleration().modifyFlat(id, 15f * effectLevel);
				stats.getTurnAcceleration().modifyPercent(id, 150f * effectLevel);
				stats.getMaxTurnRate().modifyFlat(id, 15f);
				Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/shipsystems/interceptburn.png", "Flux Drive", "+50 top speed", false);
			}
			else if (ship.isEngineBoostActive()) {
				stats.getMaxSpeed().unmodifyFlat(id);
				stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);
				stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);
				stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);
				ship.setJitter(ship, new Color(240,210,180,55),0.5f,2,5);
				ship.setJitterUnder(ship, new Color(240,210,180,155),0.5f,25,7);
				Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/damper_field.png", "Flux Drive", "50% less damage taken", false);
			}

			if (stats.getEntity() instanceof ShipAPI && false) {
				String key = ship.getId() + "_" + id;
				Object test = Global.getCombatEngine().getCustomData().get(key);
				if (state == State.IN) {
					if (test == null && effectLevel > 0.2f) {
						Global.getCombatEngine().getCustomData().put(key, new Object());
						ship.getEngineController().getExtendLengthFraction().advance(1f);
						for (ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
							if (engine.isSystemActivated()) {
								ship.getEngineController().setFlameLevel(engine.getEngineSlot(), 1f);
							}
						}
					}
				} else {
					Global.getCombatEngine().getCustomData().remove(key);
				}
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getCombatEngineRepairTimeMult().unmodify(id);
		stats.getCombatWeaponRepairTimeMult().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		/*
		if (index == 0) {
			return new StatusData("improved maneuverability", false);
		} else if (index == 1) {
			return new StatusData("+50 top speed", false);
		}
		*/
		return null;
	}
}
