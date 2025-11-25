package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.MagFieldGenPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.magiclib.util.MagicUI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;


public class Quantum_damper7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	boolean threshold = false;





	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();

	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
			float mult = 0.85f;
		if (ship.getHardFluxLevel() <= 0.75f && !ship.getFluxTracker().isOverloaded()) {
			threshold = true;
		}
		else if (ship.getHardFluxLevel() > 0.75f) {
			threshold = false;
		}

		if (ship.getHardFluxLevel() <= 0.75f && ship.getFluxTracker().isOverloaded() && threshold) {
			stats.getHullDamageTakenMult().modifyMult("quantum_damper", 0.25f);
			stats.getArmorDamageTakenMult().modifyMult("quantum_damper", 0.25f);
			stats.getEmpDamageTakenMult().modifyMult("quantum_damper", 0.15f);
			ship.setJitter(ship,ship.getOverloadColor(),0.5f,2,5f);
			ship.setJitterUnder(ship,ship.getOverloadColor(),0.5f,25,7f);
			Global.getSoundPlayer().playLoop("system_damper_omega_loop",ship,1f,1f,ship.getLocation(),ship.getVelocity(),0.5f,0.5f);
		} else {
			stats.getHullDamageTakenMult().unmodifyMult("quantum_damper");
			stats.getArmorDamageTakenMult().unmodifyMult("quantum_damper");
			stats.getEmpDamageTakenMult().unmodifyMult("quantum_damper");
		}

		boolean sMod = isSMod(stats);
		if (sMod) {
			stats.getOverloadTimeMod().modifyMult("quantum_damper", 0.75f);
			stats.getEmpDamageTakenMult().modifyMult("quantum_damper", 0.75f);
			if (ship.isCruiser() || ship.isCapital()) {
				for (ShipAPI botes : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius() / 0.5f)) {
					if (botes != null) {
						if (!botes.isHulk() && botes.getOwner() != ship.getOwner() && botes.isPhased() && !ship.isPhased()) {
							botes.getFluxTracker().beginOverloadWithTotalBaseDuration(0.5f);
							ship.setJitter(ship,ship.getOverloadColor(),0.5f,2,5f);
							ship.setJitterUnder(ship,ship.getOverloadColor(),0.5f,25,7f);
							Global.getSoundPlayer().playLoop("system_damper_omega_loop",ship,1f,1f,ship.getLocation(),ship.getVelocity(),0.5f,0.5f);
						}
					}
				}
			}
		}


		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
		}
		if (player) {
			if (ship.getHardFluxLevel() <= 0.75f && ship.getFluxTracker().isOverloaded() && threshold) {
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
						"graphics/icons/hullsys/damper_field.png", "Quantum Damper",
						Math.round(0.75f * 100f) + "% less damage taken", false);
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
		Color supermod_grey = Misc.getGrayColor();
		Color supermod_white = Misc.getTextColor();
		Color supermod_on = Misc.getStoryBrightColor();
		Color text = new Color (132, 255, 149);
		Color background = new Color (15, 21, 17);

		LabelAPI label = tooltip.addPara("After several engagements against vessels using Quantum Disruptor devices, a countermeasure has been developed. Using the quantum energy, the ship generates a dampening field that mitigates incoming damage while it is overloaded, resulting in the following:", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		tooltip.addSectionHeading("Modifiers:",text,background,Alignment.MID,opad);

		label = tooltip.addPara( "Reduces incoming damage by %s while overloaded; ", opad, b,
				"" + "75%");
		label.setHighlight(	"" + "75%");
		label.setHighlightColors(b);

		label = tooltip.addPara( "Works only if hard flux takes less than %s of total flux capacity.", opad, b,
				"" + "75%");
		label.setHighlight(	"" + "75%");
		label.setHighlightColors(bad);

		tooltip.addSectionHeading("S-Mod Bonuses",text,background,Alignment.MID,opad);
        if (ship != null) {
			boolean sMod = isSMod(ship.getMutableStats());
			if (!sMod) {

				label = tooltip.addPara("%s %s %s %s", opad, b,
						"" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
				label.setHighlight("" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
				label.setHighlightColors(supermod_on, supermod_grey, supermod_on, supermod_grey);

				label = tooltip.addPara("%s %s %s%s", opad, b,
						"" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
				label.setHighlight("" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
				label.setHighlightColors(supermod_on, supermod_grey, supermod_on, supermod_grey);

			} else if (sMod) {

				label = tooltip.addPara("%s %s %s %s", opad, b,
						"" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
				label.setHighlight("" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
				label.setHighlightColors(supermod_on, supermod_white, supermod_on, supermod_white);

				label = tooltip.addPara("%s %s %s%s", opad, b,
						"" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
				label.setHighlight("" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
				label.setHighlightColors(supermod_on, supermod_white, supermod_on, supermod_white);

			}
		}
		if (ship == null) {
			label = tooltip.addPara("%s %s %s %s", opad, b,
					"" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
			label.setHighlight("" + "Bonus for cruisers and capital ships:", "Enemy ships", "cannot sustain phase cloak", "if trying to pass through this vessel, unless if this ship is also phased.");
			label.setHighlightColors(supermod_on, supermod_grey, supermod_on, supermod_grey);

			label = tooltip.addPara("%s %s %s%s", opad, b,
					"" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
			label.setHighlight("" + "For all ship sizes:", "Reduces overload duration and EMP damage taken by", "25%", ".");
			label.setHighlightColors(supermod_on, supermod_grey, supermod_on, supermod_grey);
		}

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
