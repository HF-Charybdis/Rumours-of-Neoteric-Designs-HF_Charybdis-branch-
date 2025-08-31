package data.shipsystems.scripts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

public class FireOrder7s extends BaseShipSystemScript {
	public static final Object KEY_JITTER = new Object();
	
	public static final float DAMAGE_INCREASE_PERCENT = 50;
	
	public static final Color JITTER_UNDER_COLOR = new Color(0, 255, 111,125);
	public static final Color JITTER_COLOR = new Color(0, 255, 140,75);

	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		
		if (effectLevel > 0) {
			float jitterLevel = effectLevel;
			float maxRangeBonus = 5f;
			float jitterRangeBonus = jitterLevel * maxRangeBonus;
			for (ShipAPI fighter : getFighters(ship)) {
				if (fighter.isHulk()) continue;
				MutableShipStatsAPI fStats = fighter.getMutableStats();
//				fStats.getBallisticWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);
//				fStats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);
//				fStats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);
				
				fStats.getBallisticRoFMult().modifyMult(id, 2f * effectLevel);
				fStats.getEnergyRoFMult().modifyMult(id, 2f * effectLevel);
				fStats.getMissileRoFMult().modifyMult(id, 2f * effectLevel);
				fStats.getBallisticWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);
				fStats.getEnergyWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);
				fStats.getMissileWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);
				fStats.getBallisticAmmoRegenMult().modifyMult(id, 2f * effectLevel);
				fStats.getEnergyAmmoRegenMult().modifyMult(id, 2f * effectLevel);
				fStats.getMissileAmmoRegenMult().modifyMult(id, 2f * effectLevel);
				
				if (jitterLevel > 0) {
					//fighter.setWeaponGlow(effectLevel, new Color(255,50,0,125), EnumSet.allOf(WeaponType.class));
					fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(WeaponType.class));
					
					fighter.setJitterUnder(KEY_JITTER, JITTER_COLOR, jitterLevel, 5, 0f, jitterRangeBonus);
					fighter.setJitter(KEY_JITTER, JITTER_UNDER_COLOR, jitterLevel, 2, 0f, 0 + jitterRangeBonus * 1f);
					Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
				}
			}
		}
	}
	
	private List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();
		
//		this didn't catch fighters returning for refit		
//		for (FighterLaunchBayAPI bay : carrier.getLaunchBaysCopy()) {
//			if (bay.getWing() == null) continue;
//			result.addAll(bay.getWing().getWingMembers());
//		}
		
		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter()) continue;
			if (ship.getWing() == null) continue;
			if (ship.getWing().getSourceShip() == carrier) {
				result.add(ship);
			}
		}
		
		return result;
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		for (ShipAPI fighter : getFighters(ship)) {
			if (fighter.isHulk()) continue;
			MutableShipStatsAPI fStats = fighter.getMutableStats();
			fStats.getBallisticRoFMult().unmodifyMult(id);
			fStats.getEnergyRoFMult().unmodifyMult(id);
			fStats.getMissileRoFMult().unmodifyMult(id);
			fStats.getBallisticWeaponFluxCostMod().unmodifyMult(id);
			fStats.getEnergyWeaponFluxCostMod().unmodifyMult(id);
			fStats.getMissileWeaponFluxCostMod().unmodifyMult(id);
			fStats.getBallisticAmmoRegenMult().unmodifyMult(id);
			fStats.getEnergyAmmoRegenMult().unmodifyMult(id);
			fStats.getMissileAmmoRegenMult().unmodifyMult(id);
		}
	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		int percent = Math.round(100 * effectLevel);
		if (index == 0) {
			//return new StatusData("+" + (int)percent + "% fighter damage", false);
			return new StatusData(percent + "%" + " fighter fire rate", false);
		}
		return null;
	}

	
}








