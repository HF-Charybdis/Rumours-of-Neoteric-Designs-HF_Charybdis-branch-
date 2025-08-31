package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import java.util.HashSet;
import java.util.Set;

public class SafetyProtocols7s extends BaseHullMod {

	private static Map speed = new HashMap();
	static {
		speed.put(HullSize.FRIGATE, -25f);
		speed.put(HullSize.DESTROYER, -20f);
		speed.put(HullSize.CRUISER, -15f);
		speed.put(HullSize.CAPITAL_SHIP, -10f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		stats.getPeakCRDuration().modifyPercent(id, 100f);
		stats.getEngineMalfunctionChance().modifyMult(id, 0.7f);
		stats.getWeaponMalfunctionChance().modifyMult(id, 0.7f);
		stats.getCriticalMalfunctionChance().modifyMult(id, 0.7f);
		stats.getCrewLossMult().modifyMult(id, 0.7f);

		stats.getMaxSpeed().modifyFlat(id, (Float) speed.get(hullSize));
		stats.getFluxDissipation().modifyMult(id, 0.75f);
		stats.getMissileRoFMult().modifyMult(id, 0.75f);
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyMult(id, 0.75f);
	}
	private Color color = new Color(100, 255, 219,255);

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
			ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
			ship.getEngineController().extendFlame(this, -0.25f, -0.25f, -0.25f);
	}




	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String hullmod : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(hullmod)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), hullmod, "Safety Overrides are exactly the opposite from Safety Protocols.");
			}
		}
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
//		return !ship.getVariant().getHullMods().contains("unstable_injector") &&
//			   !ship.getVariant().getHullMods().contains("augmented_engines");
		if (ship.getVariant().getHullSize() == HullSize.CAPITAL_SHIP) return false;
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) return false;
		if (ship.getVariant().hasHullMod(HullMods.FLUX_SHUNT)) return false;


		return true;
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullSize() == HullSize.CAPITAL_SHIP) {
			return "Can not be installed on capital ships";
		}
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
			return "Can not be installed on civilian ships";
		}
		if (ship.getVariant().hasHullMod(HullMods.FLUX_SHUNT)) {
			return "Incompatible with Flux Shunt";
		}

		return null;
	}



	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		return null;
	}

	@Override
	public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color m = Misc.getMissileMountColor();
		Color e = Misc.getEnergyMountColor();
		Color b = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color text = new Color (132, 255, 149);
		Color background = new Color (15, 21, 17);

		LabelAPI label = tooltip.addPara("This schedule of regulations and inhibitors augments the ship's combat endurance, amplifying peak performance time (PPT), reducing crew causalities, malfunction chances and Combat Readiness degradation. The effects may result in the following:", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifiers:",text,background, Alignment.MID,opad);

		label = tooltip.addPara( "%s the base Peak Performance Time;", opad, b,
				"" + "Doubles");
		label.setHighlight(	"" + "Doubles");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Reduces overall chance of malfunction, crew causalities, and CR degradation rate by %s;", opad, b,
				"" + "30%");
		label.setHighlight(	"" + "30%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Reduces flux dissipation, missile fire rate, and fighter replacement rate recovery by %s;", opad, b,
				"" + "25%");
		label.setHighlight(	"" + "25%");
		label.setHighlightColors(bad);

		label = tooltip.addPara( "Diminishes max speed by %s/%s/%s, depending on hull size.", opad, b,
				"" + "25", "20", "15");
		label.setHighlight(	"" + "25", "20", "15");
		label.setHighlightColors(bad, bad, bad);

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}

	

}
