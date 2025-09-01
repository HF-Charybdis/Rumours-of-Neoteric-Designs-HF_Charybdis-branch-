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

public class Entropy_field7s_AI implements ShipSystemAIScript {

	private IntervalUtil Interval;
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
		Interval = new IntervalUtil(0f, 0.75f);

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

		int Threats = 0;
		for (ShipAPI Fighters : CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getCollisionRadius() * 4f + 300f)) {
			if (Fighters.isFighter() && !Fighters.isHulk() && Fighters.getOwner() != ship.getOwner()) {
				Threats++;
			}
		}

			if (Interval.intervalElapsed() && Threats > 0 && !system.isStateActive() && system.getCooldownRemaining() == 0f) {
				ship.useSystem();


			}
		}
}