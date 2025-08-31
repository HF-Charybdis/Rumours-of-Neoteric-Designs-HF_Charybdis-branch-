// 
// Decompiled by Procyon v0.5.36
// 
package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class CloneSmoking7s extends BaseShipSystemScript {

    public static final float JITTER_FADE_TIME = 0.5f;
    public static Color JITTER = new Color(130, 207, 180, 160);
    private int index7 = 0;
    public static final float SHIP_ALPHA_MULT = 0.25f;
    public static final float VULNERABLE_FRACTION = 0.0f;
    public static final float INCOMING_DAMAGE_MULT = 0.25f;
    public static final float MAX_TIME_MULT = 1f;
    public static Color SMOKING_2 = new Color(134, 186, 180, 90);
    public static Color SMOKING = new Color(141, 165, 141, 2);
    int empfrequency = 0;
    protected Object STATUSKEY1;
    protected Object STATUSKEY2;
    protected Object STATUSKEY3;
    protected Object STATUSKEY4;

    private IntervalUtil Interval = new IntervalUtil(8f, 14f);



    public CloneSmoking7s() {
        this.STATUSKEY1 = new Object();
        this.STATUSKEY2 = new Object();
        this.STATUSKEY3 = new Object();
        this.STATUSKEY4 = new Object();
    }

    public static float getMaxTimeMult(MutableShipStatsAPI stats) {
        return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
    }

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
        float level = effectLevel;
        float f = VULNERABLE_FRACTION;

        ShipSystemAPI cloak = playerShip.getPhaseCloak();
        if (cloak == null) {
            cloak = playerShip.getSystem();
        }
        if (cloak == null) {
            return;
        }

        if (level > f) {
//			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
//					cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "can not be hit", false);
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
                    cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
        } else {
//			float INCOMING_DAMAGE_MULT = 0.25f;
//			float percent = (1f - INCOMING_DAMAGE_MULT) * getEffectLevel() * 100;
//			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
//					spec.getIconSpriteName(), cloak.getDisplayName(), "damage mitigated by " + (int) percent + "%", false);
        }
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        boolean player = false;
            if (stats.getEntity() instanceof ShipAPI) {
                ship = (ShipAPI) stats.getEntity();
                player = ship == Global.getCombatEngine().getPlayerShip();
                id = id + "_" + ship.getId();
            } else {
                return;
            }

            if (player) {
                maintainStatus(ship, state, effectLevel);
            }

            if (Global.getCombatEngine().isPaused()) {
                return;
            }
            if (state == State.COOLDOWN || state == State.IDLE) {
                unapply(stats, id);
                return;
            }

            float speedPercentMod = stats.getDynamic().getMod(Stats.PHASE_CLOAK_SPEED_MOD).computeEffective(0f);
            stats.getMaxSpeed().modifyPercent(id, speedPercentMod * effectLevel);

            float level = effectLevel;
            //float f = VULNERABLE_FRACTION;


            float jitterLevel = 0f;
            float jitterRangeBonus = 0f;
            float levelForAlpha = level;

            ShipSystemAPI cloak = ship.getPhaseCloak();
            if (cloak == null) cloak = ship.getSystem();


            if (state == State.IN || state == State.ACTIVE) {
                ship.setPhased(true);
                //ship.setJitterUnder(ship, JITTER, 1f, 7, 10f, 15f);


                levelForAlpha = level;
            } else if (state == State.OUT) {
                if (level > 0.75f) {
                    ship.setPhased(true);
                } else {
                    ship.setPhased(false);
                }
                levelForAlpha = level;
//			if (level >= f) {
//				ship.setPhased(true);
//				if (f >= 1) {
//					levelForAlpha = level;
//				} else {
//					levelForAlpha = (level - f) / (1f - f);
//				}
//				float time = cloak.getChargeDownDur();
//				float fadeLevel = JITTER_FADE_TIME / time;
//				if (level >= f + fadeLevel) {
//					jitterLevel = 0f;
//				} else {
//					jitterLevel = (fadeLevel - (level - f)) / fadeLevel;
//				}
//			} else {
//				ship.setPhased(false);
//				levelForAlpha = 0f;
//
//				float time = cloak.getChargeDownDur();
//				float fadeLevel = JITTER_FADE_TIME / time;
//				if (level < fadeLevel) {
//					jitterLevel = level / fadeLevel;
//				} else {
//					jitterLevel = 1f;
//				}
//				//jitterLevel = level / f;
//				//jitterLevel = (float) Math.sqrt(level / f);
//			}
            }

//		ship.setJitter(JITTER_COLOR, jitterLevel, 1, 0, 0 + jitterRangeBonus);
//		ship.setJitterUnder(JITTER_COLOR, jitterLevel, 11, 0f, 7f + jitterRangeBonus);
            //ship.getEngineController().fadeToOtherColor(this, spec.getEffectColor1(), new Color(0,0,0,0), jitterLevel, 1f);
            //ship.getEngineController().extendFlame(this, -0.25f, -0.25f, -0.25f);

            ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * levelForAlpha);
            ship.setApplyExtraAlphaToEngines(true);
            stats.getEmpDamageTakenMult().modifyMult(id, 0f);
            stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - 50f * 0.01f);
            stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - 50f * 0.01f);


            final List<CombatEntityAPI> targetList = new ArrayList<CombatEntityAPI>();
            final List<CombatEntityAPI> entities = (List<CombatEntityAPI>) CombatUtils.getEntitiesWithinRange(ship.getLocation(), ship.getCollisionRadius() + 50f);
            for (final CombatEntityAPI entity : entities) {
                if ((entity instanceof ShipAPI || entity instanceof AsteroidAPI || entity instanceof MissileAPI) && entity.getOwner() != ship.getOwner()) {
                    if (entity instanceof ShipAPI) {
                        if (!((ShipAPI) entity).isAlive()) {
                            continue;
                        }
                        if (((ShipAPI) entity) == ship) {
                            continue;
                        }

                    }


                    targetList.add(entity);


                    if (entities.isEmpty()) {
                        entities.add(new SimpleEntity(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() + 200f)));
                    }


                    CombatEntityAPI target2 = entities.get(MathUtils.getRandomNumberInRange(0, entities.size() - 1));
                    if (AIUtils.getNearestShip(target2) != null) {
                        float emp = 0f;
                        int speed = Global.getCombatEngine().getTimeMult().getModifiedInt();
                        ShipAPI.HullSize hullSize = ship.getHullSize();
                            empfrequency += speed;
                            if (empfrequency == 12) {
                                Global.getCombatEngine().spawnEmpArc(ship, target2.getLocation(), target2, target2,
                                        DamageType.ENERGY, //Damage type
                                        0f, //damage
                                        emp, //EMP
                                        9999f, //Max range
                                        "", //Impact sound
                                        5f, // thickness of the lightning bolt
                                        new Color(95, 203, 239, 100), //Central color
                                        new Color(77, 75, 70, 100) //Fringe Color);
                                );
                                empfrequency = 0;



                        }
                    }
                }


                //float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * levelForAlpha;
                float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * levelForAlpha;
                stats.getTimeMult().modifyMult(id, shipTimeMult);
                if (player) {
                    Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			if (ship.areAnyEnemiesInRange()) {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			} else {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 2f / shipTimeMult);
//			}
                } else {
                    Global.getCombatEngine().getTimeMult().unmodify(id);
                }

//		float mitigationLevel = jitterLevel;
//		if (mitigationLevel > 0) {
//			stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * mitigationLevel);
//			stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * mitigationLevel);
//			stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - INCOMING_DAMAGE_MULT) * mitigationLevel);
//		} else {
//			stats.getHullDamageTakenMult().unmodify(id);
//			stats.getArmorDamageTakenMult().unmodify(id);
//			stats.getEmpDamageTakenMult().unmodify(id);
//		}
            }

    }

    public void unapply(MutableShipStatsAPI stats, String id) {
//		stats.getHullDamageTakenMult().unmodify(id);
//		stats.getArmorDamageTakenMult().unmodify(id);
        stats.getEmpDamageTakenMult().unmodify(id);

        ShipAPI ship = null;
        //boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            //player = ship == Global.getCombatEngine().getPlayerShip();
            //id = id + "_" + ship.getId();
        } else {
            return;
        }

        Global.getCombatEngine().getTimeMult().unmodify(id);
        stats.getTimeMult().unmodify(id);

        stats.getMaxSpeed().unmodifyPercent(id);

        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);

//		stats.getMaxSpeed().unmodify(id);
//		stats.getMaxTurnRate().unmodify(id);
//		stats.getTurnAcceleration().unmodify(id);
//		stats.getAcceleration().unmodify(id);
//		stats.getDeceleration().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
//		if (index == 0) {
//			return new StatusData("time flow altered", false);
//		}
//		float percent = (1f - INCOMING_DAMAGE_MULT) * effectLevel * 100;
//		if (index == 1) {
//			return new StatusData("damage mitigated by " + (int) percent + "%", false);
//		}
        return null;
    }
}
