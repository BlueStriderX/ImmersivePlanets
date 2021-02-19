package thederpgamer.immersiveplanets.graphics.planet;

import api.common.GameClient;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import javax.vecmath.Vector4f;

/**
 * PlanetDrawData.java
 * <Description>
 * ==================================================
 * Created 02/19/2021
 * @author TheDerpGamer
 */
public class PlanetDrawData implements Drawable {

    public static final int MODE_NONE = 0;
    public static final int MODE_SPRITE = 1;
    public static final int MODE_SPHERE = 2;
    public static final int MODE_SPHERE_DEBUG = 3;

    public int drawMode;

    public Vector3i sector;
    public int radius;
    public WorldType type;

    private Mesh atmosphereMesh;
    private BoundingSphere outerSphere;
    private BoundingSphere innerSphere;

    public PlanetDrawData(Planet planet) {
        sector = planet.planetSector;
        radius = planet.radius;
        type = planet.worldType;

        atmosphereMesh = ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_debug_0");
        (outerSphere = new BoundingSphere(radius)).setColor(new Vector4f(0.75f, 0.75f, 0.75f, 0.3f));
        (innerSphere = new BoundingSphere(radius * 0.9f)).setColor(new Vector4f(0.35f, 0.35f, 0.35f, 0.5f));
        onInit();
    }

    @Override
    public void cleanUp() {
        atmosphereMesh.cleanUp();
        outerSphere.cleanUp();
        innerSphere.cleanUp();
    }

    @Override
    public void draw() {
        if(drawMode == MODE_NONE) {
            cleanUp();
        } else if(drawMode == MODE_SPRITE) {
            //Todo: Draw sprite
            atmosphereMesh.cleanUp();
        } else if(drawMode == MODE_SPHERE) {
            //Todo: Cleanup sprite
            atmosphereMesh.draw();
            innerSphere.cleanUp();
            outerSphere.cleanUp();
        } else if(drawMode == MODE_SPHERE_DEBUG) {
            //Todo: Cleanup sprite
            atmosphereMesh.draw();
            innerSphere.draw();
            outerSphere.draw();
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onInit() {
        atmosphereMesh.onInit();
        outerSphere.onInit();
        innerSphere.onInit();
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
