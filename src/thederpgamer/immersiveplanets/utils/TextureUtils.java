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

    public enum PlanetTextureResolution {
            RES_2048(0),
            RES_1024(1),
            RES_512(2),
            RES_256(3),
            RES_128(4);

            public int level;

            PlanetTextureResolution(int level) {
                this.level = level;
            }

            @Override
            public String toString() {
                return super.toString().toLowerCase().split("_")[1];
            }

            public int getRes() {
                return Integer.parseInt(toString());
            }
    }

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