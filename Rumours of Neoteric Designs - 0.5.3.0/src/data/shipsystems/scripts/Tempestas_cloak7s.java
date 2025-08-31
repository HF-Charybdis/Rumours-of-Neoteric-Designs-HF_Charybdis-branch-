package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicUI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tempestas_cloak7s extends BaseShipSystemScript {
	public static final float JITTER_FADE_TIME = 0.5f;
	public static Color JITTER = new Color(91, 203, 98, 100);
	public static final float SHIP_ALPHA_MULT = 0.15f;
	public static final float VULNERABLE_FRACTION = 0.0f;
	public static final float INCOMING_DAMAGE_MULT = 0.25f;
	public static final float MAX_TIME_MULT = 5f;
	public static Color CLOAK_HULL = new Color(117, 180, 120, 65);
	private ShipAPI ship;
	private ShipSystemAPI system;
	int momentum = 0;
	int backwards = 0;
	int backwards2 = 0;
	private IntervalUtil timer = new IntervalUtil(15f, 15f);
	private IntervalUtil framer = new IntervalUtil(0.5f, 0.5f);
	private IntervalUtil backwards_timer = new IntervalUtil(0.5f, 0.5f);
	private List<ShipAPI> buffed = new ArrayList<>();
	public final float BUFF_RANGE = 0.25f;
	public static Color SMOKE = new Color(138, 154, 142, 11);
	public static Color TEXT = new Color(190, 255, 200, 155);
	public final ArrayList<Color> COLOR1 = new ArrayList<>();
	public final ArrayList<Color> COLOR2 = new ArrayList<>();
	int frame7 = 0;
    boolean impact = false;
	public static final Color CORE_COLOR = new Color(194, 250, 195, 90);
	public static final Color FRINGE_COLOR = new Color(214, 253, 216, 155);
	protected Object STATUSKEY1;
	protected Object STATUSKEY2;
	protected Object STATUSKEY3;
	protected Object STATUSKEY4;



	public Tempestas_cloak7s() {
		this.STATUSKEY1 = new Object();
		this.STATUSKEY2 = new Object();
		this.STATUSKEY3 = new Object();
		this.STATUSKEY4 = new Object();
	}

	public static float getMaxTimeMult(MutableShipStatsAPI stats) {
		return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
	}

	public static Vector2f getPointInRing(float minRadius, float maxRadius, Vector2f center) {
		Vector2f result = new Vector2f();
		float angle = MathUtils.getRandomNumberInRange(0f, 360f);
		float radius = MathUtils.getRandomNumberInRange(minRadius, maxRadius);
		result = (MathUtils.getPointOnCircumference(center, radius, angle));
		return result;
	}

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

	protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		float level = effectLevel;
		float f = VULNERABLE_FRACTION;
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		backwards_timer.advance(dur);

		if (effectLevel == 1f && playerShip.getEngineController().isAcceleratingBackwards()) {
			if (backwards_timer.intervalElapsed()) {
				backwards += 1 * Global.getCombatEngine().getTimeMult().getMult();
			}
		}

		if (backwards >= 100) backwards = 100;

		ShipSystemAPI cloak = playerShip.getPhaseCloak();
		if (cloak == null) cloak = playerShip.getSystem();
		if (cloak == null) return;
		float speed = 1f - (playerShip.getHardFluxLevel());
		if (speed < 0.25f) speed = (0.25f);

		if (level > f) {
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "Timeflow Altered", false);
			Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
					cloak.getSpecAPI().getIconSpriteName(), "Main Core Stress", "Max Speed - " + Math.round(0f - (-speed * 100f)) + "%", true);
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
		boolean dissipate = false;

		if (effectLevel >= 0f) {
			if (backwards_timer.intervalElapsed()) {
				backwards2 += 1 * Global.getCombatEngine().getTimeMult().getMult();
			}
		}
		if (backwards2 >= 100) backwards2 = 100;

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
			MagicUI.drawInterfaceStatusBar(
					ship, momentum * 0.01f,
					null,
					null,
					momentum * 0.01f,
					"HASTE",
					(momentum)
			);
			maintainStatus(ship, state, effectLevel);
		}
		if (momentum >= 100) momentum = 100;

		if (Global.getCombatEngine().isPaused()) {
			return;
		}

		if (effectLevel == 1f && ship.getHardFluxLevel() > 0.4f) {
			ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.5f);
		}

		if (state == State.COOLDOWN || state == State.IDLE) {

			if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
				for (ShipAPI enemies : AIUtils.getEnemiesOnMap(ship)) {
					if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() + 4000) {
						enemies.getMutableStats().getEmpDamageTakenMult().unmodifyMult(id);
					}
				}
			}

			impact = false;
			unapply(stats, id);
			stats.getEnergyWeaponFluxCostMod().unmodifyMult(id);
			stats.getBeamWeaponFluxCostMult().unmodifyMult(id);
			stats.getBallisticWeaponFluxCostMod().unmodifyMult(id);
			stats.getMissileWeaponFluxCostMod().unmodifyMult(id);
			return;
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

		float speedPercentMod = stats.getDynamic().getMod(Stats.PHASE_CLOAK_SPEED_MOD).computeEffective(0f);
		stats.getMaxSpeed().modifyPercent(id, speedPercentMod * effectLevel);

		float level = effectLevel;


		float jitterLevel = 0f;
		float jitterRangeBonus = 0f;
		float levelForAlpha = level;

		ShipSystemAPI cloak = ship.getPhaseCloak();
		if (cloak == null) cloak = ship.getSystem();
		float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * levelForAlpha;
		float speed = 1f - (ship.getHardFluxLevel());
		if (speed < 0.25f) speed = (0.25f);
		stats.getTimeMult().modifyMult(id, shipTimeMult);


		if (state == State.IN || state == State.ACTIVE) {
			stats.getMaxSpeed().modifyMult(id, speed);
			stats.getCombatEngineRepairTimeMult().modifyMult(id,0.0001f);
			if (level < 0.95f) {
				Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius()),
						new Vector2f(0f, 0f),
						ship.getCollisionRadius(),
						4f,
						-0.2f,
						1f,
						2f - (effectLevel * 1f),
						SMOKE);
			}

			if (level < 0.65f) {
				MagicRender.battlespace(
						Global.getSettings().getSprite("graphics/ships/Stella_tempestas7s_glow.png"), //sprite
						ship.getShieldCenterEvenIfNoShield(), //location vector2f
						new Vector2f(0f, 0f), //velocity vector2f
						new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
						new Vector2f(0f, 0f), //growth, vector2f, pixels/second
						ship.getFacing() - 90f, //angle, float
						0f, //spin, float
						CLOAK_HULL, //color Color
						true, //additive, boolean

						0f, //jitter range
						1f, //jitter tilt
						1f, // flicker range
						0f, //flicker median
						0.05f, //max delay

						0.02f * dur, //fadein, float, seconds
						0.005f * dur, //full, float, seconds
						0.02f * dur, //fadeout, float, seconds

						CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);


				if (frame7 >= 10)  {

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

					Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1f, 0.4f, ship.getLocation(), ship.getVelocity());
					Global.getCombatEngine().spawnEmpArc(ship, point3,
							new SimpleEntity(point3),
							new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
							DamageType.ENERGY, 0f, 0f,
							0f, null,
							ship.getCollisionRadius() / 70f * 4f,
							CORE_COLOR, FRINGE_COLOR);
					frame7 = 0;
				}


			}

			if (level > 0.45f) {
				stats.getFluxDissipation().modifyMult(id,0.75f);
				stats.getTimeMult().modifyMult(id, shipTimeMult);
				ship.setPhased(true);
				if (timer.intervalElapsed()) {
					momentum+= 1;
					ship.setTimeDeployed(ship.getTimeDeployedForCRReduction() - 0.2f);
				}

				if (momentum >= 100) {
					if (frame7 >= 40) {

						renderStormCloud(point3, angle, width, height,
								CloudfringeColor, CloudcoreColor, CloudfringeColor, 2, duration * 2);
						// Global.getCombatEngine().spawnEmpArcVisual(MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 0.65f, ship.getCollisionRadius() * 1.25f)), ship, MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2.5f, ship.getCollisionRadius() * 5f)), ship, ship.getCollisionRadius() / 20f * 7f - 5f, new Color(199, 183, 113, 70), new Color(199, 183, 113, 55));
						Global.getCombatEngine().spawnEmpArc(ship, point3,
								new SimpleEntity(point3),
								new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
								DamageType.ENERGY, 0f, 0f,
								0f, null,
								ship.getCollisionRadius() / 140 * 7f,
								CORE_COLOR, FRINGE_COLOR);

						Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1.5f, 0.6f, ship.getLocation(), ship.getVelocity());
						Global.getCombatEngine().spawnEmpArc(ship, point3,
								new SimpleEntity(point3),
								new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
								DamageType.ENERGY, 0f, 0f,
								0f, null,
								ship.getCollisionRadius() / 140f * 4f,
								CORE_COLOR, FRINGE_COLOR);
						frame7 = 0;
					}

				}
				if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
					for (ShipAPI enemies : AIUtils.getEnemiesOnMap(ship)) {
						if (!enemies.isFighter() && !enemies.isFrigate() && !enemies.isDestroyer()) {
							if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() / 1.75f) {
								ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.75f);
							}
						}
					}
				}

			}
			levelForAlpha = level;
		} else if (state == State.OUT) {
			if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
				for (ShipAPI enemies : AIUtils.getEnemiesOnMap(ship)) {
					if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() + 700) {
						enemies.getMutableStats().getEmpDamageTakenMult().modifyMult(id, 6f);
					}
				}
			}
			stats.getMaxSpeed().unmodify(id);
			if (ship == Global.getCombatEngine().getPlayerShip()) {
				Global.getCombatEngine().getTimeMult().unmodify();
			}

				if (level > 0.98f) {
					dissipate = true;
					if (dissipate) {
						ship.getFluxTracker().setHardFlux(ship.getFluxTracker().getHardFlux() - (ship.getFluxTracker().getHardFlux() * 0.2f));
						ship.getFluxTracker().setCurrFlux(ship.getFluxTracker().getCurrFlux() - (ship.getFluxTracker().getCurrFlux() * 0.2f));
					}
					if (momentum >= 100) {
						if (!impact) {
							impact = true;
							momentum = 0;
							MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura4.png"),
									ship,
									new Vector2f(0f, 0f),
									new Vector2f(0f, 0f),
									new Vector2f(512, 512),
									new Vector2f(12900, 12900),
									180f,
									9999f,
									true,
									new Color(154, 255, 158, 35),
									true,
									0.1f,
									0.1f,
									0.04f,
									true);

							Global.getSoundPlayer().playSound("impact_tempestas", 0.75f, 2f, ship.getLocation(), ship.getVelocity());

							if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
								for (ShipAPI enemies : AIUtils.getEnemiesOnMap(ship)) {
									if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() + 700) {
										enemies.getFluxTracker().beginOverloadWithTotalBaseDuration(0.5f);
										enemies.getEngineController().forceFlameout(true);
										Global.getSoundPlayer().playSound("shield_burnout", 1f, 0.4f, enemies.getLocation(), new Vector2f(0f, 0f));
										enemies.setOverloadColor(JITTER);
										enemies.getFluxTracker().showOverloadFloatyIfNeeded("System Disruption!", TEXT, 4f, true);
										enemies.getMutableStats().getEmpDamageTakenMult().modifyMult(id, 25f);
									}
											if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() + 1200 && MathUtils.getDistance(ship, enemies) >= ship.getCollisionRadius() + 700) {
												if (enemies.getFluxTracker().showFloaty() ||
														ship == Global.getCombatEngine().getPlayerShip() ||
														enemies == Global.getCombatEngine().getPlayerShip()) {
													enemies.getFluxTracker().showOverloadFloatyIfNeeded("Drive Interdicted!", TEXT, 4f, true);
												}


												ShipEngineControllerAPI ec = enemies.getEngineController();
												float limit = ec.getFlameoutFraction();

												float disabledSoFar = 0f;
												boolean disabledAnEngine = false;
												List<ShipEngineAPI> engines = new ArrayList<ShipEngineAPI>(ec.getShipEngines());
												Collections.shuffle(engines);

												for (ShipEngineAPI engine : engines) {
													if (engine.isDisabled()) continue;
													float contrib = engine.getContribution();
													if (disabledSoFar + contrib <= limit) {
														engine.disable();
														disabledSoFar += contrib;
														disabledAnEngine = true;
													}
												}
												if (!disabledAnEngine) {
													for (ShipEngineAPI engine : engines) {
														if (engine.isDisabled()) continue;
														engine.disable();
														break;
													}
												}
												ec.computeEffectiveStats(ship == Global.getCombatEngine().getPlayerShip());
												enemies.getMutableStats().getEmpDamageTakenMult().modifyMult(id, 12.5f);

											}
										}
									}



										if (!CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f).isEmpty()) {
											for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f)) {
												if (MathUtils.getDistance(ship, projectiles) <= 1200) {
													if (projectiles.getOwner() != ship.getOwner()) {
														Global.getCombatEngine().removeEntity(projectiles);
														Global.getCombatEngine().spawnExplosion(projectiles.getLocation(), new Vector2f(), CORE_COLOR, 75f, 0.45f);
														Global.getCombatEngine().addSmoothParticle(projectiles.getLocation(), new Vector2f(), 40f, 0.4f, 0.3f, FRINGE_COLOR);
														Global.getSoundPlayer().playSound("mine_explosion", 1f, 0.5f, projectiles.getLocation(), new Vector2f());

													}
												}
											}
										}
										if (!AIUtils.getNearbyEnemyMissiles(ship, 9999f).isEmpty()) {
											for (MissileAPI missiles : AIUtils.getNearbyEnemyMissiles(ship, 9999f)) {
												if (MathUtils.getDistance(ship, missiles) <= 1200) {
													Global.getCombatEngine().removeEntity(missiles);
													Global.getCombatEngine().spawnExplosion(missiles.getLocation(), new Vector2f(), CORE_COLOR, 75f, 0.45f);
													Global.getCombatEngine().addSmoothParticle(missiles.getLocation(), new Vector2f(), 40f, 0.4f, 0.3f, FRINGE_COLOR);
													Global.getSoundPlayer().playSound("mine_explosion", 1f, 0.5f, missiles.getLocation(), new Vector2f());
												}
								}
							}
						}
					}
				}
			if (momentum >= 25 && momentum < 100) {
				if (!impact) {
					impact = true;
					momentum -= 25;

					MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura4.png"),
							ship,
							new Vector2f(0f, 0f),
							new Vector2f(0f, 0f),
							new Vector2f(512, 512),
							new Vector2f(6450, 6450),
							180f,
							9999f,
							true,
							new Color(154, 255, 158, 18),
							true,
							0.06f,
							0.06f,
							0.02f,
							true);

					if (!AIUtils.getEnemiesOnMap(ship).isEmpty()) {
						for (ShipAPI enemies : AIUtils.getEnemiesOnMap(ship)) {
							if (MathUtils.getDistance(ship, enemies) <= ship.getCollisionRadius() + 700) {
									if (enemies.getFluxTracker().showFloaty() ||
											ship == Global.getCombatEngine().getPlayerShip() ||
											enemies == Global.getCombatEngine().getPlayerShip()) {
										enemies.getFluxTracker().showOverloadFloatyIfNeeded("Drive Interdicted!", TEXT, 4f, true);
									}


								ShipEngineControllerAPI ec = enemies.getEngineController();
								float limit = ec.getFlameoutFraction();

								float disabledSoFar = 0f;
								boolean disabledAnEngine = false;
								List<ShipEngineAPI> engines = new ArrayList<ShipEngineAPI>(ec.getShipEngines());
								Collections.shuffle(engines);

								for (ShipEngineAPI engine : engines) {
									if (engine.isDisabled()) continue;
									float contrib = engine.getContribution();
									if (disabledSoFar + contrib <= limit) {
										engine.disable();
										disabledSoFar += contrib;
										disabledAnEngine = true;
									}
								}
								if (!disabledAnEngine) {
									for (ShipEngineAPI engine : engines) {
										if (engine.isDisabled()) continue;
										engine.disable();
										break;
									}
								}
								ec.computeEffectiveStats(ship == Global.getCombatEngine().getPlayerShip());
								enemies.getMutableStats().getEmpDamageTakenMult().modifyMult(id, 12.5f);

							}
						}
					}

					Global.getSoundPlayer().playSound("impact_tempestas", 1.75f, 1f, ship.getLocation(), ship.getVelocity());

					if (!CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f).isEmpty()) {
						for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f)) {
							if (MathUtils.getDistance(ship, projectiles) <= 700) {
								if (projectiles.getOwner() != ship.getOwner()) {
									Global.getCombatEngine().removeEntity(projectiles);
									Global.getCombatEngine().spawnExplosion(projectiles.getLocation(), new Vector2f(), CORE_COLOR, 75f, 0.45f);
									Global.getCombatEngine().addSmoothParticle(projectiles.getLocation(), new Vector2f(), 40f, 0.4f, 0.3f, FRINGE_COLOR);
									Global.getSoundPlayer().playSound("mine_explosion", 1f, 0.5f, projectiles.getLocation(), new Vector2f());

								}
							}
						}
					}
					if (!AIUtils.getNearbyEnemyMissiles(ship, 9999f).isEmpty()) {
						for (MissileAPI missiles : AIUtils.getNearbyEnemyMissiles(ship, 9999f)) {
							if (MathUtils.getDistance(ship, missiles) <= 700) {
								Global.getCombatEngine().removeEntity(missiles);
								Global.getCombatEngine().spawnExplosion(missiles.getLocation(), new Vector2f(), CORE_COLOR, 75f, 0.45f);
								Global.getCombatEngine().addSmoothParticle(missiles.getLocation(), new Vector2f(), 40f, 0.4f, 0.3f, FRINGE_COLOR);
								Global.getSoundPlayer().playSound("mine_explosion", 1f, 0.5f, missiles.getLocation(), new Vector2f());
							}
						}
					}
				}
			}

			if (level > 0.90f) {
				ship.setPhased(true);
				Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius()),
						new Vector2f(0f, 0f),
						ship.getCollisionRadius(),
						4f,
						-0.2f,
						1f,
						2f,
						SMOKE);
				if (frame7 >= 10)  {

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
					Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1f, 0.4f, ship.getLocation(), ship.getVelocity());
					Global.getCombatEngine().spawnEmpArc(ship, point3,
							new SimpleEntity(point3),
							new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
							DamageType.ENERGY, 0f, 0f,
							0f, null,
							ship.getCollisionRadius() / 70f * 4f,
							CORE_COLOR, FRINGE_COLOR);
					frame7 = 0;
				}
			} else if (level > 0.10f) {
				Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius()),
						new Vector2f(0f, 0f),
						ship.getCollisionRadius(),
						4f,
						-0.2f,
						1f,
						2f,
						SMOKE);
				if (frame7 >= 10)  {

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
					Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1f, 0.4f, ship.getLocation(), ship.getVelocity());
					Global.getCombatEngine().spawnEmpArc(ship, point3,
							new SimpleEntity(point3),
							new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
							DamageType.ENERGY, 0f, 0f,
							0f, null,
							ship.getCollisionRadius() / 70f * 4f,
							CORE_COLOR, FRINGE_COLOR);
					frame7 = 0;
				}
				ship.setPhased(false);
			}
			levelForAlpha = level;

			/*

			if (level > 0.25f) {
				stats.getEnergyWeaponFluxCostMod().modifyMult(id,1f);
				stats.getBeamWeaponFluxCostMult().modifyMult(id,1f);
				stats.getBallisticWeaponFluxCostMod().modifyMult(id,1f);
				stats.getMissileWeaponFluxCostMod().modifyMult(id,1f);

			}

			 */

		}

		if (level > 0.5f) {
			MagicRender.battlespace(
					Global.getSettings().getSprite("graphics/ships/Stella_tempestas7s_glow.png"), //sprite
					ship.getShieldCenterEvenIfNoShield(), //location vector2f
					new Vector2f(0f, 0f), //velocity vector2f
					new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
					new Vector2f(0f, 0f), //growth, vector2f, pixels/second
					ship.getFacing() - 90f, //angle, float
					0f, //spin, float
					CLOAK_HULL, //color Color
					true, //additive, boolean

					0f, //jitter range
					1f, //jitter tilt
					1f, // flicker range
					0f, //flicker median
					0.05f, //max delay

					0.07f * Global.getCombatEngine().getTimeMult().getMult(), //fadein, float, seconds
					0f * Global.getCombatEngine().getTimeMult().getMult(), //full, float, seconds
					0.14f * Global.getCombatEngine().getTimeMult().getMult(), //fadeout, float, seconds

					CombatEngineLayers.ABOVE_SHIPS_LAYER);
		}
		ship.setJitter(ship, JITTER, 0.5f * (effectLevel * 2f), 7, 10f, 15f);

		ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * levelForAlpha);
		ship.setApplyExtraAlphaToEngines(true);


	}
	public void unapply(MutableShipStatsAPI stats, String id) {

		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		timer.advance(dur);
		framer.advance(dur);

        impact = false;
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
		stats.getCombatEngineRepairTimeMult().unmodify(id);

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