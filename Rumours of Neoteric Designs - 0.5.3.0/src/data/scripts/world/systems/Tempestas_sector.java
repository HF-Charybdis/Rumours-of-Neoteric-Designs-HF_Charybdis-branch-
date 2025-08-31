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
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.impl.campaign.world.ZigLeashAssignmentAI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.campaign.BuffManager;
import com.fs.starfarer.campaign.CharacterStats;
import com.fs.starfarer.campaign.fleet.FleetData;
import data.scripts.util.MagicCampaign;
import org.lazywizard.lazylib.LazyLib;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.campaign.MagicCaptainBuilder;

public class Tempestas_sector {


    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Merga Wraith");
        system.getLocation().set(55000, -35000);
        system.setBackgroundTextureFilename("graphics/backgrounds/background_tempestas7s.jpg");

        // Star
        PlanetAPI Estrela = system.initStar(
                "Merga_star",
                StarTypes.BLUE_SUPERGIANT,
                1300f,
                1700f);

        Estrela.setName("Merga Wraith");

        system.setLightColor(new Color(156, 230, 213));

        // Destroyed Remnant Nexus, so you can store your stuff after killing Tempestas
        SectorEntityToken destroyed_nexus7s = system.addCustomEntity("nexus7s",
                "Deactivated Remnant Nexus", "destroyed_nexus7s", "neutral");
        destroyed_nexus7s.setOrbit(Estrela.getOrbit());
        Misc.setAbandonedStationMarket("nexus7s_market", destroyed_nexus7s);
        destroyed_nexus7s.setCustomDescriptionId("nexus7s_description");
        destroyed_nexus7s.setInteractionImage("illustrations", "orbital_construction");
        destroyed_nexus7s.setCircularOrbitPointingDown(Estrela,345f,3600,160);

        addDerelict(system, destroyed_nexus7s, "radiant_Strike", ShipRecoverySpecial.ShipCondition.WRECKED, 300f, false);
        addDerelict(system, destroyed_nexus7s, "Ex7s_afflictor_Strike_automated", ShipRecoverySpecial.ShipCondition.PRISTINE, 350f, true);
        SectorEntityToken pods = system.addCustomEntity("stuff7s",null, "weapons_cache_remnant",Factions.NEUTRAL);
        pods.setCircularOrbit(destroyed_nexus7s,180f,200f, 45f);
        pods.getCustomEntitySpec().setDiscoverable(true);

        float innerOrbitDistance = StarSystemGenerator.addOrbitingEntities(
                system,
                Estrela,
                StarAge.YOUNG,
                4,
                7,
                7000,
                1,
                true
        );

        StarSystemGenerator.addStableLocations(system,3);

        addFleet(destroyed_nexus7s);


        // generates hyperspace destinations for in-system jump points
        system.autogenerateHyperspaceJumpPoints(true, true);
        system.autogenerateHyperspaceJumpPoints(true, true, true);
        system.generateAnchorIfNeeded();

        system.addAsteroidBelt(Estrela, 90, 4550, 500, 290, 310, Terrain.ASTEROID_BELT,  null);
        system.addRingBand(Estrela, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4500, 305f, null, null);
        system.addRingBand(Estrela, "misc", "rings_asteroids0", 256f, 1, Color.white, 256f, 4600, 295f, null, null);


    }
    public static void addFleet(SectorEntityToken destroyed_nexus7s) {
        PersonAPI Tempestas_mask7s = MagicCampaign.createCaptain(
                false,
                null,
                "01011001",
                "01001111 01010101",
                "Tempestas_mask7s",
                FullName.Gender.MALE,
                "passive_aggressive_tempestas",
                Ranks.SPACE_COMMANDER,
                Ranks.POST_FLEET_COMMANDER,
                Personalities.STEADY,
                10,
                10,
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

        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("passive_aggressive_tempestas", FleetTypes.PATROL_LARGE, null);
        fleet.setName("Unknown Vessels");
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
        fleet.getMemoryWithoutUpdate().set("$Tempestas", true);


        fleet.getFleetData().addFleetMember("Ex7s_Stella_Tempestas7s_Mission");
        fleet.getFleetData().ensureHasFlagship();
        fleet.getForceNoSensorProfileUpdate();
        fleet.getSensorRangeMod().modifyMult(fleet.getId(),1f);
        fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE,destroyed_nexus7s,9999999f);
        fleet.setTransponderOn(true);
        fleet.setCommander(Tempestas_mask7s);
        fleet.getCargo().addCommodity(Commodities.ALPHA_CORE, 4);

        FleetMemberAPI flagship = fleet.getFlagship();
        flagship.setCaptain(Tempestas_mask7s);
        flagship.updateStats();
        flagship.getRepairTracker().setCR(flagship.getRepairTracker().getMaxCR());
        flagship.setShipName("7TS Virens Interitus");
        flagship.getVariant().addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
        flagship.getVariant().addTag(Tags.SHIP_LIMITED_TOOLTIP);
        flagship.getVariant().addTag(Tags.SHIP_RECOVERABLE);

        FleetMemberAPI doom_ = fleet.getFleetData().addFleetMember("Doom7s_Support");
        doom_.setCaptain(Alpha_core);
        doom_.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);
        FleetMemberAPI doom__ = fleet.getFleetData().addFleetMember("Doom7s_Support");
        doom__.setCaptain(Alpha_core);
        doom__.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);

        FleetMemberAPI afflictor_ = fleet.getFleetData().addFleetMember("Afflictor7s_Strike");
        afflictor_.setCaptain(Alpha_core);
        afflictor_.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);
        FleetMemberAPI afflictor__ = fleet.getFleetData().addFleetMember("Afflictor7s_Strike");
        afflictor__.setCaptain(Alpha_core);
        afflictor__.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);
        FleetMemberAPI afflictor___ = fleet.getFleetData().addFleetMember("Afflictor7s_Strike");
        afflictor___.setCaptain(Alpha_core);
        afflictor___.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);
        FleetMemberAPI afflictor____ = fleet.getFleetData().addFleetMember("Afflictor7s_Strike");
        afflictor____.setCaptain(Alpha_core);
        afflictor____.getVariant().addTag(Tags.AUTOMATED_RECOVERABLE);


        Vector2f loc = new Vector2f(destroyed_nexus7s.getLocation().x + 300 * ((float) Math.random() - 0.5f),
                destroyed_nexus7s.getLocation().y + 300 * ((float) Math.random() - 0.5f));
        fleet.setLocation(loc.x, loc.y);
        destroyed_nexus7s.getContainingLocation().addEntity(fleet);

    }
    private void addDerelict (StarSystemAPI system, SectorEntityToken focus, String variantId, ShipRecoverySpecial.ShipCondition condition,
                              float orbitRadius, boolean recoverable)  {

        DerelictShipEntityPlugin.DerelictShipData params = new DerelictShipEntityPlugin.DerelictShipData(new ShipRecoverySpecial.PerShipData(variantId, condition), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);

        float orbitDays = orbitRadius / (10f + (float) Math.random() * 5f);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, orbitDays);

        if (recoverable) {
            SalvageSpecialAssigner.ShipRecoverySpecialCreator creator = new SalvageSpecialAssigner.ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
    }
}
