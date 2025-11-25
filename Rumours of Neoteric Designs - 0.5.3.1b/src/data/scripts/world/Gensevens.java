package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

import data.scripts.world.systems.Mayawati_sector;
import data.scripts.world.systems.Tempestas_sector;

public class Gensevens implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        new Mayawati_sector().generate(sector);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("tritachyon_hostile");

        FactionAPI tritachyon_hostile = sector.getFaction("tritachyon_hostile");
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);
        FactionAPI guard = sector.getFaction(Factions.LIONS_GUARD);
        FactionAPI remnant = sector.getFaction(Factions.REMNANTS);
        FactionAPI derelict = sector.getFaction(Factions.DERELICT);

        tritachyon_hostile.setRelationship(hegemony.getId(), -1f);
        tritachyon_hostile.setRelationship(player.getId(), -0.5f);
        tritachyon_hostile.setRelationship(pirates.getId(), -1f);
        tritachyon_hostile.setRelationship(independent.getId(), -1f);
        tritachyon_hostile.setRelationship(tritachyon.getId(), -1f);
        tritachyon_hostile.setRelationship(kol.getId(), -1f);
        tritachyon_hostile.setRelationship(path.getId(), -1f);
        tritachyon_hostile.setRelationship(church.getId(), -1f);
        tritachyon_hostile.setRelationship(persean.getId(), -1f);
        tritachyon_hostile.setRelationship(guard.getId(), -1f);
        tritachyon_hostile.setRelationship(diktat.getId(), -1f);
        tritachyon_hostile.setRelationship(remnant.getId(), -1f);
        tritachyon_hostile.setRelationship(derelict.getId(), -1f);



    }

}