package net.dovtech.immersiveplanets.commands;

import api.entity.StarPlayer;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.ChatCommand;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import org.lwjgl.opengl.GL11;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SectorInformation;
import org.schema.schine.graphicsengine.texture.Texture;
import org.schema.schine.graphicsengine.texture.TextureLoader;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PlanetTextureChangeCommand extends ChatCommand {

    public PlanetTextureChangeCommand() {
        super("changePlanetTexture", "/changePlanetTexture <textureName> [layer]", "Changes the texture of the nearest planet to a specified file. \nSpecify a specific layer to change only that one.", true);
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        StarPlayer player = new StarPlayer(sender);
        if(args.length > 0 && args.length <= 2) {
            try {
                if(player.getSector().getInternalSector().getSectorType().equals(SectorInformation.SectorType.PLANET)) {
                    BufferedImage newImage = ImageIO.read(ImmersivePlanets.class.getResourceAsStream("resources/texture/planet/" + args[0] + ".png"));
                    Texture newTexture = TextureLoader.getTexture(newImage, args[0], GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);

                    if(args.length == 1) {
                        ImmersivePlanets.getInstance().planet.getMaterial().setTexture(newTexture);
                        ImmersivePlanets.getInstance().planet.draw();
                        ImmersivePlanets.getInstance().clouds.cleanUp();
                    } else {
                        if(args[1].equals("0")) {
                            ImmersivePlanets.getInstance().planet.getMaterial().setTexture(newTexture);
                            ImmersivePlanets.getInstance().planet.draw();
                        } else if(args[1].equals("1")) {
                            ImmersivePlanets.getInstance().clouds.getMaterial().setTexture(newTexture);
                            ImmersivePlanets.getInstance().clouds.draw();
                        }
                    }
                } else {
                    PlayerUtils.sendMessage(sender, "[ERROR]: You must be in a sector containing a planet!");
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                PlayerUtils.sendMessage(sender, "[ERROR]: " + args[0] + " is not a valid texture!");
                return true;
            }
        } else {
            return false;
        }
    }
}
