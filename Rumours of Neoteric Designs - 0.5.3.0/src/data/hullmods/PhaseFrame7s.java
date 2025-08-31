package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.NaN;

public class PhaseFrame7s extends BaseHullMod {

    protected Object STATUSKEY1 = new Object();
    protected Object STATUSKEY2 = new Object();
    protected Object STATUSKEY3 = new Object();
    protected Object STATUSKEY4 = new Object();
    public static final Color JITTER_COLOR = new Color(90, 255, 217,55);
    public static final Color VENT = new Color(89, 255, 117,155);

    public static Color EXPLOSION = new Color(190, 247, 232, 150);
    public static Color VENT_HULL = new Color(117, 180, 163, 200);

    public static Color VENT_ARMOR = new Color(117, 180, 118, 200);
    public static Color EMP_CORE = new Color(152, 255, 236, 255);

    boolean noparadox = false;
    boolean paradox = false;
    boolean phased7s = false;
    int pulsating = 0;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        final ShipVariantAPI variant = stats.getVariant();
        variant.getHullSpec().setShipDefenseId("illusiacloak7s");
        variant.getHints().add(ShipHullSpecAPI.ShipTypeHints.PHASE);
        stats.getEnergyWeaponDamageMult().modifyMult(id, 1.5f);
        stats.getFluxDissipation().modifyFlat(id,140f);
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 0.1f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 0.1f);
    }

	public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
        ship.setCollisionRadius(143.5f);

        if (!ship.getVariant().hasHullMod("aswitcher_maya")) {
            ship.getVariant().removeMod("synergy7s");
            ship.getVariant().removeMod("dummysynergy7s");
            ship.getVariant().removeMod("frame7s");
            ship.getVariant().removeMod("dummyframe7s");
        }

        MutableShipStatsAPI stats = ship.getMutableStats();
        ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0, 0, 0, 0), 1f, 0.5f);
        ship.getEngineController().extendFlame(this, -0.25f, -0.25f, 0.5f);

        int should_not_apply = 0;
        for (ShipAPI cheaters : CombatUtils.getShipsWithinRange(ship.getLocation(), 9999f)) {
            if (cheaters.getVariant().hasHullMod("Cheat_Maya7s")) {
                should_not_apply++;
            }
        }


        if (!AIUtils.getAlliesOnMap(ship).isEmpty()) {
            for (ShipAPI spawner : AIUtils.getAlliesOnMap(ship)) {
                if (MathUtils.getDistance(ship, spawner) <= 1000f) {
                    if (spawner.getVariant().hasHullMod("active7s") && !spawner.getVariant().hasHullMod("aclone_timeout7s") && !ship.getVariant().hasHullMod("Cheat_Maya7s") && should_not_apply == 0) {
                        Global.getCombatEngine().removeEntity(spawner);
                    }
                }
            }
        }

        if (!ship.isPhased()) {
            MagicRender.battlespace(
                    Global.getSettings().getSprite("graphics/ships/Mayawati7s_jitter.png"), //sprite
                    ship.getShieldCenterEvenIfNoShield(), //location vector2f
                    new Vector2f(0f, 0f), //velocity vector2f
                    new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
                    new Vector2f(0f, 0f), //growth, vector2f, pixels/second
                    ship.getFacing() - 90f, //angle, float
                    0f, //spin, float
                    EXPLOSION, //color Color
                    true, //additive, boolean

                    0f, //jitter range
                    1f, //jitter tilt
                    1f, // flicker range
                    0f, //flicker median
                    0.05f, //max delay

                    0.25f, //fadein, float, seconds
                    0f, //full, float, seconds
                    0.25f, //fadeout, float, seconds

                    CombatEngineLayers.BELOW_SHIPS_LAYER);
        }



    }

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
        Color text = new Color (132, 255, 149);
        Color background = new Color (15, 21, 17);
        //text,background,

        LabelAPI label = tooltip.addPara("An alternative use to the Breakpoint Phenomena, with the usage of inhibitors, the vessel is limited only to P-Space, allowing it to phase cloak just like other phase ships, in addition to being immune to phase coil stress. Its effects include the following:", opad, b, "");
        label.setHighlight();
        label.setHighlightColors();

        tooltip.addSectionHeading("Phase Cloak properties:",text,background, Alignment.MID, opad);

        label = tooltip.addPara("Increases ship time flow by %s while active;", opad, b,
                "" + "2x");
        label.setHighlight(	"" + "2x");
        label.setHighlightColors(b);

        label = tooltip.addPara("Ship does not suffer from %s;", opad, b,
                "" + "phase coil stress");
        label.setHighlight(	"" + "phase coil stress");
        label.setHighlightColors(b);

        label = tooltip.addPara("%s missiles when passing by them.", opad, b,
                "" + "Disable");
        label.setHighlight(	"" + "Disable");
        label.setHighlightColors(b);

        tooltip.addSectionHeading("Interactions with other modifiers:",text,background, Alignment.MID, opad);

        label = tooltip.addPara( "EMP arcs are discharged when Illusia Dive is activated, dealing %s to nearby fighters and ships, multiplied by the remaining %s from the ship system;", opad, b,
                "" + "250 energy damage", "number of charges");
        label.setHighlight(	"" + "250 energy damage", "number of charges");
        label.setHighlightColors(b, b);

        label = tooltip.addPara( "Increased Breakpoint Pulser damage by %s;", opad, b,
                "" + "50%");
        label.setHighlight(	"" + "50%");
        label.setHighlightColors(b);

        label = tooltip.addPara( "Increased flux dissipation by %s;", opad, b,
                "" + "140");
        label.setHighlight(	"" + "140");
        label.setHighlightColors(b);

        label = tooltip.addPara( "Disabled effects from %s hullmod.", opad, b,
                "" + "Illusia Resonance");
        label.setHighlight(	"" + "Illusia Resonance");
        label.setHighlightColors(bad);

        tooltip.addSectionHeading("WARNING:",text,background, Alignment.MID, opad);

        label = tooltip.addPara( "This setting is intended for %s mode usage. %s operations are advised to be made with the %s configuration.", opad, b,
                "" + "Autopilot", "Flagship", "Breakpoint Frame");
        label.setHighlight(	"" + "Autopilot", "Flagship", "Breakpoint Frame");
        label.setHighlightColors(b, b, b);
        tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
        tooltip.addImageWithText(opad);
    }

        



}
