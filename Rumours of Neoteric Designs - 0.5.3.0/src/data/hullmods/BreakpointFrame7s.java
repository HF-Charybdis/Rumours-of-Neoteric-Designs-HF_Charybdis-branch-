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

public class BreakpointFrame7s extends BaseHullMod {

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
        variant.getHullSpec().setShipDefenseId("paradox7s");
        variant.getHints().remove(ShipHullSpecAPI.ShipTypeHints.PHASE);
        stats.getEngineHealthBonus().modifyMult(id, 9999f);
        stats.getWeaponHealthBonus().modifyMult(id, 9999f);
    }

	public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (!ship.getVariant().hasHullMod("aswitcher_maya")) {
            ship.getVariant().removeMod("breakpoint7s");
            ship.getVariant().removeMod("dummysynergy7s");
            ship.getVariant().removeMod("frame7s");
            ship.getVariant().removeMod("dummyframe7s");
        }

        MutableShipStatsAPI stats = ship.getMutableStats();
        ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0, 0, 0, 0), 1f, 0.5f);
        ship.getEngineController().extendFlame(this, -0.25f, -0.25f, 0.5f);

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


        //MagicRender.singleframe(Global.getSettings().getSprite("graphics/ships/Mayawati7s_glow.png"), ship.getLocation(), new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), ship.getFacing() - 90f, EXPLOSION, true, CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
        if (ship.getCurrFlux() >= ship.getMaxFlux()) {
            ship.getFluxTracker().setCurrFlux(ship.getMaxFlux() * 1f);
        }
        int should_not_apply = 0;
        for (ShipAPI cheaters : CombatUtils.getShipsWithinRange(ship.getLocation(), 9999f)) {
            if (cheaters.getVariant().hasHullMod("Cheat_Maya7s")) {
                should_not_apply++;
            }
        }
        stats.getCombatEngineRepairTimeMult().modifyMult(ship.getId(), 0.1f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(ship.getId(), 0.1f);
        stats.getMaxSpeed().modifyFlat(ship.getId(), 0f + ((50f) * ship.getFluxLevel()));
        stats.getHullDamageTakenMult().modifyMult(ship.getId(), 1f - (0.3f * ship.getHardFluxLevel()));
        stats.getArmorDamageTakenMult().modifyMult(ship.getId(), 1f - (0.3f * ship.getHardFluxLevel()));
        stats.getEmpDamageTakenMult().modifyMult(ship.getId(), 1f - (0.3f * ship.getHardFluxLevel()));
        if (ship.getFluxTracker().isVenting()) {
            if (should_not_apply == 0) {
            stats.getFluxDissipation().unmodify();
            stats.getFluxDissipation().setBaseValue(1200f - (ship.getVariant().getNumFluxVents() * 10f));
            if (!(ship.getFluxLevel() <= 0.05f)) {
                if (ship.hasTag("ACTIVE7S")) {
                    if (!paradox) {
                        paradox = true;

                        if (!ship.getVariant().hasHullMod("Cheat_Maya7s")) {

                            Global.getSoundPlayer().playSound("realitydisruptor_emp_impact", 1.6f, 5f, ship.getLocation(), ship.getVelocity());
                            Global.getSoundPlayer().playSound("tachyon_lance_emp_impact", 1f, 2f, ship.getLocation(), ship.getVelocity());

                            for (int i = 0; i < 4; i++) {
                                Vector2f point1 = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 1.5f);
                                Vector2f point2 = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 2.5f);
                                Global.getCombatEngine().spawnEmpArc(ship,
                                        point1,
                                        new SimpleEntity(point1),
                                        new SimpleEntity(point2),
                                        DamageType.FRAGMENTATION,
                                        0f,
                                        0f,
                                        9999f,
                                        null,
                                        10f,
                                        EMP_CORE, //Central color
                                        VENT);//Fringe Color);
                            }


                            for (CombatEntityAPI Ships : AIUtils.getNearbyEnemies(ship,450f)) {
                                Global.getCombatEngine().spawnEmpArc(ship,
                                        MathUtils.getRandomPointOnCircumference(MathUtils.getRandomPointInCircle(ship.getLocation(), 125f), 125f),
                                        new SimpleEntity(MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius() * 1.5f)),
                                        Ships,
                                        DamageType.ENERGY, //Damage type
                                        ship.getCurrFlux() * 0.2f,
                                        ship.getCurrFlux() * 0.2f,
                                        9999f, //Max range
                                        "realitydisruptor_emp_impact", //Impact sound
                                        10f * ship.getFluxLevel(), // thickness of the lightning bolt
                                        EMP_CORE, //Central color
                                        VENT //Fringe Color);
                                );


                            }


                        }

                    }

                    ship.fadeToColor(ship, new Color(180, 238, 240, 255), 0.5f, 0.25f, 1f);
                    stats.getMaxCombatHullRepairFraction().modifyFlat(ship.getId(), 1f);
                    stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(ship.getId(), 40f);
                    MagicRender.battlespace(
                            Global.getSettings().getSprite("graphics/ships/Mayawati7s_glow.png"), //sprite
                            ship.getShieldCenterEvenIfNoShield(), //location vector2f
                            new Vector2f(0f, 0f), //velocity vector2f
                            new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
                            new Vector2f(0f, 0f), //growth, vector2f, pixels/second
                            ship.getFacing() - 90f, //angle, float
                            0f, //spin, float
                            VENT_HULL, //color Color
                            true, //additive, boolean

                            0f, //jitter range
                            1f, //jitter tilt
                            1f, // flicker range
                            0f, //flicker median
                            0.05f, //max delay

                            0.02f, //fadein, float, seconds
                            0.005f, //full, float, seconds
                            0.02f, //fadeout, float, seconds

                            CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
                } else if (!ship.hasTag("ACTIVE7S")) {
                    if (!noparadox) {
                        noparadox = true;
                        Global.getSoundPlayer().playSound("realitydisruptor_emp_impact", 1.6f, 5f, ship.getLocation(), ship.getVelocity());
                        Global.getSoundPlayer().playSound("tachyon_lance_emp_impact", 1f, 2f, ship.getLocation(), ship.getVelocity());

                        for (int i = 0; i < 4; i++) {
                            Vector2f point1 = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 1.5f);
                            Vector2f point2 = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 2.5f);
                            Global.getCombatEngine().spawnEmpArc(ship,
                                    point1,
                                    new SimpleEntity(point1),
                                    new SimpleEntity(point2),
                                    DamageType.FRAGMENTATION,
                                    0f,
                                    0f,
                                    9999f,
                                    null,
                                    10f,
                                    EMP_CORE, //Central color
                                    VENT);//Fringe Color);
                        }


                        for (CombatEntityAPI Ships : AIUtils.getNearbyEnemies(ship, 450f)) {
                            Global.getCombatEngine().spawnEmpArc(ship,
                                    MathUtils.getRandomPointOnCircumference(MathUtils.getRandomPointInCircle(ship.getLocation(), 125f), 125f),
                                    new SimpleEntity(MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 1.5f)),
                                    Ships,
                                    DamageType.ENERGY, //Damage type
                                    ship.getCurrFlux() * 0.2f,
                                    ship.getCurrFlux() * 0.2f,
                                    9999f, //Max range
                                    "realitydisruptor_emp_impact", //Impact sound
                                    10f * ship.getFluxLevel(), // thickness of the lightning bolt
                                    EMP_CORE, //Central color
                                    VENT //Fringe Color);
                            );


                        }
                    }
                    }

                    MagicRender.battlespace(
                            Global.getSettings().getSprite("graphics/ships/Mayawati7s_glow.png"), //sprite
                            ship.getShieldCenterEvenIfNoShield(), //location vector2f
                            new Vector2f(0f, 0f), //velocity vector2f
                            new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()), //size vector2f
                            new Vector2f(0f, 0f), //growth, vector2f, pixels/second
                            ship.getFacing() - 90f, //angle, float
                            0f, //spin, float
                            VENT_ARMOR, //color Color
                            true, //additive, boolean

                            0f, //jitter range
                            1f, //jitter tilt
                            1f, // flicker range
                            0f, //flicker median
                            0.05f, //max delay

                            0.02f, //fadein, float, seconds
                            0.005f, //full, float, seconds
                            0.02f, //fadeout, float, seconds

                            CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);

                    ship.fadeToColor(ship, new Color(180, 240, 183, 255), 0.5f, 0.25f, 1f);
                    if (ship == null) return;
                    if (!ship.isAlive()) return;
                    //I'll be honest, needed to learn from bobulated heral armor to do this one, why armor grid stuff is so hard?
                    ArmorGridAPI armor = ship.getArmorGrid(); //ArmorAPI, I not even knew that this existed before
                    float[][] grid = armor.getGrid(); //Calls the grid
                    float Regen = armor.getMaxArmorInCell() * (0.018f); //regen rate
                    if (Regen > armor.getMaxArmorInCell())
                        Regen = 0f; //This makes the regen stops, compares the current armor vs max armor
                    for (int x = 0; x < grid.length; x++) { //I think that those loops calls the "setArmorValue" and updates
                        for (int y = 0; y < grid[0].length; y++) { //the current armor value, including the regenerated armor
                            //for me? Its rocket science tbh
                            armor.setArmorValue(x, y, Math.min(grid[x][y] + Regen, armor.getMaxArmorInCell()));
                            //this line above makes everything called before work, regenerates the entire armor grid, does not
                            //prioritize areas with less armor, its a general regen, tested to reach to that conclusion
                        }
                    }
                }
            }
        } else if (!ship.getFluxTracker().isVenting()) {
            if (ship.getFluxLevel() >= 0.1f) {
                stats.getFluxDissipation().modifyMult(ship.getId(), 1f * (ship.getFluxLevel() * 5f));
            }
            noparadox = false;
            paradox = false;
            ship.getTags().remove("ACTIVE7S");
            if (ship.getFluxLevel() == 0f) { //this check in specific is for benefit AI's strategy (aka safe mode)
                ship.setVentFringeColor(VENT);
                stats.getMaxCombatHullRepairFraction().modifyFlat(ship.getId(), 0.5f);
                stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(ship.getId(), 1.5f);
            } else if ((ship.getFluxLevel() >= 0f)) {
                stats.getMaxCombatHullRepairFraction().unmodify(ship.getId());
                stats.getHullCombatRepairRatePercentPerSecond().unmodify(ship.getId());
                stats.getFluxDissipation().setBaseValue(ship.getHullSpec().getFluxDissipation() + (ship.getVariant().getNumFluxVents() * 10f));
            }
        }
        float influence = (ship.getHardFluxLevel() * 5f);
        if (influence >= 3.5f) {
            influence = 3.5f;
        }
        float firerate = 4f - influence;
        float multiplier = Math.round(Math.round(4f / firerate * 100f));

        if (ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1, "graphics/icons/hullsys/damper_field.png", "Hardflux benefits", multiplier + "%" + " RoF" + " / " + "damage reduction " + Math.round(30f * ship.getHardFluxLevel()) + "%", false);
            //Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2, "graphics/icons/hullsys/damper_field.png", "Hardflux benefits", "Damage Taken Reduced by = " + Math.round(50f * ship.getHardFluxLevel()) + "%", false);
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3, "graphics/icons/hullsys/temporal_shell.png", "SoftFlux benefits", "Speed + " + Math.round((50f) * ship.getFluxLevel())+ " / " + "Shockwave: " + Math.round(ship.getCurrFlux() * 0.2f) + " damage", false);
            //Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY4, "graphics/icons/hullsys/temporal_shell.png", "SoftFlux benefits", "15% Flux level required to activate Paradox", false);


        }


        //makes harbinger cry
        if (ship.getFluxTracker().isOverloaded()) {
            if (!phased7s) {
                phased7s = true;
                ship.setPhased(true);
                ship.setAlphaMult(0.25f);
            }
        }
        if (!ship.getFluxTracker().isOverloaded()) {
            phased7s = false;
            ship.setPhased(false);
            ship.setAlphaMult(1f);
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
        //,text,background,

        LabelAPI label = tooltip.addPara("Using an advanced integration with the hull of the craft, this vessel harseness the Breakpoint Phenomena for substantial defensive and offensive capabilities, once under flux-pressure, it can reach peak-performance as both its overall integrity and systems are amplified to absurd degrees. Both softflux and hardflux can be synergized, granting further bonuses depending on their levels, however generating flux requires using of the %s.", opad, b, "Illusia Resonance");
        label.setHighlight("Illusia Resonance");
        label.setHighlightColors(b);

        tooltip.addSectionHeading("Breakpoint Phenomena properties:",text,background,Alignment.MID,opad);

        label = tooltip.addPara( "Defensive system is now %s; ", opad, b,
                "" + "Breakpoint Field");
        label.setHighlight(	"" + "Breakpoint Field");
        label.setHighlightColors(b);

        label = tooltip.addPara( "Enabled effects from %s hullmod. ", opad, b,
                "" + "Illusia Resonance");
        label.setHighlight(	"" + "Illusia Resonance");
        label.setHighlightColors(b);

        tooltip.addSectionHeading("Active Venting properties:",text,background,Alignment.MID,opad);

        label = tooltip.addPara("Vents %s flux per second, while restoring armor integrity;", opad, b,
                "" + "2400");
        label.setHighlight(	"" + "2400");
        label.setHighlightColors(b);

        label = tooltip.addPara("Above %s flux level, releases a shockwave, which does %s of the current flux amount in energy damage to nearby enemy ships and/or fighters.", opad, b,
                "" + "15%", "20%");
        label.setHighlight(	"" + "15%", "20%");
        label.setHighlightColors(b, b);

        tooltip.addSectionHeading("Softflux Benefits:",text,background,Alignment.MID,opad);

        label = tooltip.addPara( "Increases top speed up to %s; ", opad, b,
                "" + "50");
        label.setHighlight(	"" + "50");
        label.setHighlightColors(b);

        tooltip.addSectionHeading("Hardflux Benefits:",text,background,Alignment.MID,opad);

        label = tooltip.addPara( "Increases Breakpoint Pulser fire rate up to %s times at %s hardflux level;", opad, b,
                "" + "8", "75%");
        label.setHighlight(	"" + "8", "75%");
        label.setHighlightColors(b, b);

        label = tooltip.addPara( "Reduces damage taken up to %s, based on hardflux level.", opad, b,
                "" + "15%");
        label.setHighlight(	"" + "15%");
        label.setHighlightColors(b);

        tooltip.addSectionHeading("WARNING",text,background,Alignment.MID,opad);

        label = tooltip.addPara( "This configuration is intended for %s operation. %s.", opad, b,
                "" + "Flagship", "Autopilot is NOT advised");
        label.setHighlight(	"" + "Flagship", "Autopilot is NOT advised");
        label.setHighlightColors(b, bad);
        tooltip.beginImageWithText(Global.getSettings().getSpriteName("icons", "hullmodicon7s"), 35);
        tooltip.addImageWithText(opad);

    }

        



}
