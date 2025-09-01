//Credits to Vayra's Collapsed Cargo Holds

package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
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

public class Ordnance_Amendment7s extends BaseHullMod {
		              
    


	public static final float ROF_REDUCTION = 0.25f;
	public static final float CARGO_REDUCTION = 0.75f;

    private final Map<String, Float> LOGISTIC = new HashMap<>();

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        if (LOGISTIC.get(id) != null) {
            stats.getCargoMod().modifyMult(id, 1f - 0.9f);
            stats.getMissileAmmoBonus().modifyPercent(id, (Math.min(LOGISTIC.get(id) * 0.25f, 400f * 0.25f)));
        }

    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        float CARGO = ship.getHullSpec().getCargo();
        LOGISTIC.put(id, CARGO);

        Set<String> BLOCKED_HULLMODS = new HashSet();
        BLOCKED_HULLMODS.add(HullMods.EXPANDED_CARGO_HOLDS);
        BLOCKED_HULLMODS.add(HullMods.CONVERTED_BAY);
        BLOCKED_HULLMODS.add("seven_overcapacity_missile_racks");
        BLOCKED_HULLMODS.add("converted_fighterbay");
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "ordnance_amendment7s");
            }
        }

        }

        
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		
             return 		
                    !ship.getVariant().getHullMods().contains("expanded_cargo_holds") &&
                    !ship.getVariant().getHullMods().contains("seven_takeshido_overhaul_combat") &&
                    !ship.getVariant().getHullMods().contains("converted_fighterbay");
                    }
        
        public String getUnapplicableReason(ShipAPI ship) {
		
		
		if (ship.getVariant().getHullMods().contains("expanded_cargo_holds")) {
			return "This modification interferes with cargo holds";
                        
		}
		if (ship.getVariant().getHullMods().contains("seven_takeshido_overhaul_combat")) {
			return "This ship has already lost its cargo holds";
                        
                }
                if (ship.getVariant().getHullMods().contains("converted_fighterbay")) {
			return "This modification interferes with cargo holds";
                        
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
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color text = new Color (132, 255, 149);
        Color background = new Color (15, 21, 17);
        //text,background,



        LabelAPI label = tooltip.addPara("Modifies cargo holds to store additional missile ammunition.", opad, h,
        "");
	label.setHighlight(
            "");
    if (ship != null) {
        tooltip.addPara("Current missile ammo bonus is %s.", opad, h,
                Math.round(1f * Math.min(100f, ship.getHullSpec().getCargo() * 0.25f)) + "%"
        );
    }


        tooltip.addSectionHeading("Modifies:",text,background, Alignment.MID, opad);

        tooltip.addPara("%s missile ammo for each %s of rearranged cargo holds, with a threshold increase of %s;", opad, h,
                "" + "+25%",
                "" + "100 units",
                "" + "100%"
        );
        
        label = tooltip.addPara( "Cargo capacity reduced by %s. ", opad, bad,
        "" + "90%");

        tooltip.addSectionHeading("Interactions with other hullmods:",text,background, Alignment.MID, opad);

        label = tooltip.addPara( "Stacks with %s; ", opad, h,
                "" +
                "" + "Expanded Missile Racks");
        label.setHighlightColors(h);

        label = tooltip.addPara( "Is incompatible with %s; ", opad, h,
                "" +
                "" + "Expanded Cargo Holds");
        label.setHighlightColors(h);

        label = tooltip.addPara( "Is incompatible with %s; ", opad, h,
                "" + "Converted Fighter Bays");
        label.setHighlightColors(h);

        label = tooltip.addPara( "Does Not Stack with %s. ", opad, h,
                "" + "Other Cargo Holds Modifiers");
        label.setHighlightColors(h);

        tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
        tooltip.addImageWithText(opad);
        




    }
}
