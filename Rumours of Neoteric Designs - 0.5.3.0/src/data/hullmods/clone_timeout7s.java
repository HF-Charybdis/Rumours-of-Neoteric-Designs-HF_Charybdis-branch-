package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class clone_timeout7s extends BaseHullMod {



	public void advanceInCombat(ShipAPI ship, float amount){

		if (ship.getCurrentCR() == 0) {
            //Global.getCombatEngine().removeEntity(ship);
            ship.setHitpoints(0);
        }

        //for Mayawati clone
        if (ship.getHullLevel() <= 0.2f) {
            Global.getCombatEngine().removeEntity(ship);

        }
	}

        



}
