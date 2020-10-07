package net.dovtech.immersiveplanets.commands;

import api.utils.game.chat.ChatCommand;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import org.schema.game.common.data.player.PlayerState;

public class DebugSphereCommand extends ChatCommand {

    public DebugSphereCommand() {
        super("debugSpheres", "/debugSpheres", "Toggles debug bounding spheres for planet atmospheres.", true);
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        ImmersivePlanets.getInstance().drawDebugSpheres = !ImmersivePlanets.getInstance().drawDebugSpheres;
        return true;
    }
}