package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;
import data.shipsystems.scripts.Tempestas_cloak7s;

import java.awt.*;
import java.util.HashMap;

public class Tempestas_cloak7s_AI implements ShipSystemAIScript {

	private IntervalUtil Interval;
	private IntervalUtil FighterTrigger;
	private IntervalUtil CloseEnemyTrigger;
	private IntervalUtil DangerTrigger;
	int momentum = 0;
	private IntervalUtil timer;
	private float averageRangeSystem = 0;
	private float averageRangeNormal;
	private FluxTrackerAPI fluxTracker;
	private ShipSystemAPI system;
	private ShipAPI ship;
	boolean impact = false;
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
		timer = new IntervalUtil(15f, 15f);

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

	// after 3 weeks of working,there goes, a working AI for this thing

	@Override
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		Interval.advance(amount);
		FighterTrigger.advance(amount);
		CloseEnemyTrigger.advance(amount);
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();
		timer.advance(dur);

		if (Global.getCombatEngine() == null || Global.getCombatEngine().isPaused()) {
			return;
		}

		//All of those variables detects nearby stuff, the ship ones are most used

		int imeddiate_evade = 0;
		for (DamagingProjectileAPI projectiles : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 600f)) {
			if (projectiles.getDamageAmount() >= 750f && projectiles.getOwner() != ship.getOwner() && !ship.getVariant().hasHullMod("cheat_tempestas7s")) {
				imeddiate_evade++;
			}

		}

		for (MissileAPI missiles : CombatUtils.getMissilesWithinRange(ship.getLocation(), 500f)) {
			if (missiles.getDamageAmount() >= 750f && missiles.getOwner() != ship.getOwner()) {
				imeddiate_evade++;
			}
		}
		int Threats_too_close = 0;
		for (ShipAPI Close_enemies : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius() * 2f)) {
			if (!Close_enemies.isHulk() && Close_enemies.getOwner() != ship.getOwner()) {
				Threats_too_close++;
			}
		}

		int Threats = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 9999f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				Threats++;
			}
		}

		int Threats_at_range = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 2500f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner() && ship.getVariant().hasHullMod("unstable_injector")) {
				Threats_at_range++;
			}
		}
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 2020f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner() && !ship.getVariant().hasHullMod("unstable_injector")) {
				Threats_at_range++;
			}
		}

		int In_Range = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 700f)) {
			if (!Enemies_in_map.isFighter() && !Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range++;
			}
		}

		int In_Range_momentum = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 800f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range_momentum++;
			}
		}

		int In_Range_rush = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 900f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range_rush++;
			}
		}

		int In_Range_vent = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 1500f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range_rush++;
			}
		}

		int Vent = 0;
		for (ShipAPI Enemies_in_map : CombatUtils.getShipsWithinRange(ship.getLocation(), 1500f)) {
			if (!Enemies_in_map.isHulk() && Enemies_in_map.getOwner() != ship.getOwner()) {
				In_Range_rush++;
			}
		}

		//here starts the mess, a lot of stuff for preventing/encouraging system usage

		if (system.getEffectLevel() == 1f) {
			if (timer.intervalElapsed() && In_Range_momentum > 0) {
				momentum += 1;
			}
		}

		//This UI was just a measure to see if this is working or not
		//MagicUI.drawHUDStatusBar(ship,momentum * 0.01f, Color.white,Color.white,momentum * 0.01f,"this", "these", true);
        if (imeddiate_evade == 0) {
			if (Interval.intervalElapsed() && Threats_too_close > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f) {
				ship.useSystem();
			}
		}
		if (ship.getHullLevel() > 0.2f) {
			if (Interval.intervalElapsed() && Threats > 0 && In_Range == 0 && Threats_at_range > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f && ship.getSystem().getAmmo() == ship.getSystem().getMaxAmmo() && ship.getHardFluxLevel() < 0.95f) {
				ship.useSystem();
			}
		}


		if (ship.getEngineController().isAccelerating() && Threats == 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f) {
			ship.useSystem();
		}


		if (system.getState() == ShipSystemAPI.SystemState.ACTIVE && system.getEffectLevel() == 1f && In_Range == 0) {
			ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS);
			ship.giveCommand(ShipCommand.ACCELERATE, null, 0);

		}

		if (system.getState() == ShipSystemAPI.SystemState.OUT && system.getEffectLevel() > 0f && In_Range_rush > 0 && ship.getHardFluxLevel() < 0.25f) {
			ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS);
		}
		if (system.getState() == ShipSystemAPI.SystemState.IN && system.getEffectLevel() < 0.8f && In_Range_vent > 0 && ship.getHardFluxLevel() < 0.25f) {
			ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS);
			ship.giveCommand(ShipCommand.ACCELERATE, null, 0);
		}
		if (target != null) {
			if (system.getEffectLevel() == 1f && target.getFluxTracker().isOverloaded() && In_Range > 0) {
				ship.useSystem();
			}
			if (system.getEffectLevel() == 1f && target.getFluxTracker().isVenting() && In_Range > 0) {
				ship.useSystem();
			}
			if (system.getEffectLevel() == 1f && target.getHardFluxLevel() > 0.15f && In_Range > 0 && target.getHullLevel() > 0.15f) {
				ship.useSystem();
			}
		}


		if (system.getEffectLevel() == 1f && In_Range > 0 && ship.getHardFluxLevel() > 0.18f || momentum > 35f) {
			ship.useSystem();
			momentum = 0;
		}
		if (system.getEffectLevel() == 1f && ship.getHardFluxLevel() > 0.25f) {
			ship.useSystem();
		}
		if (ship.getVariant().hasHullMod("fluxbreakers")) {
			if (system.getCooldownRemaining() <= 0.25f && ship.getHardFluxLevel() < 0.425f && ship.getFluxLevel() < 0.6f && system.getState() == ShipSystemAPI.SystemState.COOLDOWN && ship.getHullLevel() > 0.5f && imeddiate_evade < 2 && ship.getHardFluxLevel() > 0.125f) {
				ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
			} else if (system.getCooldownRemaining() <= 0.15f && ship.getHardFluxLevel() < 0.3f && ship.getFluxLevel() < 0.475f && system.getState() == ShipSystemAPI.SystemState.COOLDOWN && ship.getHullLevel() > 0.25f && imeddiate_evade < 1 && ship.getHardFluxLevel() > 0.125f) {
				ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);

			}
		} else {
			if (system.getCooldownRemaining() <= 0.25f && ship.getHardFluxLevel() < 0.35f && ship.getFluxLevel() < 0.5f && system.getState() == ShipSystemAPI.SystemState.COOLDOWN && ship.getHullLevel() > 0.5f && imeddiate_evade < 2 && ship.getHardFluxLevel() > 0.1f) {
				ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
			} else if (system.getCooldownRemaining() <= 0.15f && ship.getHardFluxLevel() < 0.2f && ship.getFluxLevel() < 0.35f && system.getState() == ShipSystemAPI.SystemState.COOLDOWN && ship.getHullLevel() > 0.25f && imeddiate_evade < 1 && ship.getHardFluxLevel() > 0.125f) {
				ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
			}
		}
	}

}