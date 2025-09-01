package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Chasemode7s extends BaseHullMod {
	private static final float PEAK_MULT = 0.33f;
	private static final float FLUX_DISSIPATION_MULT = 2f;
	private static final float RANGE_THRESHOLD = 450f;
	private static final float RANGE_MULT = 0.25f;

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		variant.getHullSpec().setShipSystemId("phaseteleporter");
		stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, 10);
		stats.getMaxSpeed().modifyFlat(id, 50f);
		stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 2f); // set to two, meaning boost is always on
		stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
		stats.getVentRateMult().modifyMult(id, 0f);
		stats.getWeaponRangeThreshold().modifyFlat(id, RANGE_THRESHOLD);
		stats.getWeaponRangeMultPastThreshold().modifyMult(id, RANGE_MULT);

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

		ship.setSprite("elipsion", "elipsion_chase");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());


	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setSprite("elipsion", "elipsion_chase");
		ship.getSpriteAPI().setCenter(79, 70);
		ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
		ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
		ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());

		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "chasemode7s");
			}
		}
		Color color = new Color(255,100,255,255);
		ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
		ship.getEngineController().extendFlame(this, 0.25f, 0.25f, 0.25f);

	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		Color color = new Color(255,100,255,255);
		ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
		ship.getEngineController().extendFlame(this, 0.25f, 0.25f, 0.25f);

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

		LabelAPI label = tooltip.addPara("A high-end attachment focused on brief but absolutely aggressive performance, designed to make the ship operate like a large hyperion. Operates similar to safety overrides, but due to battlefield interferences from pushing the ship to it's absolute limits, it costs more DP to deploy.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifies:",text,background, Alignment.MID, opad);

		label = tooltip.addPara( "Increases max speed by %s; ", opad, b,
				"" + "50");
		label.setHighlight(	"" + "50");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Increases flux dissipation by a factor of %s; ", opad, b,
				"" + "2");
		label.setHighlight(	"" + "2");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Ship system is now %s; ", opad, b,
				"" + "Phase Teleporter");
		label.setHighlight(	"" + "Phase Teleporter");
		label.setHighlightColors(b);

		label = tooltip.addPara( "%s active venting; ", opad, b,
				"" + "Blocks");
		label.setHighlight(	"" + "Blocks");
		label.setHighlightColors(bad);

		label = tooltip.addPara( "Reduces ballistic and energy weapons range by %s, with a threshold of %s; ", opad, b,
				"" + "75%", "450");
		label.setHighlight(	"" + "75%", "450");
		label.setHighlightColors(bad, b);

		label = tooltip.addPara( "Reduces peak performance time by %s; ", opad, b,
				"" + "2/3");
		label.setHighlight(	"" + "2/3");
		label.setHighlightColors(bad);

		label = tooltip.addPara( "Increases Deployment Points (DP) by %s; ", opad, b,
				"" + "10");
		label.setHighlight(	"" + "10");
		label.setHighlightColors(bad);

		tooltip.addSectionHeading("Interactions with other hullmods:",text,background, Alignment.MID, opad);

		label = tooltip.addPara( "Incompatible with %s; ", opad, b,
				"" + "Safety Overrides");
		label.setHighlight(	"" + "Safety Overrides");
		label.setHighlightColors(b);

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}