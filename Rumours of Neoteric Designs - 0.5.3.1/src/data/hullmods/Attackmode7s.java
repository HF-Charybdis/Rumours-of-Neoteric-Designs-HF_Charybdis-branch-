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

public class Attackmode7s extends BaseHullMod {

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
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

		ship.setSprite("elipsion", "elipsion_assault");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());


		final ShipVariantAPI variant = stats.getVariant();
		variant.getHullSpec().setShipSystemId("plasmajets");
		stats.getMaxSpeed().modifyFlat(id, 30f);
	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setSprite("elipsion", "elipsion_assault");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());

		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "attackmode7s");
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

		LabelAPI label = tooltip.addPara("A balanced attachment with a mild affinity for speed, designed to make the ship operate similar to a small aurora, boosting speed and agility, with a mild boost for it's weapon ranges, however it does nothing to boost the ship's direct defenses.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifiers:",text,background,Alignment.MID,opad);

		label = tooltip.addPara( "Increases max speed by %s; ", opad, b,
				"" + "20");
		label.setHighlight(	"" + "20");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Ship system is now %s; ", opad, b,
				"" + "Plasma Jets");
		label.setHighlight(	"" + "Plasma Jets");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Interactions with other hullmods:",text,background,Alignment.MID,opad);

		label = tooltip.addPara( "Incompatible with %s; ", opad, b,
				"" + "Safety Overrides");
		label.setHighlight(	"" + "Safety Overrides");
		label.setHighlightColors(b);

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
