package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class Explodes_deployed7s extends BaseHullMod {

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		if (ship.isAlive()) {
			Global.getCombatEngine().applyDamage(ship,ship.getLocation(),150000, DamageType.ENERGY,0,true,false,ship,true);
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

		LabelAPI label = tooltip.addPara("This ship will explode if used in combat, unfortunately it is too broken to exceed any function that is not cautionary or routine scheduled.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();


		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}

}
