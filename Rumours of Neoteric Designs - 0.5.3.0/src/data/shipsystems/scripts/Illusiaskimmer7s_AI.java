package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.List;

public class Illusiaskimmer7s_AI implements ShipSystemAIScript {

	private IntervalUtil Interval;
	private IntervalUtil FighterTrigger;
	private IntervalUtil CloseEnemyTrigger;
	private IntervalUtil DangerTrigger;
	private IntervalUtil fireInterval = new IntervalUtil(0.25f, 1.75f);
	private float averageRangeSystem = 0;
	private float averageRangeNormal;
	private FluxTrackerAPI fluxTracker;
	private ShipSystemAPI system;
	private ShipAPI ship;
	//private CombatEngineAPI engine;

	// used for threat weighting
	private HashMap<ShipAPI.HullSize, Float> mults = new HashMap<>();

	@Override
	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		// load things from settings
		Interval = new IntervalUtil(0f, 0.35f);
		FighterTrigger = new IntervalUtil(1f, 4f);
		CloseEnemyTrigger = new IntervalUtil(0.75f, 2f);
		DangerTrigger = new IntervalUtil(0f, 0.05f);

		// initialize variables
		this.ship = ship;
		fluxTracker = ship.getFluxTracker();
		this.system = system;

		mults.put(ShipAPI.HullSize.CAPITAL_SHIP, 1.5f);
		mults.put(ShipAPI.HullSize.CRUISER, 1.25f);
		mults.put(ShipAPI.HullSize.DESTROYER, 1f);
		mults.put(ShipAPI.HullSize.FRIGATE, 0.75f);
		mults.put(ShipAPI.HullSize.FIGHTER, 0f); // don't turn on the system to shoot fighters
	}

	@Override
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		Interval.advance(amount);
		FighterTrigger.advance(amount);
		CloseEnemyTrigger.advance(amount);

		int Threats = 0;
		for (ShipAPI Fighters : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius() * 3f)) {
			if (Fighters.isFighter() && !Fighters.isHulk() && Fighters.getOwner() != ship.getOwner()) {
				Threats++;
			}
		}
		if (FighterTrigger.intervalElapsed() && Threats > 1 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getSystem().getAmmo() != 0) {
			ship.useSystem();
		}   // USES AGAINST FIGHTERS

		int Threats2 = 0;
		for (ShipAPI Close_enemies : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius() * 2f)) {
			if (!Close_enemies.isFighter() && !Close_enemies.isHulk() && Close_enemies.getOwner() != ship.getOwner()) {
				Threats2++;
			}
		}
		if (CloseEnemyTrigger.intervalElapsed() && Threats2 > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getSystem().getAmmo() > 1) {
			ship.useSystem();
		}   // USES AGAINST CLOSE ENEMIES

		int Threats3 = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 9999f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				Threats3++;
			}
		}
		int In_Range = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 1500f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range++;
			}
		}
		if (Interval.intervalElapsed() && Threats3 > 0 && In_Range == 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getSystem().getAmmo() == ship.getSystem().getMaxAmmo()) {
			ship.useSystem();
		}   // USES FOR MOBILITY

		int Projectiles = 0;
		for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 300f)) {
			if (projectiles.getDamageAmount() > 100f || projectiles.getDamageType().equals(DamageType.HIGH_EXPLOSIVE) || projectiles.getOwner() != ship.getOwner())  {
				Projectiles++;
			}
			if (Interval.intervalElapsed() && Projectiles > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getSystem().getAmmo() > 1 && !ship.isPhased()) {
				ship.useSystem();
			}

			if (Interval.intervalElapsed() && Projectiles > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getHullLevel() < 0.5f && !ship.isPhased()) {
				ship.useSystem();
			} // USES FOR AVOIDING PROJECTILES

		}
		int danger_projectiles = 0;
		for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 300f)) {
			if (projectiles.getDamageAmount() > 250f || projectiles.getDamageType().equals(DamageType.HIGH_EXPLOSIVE) && projectiles.getOwner() != ship.getOwner()) {
				danger_projectiles++;
			}
		}
			if (Interval.intervalElapsed() && danger_projectiles > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getHullLevel() < 0.5f && !ship.isPhased()) {
				ship.useSystem();
			}

		int imeddiate_evade = 0;
		for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 300f)) {
			if (projectiles.getDamageAmount() >= 750f && projectiles.getOwner() != ship.getOwner()) {
				imeddiate_evade++;
			}

		}
		if (DangerTrigger.intervalElapsed() && imeddiate_evade > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f) {
			ship.useSystem();
		}

		int danger_missiles = 0;
		for (MissileAPI missiles : AIUtils.getNearbyEnemyMissiles(ship, 300f)) {
			if (missiles.getDamageAmount() > 500f || missiles.getDamageType().equals(DamageType.HIGH_EXPLOSIVE) && missiles.getOwner() != ship.getOwner()) {
				danger_missiles++;
			}
		}
		if (Interval.intervalElapsed() && danger_missiles > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getHullLevel() < 0.5f && !ship.isPhased()) {
			ship.useSystem();
			// USES FOR AVOIDING MISSILES
		}


	}

}