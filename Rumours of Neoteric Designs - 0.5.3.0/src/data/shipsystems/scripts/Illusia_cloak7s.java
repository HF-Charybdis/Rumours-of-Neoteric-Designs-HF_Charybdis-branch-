package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Illusia_cloak7s extends BaseShipSystemScript {
	public static final float JITTER_FADE_TIME = 0.5f;
	public static Color JITTER = new Color(242, 85, 85, 160);
	public static final float SHIP_ALPHA_MULT = 0.25f;
	public static final float VULNERABLE_FRACTION = 0.0f;
	public static final float INCOMING_DAMAGE_MULT = 0.25f;
	public static final float MAX_TIME_MULT = 2.0f;
	protected Object STATUSKEY1;
	protected Object STATUSKEY2;
	protected Object STATUSKEY3;
	protected Object STATUSKEY4;

	int empfrequency = 0;



	public Illusia_cloak7s() {
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
		if (cloak == null) cloak = playerShip.getSystem();
		if (cloak == null) return;

		if (level > f) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered - 2x", false);
		} else {

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
		stats.getArmorDamageTakenMult().modifyMult(id,0);
		stats.getHullDamageTakenMult().modifyMult(id,0);

		float level = effectLevel;



		float jitterLevel = 0f;
		float jitterRangeBonus = 0f;
		float levelForAlpha = level;

		ShipSystemAPI cloak = ship.getPhaseCloak();
		if (cloak == null) cloak = ship.getSystem();


		if (state == State.IN || state == State.ACTIVE) {
			ship.setPhased(true);
			levelForAlpha = level;
		} else if (state == State.OUT) {
			if (level > 0.75f) {
				ship.setPhased(true);
			} else {
				ship.setPhased(false);
			}
			levelForAlpha = level;

		}

		ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * levelForAlpha);
		ship.setApplyExtraAlphaToEngines(true);

		final java.util.List<CombatEntityAPI> targetList = new ArrayList<CombatEntityAPI>();
		final java.util.List<CombatEntityAPI> entities = (List<CombatEntityAPI>) CombatUtils.getEntitiesWithinRange(ship.getLocation(), ship.getCollisionRadius() + 50f);
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
					ShipAPI.HullSize hullSize = ship.getHullSize();
					empfrequency += 1;
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
		}



		//float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * levelForAlpha;
		float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * levelForAlpha;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}


	}


	public void unapply(MutableShipStatsAPI stats, String id) {

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
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);

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