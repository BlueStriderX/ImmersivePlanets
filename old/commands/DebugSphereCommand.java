package old.commands;

import api.utils.game.chat.ChatCommand;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import org.schema.game.common.data.player.PlayerState;

public class DebugSphereCommand extends ChatCommand {

    public DebugSphereCommand() {
        super("draw_debug_spheres", "/draw_debug_spheres", "Toggles debug bounding spheres for universe atmospheres.", true);
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        ImmersivePlanets.getInstance().drawDebugSpheres = !ImmersivePlanets.getInstance().drawDebugSpheres;
        return true;
    }
}