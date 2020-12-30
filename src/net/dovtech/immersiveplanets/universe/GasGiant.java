package net.dovtech.immersiveplanets.universe;

import api.common.GameClient;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import net.dovtech.immersiveplanets.ResourceUtils;
import net.dovtech.immersiveplanets.data.shape.BoundingSphere;
import net.dovtech.immersiveplanets.universe.resources.GasResource;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.texture.Texture;
import javax.vecmath.Color4f;

public class GasGiant extends CelestialBody {

    private BoundingSphere outerAtmosphere;
    private BoundingSphere innerAtmosphere;
    public Mesh gasGiantAtmosphere;
    public Mesh gasGiantCoreSphere;
    private Vector3i sector;
    private BodyType type;
    private int radius;
    private float density;
    private float mass;
    private Color4f atmosphereColor;
    private GasResource[] resources;
    private boolean hasGravity;
    private Moon[] moons;
    private CelestialRing rings[];
    private boolean init;
    public float outerScale;
    public float innerScale;

    public GasGiant(Vector3i sector, BodyType type, int radius, int moonCount, int ringCount, float density, float mass, Color4f atmosphereColor) {
        if(!type.name().toLowerCase().contains("gas")) {
            throw new IllegalArgumentException();
        } else {
            this.type = type;
        }
        this.sector = sector;
        this.radius = radius;
        this.density = density;
        this.mass = mass;
        this.atmosphereColor = atmosphereColor;

        this.outerScale = radius / 100f;
        this.innerScale = outerScale * 0.65f;
        this.gasGiantAtmosphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();
        this.gasGiantAtmosphere.setScale(outerScale, outerScale, outerScale);
        this.gasGiantCoreSphere = (Mesh) Controller.getResLoader().getMesh("LowPolySphere").getChilds().iterator().next();
        this.gasGiantCoreSphere.setScale(innerScale, innerScale, innerScale);
        this.outerAtmosphere = new BoundingSphere(radius);
        this.innerAtmosphere = new BoundingSphere(radius * 0.65f);
        this.init = false;
    }

    public void drawDebugSpheres() {
        if(ImmersivePlanets.getInstance().debugMode && ImmersivePlanets.getInstance().drawDebugSpheres && GameClient.getClientPlayerState().getCurrentSector().equals(sector)) {
            outerAtmosphere.draw();
            innerAtmosphere.draw();
        } else {
            outerAtmosphere.cleanUp();
            innerAtmosphere.cleanUp();
        }
    }

    public Vector3i getSector() {
        return sector;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public void setGravity(boolean hasGravity) {
        this.hasGravity = hasGravity;
    }

    @Override
    public void onInit() {
        try {
            GlUtil.glEnable(GL11.GL_TEXTURE_2D);
            Texture texture = ResourceUtils.getTexture(type.getTexturePath());
            gasGiantAtmosphere.getMaterial().setTexture(texture);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            gasGiantAtmosphere.setPos(0, 0, 0);

            gasGiantCoreSphere.setVisibility(2);
            gasGiantCoreSphere.setCollisionObject(true);

            outerAtmosphere.onInit();
            innerAtmosphere.onInit();

            init = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw() {
        if(!init) {
            onInit();
        } else {
            outerAtmosphere.draw();
            drawDebugSpheres();
        }
    }
}
