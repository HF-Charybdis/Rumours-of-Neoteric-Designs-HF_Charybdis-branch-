package data.shipsystems.scripts;

import java.awt.Color;
import java.util.EnumSet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class Entropy_Damper7s extends BaseShipSystemScript {

	public static Object KEY_SHIP = new Object();
	public static float INCOMING_DAMAGE_MULT = 0.5f;
	public static float FLUX_USE_MULT = 0.5f;
	public static float REPAIR_RATE_MULT = 10f;

	protected Object STATUSKEY1;
	protected Object STATUSKEY2;
	protected Object STATUSKEY3;
	protected Object STATUSKEY4;



	public Entropy_Damper7s() {
		this.STATUSKEY1 = new Object();
		this.STATUSKEY2 = new Object();
		this.STATUSKEY3 = new Object();
		this.STATUSKEY4 = new Object();
	}
	
	public static class TargetData {
		public ShipAPI target;
		public TargetData(ShipAPI target) {
			this.target = target;
		}
	}

	protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		float level = effectLevel;
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		float percent = (1f - FLUX_USE_MULT) * effectLevel * 100;
		float percent2 = (1f - INCOMING_DAMAGE_MULT) * effectLevel * 100;
		float percent3 = (1f - REPAIR_RATE_MULT) * effectLevel * 100;

		if (level > 0f) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
					playerShip.getSystem().getSpecAPI().getIconSpriteName(), playerShip.getSystem().getDisplayName(), (int) percent + "% less flux generated", false);
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					playerShip.getSystem().getSpecAPI().getIconSpriteName(), playerShip.getSystem().getDisplayName(), (int) percent2 + "% less damage taken", false);
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
					playerShip.getSystem().getSpecAPI().getIconSpriteName(), playerShip.getSystem().getDisplayName(), (int) percent3 + "% faster repairs", false);

		} else if (level == 0f) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
					playerShip.getSystem().getSpecAPI().getIconSpriteName(), playerShip.getSystem().getDisplayName(), "Passive - Armor Regen", false);

		}
	}
	
	
	public void apply(MutableShipStatsAPI stats, final String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
	
		ship.fadeToColor(KEY_SHIP, new Color(75,75,75,255), 0.1f, 0.1f, effectLevel);
		//ship.fadeToColor(KEY_SHIP, new Color(100,100,100,255), 0.1f, 0.1f, effectLevel);
		ship.setWeaponGlow(effectLevel, new Color(100, 255, 113,255), EnumSet.of(WeaponType.BALLISTIC, WeaponType.ENERGY, WeaponType.MISSILE));
		ship.getEngineController().fadeToOtherColor(KEY_SHIP, new Color(0, 0, 0,0), new Color(0,0,0,0), effectLevel, 0.75f * effectLevel);
		//ship.setJitter(KEY_SHIP, new Color(100,165,255,55), effectLevel, 1, 0f, 5f);
		ship.setJitterUnder(KEY_SHIP, new Color(100, 255, 103,255), effectLevel, 15, 0f, 15f);
		//ship.setShowModuleJitterUnder(true);
		
		effectLevel = 1f;
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);
		stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);
		
		stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * effectLevel);
		stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * effectLevel);
		stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * effectLevel);
		stats.getBeamDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * effectLevel);
		
		stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f / (1f + (REPAIR_RATE_MULT - 1f) * effectLevel));
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f / (1f + (REPAIR_RATE_MULT - 1f) * effectLevel));
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getEnergyWeaponFluxCostMod().unmodify(id);
		stats.getMissileWeaponFluxCostMod().unmodify(id);
		
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		
		stats.getCombatEngineRepairTimeMult().unmodifyMult(id);
		stats.getCombatWeaponRepairTimeMult().unmodifyMult(id);

		ShipAPI ship = null;
		//boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			//player = ship == Global.getCombatEngine().getPlayerShip();
			//id = id + "_" + ship.getId();
		} else {
			return;
		}

			if (ship == null) return;
			if (!ship.isAlive()) return;
			//I'll be honest, needed to learn from bobulated heral armor to do this one, why armor grid stuff is so hard?
			ArmorGridAPI armor = ship.getArmorGrid(); //ArmorAPI, I not even knew that this existed before
			float[][] grid = armor.getGrid(); //Calls the grid
			float Regen = armor.getMaxArmorInCell() * (0.07f); //regen rate
			if (Regen > armor.getMaxArmorInCell())
				Regen = 0f; //This makes the regen stops, compares the current armor vs max armor
			for (int x = 0; x < grid.length; x++) { //I think that those loops calls the "setArmorValue" and updates
				for (int y = 0; y < grid[0].length; y++) { //the current armor value, including the regenerated armor
					//for me? Its rocket science tbh
					armor.setArmorValue(x, y, Math.min(grid[x][y] + Regen, armor.getMaxArmorInCell()));
					//this line above makes everything called before work, regenerates the entire armor grid, does not
					//prioritize areas with less armor, its a general regen, tested to reach to that conclusion

			}
		}

	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		effectLevel = 1f;
		float percent = (1f - FLUX_USE_MULT) * effectLevel * 100;
		if (index == 0) {
			return new StatusData((int) percent + "% less flux generated", false);
		}
		percent = (1f - INCOMING_DAMAGE_MULT) * effectLevel * 100;
		if (index == 1) {
			return new StatusData((int) percent + "% less damage taken", false);
		}
		
		percent = REPAIR_RATE_MULT * effectLevel * 100f;
		if (index == 2) {
			return new StatusData((int) percent + "% faster repairs", false);
		}
		return null;
	}

}



