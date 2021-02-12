package old.universe;

import com.bulletphysics.linearmath.Transform;
import net.dovtech.immersiveplanets.ResourceUtils;
import net.dovtech.immersiveplanets.data.shape.Sphere;
import org.lwjgl.opengl.GL11;
import org.schema.common.FastMath;
import org.schema.common.util.linAlg.TransformTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameStateInterface;
import org.schema.game.common.data.world.space.PlanetCore;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.network.objects.Sendable;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import java.util.Map;
import java.util.Random;

public class Planet extends CelestialBody {

    private float radius;
    private float mass;
    private Color4f atmosphereColor;
    private Moon[] moons;
    private BodyType bodyType;

    public Planet(Vector3i sector, float radius, int moonCount, float mass, Color4f atmosphereColor) {
        super(sector);
        this.radius = radius;
        this.shape = new Sphere(radius);
        this.mass = mass;
        this.atmosphereColor = atmosphereColor;
        generatePlanet();
        if(moonCount > 0) {
            generateMoons(moonCount);
        }
    }

    private void generatePlanet() {
        Random random = new Random();
        bodyType = BodyType.getFromID(random.nextInt(5));
    }

    private void generateMoons(int moonCount) {
        Random random = new Random();
        moons = new Moon[moonCount];
        for(int m = 0; m < moonCount; m ++) {
            BodyType moonType = BodyType.getFromID(random.nextInt(14 - 9) + 9);
            Moon moon = new Moon(sector, moonType, this);
            moons[m] = moon;
        }
    }

    @Override
    public void onInit() {
        super.onInit();
        shape.setTexture(ResourceUtils.getTexture(bodyType.getTexturePath()), true);
    }

    @Override
    public void draw() {
        absSectorCenterPos.set(
                relSectorPos.x * ((GameStateInterface) getState()).getSectorSize(),
                relSectorPos.y * ((GameStateInterface) getState()).getSectorSize(),
                relSectorPos.z * ((GameStateInterface) getState()).getSectorSize());

        absSystemPos.set(
                (relSystemPos.x) * ((GameStateInterface) getState()).getSectorSize(),
                (relSystemPos.y) * ((GameStateInterface) getState()).getSectorSize(),
                (relSystemPos.z) * ((GameStateInterface) getState()).getSectorSize());
        trans.setIdentity();
        transR.setIdentity();

        if (drawFromPlanet) {

            trans.setIdentity();
            ; //universe always in the middle. If not put origin here
            Matrix3f rot = new Matrix3f();
            rot.rotX((FastMath.PI * 2) * year);

            Vector3f bb = new Vector3f();

            //we are in a universe sector
            //-> rotate everything around us
            rot.invert();
            bb.set(trans.origin);
            bb.add(absSectorCenterPos);
            TransformTools.rotateAroundPoint(bb, rot, trans, new Transform());
            trans.origin.add(absSectorCenterPos);

            //do universe self rotation
            transR.basis.rotX((FastMath.PI * 2) * year);

        } else {
            trans.basis.rotX((FastMath.PI * 2) * year);
            trans.origin.set(absSectorCenterPos);
        }

        if (!Controller.getCamera().isBoundingSphereInFrustrum(trans.origin, dodecahedron.radius + 50)) {
            culled++;
            return;
        }
        GlUtil.glPushMatrix();
        GlUtil.glMultMatrix(trans);
        GlUtil.glMultMatrix(transR);

        float scale = atmosphereSize;
        float atmosScale = atmosphereSize;

        if (absSecPos != null) {
            for (Map.Entry<String, Sendable> e : state.getLocalAndRemoteObjectContainer().getUidObjectMap().entrySet()) {
                if (e.getValue() instanceof PlanetCore) {
                    PlanetCore core = (PlanetCore) e.getValue();
                    if ((core.getUniqueIdentifier()).contains(absSecPos.x + "_" + absSecPos.y + "_" + absSecPos.z)) {
                        scale = core.getRadius() / 200.0F;
                        atmosScale = core.getRadius() / 325.0F + 25.0F / 325.0F;
                        break;
                    }
                }
            }
        }

        GlUtil.scaleModelview(scale, scale, scale);

        if (relSectorPos.equals(0, 0, 0)) {

            //activate blend if universe in our sector
            GlUtil.glEnable(GL11.GL_BLEND);
            GlUtil.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GlUtil.glDisable(GL11.GL_BLEND);
        }

        if (!relSectorPos.equals(0, 0, 0)) {
            ShaderLibrary.planetShader.setShaderInterface(planetShaderable);
            ShaderLibrary.planetShader.load();
            //dodecahedron.draw();
            /*
            final String obamiumTexturePath = "obamium.png";
            final Material obamiumMaterial = new Material();
            Texture obamiumTexture = null;
            try {
                obamiumTexture = Controller.getTexLoader().getTexture2D(obamiumTexturePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            obamiumMaterial.setTextureFile(obamiumTexturePath);
            obamiumMaterial.setTexture(obamiumTexture);
            sphere.setMaterial(obamiumMaterial);
             */
            ShaderLibrary.planetShader.unload();
        }

        sphere.loadVBO(true);
        if (!relSectorPos.equals(0, 0, 0)) {

            GL11.glDepthRange(0.9999999f, 1.0);

            GlUtil.glDisable(GL11.GL_DEPTH_TEST);
            GlUtil.glDepthMask(false);
            GlUtil.glEnable(GL11.GL_BLEND);
            GlUtil.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }

        ShaderLibrary.atmosphereShader.setShaderInterface(atmoShaderable);
        ShaderLibrary.atmosphereShader.load();
        //	GlUtil.glDisable( GL11.GL_DEPTH_TEST );
        //	GlUtil.glDepthMask( false );

        GlUtil.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        //	GL11.glScalef(1.1f, 1.1f, 1.1f);
        //	GL11.glTranslatef(300.0f, 0f, 0f);
        GL11.glCullFace(GL11.GL_FRONT);

        GlUtil.scaleModelview(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GlUtil.scaleModelview(atmosScale, atmosScale, atmosScale);

//		sphere.renderVBO();
        GL11.glCullFace(GL11.GL_BACK);
        sphere.renderVBO();
        GL11.glCullFace(GL11.GL_BACK);
        ShaderLibrary.atmosphereShader.unload();
        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
        GlUtil.glDisable(GL11.GL_BLEND);
        GlUtil.glEnable(GL11.GL_CULL_FACE);
        GlUtil.glPopMatrix();
        GL11.glDepthRange(0.0, 1.0);
        GlUtil.glDepthMask(true);
        sphere.unloadVBO(true);
    }
}
