package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.MagFieldGenPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.magiclib.util.MagicRender;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;


public class Force_field7s extends BaseHullMod {

	protected Object STATUSKEY1 = new Object();

	boolean threshold = false;
	private int timer = 0;



	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		final ShipVariantAPI variant = stats.getVariant();

	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();
		if (Global.getCombatEngine().isPaused()) {
			return;
		}
		float dur = 1f * Global.getCombatEngine().getTimeMult().getMult();

		float alpha = ship.getFluxTracker().getCurrFlux() * 0.001f;
		if (alpha >= 12f) alpha = 12f;
		float expand = alpha * 0.06f;
		if (expand >= 0.6f) expand = 0.6f;
		timer += 1;
		if (timer >= (15 / dur)) {

			MagicRender.objectspace(Global.getSettings().getSprite("graphics/fx/seven_aura2.png"),
					ship,
					new Vector2f(0f, 0f),
					new Vector2f(0f, 0f),
					new Vector2f(1.5f * ((ship.getSpriteAPI().getWidth() + ship.getSpriteAPI().getHeight()) * expand), 1.5f * (ship.getSpriteAPI().getWidth() + ship.getSpriteAPI().getHeight()) * expand),
					new Vector2f(2f * ((ship.getSpriteAPI().getWidth() + ship.getSpriteAPI().getHeight()) * expand), 2f * (ship.getSpriteAPI().getWidth() + ship.getSpriteAPI().getHeight()) * expand),
					180f,
					1f,
					true,
					new Color(154, 243, 255, Math.round(alpha)),
					true,
					0.5f,
					0f,
					1f,
					true);
			Color stuff = new Color(154, 243, 255);
			timer = 0;
		}

		if (!AIUtils.getNearbyEnemies(ship, 9999f).isEmpty()) {
			for (ShipAPI botes : AIUtils.getNearbyEnemies(ship, 9999f)) {
				if (MathUtils.getDistance(ship, botes) <= 1000f * expand) {
					float power = ship.getFluxTracker().getCurrFlux() * 0.0002f;
					if (power >= 4f) power = 4f;

					float force = -1f * (((power) * (1f + (dur * 0.2f))) - (0.0006f * MathUtils.getDistance(ship.getLocation(), botes.getLocation())));
					if (force > 0) force = 0;

					CombatUtils.applyForce(botes, VectorUtils.getAngle(botes.getLocation(), ship.getLocation()), force);

					boolean player = false;
					if (stats.getEntity() instanceof ShipAPI) {
						ship = (ShipAPI) stats.getEntity();
						player = ship == Global.getCombatEngine().getPlayerShip();
					}
					if (Global.getCombatEngine().getPlayerShip() == botes) {
							Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
									"graphics/icons/hullsys/damper_field.png", "Force Field",
									"Ship is being pushed back!", true);
					}

				}
			}
		}
		if (!AIUtils.getNearbyEnemyMissiles(ship, 9999f).isEmpty()) {
			for (MissileAPI missiles : AIUtils.getNearbyEnemyMissiles(ship, 9999f)) {
				if (MathUtils.getDistance(ship, missiles) <= 1000f * expand) {
					if (missiles.getOwner() != ship.getOwner()) {
						float power = ship.getFluxTracker().getCurrFlux() * 0.0001f;
						if (power >= 2f) power = 2f;

						float force_missile = -1f * (((power) * (1f + (dur * 0.2f))) - (0.0006f * MathUtils.getDistance(ship.getLocation(), missiles.getLocation())));
						if (force_missile > 0) force_missile = 0f;

							CombatUtils.applyForce(missiles, VectorUtils.getAngle(missiles.getLocation(), ship.getLocation()), force_missile);
					}
				}
			}
		}

		/** You may be wondering, why float "power" is different for ships and missiles? **/

		//Balance reason: ships are heavier than missiles, which is why ship "power" is higher than missile "power".
        //Lore reason: this force field device interacts with antimatter, which can result in better efficiency
		//in higher concentration of that, which are ships.
	}



	/*public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		Set<String> BLOCKED_HULLMODS = new HashSet();
		BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
		BLOCKED_HULLMODS.add("seven_system_overflow_safeties");
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "");
			}
		}

	}

	 */

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		return null;
	}

	@Override
	public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color m = Misc.getMissileMountColor();
		Color e = Misc.getEnergyMountColor();
		Color b = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color supermod_grey = Misc.getGrayColor();
		Color supermod_white = Misc.getTextColor();
		Color supermod_on = Misc.getStoryBrightColor();
		Color text = new Color (132, 255, 149);
		Color background = new Color (15, 21, 17);

		LabelAPI label = tooltip.addPara("Through special modifications that connect flux capacitors with antimatter chambers, while also, by miracle, not affecting the fuel consumption by the ship, engineers accidentally managed to create a ''repulse-field'', which uses highly ionized antimatter that propels anything big enough away from the augmented ship.", opad, b, "");
		label.setHighlight();
		label.setHighlightColors();

		label = tooltip.addPara( "%s will be pushed away from the ship, however, such an effect only works if %s are retained, to keep the ionized antimatter concise and maintain the repulse-field functioning.", opad, b,
				"" + "Any missile, fighter, and more major spacecraft", "great amounts of soft/hardflux");
		label.setHighlight(	"" + "Any missile, fighter, and more major spacecraft", "great amounts of soft/hardflux");
		label.setHighlightColors(b, b);

		tooltip.addSectionHeading("Specifications:",text,background,Alignment.MID,opad);

		label = tooltip.addPara( "These effects are applied according to flat numbers of flux, and scales up to %s of current soft/hardflux amount.", opad, b,
				"" + "20.000");
		label.setHighlight(	"" + "20.000");
		label.setHighlightColors(b);

		label = tooltip.addPara( "The field is mostly efficient against %s, such as Annihilator rockets and fighters. However, may fail against %s, such as Atropos Torpedoes and ships in general.", opad, b,
				"" + "low acceleration light missiles and fighters", "strong acceleration and heavy entities");
		label.setHighlight(	"" + "low acceleration light missiles and fighters", "strong acceleration and heavy entities");
		label.setHighlightColors(b, bad);

		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);
	}
}
