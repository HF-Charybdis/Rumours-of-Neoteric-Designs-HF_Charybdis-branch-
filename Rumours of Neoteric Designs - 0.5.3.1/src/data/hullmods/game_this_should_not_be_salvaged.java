package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class game_this_should_not_be_salvaged extends BaseHullMod {
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();
		stats.getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, (100f));
		if (variant.hasDMods()) {
			variant.removeTag(Tags.SHIP_UNIQUE_SIGNATURE);
			variant.removeTag(Tags.NO_ENTITY_TOOLTIP);
			variant.removeTag(Tags.SHIP_LIMITED_TOOLTIP);
			variant.addPermaMod("Explodes7s");
		}

		stats.getDynamic().getMod(Stats.SHIP_RECOVERY_MOD).modifyMult(id, 0f);
		stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyMult(id, 0f);
		stats.getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 10f);

	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

}
