package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

import data.scripts.world.systems.Mayawati_sector;
import data.scripts.world.systems.Tempestas_sector;

public class Gensevens_tempestas implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {

        new Tempestas_sector().generate(sector);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("passive_aggressive_tempestas");

        FactionAPI passive_aggressive_tempestas = sector.getFaction("passive_aggressive_tempestas");
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

        passive_aggressive_tempestas.setRelationship(hegemony.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(player.getId(), -0.75f);
        passive_aggressive_tempestas.setRelationship(pirates.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(independent.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(tritachyon.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(kol.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(path.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(church.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(persean.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(guard.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(diktat.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(remnant.getId(), -1f);
        passive_aggressive_tempestas.setRelationship(derelict.getId(), -1f);



    }

}