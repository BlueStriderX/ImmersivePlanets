package net.dovtech.immersiveplanets.commands;

import api.DebugFile;
import api.universe.StarUniverse;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.ChatCommand;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import net.dovtech.immersiveplanets.planet.BodyType;
import org.schema.game.client.view.planetdrawer.PlanetDrawer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SectorInformation;

public class DebugCreateGasGiantCommand extends ChatCommand {

    public DebugCreateGasGiantCommand() {
        super("spawn_gas_giant", "/spawn_gas_giant", "Creates a random Gas Giant from the planet in your sector.", true);
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        try {
            if (StarUniverse.getUniverse().getSector(sender.getCurrentSector()).getInternalSector().getSectorType().equals(SectorInformation.SectorType.PLANET)) {
                PlanetDrawer.getInstance().sector = sender.getCurrentSector();
                PlanetDrawer.getInstance().bodyType = BodyType.GAS_GIANT_ORANGE;
                PlanetDrawer.getInstance().onInit();
                PlanetDrawer.getInstance().drawFromPlanet = true;
                PlanetDrawer.getInstance().drawGasGiant();
                if (ImmersivePlanets.getInstance().debugMode) DebugFile.log("[DEBUG] Spawned Gas Giant at " + sender.getCurrentSector().toString());
            } else {
                PlayerUtils.sendMessage(sender, "[ERROR] You must be in a planet sector to do this!");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
