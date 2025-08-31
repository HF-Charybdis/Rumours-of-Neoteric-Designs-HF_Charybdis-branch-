package data.shipsystems.scripts;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.MineStrikeStatsAIInfoProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;

public class Singularity_mines7s extends BaseShipSystemScript implements MineStrikeStatsAIInfoProvider {
	
	protected static float MINE_RANGE = 1500;
	private static Map mag = new HashMap();
	static {
		mag.put(ShipAPI.HullSize.FIGHTER, 10f);
		mag.put(ShipAPI.HullSize.FRIGATE, 1f);
		mag.put(ShipAPI.HullSize.DESTROYER, 1f);
		mag.put(ShipAPI.HullSize.CRUISER, 1f);
		mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 1f);
        }
	public static final float MIN_SPAWN_DIST = 100f;
	public static final float MIN_SPAWN_DIST_FRIGATE = 100f;
	
	public static final Color JITTER_COLOR = new Color(133, 254, 204,75);
	public static final Color JITTER_UNDER_COLOR = new Color(156, 255, 210,155);
        public static Color COLOR = new Color(133, 254, 204,255);

	
	public static float getRange(ShipAPI ship) {
		if (ship == null) return MINE_RANGE;
		return ship.getMutableStats().getSystemRangeBonus().computeEffective(MINE_RANGE);
	}
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		//boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		
		float jitterLevel = effectLevel;
		if (state == State.OUT) {
			jitterLevel *= jitterLevel;
		}
		float maxRangeBonus = 25f;
		float jitterRangeBonus = jitterLevel * maxRangeBonus;
		if (state == State.OUT) {
		}
		
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 11, 0f, 3f + jitterRangeBonus);
		ship.setJitter(this, JITTER_COLOR, jitterLevel, 4, 0f, 0 + jitterRangeBonus);
		
		if (state == State.IN) {
		} else if (effectLevel >= 1) {
			Vector2f target = ship.getMouseTarget();
			if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(AIFlags.SYSTEM_TARGET_COORDS)){
				target = (Vector2f) ship.getAIFlags().getCustom(AIFlags.SYSTEM_TARGET_COORDS);
			}
			if (target != null) {
				float dist = Misc.getDistance(ship.getLocation(), target);
				float max = getMaxRange(ship) + ship.getCollisionRadius();
				if (dist > max) {
					float dir = Misc.getAngleInDegrees(ship.getLocation(), target);
					target = Misc.getUnitVectorAtDegreeAngle(dir);
					target.scale(max);
					Vector2f.add(target, ship.getLocation(), target);
				}
				
				target = findClearLocation(ship, target);
				
				if (target != null) {
					spawnMine(ship, target);
				}
			}
			
		} else if (state == State.OUT ) {
		}
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
	}
	
	public void spawnMine(ShipAPI source, Vector2f mineLoc) {
		CombatEngineAPI engine = Global.getCombatEngine();
		Vector2f currLoc = Misc.getPointAtRadius(mineLoc, 30f + (float) Math.random() * 30f);
		//Vector2f currLoc = null;
		float start = (float) Math.random() * 360f;
		for (float angle = start; angle < start + 390; angle += 30f) {
			if (angle != start) {
				Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
				loc.scale(50f + (float) Math.random() * 30f);
				currLoc = Vector2f.add(mineLoc, loc, new Vector2f());
			}
			for (DamagingProjectileAPI other : Global.getCombatEngine().getProjectiles()) {
				if (!other.isExpired()) continue;
				
				float dist = Misc.getDistance(currLoc, other.getLocation());
				if (dist < other.getCollisionRadius() + 40f) {
					currLoc = null;
					break;
				}
			}
			if (currLoc != null) {
				break;
			}
		}
		if (currLoc == null) {
			currLoc = Misc.getPointAtRadius(mineLoc, 30f + (float) Math.random() * 30f);
		}
		
		
		
		//Vector2f currLoc = mineLoc;
		Global.getCombatEngine().getFleetManager(source.getOwner()).setSuppressDeploymentMessages(true);
		ShipAPI mine = (ShipAPI) Global.getCombatEngine().getFleetManager(source.getOwner()).spawnShipOrWing("singularity_mine_wing", currLoc, MathUtils.getRandomNumberInRange(0,360));
		Global.getCombatEngine().getFleetManager(source.getOwner()).setSuppressDeploymentMessages(false);
         
			for (int t = 0; t < 2; t++) {	
		        engine.spawnEmpArcVisual(mine.getLocation(), mine, MathUtils.getRandomPointOnCircumference(mine.getLocation(), 25f), mine, 10f, new Color (255,255,255,160), new Color (255,255,255,125));
                        }


		Global.getSoundPlayer().playSound("volition_sound7s", 1f, 1f, mine.getLocation(), mine.getVelocity());
		engine.addSwirlyNebulaParticle(mine.getLocation(),new Vector2f(),150f,0.75f,0.5f,0.25f,0.4f,COLOR,true);
		engine.spawnDamagingExplosion(createExplosionSpec(), mine, mine.getLocation());
      
      final List<CombatEntityAPI> targetList = new ArrayList<CombatEntityAPI>();
      final List<CombatEntityAPI> entities = (List<CombatEntityAPI>)CombatUtils.getEntitiesWithinRange(mine.getLocation(), 300);
      for (final CombatEntityAPI entity : entities) {
            if ((entity instanceof MissileAPI || entity instanceof ShipAPI) && entity.getOwner() != mine.getOwner()) {
                if (entity instanceof ShipAPI) {
                    if (!((ShipAPI)entity).isAlive()) {
                        continue;
                    }
                    
                    if (((ShipAPI)entity).isPhased()) {
                        continue;
                    }
                }
                
                
                targetList.add(entity);
            
        
                    if (entities.isEmpty()) {
					entities.add(new SimpleEntity(MathUtils.getRandomPointInCircle(mine.getLocation(), 100f)));
				}
                   
                        
				for (int i = 0; i < 2; i++) {
                        CombatEntityAPI target2 = entities.get(MathUtils.getRandomNumberInRange(0, entities.size() - 1));
                        if(AIUtils.getNearestShip(target2)!=null){
							engine.spawnEmpArc(AIUtils.getNearestShip(target2) , mine.getLocation(), target2, target2,
									DamageType.FRAGMENTATION, //Damage type
									200f,
									100f,
									300, //Max range
									"mote_attractor_impact_normal", //Impact sound
									10f, // thickness of the lightning bolt
									JITTER_COLOR, //Central color
									JITTER_UNDER_COLOR //Fringe Color);
							);
                                     
                          }
                    }
            }
      }
      
              
		if (source != null) {

		}
		
		
		float fadeInTime = 0.5f;
		mine.getVelocity().scale(0);
		
		
		//mine.setFlightTime((float) Math.random());
		//liveTime = 0.01f;
		
		
	}
        
	
	
	
	
	protected float getMaxRange(ShipAPI ship) {
		return getMineRange(ship);
	}

	
	@Override
	public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
		if (system.isOutOfAmmo()) return null;
		if (system.getState() != SystemState.IDLE) return null;
		
		Vector2f target = ship.getMouseTarget();
		if (target != null) {
			float dist = Misc.getDistance(ship.getLocation(), target);
			float max = getMaxRange(ship) + ship.getCollisionRadius();
			if (dist > max) {
				return "OUT OF RANGE";
			} else {
				return "READY";
			}
		}
		return null;
	}

	
	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return ship.getMouseTarget() != null;
	}
	
	
	private Vector2f findClearLocation(ShipAPI ship, Vector2f dest) {
		if (isLocationClear(dest)) return dest;
		
		float incr = 30f;

		WeightedRandomPicker<Vector2f> tested = new WeightedRandomPicker<Vector2f>();
		for (float distIndex = 1; distIndex <= 32f; distIndex *= 2f) {
			float start = (float) Math.random() * 360f;
			for (float angle = start; angle < start + 360; angle += 60f) {
				Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
				loc.scale(incr * distIndex);
				Vector2f.add(dest, loc, loc);
				tested.add(loc);
				if (isLocationClear(loc)) {
					return loc;
				}
			}
		}
		
		if (tested.isEmpty()) return dest; // shouldn't happen
		
		return tested.pick();
	}
	
	private boolean isLocationClear(Vector2f loc) {
		for (ShipAPI other : Global.getCombatEngine().getShips()) {
			if (other.isShuttlePod()) continue;
			if (other.isFighter()) continue;
			
//			Vector2f otherLoc = other.getLocation();
//			float otherR = other.getCollisionRadius();
			
//			if (other.isPiece()) {
//				System.out.println("ewfewfewfwe");
//			}
			Vector2f otherLoc = other.getShieldCenterEvenIfNoShield();
			float otherR = other.getShieldRadiusEvenIfNoShield();
			if (other.isPiece()) {
				otherLoc = other.getLocation();
				otherR = other.getCollisionRadius();
			}
			
			
//			float dist = Misc.getDistance(loc, other.getLocation());
//			float r = other.getCollisionRadius();
			float dist = Misc.getDistance(loc, otherLoc);
			float r = otherR;
			//r = Math.min(r, Misc.getTargetingRadius(loc, other, false) + r * 0.25f);
			float checkDist = MIN_SPAWN_DIST;
			if (other.isFrigate()) checkDist = MIN_SPAWN_DIST_FRIGATE;
			if (dist < r + checkDist) {
				return false;
			}
		}
		for (CombatEntityAPI other : Global.getCombatEngine().getAsteroids()) {
			float dist = Misc.getDistance(loc, other.getLocation());
			if (dist < other.getCollisionRadius() + MIN_SPAWN_DIST) {
				return false;
			}
		}
		
		return true;
	}


	public float getFuseTime() {
		return 3f;
	}


	public float getMineRange(ShipAPI ship) {
		return getRange(ship);
		//return MINE_RANGE;
	}

	public DamagingExplosionSpec createExplosionSpec() {
		// "muzzle flash"
		float radius = 0;
		float particlesizemin = 0;
		float particlesizerange = 0;
		float duration = 0;
		int count = 0;
		int alpha1 = 0;
		int alpha2 = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

			radius = 100f;
			particlesizemin = 0.5f;
			particlesizerange = 5f;
			duration = 0.7f;
			count = 150;
			alpha1 = 85;
			alpha2 = 200;
			//250, 213, 167
			red = 250;
			green = 213;
			blue = 167;
        /*
        else if (projectile.getWeapon().getId().equals("seven_redemoinho")) {
            radius = 8f;
            particlesizemin = 1f;
            particlesizerange = 2.5f;
            duration = 0.2f;
            count = 140;
            alpha1 = 40;
            alpha2 = 90;
            //yellow
            red = 250;
            green = 221;
            blue = 135;
        }

         */


		DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.7f, // duration
				radius, // radius
				radius, // coreRadius
				0, // maxDamage
				0, // minDamage
				CollisionClass.NONE, // collisionClass
				CollisionClass.NONE, // collisionClassByFighter
				particlesizemin, // particleSizeMin
				particlesizerange, // particleSizeRange
				duration, // particleDuration
				count, // particleCount
				new Color(133, 254, 204, alpha1), // particleColor
				new Color(133, 254, 204, alpha2)  // explosionColor
		);

		spec.setDamageType(DamageType.HIGH_EXPLOSIVE);
		spec.setUseDetailedExplosion(false);
		spec.setSoundSetId("");
		return spec;
	}

	
}








