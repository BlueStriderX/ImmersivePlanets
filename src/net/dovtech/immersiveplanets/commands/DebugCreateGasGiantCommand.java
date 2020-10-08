package net.dovtech.immersiveplanets.commands;

import api.DebugFile;
import api.utils.game.chat.ChatCommand;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import org.schema.game.common.data.player.PlayerState;
import javax.vecmath.Vector4f;

public class DebugCreateGasGiantCommand extends ChatCommand {

    public DebugCreateGasGiantCommand() {
        super("spawnGasGiant", "/spawnGasGiant <textureID> <radius> <ringCount> <moonCount>", "Creates a Gas Giant in the sector next to you.", true);
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        if(args.length == 4) {
            try {
                String textureID = args[0];
                int radius = Integer.parseInt(args[1]);
                int ringCount = Integer.parseInt(args[2]);
                int moonCount = Integer.parseInt(args[3]);
                Vector4f fogColor = new Vector4f(204, 140, 88, 65);
                ImmersivePlanets.getInstance().gasGiant.create(textureID, radius, ringCount, moonCount, fogColor, sender.getCurrentSector());
                if(ImmersivePlanets.getInstance().debugMode) DebugFile.log("[DEBUG] Spawned Gas Giant at " + sender.getCurrentSector().toString());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
