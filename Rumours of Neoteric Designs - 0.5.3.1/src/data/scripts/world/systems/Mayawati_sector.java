package data.scripts.world.systems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.SkillsChangeOfficerEffect;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.impl.campaign.world.ZigLeashAssignmentAI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.BuffManager;
import com.fs.starfarer.campaign.CharacterStats;
import com.fs.starfarer.campaign.fleet.FleetData;
import data.scripts.util.MagicCampaign;
import org.lazywizard.lazylib.LazyLib;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.campaign.MagicCaptainBuilder;

public class Mayawati_sector {


    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Light of Maya");
        system.getLocation().set(-18000, -26000);
        system.setBackgroundTextureFilename("graphics/backgrounds/background_mayawati7s.jpg");

        // Star
        PlanetAPI Estrela = system.initStar(
                "Maya_star",
                "star_green7s",
                805f,
                1200f);

        Estrela.setName("Light of Maya");

        system.setLightColor(new Color(156, 230, 186));


        // Irradiated heated planet
        PlanetAPI Planeta_1 = system.addPlanet("Wati_planet",
                Estrela,
                "Wati",
                "irradiated",
                140f,
                200f,
                2800f,
                270f);
        Planeta_1.setCustomDescriptionId("Wati_description");
        PlanetConditionGenerator.generateConditionsForPlanet(Planeta_1, StarAge.AVERAGE);
        Planeta_1.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);
        //Planeta_1.getCargo().addMothballedShip(FleetMemberType.SHIP, "Ex7s_Mayawati7s_Deceptive", "// BREAKPOINT //");



        float innerOrbitDistance = StarSystemGenerator.addOrbitingEntities(
                system,
                Estrela,
                StarAge.AVERAGE,
                2,
                4,
                4000,
                1,
                true
        );

        // Inner system jump point
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("jump_point7s", "Inner Jump Point");
        jumpPoint1.setCircularOrbit(Estrela, 134, 4461, 213);

        system.addEntity(jumpPoint1);

        //add an asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                Estrela, //orbit focus
                80, //number of asteroid entities
                innerOrbitDistance + 500, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Example Asteroid Belt" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(Estrela,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                innerOrbitDistance + 500, //same as above
                200f,
                null,
                null
        );
        addFleet(Planeta_1);


        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);


    }
    public static void addFleet(SectorEntityToken Planeta_1) {
        PersonAPI Vergil7s = MagicCampaign.createCaptain(
                false,
                null,
                "Vergil",
                "Ferrera",
                "Mayawati_guy",
                FullName.Gender.MALE,
                Factions.TRITACHYON,
                Ranks.SPACE_COMMANDER,
                Ranks.POST_FLEET_COMMANDER,
                Personalities.STEADY,
                9,
                9,
                OfficerManagerEvent.SkillPickPreference.YES_ENERGY_NO_BALLISTIC_NO_MISSILE_YES_DEFENSE,
                null
        );

        PersonAPI Alpha_core = MagicCampaign.createCaptain(
                true,
                Commodities.ALPHA_CORE,
                "Alpha",
                "Core",
                "Different_Alpha_Portrait",
                FullName.Gender.ANY,
                "remnant",
                Ranks.AGENT,
                Ranks.POST_AGENT,
                Personalities.RECKLESS,
                11,
                11,
                OfficerManagerEvent.SkillPickPreference.YES_ENERGY_NO_BALLISTIC_NO_MISSILE_YES_DEFENSE,
                null
        );

        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("tritachyon_hostile", FleetTypes.PATROL_LARGE, null);
        fleet.setName("// ERROR 404 //");
        fleet.setNoFactionInName(true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_REP_IMPACT, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_LOW_REP_IMPACT, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
        //fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true); // so it keeps transponder on
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MISSION_IMPORTANT, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.STAR_SYSTEM_IN_ANCHOR_MEMORY, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.OFFICER_MAX_ELITE_SKILLS, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_DO_NOT_IGNORE_PLAYER, true);
        fleet.getMemoryWithoutUpdate().set("$Mayawati", true);


        fleet.getFleetData().addFleetMember("Ex7s_Mayawati7s_Deceptive");
        fleet.getFleetData().ensureHasFlagship();
        fleet.clearAbilities();
        fleet.getForceNoSensorProfileUpdate();
        fleet.getSensorRangeMod().modifyMult(fleet.getId(),1f);
        fleet.getStats().getFleetwideMaxBurnMod().modifyFlat(fleet.getId(),10f);
        fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE,Planeta_1,9999999f);

        FleetMemberAPI Maya1 = fleet.getFleetData().addFleetMember("Ex7s_Mayawati7s_Deceptive_Mission");
        Maya1.getVariant().addTag(Tags.SHIP_UNIQUE_SIGNATURE);
        Maya1.getVariant().addTag(Tags.NO_ENTITY_TOOLTIP);
        Maya1.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        Maya1.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        Maya1.getVariant().addTag(Tags.UNRECOVERABLE);
        Maya1.getVariant().addTag(Tags.NO_DROP);
        Maya1.setCaptain(Alpha_core);

        FleetMemberAPI Maya2 = fleet.getFleetData().addFleetMember("Ex7s_Mayawati7s_Deceptive_Mission");
        Maya2.getVariant().addTag(Tags.SHIP_UNIQUE_SIGNATURE);
        Maya2.getVariant().addTag(Tags.NO_ENTITY_TOOLTIP);
        Maya2.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        Maya2.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        Maya2.getVariant().addTag(Tags.UNRECOVERABLE);
        Maya2.getVariant().addTag(Tags.NO_DROP);
        Maya2.setCaptain(Alpha_core);

        FleetMemberAPI Maya3 = fleet.getFleetData().addFleetMember("Ex7s_Mayawati7s_Deceptive_Mission");
        Maya3.getVariant().addTag(Tags.SHIP_UNIQUE_SIGNATURE);
        Maya3.getVariant().addTag(Tags.NO_ENTITY_TOOLTIP);
        Maya3.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        Maya3.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        Maya3.getVariant().addTag(Tags.UNRECOVERABLE);
        Maya3.getVariant().addTag(Tags.NO_DROP);
        Maya3.setCaptain(Alpha_core);

        FleetMemberAPI Maya4 = fleet.getFleetData().addFleetMember("Ex7s_Mayawati7s_Deceptive_Mission");
        Maya4.getVariant().addTag(Tags.SHIP_UNIQUE_SIGNATURE);
        Maya4.getVariant().addTag(Tags.NO_ENTITY_TOOLTIP);
        Maya4.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        Maya4.getVariant().addTag(Tags.VARIANT_UNBOARDABLE);
        Maya4.getVariant().addTag(Tags.UNRECOVERABLE);
        Maya4.getVariant().addTag(Tags.NO_DROP);
        Maya4.setCaptain(Alpha_core);

        // so it never shows as "Unidentified Fleet" but isn't easier to spot due to using the actual transponder ability
        fleet.setTransponderOn(true);
        fleet.setCommander(Vergil7s);
        fleet.getCargo().addCommodity(Commodities.ALPHA_CORE, 2);

        FleetMemberAPI flagship = fleet.getFlagship();
        flagship.setCaptain(Vergil7s);
        flagship.updateStats();
        flagship.getRepairTracker().setCR(flagship.getRepairTracker().getMaxCR());
        flagship.getVariant().addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
        flagship.setShipName("TTS Breakpoint");
        flagship.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        flagship.getVariant().addTag(Tags.SHIP_RECOVERABLE);
        flagship.getVariant().addMod("Cheat_Maya7s");


        Vector2f loc = new Vector2f(Planeta_1.getLocation().x + 300 * ((float) Math.random() - 0.5f),
                Planeta_1.getLocation().y + 300 * ((float) Math.random() - 0.5f));
        fleet.setLocation(loc.x, loc.y);
        Planeta_1.getContainingLocation().addEntity(fleet);

    }
}
