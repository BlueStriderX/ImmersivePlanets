package thederpgamer.immersiveplanets.graphics.planet;

import api.common.GameClient;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import javax.vecmath.Vector4f;

/**
 * PlanetDrawData.java
 * <Description>
 * ==================================================
 * Created 02/19/2021
 * @author TheDerpGamer
 */
public class PlanetDrawData {

    public static final int MODE_NONE = 0;
    public static final int MODE_SPRITE = 1;
    public static final int MODE_SPHERE = 2;
    public static final int MODE_SPHERE_DEBUG = 3;

    public int drawMode;

    public Vector3i sector;
    public int radius;
    public WorldType type;

    public boolean loaded;

    public Mesh atmosphereMesh;
    //private Mesh fogMesh;
    //private HashMap<Integer, Sprite> spriteMap;
    public BoundingSphere outerSphere;
    public BoundingSphere innerSphere;

    public PlanetDrawData(Planet planet) {
        sector = planet.planetSector;
        radius = planet.radius;
        type = planet.worldType;

        atmosphereMesh = ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_debug_0");
        (outerSphere = new BoundingSphere(radius)).setColor(new Vector4f(0.75f, 0.75f, 0.75f, 0.3f));
        (innerSphere = new BoundingSphere(radius * 0.9f)).setColor(new Vector4f(0.35f, 0.35f, 0.35f, 0.5f));

        atmosphereMesh.setMaterial(TextureUtils.getPlanetTexture(type, 0).getMaterial());
        atmosphereMesh.setType(3);
        outerSphere.onInit();
        innerSphere.onInit();
        loaded = false;
    }

    public void update() {
        float distance = getCurrentDistance();
        if(distance <= 1) {
            if(ImmersivePlanets.getInstance().debugMode) {
                drawMode = MODE_SPHERE_DEBUG;
            } else {
                drawMode = MODE_SPHERE;
            }
        } else if(distance < 5) {
            drawMode = MODE_SPRITE;
        } else {
            drawMode = MODE_NONE;
        }
    }

    private float getCurrentDistance() {
        return Math.abs(Vector3i.getDisatance(GameClient.getClientPlayerState().getCurrentSector(), sector));
    }
}
