package thederpgamer.immersiveplanets.resources.textures;

import thederpgamer.immersiveplanets.graphics.planet.PlanetSprite;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import java.util.HashMap;

/**
 * TextureLoader.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class TextureLoader {

    public final static int[] planetTextureResolutions = {2048, 1024, 512, 256, 128};
    public final static HashMap<WorldType, PlanetSprite> planetSprites = new HashMap<>();

    public static void initialize() {
        for(WorldType worldType : WorldType.values()) planetSprites.put(worldType, new PlanetSprite(worldType));
    }
}