package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Guerrilla_Overhaul7s extends BaseHullMod {


    
    public static final float SPEED_FLAT_PENALTY = 25f;
    public static final float SPEED_PERCENT_PENALTY = 0.35f;

    public static final float SPEED_BONUS = 100;
    
    private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0f);
		mag.put(HullSize.FRIGATE, 0.5f);
		mag.put(HullSize.DESTROYER, 0.45f);
		mag.put(HullSize.CRUISER, 0.4f);
		mag.put(HullSize.CAPITAL_SHIP, 0.35f);
        }
        private float MALFUNCTION_REDUCTION = 50f;

    
   
    
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        boolean player = false;
        int number = 0;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }
            stats.getPeakCRDuration().modifyMult(id, 0f);
            stats.getCRPerDeploymentPercent().modifyMult(id,0f);
            stats.getCRLossPerSecondPercent().modifyMult(id, (Float) mag.get(hullSize));
			stats.getWeaponMalfunctionChance().modifyMult(id, 1f - MALFUNCTION_REDUCTION / 100f);
			stats.getEngineMalfunctionChance().modifyMult(id, 1f - MALFUNCTION_REDUCTION / 100f);
        }
        
        //Easier way to do hullmod incompatibility
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            Set<String> BLOCKED_HULLMODS = new HashSet();
            BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
            BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
            for (String hullmod : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(hullmod)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), hullmod, "seven_antimatter_compensator");
            }
        }
        }
        
        public void advanceInCombat(ShipAPI ship, float amount) {
            boolean player = false;
            player = ship == Global.getCombatEngine().getPlayerShip();
            float CR = ship.getMutableStats().getCRLossPerSecondPercent().computeEffective(ship.getHullSpec().getCRLossPerSecond());
            float total_cr = ship.getCurrentCR() - 0.4f;
            int time = Math.round(Math.round((1f / CR) * total_cr * 100f));
            if (time < 0) time = (0);
            if (player) {
                if (ship.getCurrentCR() <= 40.1f) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(ship.getId(), "graphics/icons/tactical/cr_tactical3.png", "Guerrilla Overhauls", "Remaining time for 40% CR: " + time + " sec", false);
                }
            }
        }
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
            return  
                    
                    !ship.getVariant().getHullMods().contains("safetyoverrides") &&
                    !ship.getVariant().getHullMods().contains("seven_system_overflow_safeties");
	}

	public String getUnapplicableReason(ShipAPI ship) {
		
                if (ship.getVariant().getHullMods().contains("safetyoverrides")) {
			        return "Incompatible with Safety Overrides";
		}
                if (ship.getVariant().getHullMods().contains("seven_system_overflow_safeties")) {
                    return "Why are you still using Epta?";
		}
		
		
		return null;
	}
        
        
        
        
    public String getDescriptionParam(int index, HullSize hullSize) {
    //if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
        return null;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color m = Misc.getMissileMountColor();
        Color e = Misc.getEnergyMountColor();
        Color b = Misc.getHighlightColor();
        Color text = new Color (132, 255, 149);
        Color background = new Color (15, 21, 17);
        Color white = Misc.getTextColor();
        
        Color bad = Misc.getNegativeHighlightColor();

        LabelAPI label = tooltip.addPara("Dedicates the entire subroutines for consecutive combats. Decreases peak operating time to %s values, reduces the rate at which combat readiness degrades by %s, depending on hull size and allows for %s.", opad, b,
        "" + "minimal",
        "" + "50%/55%/60%/65%",
                "no CR reduction after battles");
	label.setHighlight("" + "minimal",
            "" + "50%/55%/60%/65%",
            "no CR reduction after battles");
	label.setHighlightColors(bad, b, b);

    if (ship != null && ship.getVariant().hasHullMod("guerrilla_overhaul7s")) {
        float CR = ship.getMutableStats().getCRLossPerSecondPercent().computeEffective(ship.getHullSpec().getCRLossPerSecond());
        String ship_name = ship.getName();
        float total_cr = ship.getCurrentCR() - 0.4f;

        tooltip.addSectionHeading("Details:", text, background, Alignment.MID, opad);

        label = tooltip.addPara("Specifically for %s, CR only decays by %s every %s once PPT runs out.", opad, b,
                "" + ship_name,
                "" + "1%",
                "" + Math.round(1f / CR) + " seconds");
        label.setHighlight("" + ship_name,
                "" + "1%",
                "" + Math.round(1f / CR) + " seconds");
        label.setHighlightColors(white, b, b, bad);

        label = tooltip.addPara("In other words, it'll take %s for this ship's %s to reach %s, and suffers %s.", opad, b,
                "" + Math.round((1f / CR) * total_cr * 100f) + " seconds",
                "" + "combat readiness",
                "" + "40%",
                "" + "malfunctions");
        label.setHighlight("" + Math.round((1f / CR) * total_cr * 100f) + " seconds",
                "" + "combat readiness",
                "" + "40%",
                "" + "malfunctions");
        label.setHighlightColors(b, b, white, white);

    } else {
        label = tooltip.addPara("Install this hullmod for further details.", opad, b, "");
        label.setHighlight();
        label.setHighlightColors();
    }
        tooltip.addSectionHeading("Interactions with other modifiers:",text,background, Alignment.MID,opad);
        
        label = tooltip.addPara("This PPT modifier is %s, so Hardened Subsystems, other PPT boosting hullmods and skills %s. CR decay modifiers %s", opad, b,
    "" + "multiplicative",

    "" + "bonuses are reduced to the minimum",

    "" + "remains intact");
	label.setHighlight(
    "" + "multiplicative",

    "" + "bonuses are reduced to the minimum",

    "" + "remains intact");
	label.setHighlightColors(b, bad, b);
        
        
        
        




    }
}