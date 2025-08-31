package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import java.awt.*;
import java.util.EnumSet;

public class InterceptBurnStats extends BaseShipSystemScript {

	protected Object STATUSKEY1 = new Object();
	protected Object STATUSKEY2 = new Object();

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
		} else {
			stats.getMaxSpeed().modifyFlat(id, 350f * effectLevel);
			stats.getAcceleration().modifyFlat(id, 700f * effectLevel);
			ship.fadeToColor(ship, new Color(240,210,180,255), 0.5f, 0.5f, effectLevel);

			stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);
			stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);
			stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - 0.5f) * effectLevel);

			stats.getBallisticWeaponDamageMult().modifyMult(id, 1f + ship.getHardFluxLevel());
			ship.setWeaponGlow(effectLevel * ship.getHardFluxLevel(), new Color(255, 68, 0,255), EnumSet.of(WeaponAPI.WeaponType.BALLISTIC));
			boolean player = false;
			if (stats.getEntity() instanceof ShipAPI) {
				ship = (ShipAPI) stats.getEntity();
				player = ship == Global.getCombatEngine().getPlayerShip();
			}
			if (player) {
					Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
							"graphics/shipsystems/interceptburn.png", "Intercept Burn",
							Math.round(0.5f * 100f) + "% less damage taken", false);

					Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2, "graphics/shipsystems/interceptburn.png", "Intercept Burn",
						"Increased Weapon Damage:" + " " + "+" + (Math.round((ship.getHardFluxLevel()) * 100f)) +
								"%", false);

			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}

		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		stats.getBallisticWeaponDamageMult().unmodify(id);
		ship.setWeaponGlow(0f, new Color(255, 68, 0,255), EnumSet.of(WeaponAPI.WeaponType.BALLISTIC));
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("increased engine power", false);
		}
		return null;
	}
	
	
	public float getActiveOverride(ShipAPI ship) {
//		if (ship.getHullSize() == HullSize.FRIGATE) {
//			return 1.25f;
//		}
//		if (ship.getHullSize() == HullSize.DESTROYER) {
//			return 0.75f;
//		}
//		if (ship.getHullSize() == HullSize.CRUISER) {
//			return 0.5f;
//		}
		return -1;
	}
	public float getInOverride(ShipAPI ship) {
		return -1;
	}
	public float getOutOverride(ShipAPI ship) {
		return -1;
	}
	
	public float getRegenOverride(ShipAPI ship) {
		return -1;
	}

	public int getUsesOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 2;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 2;
		}
		if (ship.getHullSize() == HullSize.CRUISER) {
			return 2;
		}
		return -1;
	}
}


