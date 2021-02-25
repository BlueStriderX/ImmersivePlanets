package thederpgamer.immersiveplanets.graphics.model;

import api.common.GameClient;
import api.utils.StarRunnable;
import org.lwjgl.input.Keyboard;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.texture.Material;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.world.WorldData;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.graphics.texture.TextureLoader;
import thederpgamer.immersiveplanets.graphics.universe.WorldDrawMode;

/**
 * WorldDrawData.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class WorldDrawData implements Drawable {

    private Vector3i sector;
    private float radius;
    private long worldId;

    private boolean updaterStarted;
    private WorldDrawMode drawMode;
    private Mesh atmosphereLayer;
    private Mesh cloudLayer;
    private Mesh atmosphereSimple;
    private Material atmosphereTexture;
    private Material cloudTexture;
    private Sprite[] sprites;

    private boolean debugMode = ImmersivePlanets.getInstance().debugMode;
    private BoundingSphere debugOuterSphere;
    private BoundingSphere debugInnerSphere;

    public WorldDrawData(WorldData worldData) {
        radius = worldData.getRadius();
        worldId = worldData.getWorldId();
        sector = worldData.getSector();

        atmosphereLayer = Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_sphere");
        cloudLayer = Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_sphere");
        atmosphereSimple = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();

        atmosphereTexture = TextureLoader.getSprite(worldData.getWorldType().toString().toLowerCase() + "_atmosphere").getMaterial();
        cloudTexture = TextureLoader.getSprite(worldData.getWorldType().toString().toLowerCase() + "_clouds").getMaterial();

        debugOuterSphere = new BoundingSphere(radius);
        debugInnerSphere = new BoundingSphere(radius * 0.95f);

        sprites = new Sprite[] {
                TextureLoader.getSprite(worldData.getWorldType().toString().toLowerCase() + "_64"),
                TextureLoader.getSprite(worldData.getWorldType().toString().toLowerCase() + "_256"),
                TextureLoader.getSprite(worldData.getWorldType().toString().toLowerCase() + "_512")
        };
    }

    @Override
    public void onInit() {
        atmosphereLayer.setMaterial(atmosphereTexture);
        cloudLayer.setMaterial(cloudTexture);
        debugOuterSphere.onInit();
        debugInnerSphere.onInit();
        updaterStarted = false;
    }

    @Override
    public void draw() {
        if(!updaterStarted) startDrawUpdater();
        if(drawMode.equals(WorldDrawMode.NONE)) {
            cleanUp();
        } else if(drawMode.equals(WorldDrawMode.SPHERE_FULL)) {
            cleanupSprites();
            if(drawDebugSpheres()) {
                cleanupSpheres();
                debugOuterSphere.draw();
                debugInnerSphere.draw();
            } else {
                cleanupDebug();
                atmosphereLayer.draw();
                cloudLayer.draw();
            }
        } else if(drawMode.equals(WorldDrawMode.SPHERE_HALF)) {
            cleanupSprites();
            atmosphereLayer.cleanUp();
            cloudLayer.cleanUp();
            atmosphereSimple.draw();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_512)) {
            cleanupDebug();
            cleanupSpheres();
            sprites[0].cleanUp();
            sprites[1].cleanUp();
            sprites[2].draw();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_256)) {
            cleanupDebug();
            cleanupSpheres();
            sprites[0].cleanUp();
            sprites[1].draw();
            sprites[2].cleanUp();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_64)) {
            cleanupDebug();
            cleanupSpheres();
            sprites[0].draw();
            sprites[1].cleanUp();
            sprites[2].cleanUp();
        }
    }

    @Override
    public void cleanUp() {
        cleanupDebug();
        cleanupSpheres();
        cleanupSprites();
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    public WorldDrawMode getDrawMode() {
        updateDraw();
        return drawMode;
    }

    public Vector3i getSector() {
        return sector;
    }

    private void updateDraw() {
        drawMode = WorldDrawMode.getFromDistance(getDistanceFromPlayer());
    }

    private float getDistanceFromPlayer() {
        return Math.abs(Vector3i.getDisatance(GameClient.getClientPlayerState().getCurrentSector(), sector));
    }

    private void startDrawUpdater() {
        updaterStarted = true;
        new StarRunnable() {
            @Override
            public void run() {
                updateDraw();
            }
        }.runTimer(ImmersivePlanets.getInstance(), 300);
    }

    private boolean drawDebugSpheres() {
        return drawMode.equals(WorldDrawMode.SPHERE_FULL) && debugMode && Keyboard.isKeyDown(Keyboard.KEY_INSERT);
    }

    private void cleanupDebug() {
        debugOuterSphere.cleanUp();
        debugInnerSphere.cleanUp();
    }

    private void cleanupSpheres() {
        atmosphereLayer.cleanUp();
        cloudLayer.cleanUp();
        atmosphereSimple.cleanUp();
    }

    private void cleanupSprites() {
        for(Sprite sprite : sprites) sprite.cleanUp();
    }

    public float getRadius() {
        return radius;
    }

    public long getWorldId() {
        return worldId;
    }
}
