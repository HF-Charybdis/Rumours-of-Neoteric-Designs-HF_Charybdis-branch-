package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class Hypercharger7s extends BaseShipSystemScript {

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (state == ShipSystemStatsScript.State.OUT) {
				stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			} else {
				stats.getMaxSpeed().modifyFlat(id, 250f * effectLevel);
				stats.getAcceleration().modifyFlat(id, 200f * effectLevel);
				for (ShipEngineControllerAPI.ShipEngineAPI w : ship.getEngineController().getShipEngines()) {
					Color start = new Color(117, 140, 157, 20);
					Color end = new Color(24, 99, 140, 10);
					float time = Math.round(ship.getMutableStats().getTimeMult().getMult());
					Vector2f zero = new Vector2f();
					Global.getCombatEngine().addNebulaParticle(new Vector2f(w.getLocation().getX(), w.getLocation().getY()), new Vector2f(ship.getVelocity().x * -1f, ship.getVelocity().y * -1f), w.getEngineSlot().getWidth() * 10f * effectLevel, 0.25f, 0, 0.5f * effectLevel, 0.75f + w.getEngineSlot().getLength() * 0.002f / time * effectLevel, start);
					Global.getCombatEngine().addNebulaParticle(new Vector2f(w.getLocation().getX(), w.getLocation().getY()), new Vector2f(ship.getVelocity().x * -1f, ship.getVelocity().y * -1f), w.getEngineSlot().getWidth() * 10f * effectLevel, -0.25f, 0, 0.5f * effectLevel, 0.75f + w.getEngineSlot().getLength() * 0.02f / time * effectLevel, end);
				}

			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
	}

	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("greatly increased engine power", false);
		}
		return null;
	}

}
