// This is my trial to force AI to behave like it have Phase Anchor hullmod, without the stat
// effects from said hullmod.


package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;

public class Breakpoint_Anchor7s extends BaseHullMod {

	public static float PHASE_DISSIPATION_MULT = 1f; //placebo

	public static class PhaseAnchorScript implements AdvanceableListener {
		public ShipAPI ship;
		public PhaseAnchorScript(ShipAPI ship) {
			this.ship = ship;
		}

		public void advance(float amount) {
			String id = "phase_anchor_modifier";

			boolean phased = ship.isPhased();
			if (ship.getPhaseCloak() != null && ship.getPhaseCloak().isChargedown()) {
				phased = false;
			}
			
			MutableShipStatsAPI stats = ship.getMutableStats();
			if (phased) {
				stats.getEnergyRoFMult().modifyMult(id, PHASE_DISSIPATION_MULT);
				stats.getEnergyAmmoRegenMult().modifyMult(id, PHASE_DISSIPATION_MULT);


			} else {
				stats.getEnergyRoFMult().unmodifyMult(id);
				stats.getEnergyAmmoRegenMult().unmodifyMult(id);
			}
		}

	}

}

