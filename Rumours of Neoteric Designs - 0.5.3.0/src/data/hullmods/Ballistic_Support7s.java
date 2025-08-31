package data.hullmods;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;

public class Ballistic_Support7s extends BaseHullMod {

	public static float RANGE_BONUS = -200;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new BallisticSupportRangeModifier());
		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add("ballistic_rangefinder");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "seven_ballistic_adapter");
			}
		}
	}

	public static class BallisticSupportRangeModifier implements WeaponBaseRangeModifier {
		public BallisticSupportRangeModifier() {
		}

		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}

		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}

		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.isBeam()) return 0f;
			if (weapon.hasAIHint(WeaponAPI.AIHints.PD)) return 0f;
			if (weapon.getSpec().getMaxRange() <= 500) return 0f;
			if (weapon.getOriginalSpec().getMaxRange() <= 600) return -weapon.getOriginalSpec().getMaxRange() + 500f;
			if (weapon.getType() == WeaponType.BALLISTIC || weapon.getType() == WeaponType.HYBRID) {
				return RANGE_BONUS;
			}

			return 0f;
		}
	}

	public boolean isApplicableToShip(ShipAPI ship) {
		return

				!ship.getVariant().getHullMods().contains("ballistic_rangefinder");

	}

	public String getUnapplicableReason(ShipAPI ship) {


		if (ship.getVariant().getHullMods().contains("ballistic_rangefinder")) {
			return "Ship already has range modifiers for ballistics";
		}

		return null;
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color m = Misc.getMissileMountColor();
		Color e = Misc.getEnergyMountColor();
		Color b = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color supermod_grey = Misc.getGrayColor();
		Color supermod_white = Misc.getTextColor();
		Color supermod_on = Misc.getStoryBrightColor();
		Color text = new Color (132, 255, 149);
		Color background = new Color (15, 21, 17);


		LabelAPI label = tooltip.addPara("This vessel offer hybrid adapters for %s both ballistic and energy weapons, allowing vast options for possible refits.  At the price of %s often shown by ballistic weaponry.", opad, b,
		        "combining", "sacrificing the range advantage");
		label.setHighlight(
				"combining", "sacrificing the range advantage");
		label.setHighlightColors(b, bad);

		label = tooltip.addPara("Incompatible with %s.", opad, b,
				"Ballistic Rangefinder");
		label.setHighlight("Ballistic Rangefinder");
		label.setHighlightColors(b);


		tooltip.addSectionHeading("Modifiers:",text,background,Alignment.MID,opad);

		label = tooltip.addPara("%s ballistic base range, with a threshold of %s; ", opad, b,
				"" + "-200", "500");
		label.setHighlight("" + "-200", "500");
		label.setHighlightColors(bad, b);

		label = tooltip.addPara("Not affects %s; ", opad, b,
				"" + "Point Defenses");
		label.setHighlight("" + "Point Defenses");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Interactions with other modifiers",text,background,Alignment.MID,opad);
		tooltip.addPara("Since the base range is decreased, this range modifier"
				+ " - unlike most other flat modifiers in the game - "
				+ "is increased by percentage modifiers from other hullmods and skills.", opad);

	}
}









