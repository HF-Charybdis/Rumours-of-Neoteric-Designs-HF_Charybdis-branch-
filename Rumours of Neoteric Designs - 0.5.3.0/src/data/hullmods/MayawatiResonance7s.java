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
import org.lazywizard.lazylib.combat.CombatUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class MayawatiResonance7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();

	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
        int should_not_apply = 0;
		for (ShipAPI cheaters: CombatUtils.getShipsWithinRange(ship.getLocation(), 9999f)) {
			if (cheaters.getVariant().hasHullMod("Cheat_Maya7s")) {
				should_not_apply++;
			}
		}
		if (should_not_apply == 0) {

		if (ship.getVariant().hasHullMod("breakpoint7s")) {

			int Clones = 0;
			for (ShipAPI testShip : CombatUtils.getShipsWithinRange(ship.getLocation(), 99999f)) {
				if (testShip.getVariant().getHullMods().contains("aclone_timeout7s") && !testShip.isHulk() && testShip.getOwner() == ship.getOwner()) {
					Clones++;
				}

				if (Clones == 1) {
				/*
				stats.getMaxSpeed().modifyFlat(ship.getId(), 20f);
				stats.getAcceleration().modifyMult(ship.getId(), 1.20f);
				stats.getDeceleration().modifyMult(ship.getId(), 1.20f);
				stats.getMaxTurnRate().modifyMult(ship.getId(), 1.20f);
				stats.getTurnAcceleration().modifyMult(ship.getId(), 1.20f);
				*/
					if (!ship.getFluxTracker().isOverloadedOrVenting()) {
						if ((ship.getCurrFlux() <= ship.getMaxFlux())) {
							ship.getFluxTracker().setCurrFlux((stats.getFluxDissipation().getBaseValue() * 0.01f) + ship.getCurrFlux());
						}
					}
				} else if (Clones == 2) {
				/*
				stats.getMaxSpeed().modifyFlat(ship.getId(), 40f);
				stats.getAcceleration().modifyMult(ship.getId(), 1.40f);
				stats.getDeceleration().modifyMult(ship.getId(), 1.40f);
				stats.getMaxTurnRate().modifyMult(ship.getId(), 1.40f);
				stats.getTurnAcceleration().modifyMult(ship.getId(), 1.40f);
				*/
					if (!ship.getFluxTracker().isOverloadedOrVenting()) {
						if ((ship.getCurrFlux() <= ship.getMaxFlux())) {
							ship.getFluxTracker().setCurrFlux((stats.getFluxDissipation().getBaseValue() * 0.02f) + ship.getCurrFlux());
						}
					}

				} else if (Clones == 3) {
				/*
				stats.getMaxSpeed().modifyFlat(ship.getId(), 40f);
				stats.getAcceleration().modifyMult(ship.getId(), 1.40f);
				stats.getDeceleration().modifyMult(ship.getId(), 1.40f);
				stats.getMaxTurnRate().modifyMult(ship.getId(), 1.40f);
				stats.getTurnAcceleration().modifyMult(ship.getId(), 1.40f);
				*/
					if (!ship.getFluxTracker().isOverloadedOrVenting()) {
						if ((ship.getCurrFlux() <= ship.getMaxFlux())) {
							ship.getFluxTracker().setCurrFlux((stats.getFluxDissipation().getBaseValue() * 0.03f) + ship.getCurrFlux());
						}
					}

				} else if (Clones == 4) {
				/*
				stats.getMaxSpeed().modifyFlat(ship.getId(), 40f);
				stats.getAcceleration().modifyMult(ship.getId(), 1.40f);
				stats.getDeceleration().modifyMult(ship.getId(), 1.40f);
				stats.getMaxTurnRate().modifyMult(ship.getId(), 1.40f);
				stats.getTurnAcceleration().modifyMult(ship.getId(), 1.40f);
				*/
					if (!ship.getFluxTracker().isOverloadedOrVenting()) {
						if ((ship.getCurrFlux() <= ship.getMaxFlux())) {
							ship.getFluxTracker().setCurrFlux((stats.getFluxDissipation().getBaseValue() * 0.04f) + ship.getCurrFlux());
						}
					}
				} else if (Clones >= 5) {
				/*
				stats.getMaxSpeed().modifyFlat(ship.getId(), 60f);
				stats.getAcceleration().modifyMult(ship.getId(), 1.60f);
				stats.getDeceleration().modifyMult(ship.getId(), 1.60f);
				stats.getMaxTurnRate().modifyMult(ship.getId(), 1.60f);
				stats.getTurnAcceleration().modifyMult(ship.getId(), 1.60f);
				 */
					if (!ship.getFluxTracker().isOverloadedOrVenting()) {
						if ((ship.getCurrFlux() <= ship.getMaxFlux())) {
							ship.getFluxTracker().setCurrFlux(((stats.getFluxDissipation().getBaseValue() + 10f) * 0.05f) + ship.getCurrFlux());
						}
					}
				}
			}

				if (Clones <= 0) {
					stats.getMaxSpeed().unmodify(ship.getId());
					stats.getAcceleration().unmodify(ship.getId());
					stats.getDeceleration().unmodify(ship.getId());
					stats.getMaxTurnRate().unmodify(ship.getId());
					stats.getTurnAcceleration().unmodify(ship.getId());
				}
				if (ship == Global.getCombatEngine().getPlayerShip()) {
					if (Clones == 1) {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "1 Illusia clone - slow flux generation", false);
					} else if (Clones == 2) {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "2 Illusia clones - slow flux generation", false);

					} else if (Clones == 3) {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "3 Illusia clones - flux generation", false);

					} else if (Clones == 4) {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "4 Illusia clones - flux generation", false);

					} else if (Clones >= 5) {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "5 Illusia clones - fast flux generation", false);

					} else {
						Global.getCombatEngine().maintainStatusForPlayerShip(ship, "graphics/icons/hullsys/displacer.png", "Resonance", "Stack Illusia clones to raise softflux", false);
					}
				}

			}
		}
	}



	/*public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "");
			}
		}

	}

	 */
    
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

		LabelAPI label = tooltip.addPara("This vessel manipulates the Breakpoint Phenomena, allowing it to synthesize clones while using the Illusia Dive. Those draw point defense fire and disable nearby missiles, while also resonating with the main vessel, passively generating softflux while close to it. In addition, this vessel passively produces illusions orbiting it, that constantly draw fire and distract enemy ships.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifiers:",text,background, Alignment.MID, opad);

		label = tooltip.addPara("Generates flux based on its base dissipation plus its flux vents, with the percentage increasing with more clones, up to 5 at once.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		label = tooltip.addPara( "1 Clone: %s; ", opad, b,
				"" + "50%");
		label.setHighlight(	"" + "50%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "2 Clones: %s; ", opad, b,
				"" + "100%");
		label.setHighlight(	"" + "100%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "3 Clones: %s;", opad, b,
				"" + "150%");
		label.setHighlight(	"" + "150%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "4 Clones: %s;", opad, b,
				"" + "200%");
		label.setHighlight(	"" + "200%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "5 Clones: %s;", opad, b,
				"" + "250%");
		label.setHighlight(	"" + "250%");
		label.setHighlightColors(b);

		label = tooltip.addPara("Furthermore, each use of its ship system generates hardflux proportional to %s of its flux capacity.", opad, b,
				"" + "12%");
		label.setHighlight(	"" + "12%");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("WARNING:",text,background, Alignment.MID, opad);

		label = tooltip.addPara("Effects only applies when using the Breakpoint Frame configuration.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
