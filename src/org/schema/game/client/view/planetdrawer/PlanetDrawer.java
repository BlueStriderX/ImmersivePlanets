package org.schema.game.client.view.planetdrawer;

import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.PlanetDrawListener;
import com.bulletphysics.linearmath.Transform;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.TransformTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
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
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.planet.DebugPlanet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import thederpgamer.immersiveplanets.utils.TextureUtils;

/**
 * PlanetDrawer.java
 * Planet Draw Handler (modified)
 * ==================================================
 * Modified 02/12/2021
 */
public class PlanetDrawer implements Drawable {
    private static final Vector4f diffuse = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    public static int culled;
    private final GameClientState state;
    public float year;
    public Vector3i relSystemPos = new Vector3i();
    public boolean drawFromPlanet;
    public Vector3i absSecPos;
    private Dodecahedron dodecahedron;
    private PlanetDrawer.PlanetShaderable planetShaderable;
    private PlanetDrawer.AtmoShaderable atmoShaderable;
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

    public PlanetDrawer(GameClientState var1) {
        this.trans.setIdentity();
        this.state = var1;
        this.infoBase = new PlanetInformations[PlanetType.values().length];

        for(int var2 = 0; var2 < this.infoBase.length; ++var2) {
            this.infoBase[var2] = new PlanetInformations();
            this.infoBase[var2].getAtmosphereColor().set(PlanetType.values()[var2].atmosphere);
        }

        new PlanetInformations();
    }

    public void cleanUp() {
    }

    public void draw() {
        this.infos = this.infoBase[this.type.ordinal()];
        this.drawPlanet();
    }

    public void drawPlanet() {
        this.absSectorCenterPos.set((float)this.relSectorPos.x * this.getState().getSectorSize(), (float)this.relSectorPos.y * this.getState().getSectorSize(), (float)this.relSectorPos.z * this.getState().getSectorSize());
        this.absSystemPos.set((float)this.relSystemPos.x * this.getState().getSectorSize(), (float)this.relSystemPos.y * this.getState().getSectorSize(), (float)this.relSystemPos.z * this.getState().getSectorSize());
        this.trans.setIdentity();
        this.transR.setIdentity();
        if (this.drawFromPlanet) {
            this.trans.setIdentity();
            Matrix3f var1;
            (var1 = new Matrix3f()).rotX(6.2831855F * this.year);
            Vector3f var2 = new Vector3f();
            var1.invert();
            var2.set(this.trans.origin);
            var2.add(this.absSectorCenterPos);
            TransformTools.rotateAroundPoint(var2, var1, this.trans, new Transform());
            this.trans.origin.add(this.absSectorCenterPos);
            this.transR.basis.rotX(6.2831855F * this.year);
        } else {
            this.trans.basis.rotX(6.2831855F * this.year);
            this.trans.origin.set(this.absSectorCenterPos);
        }


        if(absSecPos != null && DataUtils.getFromSector(absSecPos) == null) {
            (new DebugPlanet(500, DataUtils.getNewPlanetId(), absSecPos)).initialize();
        }

        if (!Controller.getCamera().isBoundingSphereInFrustrum(this.trans.origin, this.dodecahedron.radius + 50.0F)) {
            ++culled;
        } else {
            GlUtil.glPushMatrix();
            GlUtil.glMultMatrix(this.trans);
            GlUtil.glMultMatrix(this.transR);
            float var5 = this.atmosphereSize;
            float var6 = this.atmosphereSize;
            if (this.absSecPos != null) {
                Iterator var3 = this.state.getLocalAndRemoteObjectContainer().getUidObjectMap().entrySet().iterator();

                while(var3.hasNext()) {
                    Entry var4;
                    PlanetCore var7;
                    if ((var4 = (Entry)var3.next()).getValue() instanceof PlanetCore && (var7 = (PlanetCore)var4.getValue()).getUniqueIdentifier().contains(this.absSecPos.x + "_" + this.absSecPos.y + "_" + this.absSecPos.z)) {
                        var5 = var7.getRadius() / 200.0F;
                        var6 = var7.getRadius() / 325.0F + 0.07692308F;
                        break;
                    }
                }
            }

            GlUtil.scaleModelview(var5, var5, var5);
            if (this.relSectorPos.equals(0, 0, 0)) {
                GlUtil.glEnable(3042);
                GlUtil.glBlendFunc(770, 771);
            } else {
                GlUtil.glDisable(3042);
            }

            if (!this.relSectorPos.equals(0, 0, 0)) {
                ShaderLibrary.planetShader.setShaderInterface(this.planetShaderable);
                ShaderLibrary.planetShader.load();
                this.dodecahedron.draw();
                ShaderLibrary.planetShader.unload();
            }

            this.sphere.loadVBO(true);
            if (!this.relSectorPos.equals(0, 0, 0)) {
                GL11.glDepthRange(0.9999998807907104D, 1.0D);
                GlUtil.glDisable(2929);
                GlUtil.glDepthMask(false);
                GlUtil.glEnable(3042);
                GlUtil.glBlendFunc(770, 1);
            }

            ShaderLibrary.atmosphereShader.setShaderInterface(this.atmoShaderable);
            ShaderLibrary.atmosphereShader.load();
            GlUtil.glEnable(2884);
            GL11.glCullFace(1029);
            GL11.glCullFace(1028);
            GlUtil.scaleModelview(1.0F / var5, 1.0F / var5, 1.0F / var5);
            GlUtil.scaleModelview(var6, var6, var6);
            GL11.glCullFace(1029);
            this.sphere.renderVBO();
            GL11.glCullFace(1029);
            ShaderLibrary.atmosphereShader.unload();
            GlUtil.glEnable(2929);
            GlUtil.glDisable(3042);
            GlUtil.glEnable(2884);
            GlUtil.glPopMatrix();
            GL11.glDepthRange(0.0D, 1.0D);
            GlUtil.glDepthMask(true);
            this.sphere.unloadVBO(true);
            this.sphere.draw();
            //INSERTED CODE @197
            for(PlanetDrawListener drawListener : FastListenerCommon.planetDrawListeners) {
                drawListener.onPlanetDraw(this, absSecPos, infos, type, sphere, dodecahedron);
            }
            //
        }
    }

    public int getCloudMapId() {
        return Controller.getResLoader().getSprite(this.type.clouds).getMaterial().getTexture().getTextureId();
    }

    public int getDiffMapId() {
        return Controller.getResLoader().getSprite(this.type.diff).getMaterial().getTexture().getTextureId();
    }

    public int getNormMapId() {
        return Controller.getResLoader().getSprite(this.type.normal).getMaterial().getTexture().getTextureId();
    }

    public int getSpecularMapId() {
        return Controller.getResLoader().getSprite(this.type.specular).getMaterial().getTexture().getTextureId();
    }

    public void onInit() {
        this.sphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();
        this.sphere.setVertCount(872);
        this.sphere.setFaceCount(900);
        this.sphere.getScale().scale(5);
        this.sphere.updateBound();
        this.sphere.setMaterial(TextureUtils.getPlanetTexture(WorldType.PLANET_DEBUG, 0).getMaterial());
        //this.sphere = Controller.getResLoader().getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_debug_0");
        this.dodecahedron = new Dodecahedron(200.0F);
        this.dodecahedron.create();
        this.planetShaderable = new PlanetDrawer.PlanetShaderable();
        this.atmoShaderable = new PlanetDrawer.AtmoShaderable();
        float var1 = this.state.getGameState().getPlanetSizeMean();
        float var2 = this.state.getGameState().getPlanetSizeDeviation();
        //this.atmosphereSize = (var1 + var2) / 275.0F;
        this.atmosphereSize = 750.0f;
    }

    public void setPlanetSectorPos(Vector3i var1) {
        this.relSectorPos = var1;
    }

    public void setPlanetType(PlanetType var1) {
        this.type = var1;
    }

    public void update(Timer var1) {
        this.planetTime += var1.getDelta();
    }

    public GameClientState getState() {
        return this.state;
    }

    public boolean isInvisible() {
        return false;
    }

    class PlanetShaderable implements Shaderable {
        private PlanetShaderable() {
        }

        public void onExit() {
            GlUtil.glActiveTexture(33984);
            GlUtil.glDisable(3553);
            GlUtil.glBindTexture(3553, 0);
            GlUtil.glActiveTexture(33985);
            GlUtil.glDisable(3553);
            GlUtil.glBindTexture(3553, 0);
            GlUtil.glActiveTexture(33986);
            GlUtil.glDisable(3553);
            GlUtil.glBindTexture(3553, 0);
            GlUtil.glActiveTexture(33987);
            GlUtil.glDisable(3553);
            GlUtil.glBindTexture(3553, 0);
            GlUtil.glActiveTexture(33988);
            GlUtil.glDisable(3553);
            GlUtil.glBindTexture(3553, 0);
            GlUtil.glActiveTexture(33984);
        }

        public void updateShader(DrawableScene var1) {
        }

        public void updateShaderParameters(Shader var1) {
            GlUtil.updateShaderFloat(var1, "fCloudRotation", PlanetDrawer.this.planetTime * 0.005F);
            GlUtil.updateShaderVector4f(var1, "fvSpecular", 1.0F, 1.0F, 1.0F, 1.0F);
            GlUtil.updateShaderVector4f(var1, "fvDiffuse", 1.0F, 1.0F, 1.0F, 1.0F);
            GlUtil.updateShaderFloat(var1, "fSpecularPower", 20.0F);
            GlUtil.updateShaderFloat(var1, "fCloudHeight", PlanetDrawer.this.infos.getCloudHeight());
            GlUtil.updateShaderFloat(var1, "density", 1.5F);
            if (PlanetDrawer.this.relSectorPos.equals(0, 0, 0)) {
                GlUtil.updateShaderFloat(var1, "dist", Controller.getCamera().getPos().length());
            } else {
                GlUtil.updateShaderFloat(var1, "dist", -1.0F);
            }

            GlUtil.glEnable(3553);
            GlUtil.glActiveTexture(33984);
            GlUtil.glBindTexture(3553, PlanetDrawer.this.getDiffMapId());
            GlUtil.glActiveTexture(33985);
            GlUtil.glBindTexture(3553, PlanetDrawer.this.getNormMapId());
            GlUtil.glActiveTexture(33986);
            GlUtil.glBindTexture(3553, PlanetDrawer.this.getSpecularMapId());
            GlUtil.glActiveTexture(33987);
            GlUtil.glBindTexture(3553, PlanetDrawer.this.getCloudMapId());
            GlUtil.glActiveTexture(33988);
            GlUtil.updateShaderInt(var1, "baseMap", 0);
            GlUtil.updateShaderInt(var1, "normalMap", 1);
            GlUtil.updateShaderInt(var1, "specMap", 2);
            GlUtil.updateShaderInt(var1, "cloudsMap", 3);
        }
    }

    class AtmoShaderable implements Shaderable {
        private Vector4f lastColor;

        private AtmoShaderable() {
            this.lastColor = new Vector4f(-1.0F, 0.0F, 0.0F, 0.0F);
        }

        public void onExit() {
        }

        public void updateShader(DrawableScene var1) {
        }

        public void updateShaderParameters(Shader var1) {
            if (this.lastColor.x < 0.0F) {
                GlUtil.updateShaderVector4f(var1, "fvDiffuse", PlanetDrawer.diffuse);
            }

            if (!PlanetDrawer.this.infos.getAtmosphereColor().equals(this.lastColor)) {
                GlUtil.updateShaderColor4f(var1, "fvAtmoColor", PlanetDrawer.this.infos.getAtmosphereColor());
            } else {
                this.lastColor.set(PlanetDrawer.this.infos.getAtmosphereColor());
            }

            GlUtil.updateShaderFloat(var1, "fCloudHeight", PlanetDrawer.this.infos.getCloudHeight());
            GlUtil.updateShaderFloat(var1, "fAbsPower", PlanetDrawer.this.infos.getAtmosphereAbsorptionPower());
            GlUtil.updateShaderFloat(var1, "fAtmoDensity", PlanetDrawer.this.infos.getAtmosphereDensity());
            GlUtil.updateShaderFloat(var1, "fGlowPower", PlanetDrawer.this.infos.getAtmosphereGlowPower());
            GlUtil.updateShaderFloat(var1, "density", 1.5F / Controller.vis.getVisLen());
            if (PlanetDrawer.this.relSectorPos.equals(0, 0, 0)) {
                GlUtil.updateShaderFloat(var1, "dist", Controller.getCamera().getPos().length());
            } else {
                GlUtil.updateShaderFloat(var1, "dist", PlanetDrawer.this.relSectorPos.length());
            }
        }
    }
}