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
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.magiclib.util.MagicRender;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.combat.DamageType.*;


public class Singularity_effect7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	boolean threshold = false;
	private int timer = 0;



	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		stats.getWeaponHealthBonus().modifyMult("stop7s",9999999999999f);
		stats.getZeroFluxSpeedBoost().modifyMult("stop7s", 0f);
	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused() || !ship.isAlive()) {
			return;
		}
		Global.getCombatEngine().applyDamage(ship,ship.getLocation(),15,FRAGMENTATION,0,true,true,ship);


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
}
