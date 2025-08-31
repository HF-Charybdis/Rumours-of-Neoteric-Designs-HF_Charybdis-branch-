package data.weapons.onhit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import java.awt.Color;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.MoteControlScript;
import com.fs.starfarer.api.util.Misc;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.magiclib.util.MagicRender;

public class PlasmaAcceleratorOnHit7s implements OnHitEffectPlugin {

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if (target instanceof CombatEntityAPI) {

			MagicRender.battlespace(Global.getSettings().getSprite("graphics/fx/particlealpha_textured.png"), point, new Vector2f(),
					new Vector2f(150f, 150f),
					new Vector2f(450f, 450f),
					projectile.getFacing() - 90f,
					0f,
					new Color(154, 243, 255, 255),
					true,
					0.12f,
					0f,
					0.24f);

			MagicRender.battlespace(Global.getSettings().getSprite("graphics/starscape/blackhole.png"), point, new Vector2f(),
					new Vector2f(150f, 150f),
					new Vector2f(450f, 450f),
					MathUtils.getRandomNumberInRange(0,360),
					0f,
					new Color(154, 243, 255, 255),
					true,
					0.12f,
					0f,
					0.24f);

		}
	}
}


















