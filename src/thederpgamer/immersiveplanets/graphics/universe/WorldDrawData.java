package thederpgamer.immersiveplanets.graphics.universe;

import api.common.GameClient;
import api.utils.draw.Updatable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.*;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.world.WorldData;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import javax.vecmath.Vector3f;

/**
 * WorldDrawData.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class WorldDrawData implements Shaderable, Drawable, Updatable {

    private boolean debugMode = ImmersivePlanets.getInstance().debugMode;
    private float time = 0;
    private boolean initialized;

    private Vector3f scale;
    private Vector3f pos;
    private Vector3i sector;

    private Mesh sphere;
    private Sprite texture;

    private BoundingSphere debugOuterSphere;
    private BoundingSphere debugInnerSphere;

    public WorldDrawData(WorldData worldData) {
        initialized = false;
        scale = new Vector3f(1, 1,1);
        pos = new Vector3f();
        sector = worldData.getSector();

        sphere = (Mesh) Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_sphere").getChilds().iterator().next();
        texture = ImmersivePlanets.getInstance().spriteMap.get(worldData.getWorldType().name + "_texture");
        debugOuterSphere = new BoundingSphere(worldData.getRadius());
        debugInnerSphere = new BoundingSphere(worldData.getRadius() * 0.95f);
    }

    @Override
    public void onInit() {
        texture.onInit();
        sphere.setMaterial(texture.getMaterial());
        debugOuterSphere.onInit();
        debugInnerSphere.onInit();
        initialized = true;
    }

    @Override
    public void draw() {
        if(!initialized) onInit();
        GlUtil.glDisable(GL11.GL_CULL_FACE);
        GlUtil.scaleModelview(scale.x, scale.y, scale.z);
        GlUtil.translateModelview(pos);
        if(debugMode && Keyboard.isKeyDown(Keyboard.KEY_INSERT) && GameClient.getClientPlayerState().getCurrentSector().equals(sector)) {
            sphere.cleanUp();
            debugOuterSphere.draw();
            debugInnerSphere.draw();
        } else {
            debugOuterSphere.cleanUp();
            debugInnerSphere.cleanUp();

            ShaderLibrary.lavaShader.setShaderInterface(this);
            ShaderLibrary.lavaShader.load();

            sphere.loadVBO(true);
            sphere.renderVBO();
            sphere.unloadVBO(true);

            ShaderLibrary.lavaShader.unload();
        }
        GlUtil.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public void cleanUp() {
        sphere.cleanUp();
        debugOuterSphere.cleanUp();
        debugInnerSphere.cleanUp();
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void update(Timer timer) {
        time += timer.getDelta() * 10;
    }

    @Override
    public void onExit() {

    }

    @Override
    public void updateShader(DrawableScene drawableScene) {
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public void updateShaderParameters(Shader shader) {
        GlUtil.updateShaderFloat(shader, "time", time);
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getMaterial().getTexture().getTextureId());
        GlUtil.updateShaderInt(shader, "lavaTex", 0);
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
