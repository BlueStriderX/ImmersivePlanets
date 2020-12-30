package net.dovtech.immersiveplanets.universe;

import api.common.GameClient;
import api.common.GameCommon;
import api.entity.StarPlayer;
import net.dovtech.immersiveplanets.ImmersivePlanets;
import net.dovtech.immersiveplanets.data.shape.BoundingSphere;
import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.texture.Texture;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;
import java.io.IOException;

public class GasGiantOld extends CelestialBody {

    private Mesh cloudSphere;
    private Mesh lodCloudSphere;
    private Texture texture;
    private float radius;
    private Color4f fogColor;
    private int ringCount;
    private int moonCount;
    private Vector3i sector;
    private Vector3f position;
    private BoundingSphere outerAtmosphere;
    private BoundingSphere innerAtmosphere;
    private Mesh killSphere;
    private StarPlayer player;
    private int scale;
    private int textureID;

    public GasGiantOld(Vector3i sector, int radius, int ringCount, int moonCount, int textureID, Color4f fogColor) throws IOException {
        this.radius = radius;
        this.ringCount = ringCount;
        this.moonCount = moonCount;
        this.fogColor = fogColor;
        this.textureID = textureID;
        this.sector = sector;
        this.position = new Vector3f(0, 0, 0);
        //this.texture = TextureLoader.getTexture(ImageIO.read(ImmersivePlanets.getInstance().getResource("texture/universe/gas-giant-" + textureID + ".png")), "gas-giant-" + textureID + "-texture", GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
        this.outerAtmosphere = new BoundingSphere(radius);
        this.innerAtmosphere = new BoundingSphere(radius * 0.72f);
        //this.killSphere = new BoundingSphere(radius * 0.65f);
        this.killSphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().get(0);
        this.cloudSphere = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().get(0);
        //this.lodCloudSphere = (Mesh) Controller.getResLoader().getMesh("SphereLowPoly").getChilds().get(0);
        this.player = new StarPlayer(GameClient.getClientPlayerState());
        this.scale = radius / 100;
    }

    @Override
    public void cleanUp() {
        //cloudSphere.cleanUp();
        //lodCloudSphere.cleanUp();
    }

    @Override
    public void draw() {
        if (cloudSphere != null && outerAtmosphere != null && innerAtmosphere != null && killSphere != null) {
            //GlUtil.glPushMatrix();

            if (ImmersivePlanets.getInstance().drawDebugSpheres && ImmersivePlanets.getInstance().debugMode) {
                outerAtmosphere.draw();
                innerAtmosphere.draw();
                //killSphere.draw();
            } else {
                outerAtmosphere.cleanUp();
                innerAtmosphere.cleanUp();
                //killSphere.cleanUp();
            }

            //GlUtil.glPushMatrix();
            cloudSphere.draw();
            //GlUtil.glPopMatrix();

            //cloudSphere.draw();

            /*
            float distance = Math.abs(Vector3i.getDisatance(sector, player.getSector().getCoordinates()));
            if (distance <= 5 && !(distance <= 2)) {
                cloudSphere.cleanUp();
                lodCloudSphere.draw();
                //checkPlayerPos();
            } else if (distance <= 2) {
                cloudSphere.draw();
                lodCloudSphere.cleanUp();
                //checkPlayerPos();
            } else {
                //draw = false;
                //cleanUp();
            }

             */
            //GlUtil.glPopMatrix();
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

            /*
            if (player.getCurrentEntity() != null && player.getCurrentEntity().getEntityType().equals(EntityType.SHIP) && !player.getPlayerState().isGodMode()) {
                Vector3i entityPos = player.getCurrentEntity().getSectorPosition();
                if (player.getCurrentEntity().getCurrentReactor() != null) {
                    GravityCompressionDebuff compressionDebuff = new GravityCompressionDebuff(player.getCurrentEntity().getCurrentReactor().getRegen());
                    if (outerAtmosphere.isPositionInRadius(entityPos) && !innerAtmosphere.isPositionInRadius(entityPos)) {
                        if (player.getCurrentEntity().getCurrentReactor().internalReactor.pw.getPowerConsumerList().contains(compressionDebuff)) {
                            player.getCurrentEntity().getCurrentReactor().internalReactor.pw.removeConsumer(compressionDebuff);
                        }
                    } else if (outerAtmosphere.isPositionInRadius(entityPos) && innerAtmosphere.isPositionInRadius(entityPos)) {
                        compressionDebuff.setCompressionPercentage(innerAtmosphere.getDistanceToCenter(entityPos) / 100);
                        if (!player.getCurrentEntity().getCurrentReactor().internalReactor.pw.getPowerConsumerList().contains(compressionDebuff)) {
                            player.getCurrentEntity().getCurrentReactor().internalReactor.pw.addConsumer(compressionDebuff);
                        }
                        if (!player.getPlayerState().isGodMode()) {
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

             */
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
        //}
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onInit() {
        //ShaderLibrary.hoffmanSkyShader.loadWithoutUpdate();

        GlUtil.glEnable(GL11.GL_TEXTURE_2D);
        cloudSphere.setPos(0, 0, 0);
        cloudSphere.setScale(scale, scale, scale);
        //cloudSphere.getMaterial().setTextureFile(ImmersivePlanets.class.getResource("resources/texture/universe/gas-giant-" + textureID + ".png").getPath());
        cloudSphere.getMaterial().setTexture(texture);
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        /*
        GlUtil.glEnable(GL11.GL_TEXTURE_2D);
        lodCloudSphere.setPos(cloudSphere.getPos());
        lodCloudSphere.setScale(new Vector3f(scale, scale, scale));
        lodCloudSphere.getMaterial().setTexture(texture);
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
         */

        outerAtmosphere.setRadius(radius);
        outerAtmosphere.setPosition(cloudSphere.getPos());
        outerAtmosphere.onInit();

        innerAtmosphere.setRadius(radius * 0.7f);
        innerAtmosphere.setPosition(cloudSphere.getPos());
        innerAtmosphere.onInit();

        killSphere.setScale(scale * 0.6f, scale * 0.6f, scale * 0.6f);
        killSphere.setCollisionObject(true);
        killSphere.setVisibility(2);

        /*
        killSphere.setRadius(radius * 0.65f);
        killSphere.setPosition(cloudSphere.getPos());
        killSphere.onInit();

         */
        //ShaderLibrary.hoffmanSkyShader.unloadWithoutExit();
    }

    public Mesh getCloudSphere() {
        return cloudSphere;
    }

    public Mesh getLodCloudSphere() {
        return lodCloudSphere;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getRadius() {
        return radius;
    }

    public Color4f getFogColor() {
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

    public BoundingSphere getOuterAtmosphere() {
        return outerAtmosphere;
    }

    public BoundingSphere getInnerAtmosphere() {
        return innerAtmosphere;
    }

    /*
    public BoundingSphere getKillSphere() {
        return killSphere;
    }

     */
}