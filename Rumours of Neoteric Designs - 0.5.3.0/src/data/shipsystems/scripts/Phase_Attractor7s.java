package data.shipsystems.scripts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import data.scripts.util.MagicUI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class Phase_Attractor7s extends BaseShipSystemScript {
	public static Object KEY_SHIP = new Object();
	public static Object KEY_TARGET = new Object();

	protected Object STATUSKEY1;
	protected Object STATUSKEY2;
	protected Object STATUSKEY3;
	protected Object STATUSKEY4;
	
	public static float DAM_MULT = 1.5f;
	protected static float RANGE = 1500f;
	private IntervalUtil timer = new IntervalUtil(15f, 15f);
	private IntervalUtil framer = new IntervalUtil(0.5f, 0.5f);
	private IntervalUtil backwards_timer = new IntervalUtil(0.5f, 0.5f);
	int frame7 = 0;
	
	public static Color TEXT_COLOR = new Color(55, 255, 88,255);
	
	public static Color JITTER_COLOR = new Color(50, 255, 122,75);

	public static final Color CORE_COLOR = new Color(194, 250, 195, 90);
	public static final Color FRINGE_COLOR = new Color(214, 253, 216, 155);

	public void renderStormCloud(
			Vector2f point,
			float angle,
			float width,
			float height,
			Color fringeColor,
			Color coreColor,
			Color lightningColor,
			int numParticles,
			float Duration) {

		float endSizeMult = 2f;
		float rampUpFraction = -0.05f;
		float fullBrightnessFraction = 0.25f;
		float totalDuration = Duration;
		float maxSize = height;
		float maxVelocity = 1f;

		java.util.List<Color> colorList = new ArrayList<>(Arrays.asList(
				fringeColor,
				coreColor));

		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j <= numParticles; j++) {

				float X = width * MathUtils.getRandomNumberInRange(-0.5f, 0.5f);
				float Y = height * MathUtils.getRandomNumberInRange(-0.5f, 0.5f);
				Vector2f vel = MathUtils.getRandomPointInCircle(new Vector2f(0f, 0f), maxVelocity);
				float size = maxSize * MathUtils.getRandomNumberInRange(0.5f, 1f);


				Vector2f spawnnPoint = MathUtils.getPointOnCircumference(MathUtils.getPointOnCircumference(point, X, angle + 90f), Y, angle);
				Global.getCombatEngine().addNebulaParticle(spawnnPoint,
						vel,
						size,
						endSizeMult,
						rampUpFraction,
						fullBrightnessFraction,
						totalDuration,
						colorList.get(i));

			}
		}
	}
	public static class TargetData {
		public ShipAPI ship;
		public ShipAPI target;
		public EveryFrameCombatPlugin targetEffectPlugin;
		public float currDamMult;
		public float elaspedAfterInState;
		public TargetData(ShipAPI ship, ShipAPI target) {
			this.ship = ship;
			this.target = target;
		}

	}
	protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		float level = effectLevel;
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		backwards_timer.advance(dur);

		ShipSystemAPI cloak = playerShip.getPhaseCloak();
		if (cloak == null) cloak = playerShip.getSystem();
		if (cloak == null) return;
		float speed = 1f - (playerShip.getHardFluxLevel());
		if (speed < 0.25f) speed = (0.25f);

		if (level > 0) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					playerShip.getSystem().getSpecAPI().getIconSpriteName(), playerShip.getSystem().getSpecAPI().getIconSpriteName(), "Timeflow Altered", false);
		} else {

		}
	}


	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		timer.advance(dur);
		framer.advance(dur);
		backwards_timer.advance(dur);

		if (effectLevel >= 0f) {
			if (framer.intervalElapsed()) {
				frame7 += 1 * Global.getCombatEngine().getTimeMult().getMult();
			}
		}

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

		float ringWidth = 1.5f;
		float damage = 100f;
		float empDamage = 100f;
		float empThickness = 5f;
		float width = ship.getCollisionRadius() / 3f;
		float height = ship.getCollisionRadius() * 2f;
		float duration = 1f;

		Color CloudcoreColor = new Color(141, 149, 153, 10);
		Color CloudfringeColor = new Color(141, 149, 153, 10);

		Vector2f point3 = MathUtils.getRandomPointInCircle(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 1f, ship.getCollisionRadius() * 2f));

		float angle = VectorUtils.getAngle(point3, ship.getLocation());
		java.util.List<SpriteAPI> sprite = new ArrayList<>(Arrays.asList(
				Global.getSettings().getSprite("lightfx", "bolt0"),
				Global.getSettings().getSprite("lightfx", "bolt1"),
				Global.getSettings().getSprite("lightfx", "bolt2"),
				Global.getSettings().getSprite("lightfx", "bolt3"),
				Global.getSettings().getSprite("lightfx", "bolt4"),
				Global.getSettings().getSprite("lightfx", "bolt5"),
				Global.getSettings().getSprite("lightfx", "bolt6"),
				Global.getSettings().getSprite("lightfx", "bolt7")));

		ShipAPI target_lock = ship.getShipTarget();
		if (effectLevel < 1f && state == State.IN) {
			if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
				for (ShipAPI Enemies : CombatUtils.getShipsWithinRange(ship.getLocation(), 1200f)) {
					if (!Enemies.isHulk() && Enemies.getOwner() != ship.getOwner()) {
						ship.setShipTarget(AIUtils.getNearestEnemy(ship));
					}
				}
			} if (state == State.IN) {
			ship.setShipTarget(AIUtils.getNearestEnemy(ship));
			}
		}
			if (target_lock != null) {
				Vector2f point4 = MathUtils.getRandomPointInCircle(target_lock.getLocation(), MathUtils.getRandomNumberInRange(target_lock.getCollisionRadius() * 1f, target_lock.getCollisionRadius() * 2f));
				if (effectLevel > 0) {
					if (target_lock == Global.getCombatEngine().getPlayerShip()) {
						Global.getCombatEngine().maintainStatusForPlayerShip(target_lock,
								ship.getSystem().getSpecAPI().getIconSpriteName(), ship.getSystem().getSpecAPI().getIconSpriteName(), "Ship is being attracted!", true);
					}
					if (MathUtils.getDistance(ship.getLocation(),target_lock.getLocation()) > 350f) {
						CombatUtils.applyForce(target_lock, VectorUtils.getAngle(target_lock.getLocation(), ship.getLocation()), 20f * effectLevel);
					}
					if (frame7 >= 20) {
						renderStormCloud(point4, angle, width, height,
								CloudfringeColor, CloudcoreColor, CloudfringeColor, 5, duration);
						// Global.getCombatEngine().spawnEmpArcVisual(MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 0.65f, ship.getCollisionRadius() * 1.25f)), ship, MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2.5f, ship.getCollisionRadius() * 5f)), ship, ship.getCollisionRadius() / 20f * 7f - 5f, new Color(199, 183, 113, 70), new Color(199, 183, 113, 55));
						Global.getCombatEngine().spawnEmpArc(target_lock, point4,
								new SimpleEntity(point4),
								new SimpleEntity(MathUtils.getRandomPointInCircle(point4, ship.getCollisionRadius())),
								DamageType.ENERGY, 0f, 0f,
								0f, null,
								target_lock.getCollisionRadius() / 70f * 7f,
								CORE_COLOR, FRINGE_COLOR);

						renderStormCloud(point3, angle, width, height,
								CloudfringeColor, CloudcoreColor, CloudfringeColor, 5, duration);
						// Global.getCombatEngine().spawnEmpArcVisual(MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 0.65f, ship.getCollisionRadius() * 1.25f)), ship, MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2.5f, ship.getCollisionRadius() * 5f)), ship, ship.getCollisionRadius() / 20f * 7f - 5f, new Color(199, 183, 113, 70), new Color(199, 183, 113, 55));
						Global.getCombatEngine().spawnEmpArc(ship, point3,
								new SimpleEntity(point3),
								new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
								DamageType.ENERGY, 0f, 0f,
								0f, null,
								ship.getCollisionRadius() / 70f * 7f,
								CORE_COLOR, FRINGE_COLOR);

						frame7 = 0;
					}
				}
		}
			float jitterLevel = effectLevel;
			if (state == State.OUT) {
				jitterLevel *= jitterLevel;
			}
			float shipJitterLevel = 0;
			if (state == State.IN) {
				shipJitterLevel = effectLevel;
			}
			float targetJitterLevel = effectLevel;

			float maxRangeBonus = 50f;
			float jitterRangeBonus = shipJitterLevel * maxRangeBonus;

			Color color = JITTER_COLOR;
			if (shipJitterLevel > 0) {
				//ship.setJitterUnder(KEY_SHIP, JITTER_UNDER_COLOR, shipJitterLevel, 21, 0f, 3f + jitterRangeBonus);
				ship.setJitter(KEY_SHIP, color, shipJitterLevel, 4, 0f, 0 + jitterRangeBonus * 1f);
			}
		}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		
	}
	
	protected ShipAPI findTarget(ShipAPI ship) {
		float range = getMaxRange(ship);
		boolean player = ship == Global.getCombatEngine().getPlayerShip();
		ShipAPI target = ship.getShipTarget();
		
		if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(AIFlags.TARGET_FOR_SHIP_SYSTEM)){
			target = (ShipAPI) ship.getAIFlags().getCustom(AIFlags.TARGET_FOR_SHIP_SYSTEM);
		}
		
		if (target != null) {
			float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
			float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
			if (dist > range + radSum) target = null;
		} else {
			if (target == null || target.getOwner() == ship.getOwner()) {
				if (player) {
					target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FIGHTER, range, true);
				} else {
					Object test = ship.getAIFlags().getCustom(AIFlags.MANEUVER_TARGET);
					if (test instanceof ShipAPI) {
						target = (ShipAPI) test;
						float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
						float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
						if (dist > range + radSum) target = null;
					}
				}
			}
			if (target == null) {
				target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FIGHTER, range, true);
			}
		}
		
		return target;
	}
	
	
	public static float getMaxRange(ShipAPI ship) {
		return ship.getMutableStats().getSystemRangeBonus().computeEffective(RANGE);
	}

	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (effectLevel > 0) {
			if (index == 0) {
				float damMult = 1f + (DAM_MULT - 1f) * effectLevel;
				return new StatusData("" + (int)((damMult - 1f) * 100f) + "% more damage to target", false);
			}
		}
		return null;
	}


	@Override
	public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
		if (system.isOutOfAmmo()) return null;
		if (system.getState() != SystemState.IDLE) return null;
		
		ShipAPI target = findTarget(ship);
		if (target != null && target != ship) {
			return "READY";
		}
		if ((target == null) && ship.getShipTarget() != null) {
			return "OUT OF RANGE";
		}
		return "NO TARGET";
	}

	
	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		//if (true) return true;
		ShipAPI target = findTarget(ship);
		return target != null && target != ship;
	}

}








