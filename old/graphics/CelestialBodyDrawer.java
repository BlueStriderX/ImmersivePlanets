/*
CelestialBodyDrawer.java
(Modified version of PlanetDrawer.java)
 */

package old.graphics;

import java.util.Map;
import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.dovtech.immersiveplanets.DataUtils;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import net.dovtech.immersiveplanets.universe.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.schema.common.FastMath;
import org.schema.common.util.linAlg.TransformTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.data.GameStateInterface;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.world.SectorInformation.PlanetType;
import org.schema.game.common.data.world.space.PlanetCore;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.ShaderLibrary;
import org.schema.schine.graphicsengine.shader.Shaderable;
import org.schema.schine.network.objects.Sendable;

import com.bulletphysics.linearmath.Transform;

public class CelestialBodyDrawer implements Drawable {

    private static final Vector4f diffuse = new Vector4f(1, 1, 1, 1);
    public static int culled;
    private final GameClientState state;
    public float year;
    public Vector3i relSystemPos = new Vector3i();
    public boolean drawFromPlanet;
    public Vector3i absSecPos;
    //	private Mesh planetMesh;
    private Dodecahedron dodecahedron;
    private PlanetShaderable planetShaderable;
    private AtmoShaderable atmoShaderable;
    private PlanetInformations infos;
    private float planetTime;
    private Vector3i relSectorPos;
    private Vector3f absSectorCenterPos = new Vector3f();
    private Vector3f absSystemPos = new Vector3f();
    private PlanetType type;
    private PlanetInformations[] infoBase;
    private Transform trans = new Transform();
    private Transform transR = new Transform();
    private Mesh sphere;
    private float atmosphereSize = 0.0F;

    //New
    private Vector3i sector;
    private CelestialBody celestialBody;
    //

    //Modified
    public CelestialBodyDrawer(GameClientState state) {
        trans.setIdentity();
        this.state = state;
        /*
        infoBase = new PlanetInformations[PlanetType.values().length];
        for (int i = 0; i < infoBase.length; i++) {
            infoBase[i] = new PlanetInformations();
            infoBase[i].getAtmosphereColor().set(PlanetType.values()[i].atmosphere);
        }
        new PlanetInformations();
         */
    }
    //

    //Modified
    @Override
    public void cleanUp() {
        if(celestialBody != null) {
            celestialBody.cleanUp();
        }
    }
    //

    //Modified
    @Override
    public void draw() {
        if(celestialBody instanceof Planet) {
            Planet planet = (Planet) celestialBody;
            planet.draw();
        } else if(celestialBody instanceof GasGiant) {
            GasGiant gasGiant = (GasGiant) celestialBody;
            gasGiant.draw();
        } else if(celestialBody instanceof Moon) {
            Moon moon = (Moon) celestialBody;
            moon.draw();
        } else if(celestialBody instanceof CelestialRing) {
            CelestialRing ring = (CelestialRing) celestialBody;
            ring.draw();
        }
    }
    //

    //Modified
    public void drawGasGiant() {
        if(dodecahedron != null) dodecahedron.cleanUp();

        GasGiant gasGiant = DataUtils.gasGiants.get(sector);
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

        GlUtil.scaleModelview(gasGiant.outerScale, gasGiant.outerScale, gasGiant.outerScale);

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

        gasGiant.gasGiantAtmosphere.loadVBO(true);
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

        GlUtil.scaleModelview(1.0F / gasGiant.outerScale, 1.0F / gasGiant.outerScale, 1.0F / gasGiant.outerScale);
        GlUtil.scaleModelview(gasGiant.outerScale, gasGiant.outerScale, gasGiant.outerScale);

//		sphere.renderVBO();
        GL11.glCullFace(GL11.GL_BACK);
        gasGiant.gasGiantAtmosphere.renderVBO();
        GL11.glCullFace(GL11.GL_BACK);
        ShaderLibrary.atmosphereShader.unload();
        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
        GlUtil.glDisable(GL11.GL_BLEND);
        GlUtil.glEnable(GL11.GL_CULL_FACE);
        GlUtil.glPopMatrix();
        GL11.glDepthRange(0.0, 1.0);
        GlUtil.glDepthMask(true);
        gasGiant.gasGiantAtmosphere.unloadVBO(true);
        gasGiant.drawDebugSpheres();
        //DataUtils.gasGiants.get(sector).draw();
    }
    //

    public void drawPlanet() {
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

    public int getCloudMapId() {
        return Controller.getResLoader().getSprite(type.clouds).getMaterial().getTexture().getTextureId();
    }

    public int getDiffMapId() {
        return Controller.getResLoader().getSprite(type.diff).getMaterial().getTexture().getTextureId();
    }

    public int getNormMapId() {
        return Controller.getResLoader().getSprite(type.normal).getMaterial().getTexture().getTextureId();
    }

    public int getSpecularMapId() {
        return Controller.getResLoader().getSprite(type.specular).getMaterial().getTexture().getTextureId();
    }

    //Modified
    @Override
    public void onInit() {
        if(celestialBody instanceof Planet) {
            Planet planet = (Planet) celestialBody;
            planet.onInit();

        } else if(celestialBody instanceof GasGiant) {
            GasGiant gasGiant = (GasGiant) celestialBody;
            gasGiant.onInit();
            /*
            Random random = new Random();

            int radius = random.nextInt(ImmersivePlanets.getInstance().gasGiantMaxRadius = ImmersivePlanets.getInstance().gasGiantMinRadius) + ImmersivePlanets.getInstance().gasGiantMinRadius;

            int ringCount = 0;
            int ringTurns = 0;
            int ringsRandom = random.nextInt(ImmersivePlanets.getInstance().gasGiantMaxRings);
            while (ringTurns <= ImmersivePlanets.getInstance().gasGiantMaxRings) {
                if (ImmersivePlanets.getInstance().gasGiantRingGenerationChance <= (float) ringsRandom / 100) {
                    ringCount++;
                }
                ringTurns++;
            }

            int moonCount = 0;
            int moonTurns = 0;
            int moonRandom = random.nextInt(ImmersivePlanets.getInstance().gasGiantMaxMoons);
            while (moonTurns <= ImmersivePlanets.getInstance().gasGiantMaxMoons) {
                if (ImmersivePlanets.getInstance().gasGiantMoonGenerationChance <= (float) moonRandom / 100) {
                    moonCount++;
                }
                moonTurns++;
            }



            gasGiantAtmosphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();
            gasGiantAtmosphere.setScale(outerScale, outerScale, outerScale);

            gasGiantCoreSphere = (Mesh) Controller.getResLoader().getMesh("LowPolySphere").getChilds().iterator().next();
            gasGiantCoreSphere.setScale(innerScale, innerScale, innerScale);



            //Test Values
            Color4f atmosphereColor = new Color4f(0.204f, 0.140f, 0.88f, 1.0f);
            float density = 4.5f;
            float mass = radius * 100;
            //

            GasGiant gasGiant = new GasGiant(sector, bodyType, radius, moonCount, ringCount, density, mass, atmosphereColor);
            DataUtils.gasGiants.put(sector, gasGiant);
            DataUtils.gasGiants.get(sector).onInit();

            planetShaderable = new PlanetShaderable();
            atmoShaderable = new AtmoShaderable();

             */
        } else if(celestialBody instanceof Moon) {
            Moon moon = (Moon) celestialBody;
            moon.onInit();
        } else if(celestialBody instanceof CelestialRing) {
            CelestialRing ring = (CelestialRing) celestialBody;
            ring.onInit();
        }
            /*
            sphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();
            dodecahedron = new Dodecahedron(200);
            dodecahedron.create();
            //		planetMesh = (Mesh) Controller.getResLoader().getMesh("Planet").getChilds().iterator().next();
            planetShaderable = new PlanetShaderable();
            atmoShaderable = new AtmoShaderable();

            float expected = state.getGameState().getPlanetSizeMean();
            float deviation = state.getGameState().getPlanetSizeDeviation();
            atmosphereSize = (expected + deviation) / 275.0F; // divide maximum possible by maximum default (275);

             */
    }
    //

    //Modified
    public Vector3i getSector() {
        return sector;
    }

    public void setSector(Vector3i sector) {
        this.sector = sector;
    }
    //

    public void setPlanetSectorPos(Vector3i currentSector) {
        this.relSectorPos = (currentSector);

    }

    public void setPlanetType(PlanetType planetType) {
        this.type = planetType;
    }

    public void update(Timer timer) {
        planetTime += timer.getDelta();

    }

    /**
     * @return the state
     */
    public GameClientState getState() {
        return state;
    }

    private class AtmoShaderable implements Shaderable {

        private Vector4f lastColor = new Vector4f(-1, 0, 0, 0);

        @Override
        public void onExit() {

        }

        @Override
        public void updateShader(DrawableScene scene) {

        }

        @Override
        public void updateShaderParameters(Shader shader) {

            //GlUtil.updateShaderVector3f( shader, "fvLightPosition", AbstractScene.mainLight.getPos());
            //		GlUtil.updateShaderVector3f( shader, "fvViewPosition", Controller.camera.getPosition().getVector3f());
            if (lastColor.x < 0) {
                GlUtil.updateShaderVector4f(shader, "fvDiffuse", diffuse);
            }
            //The light which comes on the atmosphere
            if (!infos.getAtmosphereColor().equals(lastColor)) {
                GlUtil.updateShaderColor4f(shader, "fvAtmoColor", infos.getAtmosphereColor());
            } else {
                lastColor.set(infos.getAtmosphereColor());
            }

            GlUtil.updateShaderFloat(shader, "fCloudHeight", infos.getCloudHeight());
            GlUtil.updateShaderFloat(shader, "fAbsPower", infos.getAtmosphereAbsorptionPower());
            GlUtil.updateShaderFloat(shader, "fAtmoDensity", infos.getAtmosphereDensity());
            GlUtil.updateShaderFloat(shader, "fGlowPower", infos.getAtmosphereGlowPower());
            GlUtil.updateShaderFloat(shader, "density", (1.5f / Controller.vis.getVisLen()));
            if (relSectorPos.equals(0, 0, 0)) {
                GlUtil.updateShaderFloat(shader, "dist", Controller.getCamera().getPos().length());
            } else {
                GlUtil.updateShaderFloat(shader, "dist", relSectorPos.length());
            }
        }
    }

    private class PlanetShaderable implements Shaderable {

        @Override
        public void onExit() {
            //		baseTex.unbindFromIndex( );
            //		normTex.unbindFromIndex( );
            //		specTex.unbindFromIndex( );
            //
            //		if(infos.hasCloud()) {
            //			Controller.getResLoader().getSprite("clouds").getMaterial().getTexture().unbindFromIndex( );
            //		} else {
            //			//pointer to noTextures
            //			Controller.getResLoader().getSprite("clouds").getMaterial().getTexture().unbindFromIndex( );
            //		}

            GlUtil.glActiveTexture(GL13.GL_TEXTURE0);
            GlUtil.glDisable(GL11.GL_TEXTURE_2D);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE1);
            GlUtil.glDisable(GL11.GL_TEXTURE_2D);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE2);
            GlUtil.glDisable(GL11.GL_TEXTURE_2D);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE3);
            GlUtil.glDisable(GL11.GL_TEXTURE_2D);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE4);
            GlUtil.glDisable(GL11.GL_TEXTURE_2D);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE0);

        }

        @Override
        public void updateShader(DrawableScene scene) {

        }

        @Override
        public void updateShaderParameters(Shader shader) {

            //GlUtil.updateShaderVector3f( shader, "fvViewPosition", Controller.camera.getPosition().getVector3f());
            //GlUtil.updateShaderVector3f( shader, "fvLightPosition", AbstractScene.mainLight.getPos());

            GlUtil.updateShaderFloat(shader, "fCloudRotation", planetTime * 0.005f);

            GlUtil.updateShaderVector4f(shader, "fvSpecular", 1, 1, 1, 1);
            GlUtil.updateShaderVector4f(shader, "fvDiffuse", 1, 1, 1, 1);

            GlUtil.updateShaderFloat(shader, "fSpecularPower", 20.0f);
            GlUtil.updateShaderFloat(shader, "fCloudHeight", infos.getCloudHeight());
            GlUtil.updateShaderFloat(shader, "density", 1.5f);

            if (relSectorPos.equals(0, 0, 0)) {
                GlUtil.updateShaderFloat(shader, "dist", Controller.getCamera().getPos().length());
            } else {
                GlUtil.updateShaderFloat(shader, "dist", -1);
            }

            GlUtil.glEnable(GL11.GL_TEXTURE_2D);

            GlUtil.glActiveTexture(GL13.GL_TEXTURE0);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, getDiffMapId());

            GlUtil.glActiveTexture(GL13.GL_TEXTURE1);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, getNormMapId());

            GlUtil.glActiveTexture(GL13.GL_TEXTURE2);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, getSpecularMapId());

            GlUtil.glActiveTexture(GL13.GL_TEXTURE3);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, getCloudMapId());

            GlUtil.glActiveTexture(GL13.GL_TEXTURE4);

            GlUtil.updateShaderInt(shader, "baseMap", 0);
            GlUtil.updateShaderInt(shader, "normalMap", 1);
            GlUtil.updateShaderInt(shader, "specMap", 2);
            GlUtil.updateShaderInt(shader, "cloudsMap", 3);

            //		Controller.getResLoader().getSprite("clouds").getMaterial().getTexture().bindOnShader(  GL13.GL_TEXTURE0, 0, "cloudsMap", shaderprogram);
            //		baseTex.bindOnShader(  GL13.GL_TEXTURE1, 1, "baseMap", shaderprogram);
            //		normTex.bindOnShader(  GL13.GL_TEXTURE2, 2, "normalMap", shaderprogram);
            //		specTex.bindOnShader(  GL13.GL_TEXTURE3, 3, "specMap", shaderprogram);

            //		if(infos.hasCloud()) {
            //
            //		} else {
            //			//pointer to noTextures
            //			Controller.getResLoader().getSprite("clouds").getMaterial().getTexture().bindOnShader(  GL11.GL_TEXTURE4, 4, "cloudsMap", shaderprogram);
            //		}

        }
    }

    @Override
    public boolean isInvisible() {
        // TODO Auto-generated method stub
        return false;
    }

}