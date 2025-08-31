
package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;


public class SoulcrierOnFire7s implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {

public static Color LIGHTNING_CORE_COLOR = new Color(190, 247, 232, 200);
public static Color EXPLOSION = new Color(190, 247, 232, 150);
public static Color LIGHTNING_FRINGE_COLOR = new Color(99, 255, 226, 175);
public static Color JITTER = new Color(152, 255, 236, 255);

	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		CombatEntityAPI target = projectile.getSource();
		Vector2f point = projectile.getLocation();
		ShipAPI ship = weapon.getShip();
		engine.spawnExplosion(projectile.getLocation(), ship.getVelocity(), EXPLOSION, 100f, 0.65f);
		engine.addSmoothParticle(projectile.getLocation(), ship.getVelocity(), 70f, 0.4f, 0.5f, LIGHTNING_FRINGE_COLOR);

		if (!ship.getVariant().getHullMods().contains("orbtraits7s") || !ship.getVariant().getHullMods().contains("orbtraits7s_2")) {

			MagicRender.objectspace(Global.getSettings().getSprite("graphics/ships/Mayawati7s.png"),
					ship,
					new Vector2f(0f, 0f),
					new Vector2f(0f, 0f),
					new Vector2f(106, 96),
					new Vector2f(318, 288),
					180f,
					0f,
					true,
					JITTER,
					true,
					0.05f,
					0.05f,
					0.0375f,
					true);
		}

		for (int i = 0; i < 2; i++) {
			engine.spawnEmpArcVisual(weapon.getFirePoint(0), projectile.getSource(), MathUtils.getRandomPointInCircle(point, MathUtils.getRandomNumberInRange(15f, 30f)), projectile.getSource(), 3f, LIGHTNING_CORE_COLOR, LIGHTNING_FRINGE_COLOR);
		}
	}






	@Override
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

	}
}
  
