package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class Necro_Scavenger7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();

	}

	public static float getRateCost(int bays) {
		if (bays <= 1) return 0;
		return 0;
	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		float minRate = Global.getSettings().getFloat("minFighterReplacementRate");

		float SalvageVal = 0f;
		int DesHulk = 0;
		int CrusHulk = 0;
		int CapHulk = 0;
		for (ShipAPI testShip1 : CombatUtils.getShipsWithinRange(ship.getLocation(), 99999f)) {
				if (testShip1.isHulk() && testShip1.isDestroyer()) {
				DesHulk = 1;
				}
				if (testShip1.isHulk() && testShip1.isCruiser()) {
				CrusHulk = 1;
				}
				if (testShip1.isHulk() && testShip1.isCapital()) {
				CapHulk = 1;
				}
			}

		if (DesHulk >= 1 && CrusHulk == 0 && CapHulk == 0) {
			SalvageVal = 1f;
		}
		else if (CrusHulk >= 1 && CapHulk == 0) {
			SalvageVal = 1.5f;
		}
		else if (CapHulk >= 1) {
			SalvageVal = 2f;
		}


		int bays = ship.getLaunchBaysCopy().size();
		float cost = getRateCost(bays);
		for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
			if (bay.getWing() == null) continue;

			float rate = Math.max(minRate, bay.getCurrRate() - cost);
			bay.setCurrRate(rate);

			bay.makeCurrentIntervalFast();
			FighterWingSpecAPI spec = bay.getWing().getSpec();
		if (SalvageVal > 0) {
				int addForWing = Math.round(spec.getNumFighters() * SalvageVal);
				int maxTotal = (spec.getNumFighters() + addForWing);
				int actualAdd = Math.round((maxTotal - bay.getWing().getWingMembers().size()) * SalvageVal);
				if (actualAdd > 0) {
					bay.setExtraDeployments(actualAdd);
					bay.setExtraDeploymentLimit(maxTotal);
					bay.setExtraDuration(35);
				}

			}
		}
		//if (ship == playerShip) {}
		if (ship == Global.getCombatEngine().getPlayerShip()) {
			{
				Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/construction_swarm.png", "Necro Scavenger", "Destroy enemy ships for extra fighters", false);
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

		LabelAPI label = tooltip.addPara("Designed for short-term salvaging, this vessel can use disabled/destroyed %s ship hulks to manufacture and deploy extra fighters on the battle space.", opad, b, "cruiser and capital");
		label.setHighlight("cruiser and capital");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Modifiers:",text,background, Alignment.MID, opad);

		label = tooltip.addPara("After your fleet destroys a ship, depending on its size, temporary multiplies the amount of fighters that can be fielded at the same time:", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		label = tooltip.addPara( "Destroyer: %s more fighters;", opad, b,
				"" + "2x");
		label.setHighlight(	"" + "2x");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Cruiser: %s more fighters (rounded up);", opad, b,
				"" + "2.5x");
		label.setHighlight(	"" + "2.5x");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Capital: %s more fighters.", opad, b,
				"" + "3x");
		label.setHighlight(	"" + "3x");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Observation:",text,background, Alignment.MID, opad);

		label = tooltip.addPara("If a ship is disabled (not destroyed), the extra fighters will be sustained for longer, until the hulk is lost.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
