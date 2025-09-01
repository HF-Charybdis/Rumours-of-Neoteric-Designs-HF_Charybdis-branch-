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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;


public class Tempestas_Cheat_Core7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	public static Color NOT_INTENSE = new Color(190, 247, 232, 150);
	public static Color INTENSE = new Color(152, 255, 236, 255);



	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		if (variant.hasDMods()) {
			variant.removeTag(Tags.SHIP_UNIQUE_SIGNATURE);
			variant.removeTag(Tags.NO_ENTITY_TOOLTIP);
			variant.removeTag(Tags.SHIP_LIMITED_TOOLTIP);
			variant.removeMod("cheat_tempestas7s"); //this is for guarantee that this thing not drops with the
			//overpowered hullmod
		}
		//this WILL have dmods, no matters what
		stats.getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyFlat(id, (1000000f));
		stats.getDynamic().getMod(Stats.SHIP_RECOVERY_MOD).modifyFlat(id, (10000000f));
		variant.addTag(Tags.SHIP_UNIQUE_SIGNATURE);
		variant.addTag(Tags.NO_ENTITY_TOOLTIP);
		variant.addTag(Tags.SHIP_LIMITED_TOOLTIP);
		stats.getSightRadiusMod().modifyFlat(id, 50000);
		stats.getCRLossPerSecondPercent().modifyMult(id,0.0001f);
		variant.addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
		stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id, 0f);


	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		String cheat = "cheat7s";
		float turning = 1.25f;
        ShieldAPI shield = ship.getShield();
		shield.setArc(300);
		shield.setType(ShieldAPI.ShieldType.OMNI);
		stats.getMissileAmmoBonus().modifyMult(cheat,25f);
		stats.getTimeMult().modifyMult(cheat,1.25f);
		stats.getEnergyWeaponFluxCostMod().modifyMult(cheat, 0.5f);
		if (!AIUtils.getAlliesOnMap(ship).isEmpty()) {
			for (ShipAPI Allies : CombatUtils.getShipsWithinRange(ship.getLocation(), 999999f)) {
				if (!Allies.isHulk() && Allies.getOwner() == ship.getOwner()) {
					Allies.setCurrentCR(1);
					Allies.setTimeDeployed(200);
					Allies.getMutableStats().getTimeMult().modifyMult(cheat,1.15f);
				}
				if (!Allies.isHulk() && Allies.getOwner() == ship.getOwner() && !ship.isAlive()) {
					Allies.getMutableStats().getTimeMult().unmodifyMult(cheat);
					Allies.getMutableStats().getEmpDamageTakenMult().unmodifyMult(cheat);
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

		LabelAPI label = tooltip.addPara("Brings the ultimate overpower version from this ship, intended to be used by AI enemy when you face it. If you got this somehow, you are a declared cheater, and any complains about balance from you are negated, thank you.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();


		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
