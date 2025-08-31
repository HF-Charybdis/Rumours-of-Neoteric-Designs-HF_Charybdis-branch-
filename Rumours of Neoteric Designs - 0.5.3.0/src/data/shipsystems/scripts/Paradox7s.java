package data.shipsystems.scripts;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.combat.entities.Ship;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import org.magiclib.util.MagicTargeting;

//import static java.lang.Float.NaN;

public class Paradox7s extends BaseShipSystemScript {
	public static final float MAX_TIME_MULT = 10f;
	public static final float MIN_TIME_MULT = 0.1f;
	public static final float DAM_MULT = 0.1f;

	public static final Color JITTER_COLOR = new Color(90, 255, 217,55);
	public static final Color JITTER_UNDER_COLOR = new Color(90, 255, 230,155);

	public static Color EXPLOSION = new Color(190, 247, 232, 150);
	public static Color LIGHTNING_FRINGE_COLOR = new Color(99, 255, 226, 175);


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

		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
		if (state == State.IN) {
			jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
			if (jitterLevel > 1) {
				jitterLevel = 1f;
			}
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		} else if (state == State.ACTIVE) {
			jitterLevel = 1f;
			jitterRangeBonus = maxRangeBonus;
		} else if (state == State.OUT) {
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		}
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;

		ship.setJitter(this, JITTER_COLOR, effectLevel, 2, 0, 0 + jitterRangeBonus);
		//ship.setJitterUnder(this, JITTER_UNDER_COLOR, effectLevel, 12, 0f, 12f + jitterRangeBonus);


		//float shipTimeMult = 1f + (1f - 1f) * effectLevel;
		//stats.getTimeMult().modifyMult(id, shipTimeMult);
		/*
		stats.getMaxSpeed().modifyMult(id,3f);
		stats.getAcceleration().modifyMult(ship.getId(), 6f);
		stats.getDeceleration().modifyMult(ship.getId(), 6f);
		stats.getMaxTurnRate().modifyMult(ship.getId(), 6f);
		stats.getTurnAcceleration().modifyMult(ship.getId(), 6f);
		 */
		stats.getFluxDissipation().modifyMult(id, 1f);
		stats.getEnergyRoFMult().modifyMult(id, 1f);
		stats.getBeamDamageTakenMult().modifyMult(id, 0.2f);
		//ship.addTag("ACTIVE7S");

		Color AURA = new Color(90, 255, 230, Math.round(5 * effectLevel));
		Color AURA2 = new Color(90, 255, 230, Math.round(20 * effectLevel));
		Color MISSILE_JITTER = new Color(80, 255, 218, 150);

		MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura2.png"),
				ship,
				new Vector2f(0f, 0f),
				new Vector2f(0f, 0f),
				new Vector2f(1300, 1300),
				new Vector2f(0, 0),
				0f,
				360,
				false,
				AURA,
				true,
				0.1f,
				0f,
				0.1f,
				true);

         /*
       if (ship.isAlive()) {
			MagicRender.singleframe(Global.getSettings().getSprite("graphics/fx/seven_aura2.png"), ship.getLocation(), new Vector2f(1300, 1300), ship.getFacing() - 90f, AURA, true, CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
		}

          */
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}


		if (!CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f).isEmpty()) {
			for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 9999f)) {
				if (MathUtils.getDistance(ship, projectiles) <= 550) {
					if (projectiles.getOwner() != ship.getOwner()) {

						MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura2.png"),
								projectiles,
								new Vector2f(0f, 0f),
								new Vector2f(0f, 0f),
								new Vector2f(60, 60),
								new Vector2f(0, 0),
								0f,
								360,
								false,
								AURA2,
								true,
								0.2f,
								0f,
								0.2f,
								true);


						//missiles.setJitter(missiles.getProjectileSpecId(), MISSILE_JITTER, effectLevel, 10, 0f, 5f);
						//projectiles.setFacing(projectiles.getFacing() + (2f * (float) Math.cos((Math.random() * MathUtils.FPI * 500f + Global.getCombatEngine().getElapsedInLastFrame()) * (MathUtils.FPI / 1000f))));
						projectiles.getVelocity().set(projectiles.getVelocity().getX() * 0.975f, projectiles.getVelocity().getY() * 0.975f);
						if (projectiles.getSpawnType().equals(ProjectileSpawnType.BALLISTIC_AS_BEAM) && MathUtils.getDistance(ship, projectiles) <= MathUtils.getRandomNumberInRange(-2000f, 400f)) {
							Global.getCombatEngine().removeEntity(projectiles);
							Global.getCombatEngine().spawnExplosion(projectiles.getLocation(), new Vector2f(), projectiles.getProjectileSpec().getGlowColor(), 10f + (projectiles.getCollisionRadius() / 2.5f), 0.25f);
							Global.getCombatEngine().addSmoothParticle(projectiles.getLocation(), new Vector2f(), 5f + (projectiles.getCollisionRadius() / 2.5f), 0.4f, 0.15f, projectiles.getProjectileSpec().getFringeColor());
							Global.getSoundPlayer().playSound("prox_charge_explosion", 1.6f, 0.3f + (projectiles.getCollisionRadius() / 120f), projectiles.getLocation(), projectiles.getVelocity());
						}

					}
				} else {
					projectiles.getVelocity().set(projectiles.getVelocity().getX(), projectiles.getVelocity().getY());
				}

			}
		}


		if (!AIUtils.getNearbyEnemyMissiles(ship, 9999f).isEmpty()) {
			for (MissileAPI missiles : AIUtils.getNearbyEnemyMissiles(ship, 9999f)) {
				if (MathUtils.getDistance(ship, missiles) <= 550f) {


					MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura2.png"),
							missiles,
							new Vector2f(0f, 0f),
							new Vector2f(0f, 0f),
							new Vector2f(60, 60),
							new Vector2f(0, 0),
							0f,
							360,
							false,
							AURA2,
							true,
							0.2f,
							0f,
							0.2f,
							true);


					//missiles.setJitter(missiles.getProjectileSpecId(), MISSILE_JITTER, effectLevel, 10, 0f, 5f);
					missiles.setFacing(MathUtils.getRandomNumberInRange(missiles.getFacing() + 4f, missiles.getFacing() - 4f));
					missiles.getVelocity().set(missiles.getVelocity().getX() / 1.05f, missiles.getVelocity().getY() / 1.05f);
					missiles.setAngularVelocity(0f);
					if (missiles.getDamage().getType().equals(DamageType.FRAGMENTATION) && missiles.getBaseDamageAmount() <= 201f) {
						missiles.getEngineStats().getTurnAcceleration().modifyMult(missiles.getProjectileSpecId(), 0.2f);
						missiles.getEngineStats().getAcceleration().modifyMult(missiles.getProjectileSpecId(), 0.2f);
					}
					if (missiles.getBaseDamageAmount() <= 101f) {
						missiles.getEngineStats().getTurnAcceleration().modifyMult(missiles.getProjectileSpecId(), 0.2f);
						missiles.getEngineStats().getAcceleration().modifyMult(missiles.getProjectileSpecId(), 0.2f);
					}
					if (missiles.isMine()) {
						Global.getCombatEngine().removeEntity(missiles);
						Global.getCombatEngine().spawnExplosion(missiles.getLocation(), missiles.getVelocity(), EXPLOSION, 75f, 0.45f);
						Global.getCombatEngine().addSmoothParticle(missiles.getLocation(), missiles.getVelocity(), 40f, 0.4f, 0.3f, LIGHTNING_FRINGE_COLOR);
						Global.getSoundPlayer().playSound("mine_explosion", 1f, 1f, missiles.getLocation(), missiles.getVelocity());
					}
				} else {
					missiles.getVelocity().set(missiles.getVelocity().getX() / 1f, missiles.getVelocity().getY() / 1f);
				}

			}
		}
	}

        /*
		if (!CombatUtils.getProjectilesWithinRange(ship.getLocation(), 600f).isEmpty()) {
			for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 600f)) {
				if (MathUtils.getDistance(ship, projectiles) <= 600f) {
					if (projectiles.getOwner() != ship.getOwner()) {

					}

				}
			}
		}

	}
         */



	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		Global.getCombatEngine().getTimeMult().unmodify(id);
		/*
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		 */
		stats.getTimeMult().unmodify(id);
		stats.getFluxDissipation().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getBeamDamageTakenMult().unmodifyMult(id);

//		stats.getHullDamageTakenMult().unmodify(id);
//		stats.getArmorDamageTakenMult().unmodify(id);
//		stats.getEmpDamageTakenMult().unmodify(id);
	}

	public StatusData getStatusData(int index, State state, float effectLevel) {
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		if (index == 0) {
			return new StatusData("time flow altered", false);
		}
//		if (index == ) {
//			return new StatusData("increased speed", false);
//		}
//		if (index == 1) {
//			return new StatusData("increased acceleration", false);
//		}
		return null;

        }
/*
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return ((ship.getFluxLevel() >= 0.15f));
		//return super.isUsable(system, ship);
	}

 */

	/*@Override
	public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
		if (system.isOutOfAmmo()) {
			return null;
		}
		if (system.getState() != ShipSystemAPI.SystemState.IDLE) {
			return null;
		}
		    {
				return "flux level: " + Math.round(ship.getFluxLevel() * 100f) + "%";
			}
		}
		*/

}