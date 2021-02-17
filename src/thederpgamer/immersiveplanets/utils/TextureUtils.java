package thederpgamer.immersiveplanets.utils;

import api.common.GameClient;
import api.utils.other.HashList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import java.util.ArrayList;

/**
 * TextureUtils.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class TextureUtils {

    public enum PlanetTextureResolution {
            RES_2048(0);
            //RES_1024(1),
            //RES_512(2),
            //RES_256(3),
            //RES_128(4);

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

    public final static HashList<WorldType, Sprite> planetTextures = new HashList<>();

    public static Sprite getPlanetTexture(WorldType worldType, int level) {
        return planetTextures.getList(worldType).get(level);
    }

    public static ArrayList<Sprite> getPlanetTextureSet(WorldType worldType) {
        return planetTextures.getList(worldType);
    }

    public static HashList<WorldType, Sprite> getAllPlanetTextures() {
        return planetTextures;
    }

    public static int getCurrentLevel(Planet planet) {
        float distance = Math.abs(Vector3i.getDisatance(planet.planetSector, GameClient.getClientPlayerState().getCurrentSector()));
        if(distance >= 5) {
            return -1;
        } else if(distance >= 4) {
            return 4;
        } else if(distance >= 3) {
            return 3;
        } else if(distance >= 2) {
            return 2;
        } else if(distance >= 1) {
            return 1;
        } else {
            return 0;
        }
    }
}