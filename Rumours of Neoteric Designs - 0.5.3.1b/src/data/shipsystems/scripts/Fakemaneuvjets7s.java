package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class Fakemaneuvjets7s extends BaseShipSystemScript {

	private boolean activated = false;
	public static Color EXPLOSION = new Color(190, 247, 232, 150);
	public static Color LIGHTNING_FRINGE_COLOR = new Color(99, 255, 226, 175);

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (state == ShipSystemStatsScript.State.OUT) {
			//stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			//stats.getMaxTurnRate().unmodify(id);
		} else {
			/*
			stats.getMaxSpeed().modifyFlat(id, 50f);
			stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
			stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
			stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);

			 */
		}
		if (stats.getEntity() instanceof ShipAPI && false) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			String key = ship.getId() + "_" + id;
			Object test = Global.getCombatEngine().getCustomData().get(key);
		if (!activated) {
			activated = true;
			ship.setSprite("Mayawati", "sprite7s");
			ship.getSpriteAPI().setCenter(48, 53);
			ship.getSpriteAPI().setAlphaMult(ship.getSpriteAPI().getAlphaMult());
			ship.getSpriteAPI().setAngle(ship.getSpriteAPI().getAngle());
			ship.getSpriteAPI().setColor(ship.getSpriteAPI().getColor());

		}
		

			if (state == State.IN) {
				if (test == null && effectLevel > 0f) {
					Global.getCombatEngine().getCustomData().put(key, new Object());
					ship.getEngineController().getExtendLengthFraction().advance(1f);
					for (ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
						if (engine.isSystemActivated()) {
							ship.getEngineController().setFlameLevel(engine.getEngineSlot(), 1f);
						}
					}
				}
			} else {
				Global.getCombatEngine().getCustomData().remove(key);
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
}
