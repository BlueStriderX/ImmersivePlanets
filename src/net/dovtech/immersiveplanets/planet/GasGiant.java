package net.dovtech.immersiveplanets.planet;

import api.common.GameClient;
import api.common.GameCommon;
import api.entity.EntityType;
import api.entity.StarPlayer;
import api.utils.game.PlayerUtils;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import net.dovtech.immersiveplanets.data.AtmosphereEntryDamager;
import net.dovtech.immersiveplanets.graphics.shape.BoundingSphere;
import net.dovtech.immersiveplanets.system.power.GravityCompressionDebuff;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;
import org.schema.schine.graphicsengine.texture.Texture;
import org.schema.schine.graphicsengine.texture.TextureLoader;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.IOException;

public class GasGiant implements Drawable, Shaderable {

    private Mesh sphere;
    private Mesh lodSphere;
    private Texture texture;
    private float radius;
    private Vector4f fogColor;
    private int ringCount;
    private int moonCount;
    private Vector3i sector;
    private Vector3f position;
    private BoundingSphere outerAtmosphere;
    private BoundingSphere innerAtmosphere;
    private BoundingSphere killSphere;
    private boolean draw;
    private StarPlayer player;

    public GasGiant() {

    }

    public void create(String textureID, int radius, int ringCount, int moonCount, Vector4f fogColor, Vector3i sector) throws IOException {
        this.radius = radius;
        this.ringCount = ringCount;
        this.moonCount = moonCount;
        this.fogColor = fogColor;
        this.sector = sector;
        this.position = new Vector3f(0, 0, 0);
        this.texture = TextureLoader.getTexture(ImageIO.read(ImmersivePlanets.getInstance().getResource("texture/planet/gas-giant-" + textureID + ".png")), "gas-giant-" + textureID + "-texture", GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
        this.outerAtmosphere = new BoundingSphere(radius);
        this.innerAtmosphere = new BoundingSphere(radius * 0.72f);
        this.killSphere = new BoundingSphere(radius * 0.65f);
        this.sphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().get(0);
        this.lodSphere = (Mesh) Controller.getResLoader().getMesh("SphereLowPoly").getChilds().get(0);
        this.player = new StarPlayer(GameClient.getClientPlayerState());
        onInit();
    }

    @Override
    public void cleanUp() {
        draw = false;
        sphere.cleanUp();
        lodSphere.cleanUp();
    }

    @Override
    public void draw() {
        if(sphere != null && lodSphere != null && outerAtmosphere != null && innerAtmosphere != null && killSphere != null && draw) {
            GlUtil.glPushMatrix();

            if (ImmersivePlanets.getInstance().drawDebugSpheres && ImmersivePlanets.getInstance().debugMode && GameClient.getClientPlayerState().getCurrentSector().equals(sector)) {
                outerAtmosphere.draw();
                innerAtmosphere.draw();
                killSphere.draw();
            } else {
                outerAtmosphere.cleanUp();
                innerAtmosphere.cleanUp();
                killSphere.cleanUp();
            }

            float distance = Math.abs(Vector3i.getDisatance(sector, player.getSector().getCoordinates()));
            if (distance <= 5 && !(distance <= 2)) {
                sphere.cleanUp();
                lodSphere.draw();
                //checkPlayerPos();
            } else if (distance <= 2) {
                sphere.draw();
                lodSphere.cleanUp();
                //checkPlayerPos();
            } else {
                draw = false;
                cleanUp();
            }
            GlUtil.glPopMatrix();
        }
    }

    private void checkPlayerPos() {
        if (GameCommon.isOnSinglePlayer() || GameCommon.isClientConnectedToServer()) {
            GameClientState clientState = GameClient.getClientState();
            StarPlayer player = new StarPlayer(clientState.getPlayer());
            //Vector3f clientPos = Controller.getCamera().getPos();

            /*
            if(killSphere.isPositionInRadius(clientPos) && player.getPlayerState().isGodMode()) {
                Vector3i safeSector = player.getSector().getCoordinates();
                String oldSector = safeSector.toString();
                safeSector.add(0, 3, 0);
                player.getPlayerState().setCurrentSector(safeSector);
                PlayerUtils.sendMessage(player.getPlayerState(), "[DEBUG] Warping out of Gas Giant to safe sector!");
                PlayerUtils.sendMessage(player.getPlayerState(), "[DEBUG] " + oldSector + " -> " + safeSector.toString());
            } else if (killSphere.isPositionInRadius(clientPos) && !GameClient.getClientPlayerState().isGodMode()) {
                //PacketUtil.sendPacketToServer(new ClientAtmoKillSendPacket(GameClient.getClientPlayerState().getCurrentSector()));
            }\
             */

            if (player.getCurrentEntity() != null && player.getCurrentEntity().getEntityType().equals(EntityType.SHIP) && !player.getPlayerState().isGodMode()) {
                Vector3i entityPos = player.getCurrentEntity().getSectorPosition();
                if(player.getCurrentEntity().getCurrentReactor() != null) {
                    GravityCompressionDebuff compressionDebuff = new GravityCompressionDebuff(player.getCurrentEntity().getCurrentReactor().getRegen());
                    if (outerAtmosphere.isPositionInRadius(entityPos) && ! innerAtmosphere.isPositionInRadius(entityPos)) {
                        if (player.getCurrentEntity().getCurrentReactor().internalReactor.pw.getPowerConsumerList().contains(compressionDebuff)) {
                            player.getCurrentEntity().getCurrentReactor().internalReactor.pw.removeConsumer(compressionDebuff);
                        }
                    } else if(outerAtmosphere.isPositionInRadius(entityPos) && innerAtmosphere.isPositionInRadius(entityPos)) {
                        compressionDebuff.setCompressionPercentage(innerAtmosphere.getDistanceToCenter(entityPos) / 100);
                        if(!player.getCurrentEntity().getCurrentReactor().internalReactor.pw.getPowerConsumerList().contains(compressionDebuff)) {
                            player.getCurrentEntity().getCurrentReactor().internalReactor.pw.addConsumer(compressionDebuff);
                        }
                        if(!player.getPlayerState().isGodMode()) {
                            Vector3f velocity = player.getCurrentEntity().getVelocity();
                            velocity.sub(position);
                            velocity.normalize();
                            velocity.scale((1 / Vector3fTools.distance(entityPos.x, entityPos.y, entityPos.z, position.x, position.y, position.z)) * 0.85f);
                            player.getCurrentEntity().setVelocity(velocity);
                        }
                    }
                }

                if (killSphere.isPositionInRadius(entityPos) && innerAtmosphere.isPositionInRadius(entityPos) && outerAtmosphere.isPositionInRadius(entityPos) && !player.getPlayerState().isGodMode()) {
                    player.getCurrentEntity().internalEntity.startCoreOverheating(new AtmosphereEntryDamager(GameClient.getClientState(), player.getSector()));
                }
            }

            /*
            if ((clientState.isInCharacterBuildMode())) {
                Vector3f clientBuildModePos = player.getPlayerState().getBuildModePosition().getWorldTransformOnClient().origin;
                if (killSphere.isPositionInRadius(clientBuildModePos)) {
                    String oldPosString = clientBuildModePos.toString();
                    float pushBack = killSphere.getDistanceToCenter(clientBuildModePos);
                    clientBuildModePos.sub(new Vector3f(pushBack, pushBack, pushBack));
                    String newPosString = clientBuildModePos.toString();
                    player.getPlayerState().getBuildModePosition().getWorldTransformOnClient().origin.set(clientBuildModePos);
                    if (ImmersivePlanets.getInstance().debugMode) {
                        DebugFile.log("[DEBUG] Pushed client build mode camera out of kill sphere");
                        DebugFile.log("OldPos: " + oldPosString);
                        DebugFile.log("NewPos : " + newPosString);
                    }
                }
            }
             */
        }
    }

    @Override
    public boolean isInvisible() {
        return !draw;
    }

    @Override
    public void onInit() {
        if(sphere != null && lodSphere != null) {
            GlUtil.glEnable(GL11.GL_TEXTURE_2D);
            sphere.setPos(0, 0, 0);
            sphere.setScale(new Vector3f(5, 5, 5));
            sphere.getMaterial().setTexture(texture);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            GlUtil.glEnable(GL11.GL_TEXTURE_2D);
            lodSphere.setPos(sphere.getPos());
            lodSphere.setScale(new Vector3f(5, 5, 5));
            lodSphere.getMaterial().setTexture(texture);
            GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            outerAtmosphere.setRadius(sphere.getBoundingSphereRadius());
            outerAtmosphere.setPosition(sphere.getPos());
            outerAtmosphere.onInit();

            innerAtmosphere.setRadius(sphere.getBoundingSphereRadius() * 0.72f);
            innerAtmosphere.setPosition(sphere.getPos());
            innerAtmosphere.onInit();

            killSphere.setRadius(sphere.getBoundingSphereRadius() * 0.65f);
            killSphere.setPosition(sphere.getPos());
            killSphere.onInit();

            draw = true;
        }
    }

    public Mesh getSphere() {
        return sphere;
    }

    public Mesh getLodSphere() {
        return lodSphere;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getRadius() {
        return radius;
    }

    public Vector4f getFogColor() {
        return fogColor;
    }

    public int getRingCount() {
        return ringCount;
    }

    public int getMoonCount() {
        return moonCount;
    }

    public Vector3i getSector() {
        return sector;
    }

    public Vector3f getPosition() {
        return position;
    }

    public boolean isDraw() {
        return draw;
    }

    public BoundingSphere getOuterAtmosphere() {
        return outerAtmosphere;
    }

    public BoundingSphere getInnerAtmosphere() {
        return innerAtmosphere;
    }

    public BoundingSphere getKillSphere() {
        return killSphere;
    }

    @Override
    public void onExit() {
        draw = false;
        cleanUp();
    }

    @Override
    public void updateShader(DrawableScene drawableScene) {

    }

    @Override
    public void updateShaderParameters(Shader shader) {

    }
}