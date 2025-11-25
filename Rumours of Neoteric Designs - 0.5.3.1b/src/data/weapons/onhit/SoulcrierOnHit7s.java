package data.weapons.onhit;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class SoulcrierOnHit7s implements OnHitEffectPlugin {

	public static final Color EMP_FRINGE = new Color(89, 255, 117, 155);
	public static Color EMP_CORE = new Color(152, 255, 236, 255);


	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		ShipAPI ship = projectile.getSource();

		if ((float) Math.random() > 0.2f && !shieldHit && target instanceof ShipAPI) {

			float emp = projectile.getDamageAmount() * 1.25f;
			float dam = 0f;

			if (ship.getHardFluxLevel() >= 0.75f && ship.getVariant().hasHullMod("breakpoint7s")) {

				Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1f, 2f, point, new Vector2f(0f, 0f));
				engine.spawnEmpArc(projectile.getSource(), point, target, target,
						DamageType.ENERGY,
						dam,
						emp, // emp
						100000f, // max range
						"",
						40f, // thickness
						EMP_FRINGE,
						EMP_CORE
				);

				//engine.spawnProjectile(null, null, "plasma", point, 0, new Vector2f(0, 0));

			}
		}
	}
}
