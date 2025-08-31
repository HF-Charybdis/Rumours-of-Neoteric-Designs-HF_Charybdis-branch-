package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import data.shipsystems.scripts.Tempestas_cloak7s;

import java.awt.*;

public class celeritas_core_placeboo extends BaseHullMod {
	boolean dead = false;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		// Hey,if you are seeing this, curiosity killed the cat, ya know? hehe

		//Anyways, thanks for downloading my mod, and hopefully the code inside here can help you,
		//its just a bunch of random low brainer adaptations, that can be useful for crash your game
		//But if you still want to venture here, don't bring me a tear^^
		//After Matt drama happened, I should tell this, THERE IS NO TRUE CRASHCODE
	}

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
		Color text = new Color (132, 255, 149);
		Color background = new Color (15, 21, 17);
		//,text,background,

		LabelAPI label = tooltip.addPara("A powerful core has been installed on this ship, powerful enough for the ship to be able to tap into p-space, however, such a hard to contain power leads to the breakdown and damage of subsystems on the enemy ships after using the Tempestas Vanish system.", opad, b, "");
		label.setHighlight("");
		label.setHighlightColors(b);
		tooltip.addPara("Almost none of the existing antimatter drivers are capable of handling this magnitude of p-space energy discharges, flux subsystems may become vulnerable and disrupted, while projectiles get erased.", opad, b, "");
		label.setHighlight("");
		label.setHighlightColors(b);

		tooltip.addSectionHeading("Active Modifiers:",text,background, Alignment.MID, opad);

		label = tooltip.addPara("While cloaked, this vessel will continuously generate %s, up to %s stacks, which will be dispersed when deactivating the system, causing the following effects:", opad, b,
				"" + "stacks of Haste", "100");
		label.setHighlight("" + "stacks of Haste", "100");
		label.setHighlightColors(b);

		label = tooltip.addPara("At %s or more stacks of Haste:", opad, b,
				"" + "25");
		label.setHighlight("" + "25");
		label.setHighlightColors(b);

		label = tooltip.addPara("Enemy projectiles and missiles at %s range will be destroyed;", opad, b,
				"" + "700");
		label.setHighlight("" + "700");
		label.setHighlightColors(b);
		label = tooltip.addPara("Enemy ships at %s range will suffer engine disruption and have decreased EMP damage resistance for a short period.", opad, b,
				"" + "700");
		label.setHighlight("" + "700");
		label.setHighlightColors(b);

		label = tooltip.addPara("At %s stacks of Haste:", opad, b,
				"" + "100");
		label.setHighlight("" + "100");
		label.setHighlightColors(b);

		label = tooltip.addPara("Enemy projectiles and missiles at %s range will be destroyed;", opad, b,
				"" + "1200");
		label.setHighlight("" + "1200");
		label.setHighlightColors(b);
		label = tooltip.addPara("Enemy ships at %s range will suffer engine disruption and have decreased EMP damage resistance for a short period.", opad, b,
				"" + "1200");
		label.setHighlight("" + "1200");
		label.setHighlightColors(b);
		label = tooltip.addPara("Enemy ships at %s range will suffer flameouts, a 0.5-second overload, and have massively decreased EMP damage resistance for a short period.", opad, b,
				"" + "700");
		label.setHighlight("" + "700");
		label.setHighlightColors(b);
		label.setHighlightColors(b, b);
		tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
		tooltip.addImageWithText(opad);

	}
}
