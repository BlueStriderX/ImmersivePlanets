package net.dovtech.immersiveplanets;

import org.lwjgl.opengl.GL11;
import org.schema.schine.graphicsengine.texture.Texture;
import org.schema.schine.graphicsengine.texture.TextureLoader;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ResourceUtils {

    public static Texture getTexture(String path) {
        try {
            return TextureLoader.getTexture(ImageIO.read(ImmersivePlanets.class.getResourceAsStream("resources/texture/" + path)), path.substring(path.lastIndexOf("/"), path.indexOf(".") - 1), GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                return TextureLoader.getTexture(ImageIO.read(ImmersivePlanets.class.getResourceAsStream("resources/texture/debug-texture.png")),"debug-texture", GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
