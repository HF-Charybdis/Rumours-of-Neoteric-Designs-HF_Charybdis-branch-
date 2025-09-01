package data.scripts;

import com.fs.starfarer.api.Global;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.scripts.util.MagicRender;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class seven_template_justforme extends BaseHullMod {


    
    public static final float TAG_1 = 0f;
    
    public static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0f);
		mag.put(HullSize.FRIGATE, 10f);
		mag.put(HullSize.DESTROYER, 15f);
		mag.put(HullSize.CRUISER, 25f);
		mag.put(HullSize.CAPITAL_SHIP, 40f);
        }
        public static final Color CORE_COLOR = new Color(254, 133, 133,75);
	public static final Color FRINGE_COLOR = new Color(255, 156, 156,155);
    
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, (Float) mag.get(hullSize));
                stats.getEnergyWeaponFluxCostMod().modifyPercent(id, 0.5f * (Float) mag.get(hullSize));
        }

        //EZ way to make incompatible stuff for hullmods
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            Set<String> BLOCKED_HULLMODS = new HashSet();
            BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
            BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
            for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "seven_antimatter_compensator");
            }
        }
        }
        @Override
	public boolean isApplicableToShip(ShipAPI ship) {
		
            
            
            return 		
                    !ship.getVariant().getHullMods().contains("hullmod1") &&
                    !ship.getVariant().getHullMods().contains("hullmod2");

	}
	
	
	public String getUnapplicableReason(ShipAPI ship) {
		
		
		if (ship.getVariant().getHullMods().contains("hullmod1")) {
			return "Serious, why more range?";
                        
		}
		if (ship.getVariant().getHullMods().contains("hullmod2")) {
			return "No, Paragons should not receive more range :)";
                        
		}
		
		return null;
	}
        
        

        public void advanceInCombat(ShipAPI ship, WeaponAPI weapon, float amount){ 
            
            
            ShipAPI example_location = weapon.getShip();
            ShipAPI owner_ship = weapon.getShip();
            
      final List<CombatEntityAPI> targetList = new ArrayList<CombatEntityAPI>();
      final List<CombatEntityAPI> entities = (List<CombatEntityAPI>)CombatUtils.getEntitiesWithinRange(example_location.getLocation(), 300);
      for (final CombatEntityAPI entity : entities) {
            if ((entity instanceof MissileAPI || entity instanceof ShipAPI) && entity.getOwner() != owner_ship.getOwner()) {
                if (entity instanceof ShipAPI) {
                    if (!((ShipAPI)entity).isAlive()) {
                        continue;
                    }
                    
                    if (!((ShipAPI)entity).getHullSize().equals(HullSize.FRIGATE)) {
                        continue;
                    }
                    
                    if (!((ShipAPI)entity).getHullSize().equals(HullSize.DESTROYER)) {
                        continue;
                    }
                    
                    if (!((ShipAPI)entity).getHullSize().equals(HullSize.CRUISER)) {
                        continue;
                    }
                    
                    if (!((ShipAPI)entity).getHullSize().equals(HullSize.CAPITAL_SHIP)) {
                        continue;
                    }
                    
                    if (((ShipAPI)entity).isPhased()) {
                        continue;
                    }
                }
                
                
                targetList.add(entity);
            
        
                    if (entities.isEmpty()) {
					entities.add(new SimpleEntity(MathUtils.getRandomPointInCircle(example_location.getLocation(), 100f)));
				}
                   
                        
				for (int i = 0; i < 5; i++) {
                        CombatEntityAPI target2 = entities.get(MathUtils.getRandomNumberInRange(0, entities.size() - 1));
                        if(AIUtils.getNearestShip(target2)!=null){
							Global.getCombatEngine().spawnEmpArc(AIUtils.getNearestShip(target2) , example_location.getLocation(), target2, target2,
									DamageType.FRAGMENTATION, //Damage type
									300f, //damage
									100f, //EMP
									300, //Max range
									"mote_attractor_impact_normal", //Impact sound
									10f, // thickness of the lightning bolt
									CORE_COLOR, //Central color
									FRINGE_COLOR //Fringe Color);
							);
                                     
                          }
                    }
            }
      }
      ShipAPI enemy = ship.getShipTarget();
      if (ship.getSystem().getEffectLevel() >= 0.8f) {
                            Vector2f joltvel = VectorUtils.rotate(new Vector2f(320,0),enemy.getFacing());
		            Vector2f aaloc = VectorUtils.rotate(new Vector2f(-50,0),enemy.getFacing());
			enemy.getVelocity().set(joltvel);
			MagicRender.battlespace(
					Global.getSettings().getSprite(enemy.getHullSpec().getSpriteName()),
					new Vector2f(aaloc.getX()+enemy.getLocation().getX(),aaloc.getY()+enemy.getLocation().getY()),
					new Vector2f(0,0),
					new Vector2f(enemy.getSpriteAPI().getWidth(),enemy.getSpriteAPI().getHeight()),
					new Vector2f(0,0),
					enemy.getFacing()-90f,
					0,
					new Color(255, 255, 255, 1),
					true,
					0f,
					0f,
					0f,
					0f,
					0f,
					0.1f,
					0.1f,
					1f,
					CombatEngineLayers.BELOW_SHIPS_LAYER
                               );
                        
                        if (Global.getCombatEngine().isPaused()) return;
			if (enemy == Global.getCombatEngine().getPlayerShip()) { 
			Global.getCombatEngine().maintainStatusForPlayerShip(this, 
			enemy.getSystem().getSpecAPI().getIconSpriteName(),
			enemy.getSystem().getDisplayName(), 
			"" + "NO NO NO NO NO, STOP!!! STOOOOOOP!!!", true);
                                                
		}
                        }
    }
        
        
        // START HERE
        
        
        
        
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
        Color t = Misc.getDesignTypeColor("Takeshido");
        Color l = Misc.getDesignTypeColor("Low Tech");
        Color md = Misc.getDesignTypeColor("Midline");
        Color h = Misc.getDesignTypeColor("High Tech");
        Color et = Misc.getDesignTypeColor("Epta Tech");
        Color p = Misc.getDesignTypeColor("seven phase");
        Color w = Misc.getDesignTypeColor("seven white");
        Color prt = Misc.getDesignTypeColor("Pirate");
        
        Color bad = Misc.getNegativeHighlightColor();



        LabelAPI label = tooltip.addPara("add flavour here %s,%s,%s.", opad, t, 
        "" + "tag 1",
        "" + "tag 2",
        "" + "tag 3");
	label.setHighlight("" + "tag 1",
                           "" + "tag 2",
                           "" + "tag 3");
	label.setHighlightColors(t, b, t);


        tooltip.addSectionHeading("Modifies:", Alignment.MID, opad);
        
        label = tooltip.addPara( "%s of %s; ", opad, t, 
        "" + "+1",
        "" + "Burn Level");
	label.setHighlight("" + "+1",
                           "" + "Burn Level");
	label.setHighlightColors(b, t);
        
        label = tooltip.addPara( "%s of %s; ", opad, b, 
        "" + "+10%",
        "" + "Fuel Consumption");
	label.setHighlight("" + "10%",
        "" + "Fuel Consumption");
	label.setHighlightColors(bad, t);
        
        



        tooltip.addSectionHeading("Suggestions", Alignment.MID, opad);


        label = tooltip.addPara("add flavour text here %s,%s,%s,%s.", opad, l, 
        "" + "Onslaughts",
        
        "" + "Conquests",
        
        "" + "first one",
        
        "" + "second one");
	label.setHighlight(
        "" + "Onslaughts",
        
        "" + "Conquests",
        
        "" + "first one",
        
        "" + "second one");
	label.setHighlightColors(l, md, l, md);
        
        tooltip.addSectionHeading("Interactions with other hullmods:", Alignment.MID, opad);

        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Stacks",
        "" + "Integrated Targeting Unity");
	label.setHighlight("" + "Stacks",
        "" + "Integrated Targeting Unity");
	label.setHighlightColors(m, b);
        
        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Stacks",
        "" + "Systems Overflow - Accelerator");
	label.setHighlight("" + "Stacks",
        "" + "Systems Overflow - Accelerator");
	label.setHighlightColors(m, et);
        
        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Is incompatible",
        "" + "Scatter Targeting Device");
	label.setHighlight("" + "Is incompatible",
        "" + "Scatter Targeting Device");
	label.setHighlightColors(bad, et);
        
        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Is incompatible",
        "" + "Advanced Targeting Unity");
	label.setHighlight("" + "Is incompatible",
        "" + "Advanced Targeting Unity");
	label.setHighlightColors(bad, b);
        
        label = tooltip.addPara( "%s with %s; ", opad, b, 
        "" + "Is incompatible",
        "" + "Targeting Supercomputer");
	label.setHighlight("" + "Is incompatible",
        "" + "Targeting Supercomputer");
	label.setHighlightColors(bad, b);
        




    }
}