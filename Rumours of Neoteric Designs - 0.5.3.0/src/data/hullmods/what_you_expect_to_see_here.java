package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class what_you_expect_to_see_here extends BaseHullMod {
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		// Hey,if you are seeing this, curiosity killed the cat, ya know? hehe

		//Anyways, thanks for downloading my mod, and hopefully the code inside here can help you,
		//its just a bunch of random low brainer adaptations, that can be useful for crash your game
		//But if you still want to venture here, don't bring me a tear^^
		//After Matt drama happened, I should tell this, THERE IS NO TRUE CRASHCODE
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

}
