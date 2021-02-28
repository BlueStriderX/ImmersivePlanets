package thederpgamer.immersiveplanets.graphics.model;

import api.common.GameClient;
import api.utils.StarRunnable;
import org.lwjgl.input.Keyboard;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.world.WorldData;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.graphics.universe.WorldDrawMode;
import javax.vecmath.Vector3f;

/**
 * WorldDrawData.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class WorldDrawData implements Drawable {

    private Vector3i sector;
    private Vector3f pos;
    private Vector3f scale;
    private float radius;
    private long worldId;

    private boolean updaterStarted;
    private WorldDrawMode drawMode;
    private Mesh atmosphereLayer;
    private Mesh cloudLayer;
    private Mesh atmosphereSimple;
    private Sprite atmosphereTexture;
    private Sprite cloudTexture;
    private Sprite[] sprites;

    private boolean debugMode = ImmersivePlanets.getInstance().debugMode;
    private BoundingSphere debugOuterSphere;
    private BoundingSphere debugInnerSphere;

    public WorldDrawData(WorldData worldData) {
        radius = worldData.getRadius();
        worldId = worldData.getWorldId();
        sector = worldData.getSector();
        pos = new Vector3f();
        scale = new Vector3f(1, 1, 1);

        atmosphereLayer = Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_sphere");
        cloudLayer = Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_sphere");
        atmosphereSimple = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();

        atmosphereTexture = ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_atmosphere");
        cloudTexture = ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_clouds");

        debugOuterSphere = new BoundingSphere(radius);
        debugInnerSphere = new BoundingSphere(radius * 0.95f);

        sprites = new Sprite[] {
                ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_64"),
                ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_256"),
                ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_512")
        };
    }

    @Override
    public void onInit() {
        atmosphereTexture.onInit();
        cloudTexture.onInit();
        atmosphereLayer.setMaterial(atmosphereTexture.getMaterial());
        cloudLayer.setMaterial(cloudTexture.getMaterial());
        debugOuterSphere.onInit();
        debugInnerSphere.onInit();
        updaterStarted = false;
        updateDraw();
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
                debugOuterSphere.setPosition(pos);
                debugInnerSphere.setPosition(pos);

                debugOuterSphere.draw();
                debugInnerSphere.draw();
            } else {
                cleanupDebug();
                atmosphereLayer.setPos(pos);
                cloudLayer.setPos(pos);

                atmosphereLayer.setScale(scale);
                cloudLayer.setScale(scale.x * 1.05f, scale.y * 1.05f, scale.z * 1.05f);

                atmosphereLayer.draw();
                //cloudLayer.draw();
            }
        } else if(drawMode.equals(WorldDrawMode.SPHERE_HALF)) {
            cleanupSprites();
            atmosphereLayer.cleanUp();
            cloudLayer.cleanUp();

            atmosphereSimple.setPos(pos);
            atmosphereSimple.draw();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_512)) {
            cleanupDebug();
            cleanupSpheres();

            sprites[2].setPos(pos);

            sprites[0].cleanUp();
            sprites[1].cleanUp();
            sprites[2].draw();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_256)) {
            cleanupDebug();
            cleanupSpheres();

            sprites[1].setPos(pos);

            sprites[0].cleanUp();
            sprites[1].draw();
            sprites[2].cleanUp();
        } else if(drawMode.equals(WorldDrawMode.SPRITE_64)) {
            cleanupDebug();
            cleanupSpheres();

            sprites[0].setPos(pos);

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

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
