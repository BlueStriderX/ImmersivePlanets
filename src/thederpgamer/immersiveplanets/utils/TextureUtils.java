package thederpgamer.immersiveplanets.utils;

import api.utils.other.HashList;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.graphics.planet.PlanetSprite;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * TextureUtils.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class TextureUtils {

    public final static int[] planetTextureResolutions = {2048, 1024, 512, 256, 128};
    public final static HashMap<WorldType, PlanetSprite> planetSprites = new HashMap<>();
    private final static HashList<WorldType, Sprite> planetTextures = new HashList<>();

    public static void initialize() {
        for(WorldType worldType : WorldType.values()) planetSprites.put(worldType, new PlanetSprite(worldType));
    }

    public static Sprite getPlanetTexture(WorldType worldType, int level) {
        return planetTextures.getList(worldType).get(level);
    }

    public static ArrayList<Sprite> getPlanetTextureSet(WorldType worldType) {
        return planetTextures.getList(worldType);
    }

    public static HashList<WorldType, Sprite> getAllPlanetTextures() {
        return planetTextures;
    }
}