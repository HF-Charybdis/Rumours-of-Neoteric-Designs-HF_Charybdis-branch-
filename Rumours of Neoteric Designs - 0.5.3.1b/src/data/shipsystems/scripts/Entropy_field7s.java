package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicRender;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import static com.fs.starfarer.api.combat.DamageType.FRAGMENTATION;
import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;

public class Entropy_field7s extends BaseShipSystemScript {

	private ShipAPI ship;
	private ShipSystemAPI system;
	protected Object STATUSKEY1 = new Object();
	private final IntervalUtil timer = new IntervalUtil(3f, 4f);
	private List<ShipAPI> buffed = new ArrayList<>();
	public final float BUFF_RANGE = 0.25f;
	public static Color SMOKE = new Color(141, 149, 153, 11);
	public static Color SMOKE_2 = new Color(65, 68, 70, 155);
	public final ArrayList<Color> COLOR1 = new ArrayList<>();
	public final ArrayList<Color> COLOR2 = new ArrayList<>();

	int frame7 = 0;
	public static final Color CORE_COLOR = new Color(167, 255, 192, 90);
	public static final Color FRINGE_COLOR = new Color(155, 255, 195, 155);

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

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		ShipAPI ship = (ShipAPI) stats.getEntity();

		if (!ship.isAlive()) {
			return;

		}





		frame7 += 1;
		if (frame7 == 70) {

			float ringWidth = 1.5f;
			float damage = 100f;
			float empDamage = 100f;
			float empThickness = 5f;
			float width = ship.getCollisionRadius() / 3f;
			float height = ship.getCollisionRadius() * 2f;
			float duration = 1f;

			Color CloudcoreColor = new Color(141, 149, 153, 10);
			Color CloudfringeColor = new Color(141, 149, 153, 10);

			Vector2f point3 = MathUtils.getRandomPointInCircle(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2f, ship.getCollisionRadius() * 2f + 300f));
			Vector2f point2 = MathUtils.getRandomPointInCircle(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2f, ship.getCollisionRadius() * 4f));

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

			frame7 = 0;
		}

		Global.getCombatEngine().addNebulaParticle(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 5f + 200f),
				new Vector2f(0f, 0f),
				ship.getCollisionRadius() / 0.5f + 90f,
				4f,
				-0.2f,
				0.5f,
				2.5f,
				SMOKE);

		ShipAPI player = Global.getCombatEngine().getPlayerShip();

		if (ship.getSystem().isChargeup() && effectLevel <= 0.9f) {

			Global.getSoundPlayer().playSound("mote_attractor_impact_normal", 1f, 0.5f, ship.getLocation(), ship.getVelocity());
			Vector2f point3 = MathUtils.getRandomPointInCircle(ship.getLocation(), MathUtils.getRandomNumberInRange(ship.getCollisionRadius() * 2f, ship.getCollisionRadius() * 2f + 300f));

			Global.getCombatEngine().spawnEmpArc(ship, point3,
					new SimpleEntity(point3),
					new SimpleEntity(MathUtils.getRandomPointInCircle(point3, ship.getCollisionRadius())),
					DamageType.ENERGY, 0f, 0f,
					0f, null,
					ship.getCollisionRadius() / 70f * 7f,
					CORE_COLOR, FRINGE_COLOR);


		}


		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();

		} else {
			return;
		}

		boolean visible = MagicRender.screenCheck(0.1f, ship.getLocation());


		// range
		java.util.List<ShipAPI> nearby = AIUtils.getNearbyEnemies(ship, ship.getCollisionRadius() * 5f + 200f);
		List<ShipAPI> previous = new ArrayList<>(buffed);

		if (Global.getCombatEngine().isPaused()) {
			return;
		}

		if (!nearby.isEmpty()) {
			for (ShipAPI affected : nearby) {
				//new affected ship
				if (!previous.contains(affected)) {
					applyBuff(affected, ship, 1f, visible);
					buffed.add(affected);
				}
				//affected ship already present
				if (previous.contains(affected)) {
					previous.remove(affected);
					applyBuff(affected, ship, 1f, visible);
				}
			}
			if (!previous.isEmpty()) {
				for (ShipAPI s : previous) {
					buffed.remove(s);
					unapplyBuff(s);
				}
			}
		} else if (!buffed.isEmpty()) {
			for (ShipAPI affected : buffed) {
				unapplyBuff(affected);
			}
			buffed.clear();
		}

	}


	private void applyBuff(ShipAPI ship, ShipAPI source, float level, boolean visible) {
		if (ship.isFighter()) {
			ship.fadeToColor("cloud_affected2", new Color(190, 190, 190, 255), 0.1f, 0.1f, 1f);


			/*   boolean player = false;
			//player = ship == Global.getCombatEngine().getPlayerShip();
			// (player) {
			//	Global.getCombatEngine().maintainStatusForPlayerShip("cloud_affected2", "graphics/icons/hullsys/entropy_amplifier.png", "MIST CLOUD", "REDUCED MANEUVERABILITY BY 1/2", true);
			}   */


			ship.getEngineController().forceFlameout();
			ship.getVelocity().set(ship.getVelocity().getX() * 0.992f, ship.getVelocity().getY() * 0.992f);
			ship.setAngularVelocity(ship.getAngularVelocity() * 1.005f);
			Global.getCombatEngine().applyDamage (ship, ship.getLocation(), 1f, HIGH_EXPLOSIVE, 9999, true, false, ship);
			if (ship.getHitpoints() <= 10f) {
				Global.getCombatEngine().applyDamage (ship, ship.getLocation(), 10, HIGH_EXPLOSIVE, 9999, true, false, ship);
			}
		}
	}

	private void unapplyBuff(ShipAPI ship) {


	}
}
