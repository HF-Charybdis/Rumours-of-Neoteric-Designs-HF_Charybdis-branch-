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
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;


public class Mayawati_Cheat_Core7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	public static Color NOT_INTENSE = new Color(190, 247, 232, 150);
	public static Color INTENSE = new Color(152, 255, 236, 255);



	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		variant.addTag(Tags.SHIP_UNIQUE_SIGNATURE);
		variant.addTag(Tags.NO_ENTITY_TOOLTIP);
		variant.addTag(Tags.SHIP_LIMITED_TOOLTIP);
		variant.removeMod("breakpoint7s");
		variant.removeMod("dummysynergy7s");
		variant.addMod("frame7s");
		variant.addMod("dummyframe7s");
		if (variant.hasDMods()) {
			variant.removeTag(Tags.SHIP_UNIQUE_SIGNATURE);
			variant.removeTag(Tags.NO_ENTITY_TOOLTIP);
			variant.removeTag(Tags.SHIP_LIMITED_TOOLTIP);
			variant.removeMod("Cheat_Maya7s"); //this is for guarantee that this thing not drops with the
			//overpowered hullmod
		}
		//this WILL have dmods, no matters what
		stats.getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, (100f));

		stats.getHullBonus().modifyMult(id,1f);
		stats.getMaxSpeed().modifyMult(id,1f);
		stats.getArmorDamageTakenMult().modifyMult(id,1f);
		stats.getEmpDamageTakenMult().modifyMult(id,2f);
		stats.getHullDamageTakenMult().modifyMult(id,1f);
		stats.getEnergyRoFMult().modifyMult(id,2.5f);
		stats.getEnergyWeaponRangeBonus().modifyMult(id,1.5f);
		stats.getMaxSpeed().modifyMult(id,0.65f);
		stats.getEnergyProjectileSpeedMult().modifyMult(id,1.5f);
		stats.getSystemRegenBonus().modifyMult(id,1f);
		stats.getFluxDissipation().modifyMult(id,2f);
		stats.getCombatEngineRepairTimeMult().modifyMult(id,1.5f);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id,1.5f);
		stats.getPeakCRDuration().modifyMult(id, 0f);
		stats.getCRLossPerSecondPercent().modifyMult(id, 0.01f);
		stats.getPhaseCloakCooldownBonus().modifyFlat(id, 10f);
		stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 2f);
		variant.addTag(Tags.SHIP_UNIQUE_SIGNATURE);
		variant.addTag(Tags.NO_ENTITY_TOOLTIP);
		variant.addTag(Tags.SHIP_LIMITED_TOOLTIP);


	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}

		if (!AIUtils.getAlliesOnMap(ship).isEmpty()) {
			for (ShipAPI spawner : AIUtils.getAlliesOnMap(ship)) {
				if (MathUtils.getDistance(ship, spawner) <= 9999F) {
					if (spawner.getId().equals("Mayawati7s")) {
						spawner.getVariant().addMod("Cheat_Maya7s");
					}
				}
			}
		}


		if (ship.getFluxTracker().isVenting()) {
			ArmorGridAPI armor = ship.getArmorGrid(); //ArmorAPI, I not even knew that this existed before
			float[][] grid = armor.getGrid(); //Calls the grid
			float Regen = armor.getMaxArmorInCell() * (0.005f); //regen per frame (or per time elapsed or per second?)
			if (Regen > armor.getMaxArmorInCell())
				Regen = 0f; //This makes the regen stops, compares the current armor vs max armor
			for (int x = 0; x < grid.length; x++) { //I think that those loops calls the "setArmorValue" and updates
				for (int y = 0; y < grid[0].length; y++) { //the current armor value, including the regenerated armor
					//for me? Its rocket science tbh
					armor.setArmorValue(x, y, Math.min(grid[x][y] + Regen, armor.getMaxArmorInCell()));
					//this line above makes everything called before work, regenerates the entire armor grid,
					//does not prioritize areas with less armor, its a general regen, tested to reach to that conclusion
				}
			}
		}


			if (!ship.getFluxTracker().isVenting()) {
				if (ship.getSystem().getAmmo() >= 4) {
					ship.useSystem();
					ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF,2f);
				}

				if (ship.getHullLevel() <= 0.4f) {
                    ship.useSystem();
					ship.setRetreating(false,false);
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

		LabelAPI label = tooltip.addPara("Brings the ultimate overpower version from this ship, intended to be used by AI enemy when you face it. If you got this somehow, you are a declared cheater, and any complains about balance from you are negated, thank you.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();


		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
