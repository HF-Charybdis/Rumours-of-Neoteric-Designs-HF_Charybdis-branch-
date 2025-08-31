package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class Defensemode7s extends BaseHullMod {

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		variant.getHullSpec().setShipSystemId("fortressshield7s");
		stats.getShieldArcBonus().modifyFlat(id, 180f);
		stats.getShieldUnfoldRateMult().modifyMult(id, 1.5f);
		stats.getBallisticWeaponRangeBonus().modifyMult(id, 1.15f);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, 1.15f);
		stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -10f);
		stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -10f);
		stats.getPeakCRDuration().modifyFlat(id, 60f);


		ShipAPI ship = null;
		boolean player = false;
		int number = 0;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
		} else {
			return;
		}

		if (!ship.getVariant().hasHullMod("aswitcher_elipsion")) {
			ship.getVariant().removeMod("defensemode7s");
			ship.getVariant().removeMod("attackmode7s");
			ship.getVariant().removeMod("chasemode7s");
			ship.getVariant().removeMod("dummydefense7s");
			ship.getVariant().removeMod("dummyattack7s");
			ship.getVariant().removeMod("dummychase7s");
		}

		ship.setSprite("elipsion", "elipsion_defense");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());

	}
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setSprite("elipsion", "elipsion_defense");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());

		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "defensemode7s");
			}
		}

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
		//text,background,

		LabelAPI label = tooltip.addPara("A defensive attachment designed to make the ship operate similar to a mini Paragon, boosting weapon range and shield capabilities, however it does nothing for the speed nor agility of the ship.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifies:",text,background, Alignment.MID, opad);

		label = tooltip.addPara( "Increases shield unfold rate by %s; ", opad, b,
				"" + "50%");
		label.setHighlight(	"" + "50%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Increases shield arc by %s; ", opad, b,
				"" + "180");
		label.setHighlight(	"" + "180");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Increases ballistic and energy weapons range by %s, Point Defense bonus is limited to %s;", opad, b,
				"" + "15%", "5%");
		label.setHighlight(	"" + "15%", "5%");
		label.setHighlightColors(b, b);

		label = tooltip.addPara( "Increases peak performance time by %s; ", opad, b,
				"" + "60");
		label.setHighlight(	"" + "60");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Ship system is now %s; ", opad, b,
				"" + "Fortress Shield");
		label.setHighlight(	"" + "Fortress Shield");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Interactions with other hullmods:",text,background, Alignment.MID, opad);

		label = tooltip.addPara( "Incompatible with %s; ", opad, b,
				"" + "Safety Overrides");
		label.setHighlight(	"" + "Safety Overrides");
		label.setHighlightColors(b);

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
