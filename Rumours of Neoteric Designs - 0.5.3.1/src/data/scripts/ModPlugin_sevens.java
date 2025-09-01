package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import data.scripts.world.systems.Tempestas_sector;


import data.scripts.world.Gensevens;
import data.scripts.world.Gensevens_tempestas;
import data.scripts.world.systems.Mayawati_sector;

import java.util.ArrayList;

//import exerelin.campaign.SectorManager;
public class ModPlugin_sevens extends BaseModPlugin {

    int merga_count = 0;

    //New game stuff
    @Override
    public void onGameLoad(boolean newGame) {
        if (Global.getSector().getEntityById("Maya_star") == null) { //star id
            new Gensevens().generate(Global.getSector());
        }
        if (Global.getSector().getEntityById("Merga_star") == null) { //star id
            new Gensevens_tempestas().generate(Global.getSector());
        }

    }
    }
