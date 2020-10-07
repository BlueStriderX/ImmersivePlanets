package net.dovtech.immersiveplanets;

import api.DebugFile;
import api.common.GameClient;
import api.common.GameCommon;
import api.config.BlockConfig;
import api.entity.StarPlayer;
import api.listener.Listener;
import api.listener.events.draw.PlanetDrawEvent;
import api.listener.events.draw.SegmentDrawEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.network.packets.PacketUtil;
import net.dovtech.immersiveplanets.commands.DebugSphereCommand;
import net.dovtech.immersiveplanets.commands.PlanetTextureChangeCommand;
import net.dovtech.immersiveplanets.graphics.shape.BoundingSphere;
import net.dovtech.immersiveplanets.network.client.ClientAtmoKillSendPacket;
import org.lwjgl.opengl.GL11;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.texture.Texture;
import org.schema.schine.graphicsengine.texture.TextureLoader;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImmersivePlanets extends StarMod {

    static ImmersivePlanets inst;

    public ImmersivePlanets() {
        inst = this;
    }

    //Other
    private int clientViewDistance = 1000;
    private GameClientState clientState;
    private StarPlayer player;
    public boolean drawDebugSpheres = false;

    //Mesh
    public Mesh clouds;
    public Mesh planet;
    private BoundingSphere outerBoundingSphere;
    private BoundingSphere innerBoundingSphere;

    //Textures
    public BufferedImage cloudsImage;
    public Texture cloudsTexture;
    public BufferedImage planetImage;
    public Texture planetTexture;


    //Config
    private FileConfiguration config;
    private String[] defaultConfig = {
            "debug-mode: false",
            "sky-offset: 1.3",
            "reduce-draw-on-planets: true",
            "planet-chunk-view-distance: 250"
    };

    //Config Settings
    public boolean debugMode = true;
    public float skyOffset = 1.3f;
    public boolean reduceDrawOnPlanets = true;
    public int planetChunkViewDistance = 250;

    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        inst = this;
        setModName("ImmersivePlanets");
        setModAuthor("Dovtech");
        setModVersion("0.4.1");
        setModDescription("Adds larger and more immersive planets with their own atmospheres and features.");

        if (GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) initConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer())
            clientViewDistance = (int) EngineSettings.G_MAX_SEGMENTSDRAWN.getCurrentState();

        registerListeners();
        registerCommands();
        registerPackets();

        DebugFile.log("Enabled", this);
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {

    }

    public InputStream getResource(String path) {
        try {
            return ImmersivePlanets.class.getResourceAsStream("resources/" + path);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            DebugFile.err("Could not fetch file " + path + "!");
            return null;
        }
    }

    private void registerPackets() {
        PacketUtil.registerPacket(ClientAtmoKillSendPacket.class);
    }

    private void registerCommands() {
        if (debugMode) {
            StarLoader.registerCommand(new PlanetTextureChangeCommand());
            StarLoader.registerCommand(new DebugSphereCommand());
        }
    }

    private void registerListeners() {

        StarLoader.registerListener(SegmentDrawEvent.class, new Listener<SegmentDrawEvent>() {
            @Override
            public void onEvent(SegmentDrawEvent event) {
                if(event.getDrawer().showUpdateNotification) event.getDrawer().showUpdateNotification = false;
            }
        });

        StarLoader.registerListener(PlanetDrawEvent.class, new Listener<PlanetDrawEvent>() {
            @Override
            public void onEvent(PlanetDrawEvent event) {
                if (clientState == null || player == null) {
                    clientState = GameClient.getClientState();
                    player = new StarPlayer(GameClient.getClientPlayerState());
                }
                try {
                    if (outerBoundingSphere == null || innerBoundingSphere == null || clouds == null || planet == null) {
                        float skyRadius = event.getPlanetInfo().getRadius() * skyOffset;
                        if (skyRadius <= 0) skyRadius = 300; //Todo: Detect world mean planet size
                        Vector3f skyScale = event.getSphere().getScale();
                        //skyScale.scale(skyOffset);
                        //ShaderLibrary.loadShaders();

                        outerBoundingSphere = new BoundingSphere(skyRadius, event.getSphere().getPos());
                        outerBoundingSphere.onInit();
                        //Todo: Add a check so that the client cannot enter the sphere through build mode
                        innerBoundingSphere = new BoundingSphere(Math.max(skyRadius * 0.85f, event.getPlanetInfo().getRadius()), event.getSphere().getPos());
                        innerBoundingSphere.onInit();
                        /*
                        //clouds = (Mesh) Controller.getResLoader().getMesh("GeoSphere").getChilds().get(0);
                        cloudsImage = ImageIO.read(getResource("texture/planet/clouds.png"));
                        cloudsTexture = TextureLoader.getTexture(cloudsImage, "cloudsTexture", GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
                        //GlUtil.glPushMatrix();
                        //GL11.glScalef(skyScale.x, skyScale.y, skyScale.z);
                        //ShaderLibrary.atmosphereShader.load();
                        //GlUtil.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                        GlUtil.glEnable(GL11.GL_TEXTURE_2D);
                        //GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);
                        //GlUtil.glActiveTexture(GL13.GL_TEXTURE1);
                        //if(event.getSector().equals(player.getSector().getCoordinates())) GlUtil.glEnable(GL11.GL_BLEND);
                            GlUtil.glPushMatrix();
                            GlUtil.scaleModelview(1.15f, 1.15f, 1.15f);
                            clouds = Controller.getResLoader().getMesh("Sphere");
                        clouds = (Mesh) Controller.getResLoader().getMesh("Sphere").getChilds().iterator().next();
                        //Vector3f cloudScale = event.getSphere().getScale();
                        //cloudScale.scale(skyOffset);
                        clouds.setScale(skyScale);
                        //clouds.setRadius(skyRadius);
                        //clouds.setSector(event.getSector());
                        if (debugMode) DebugFile.log("[DEBUG] Scaled cloud mesh to " + skyScale.toString());

                        clouds.setPos(event.getSphere().getPos());
                        //clouds.setMaterial(new Material());
                        clouds.getMaterial().setTexture(cloudsTexture);
                        //clouds.loadVBO(true);
                        //clouds.getMaterial().getTexture().bindOnShader(GL13.GL_TEXTURE1, 1, "cloudsTexture", ShaderLibrary.atmosphereShader);
                        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, clouds.getMaterial().getTexture().getTextureId());
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                        GlUtil.glEnable(GL11.GL_CULL_FACE);
                        GL11.glCullFace(GL11.GL_BACK);
                        //clouds.renderVBO();
                        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
                        //GlUtil.glDisable(GL11.GL_BLEND);
                        GlUtil.glEnable(GL11.GL_CULL_FACE);
                        //GlUtil.glPopMatrix();
                        GL11.glDepthRange(0.0, 1.0);
                        GlUtil.glDepthMask(true);
                        //clouds.unloadVBO(true);
                        //ShaderLibrary.atmosphereShader.unload();
                        //GlUtil.glPopMatrix();
                        */

                        String texturePath;
                        switch (event.getPlanetType()) {
                            case ICE:
                                texturePath = "texture/planet/ice-planet.png";
                                break;
                            case DESERT:
                                texturePath = "texture/planet/desert-planet.png";
                                break;
                            case MARS:
                                texturePath = "texture/planet/mars-planet.png";
                                break;
                            case PURPLE:
                                texturePath = "texture/planet/purple-planet.png";
                                break;
                            default:
                                texturePath = "texture/planet/debug-planet.png";
                                break;
                        }
                        planetImage = ImageIO.read(getResource(texturePath));
                        planetTexture = TextureLoader.getTexture(planetImage, event.getPlanetType().name().toLowerCase() + "-texture", GL11.GL_TEXTURE_2D, GL11.GL_RGBA, GL11.GL_LINEAR, GL11.GL_LINEAR, true, false);
                        //GlUtil.glPushMatrix();
                        //cloudScale.scale(0.95f);
                        //GL11.glScalef(planetScale.x, planetScale.y, planetScale.z);
                        //ShaderLibrary.planetShader.load();
                        //GlUtil.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                        GlUtil.glEnable(GL11.GL_TEXTURE_2D);
                        //GlUtil.glActiveTexture(GL13.GL_TEXTURE2);
                        //if(event.getSector().equals(player.getSector().getCoordinates())) GlUtil.glEnable(GL11.GL_BLEND);
                        planet = event.getSphere();
                        //planet.setSector(event.getSector());
                        skyScale.scale(skyOffset);
                        planet.setScale(skyScale);
                        //if (debugMode) DebugFile.log("[DEBUG] Scaled planet mesh to " + cloudScale.toString());
                        planet.setPos(event.getSphere().getPos());
                        //planet.setMaterial(new Material());
                        planet.getMaterial().setTexture(planetTexture);
                        //planet.loadVBO(true);
                        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, planet.getMaterial().getTexture().getTextureId());
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                        /*
                        GlUtil.glEnable(GL11.GL_CULL_FACE);
                        GL11.glCullFace(GL11.GL_BACK);
                        GlUtil.glEnable(GL11.GL_DEPTH_TEST);
                        GlUtil.glDisable(GL11.GL_BLEND);
                        GlUtil.glEnable(GL11.GL_CULL_FACE);
                        GlUtil.glPopMatrix();
                        GL11.glDepthRange(0.0, 1.0);
                        GlUtil.glDepthMask(true);
                        //planet.unloadVBO(true);
                        ShaderLibrary.planetShader.unload();
                        GlUtil.glPopMatrix();
                        */
                        //GlUtil.glActiveTexture(GL13.GL_TEXTURE3);
                    }


                    if (outerBoundingSphere != null && innerBoundingSphere != null && clouds != null && planet != null) {
                        if (debugMode && drawDebugSpheres) {
                            outerBoundingSphere.draw();
                            innerBoundingSphere.draw();
                        } else {
                            outerBoundingSphere.cleanUp();
                            innerBoundingSphere.cleanUp();
                        }

                        //if (Math.abs(Vector3i.getDisatance(event.getSector(), player.getSector().getCoordinates())) <= 5) {
                            //if (clouds.getPos() != event.getSphere().getPos()) clouds.setPos(event.getSphere().getPos());
                            //clouds.draw();
                            //clouds.drawVBO();
                            //if (planet.getPos() != event.getSphere().getPos()) planet.setPos(event.getSphere().getPos());
                            //planet.drawVBO();

                            Vector3f clientPos = Controller.getCamera().getPos();
                            if (outerBoundingSphere.isPositionInRadius(clientPos) && !innerBoundingSphere.isPositionInRadius(clientPos) && player.getSector().getCoordinates().equals(event.getSector())) {
                                if (debugMode)
                                    DebugFile.log("[DEBUG] Client is within atmosphere of planet at " + event.getSector().toString());

                                if ((clientState.isInAnyStructureBuildMode() || clientState.isInCharacterBuildMode()) && !player.getPlayerState().isGodMode()) {
                                    Vector3f clientBuildModePos = player.getPlayerState().getBuildModePosition().getWorldTransformOnClient().origin;
                                    if (outerBoundingSphere.isPositionInRadius(clientBuildModePos) && !innerBoundingSphere.isPositionInRadius(clientBuildModePos) && player.getSector().getCoordinates().equals(event.getSector())) {
                                        Vector3f pos = player.getPlayerState().getBuildModePosition().getWorldTransformOnClient().origin;
                                        String oldPosString = pos.toString();
                                        float pushBack = outerBoundingSphere.getDistanceToCenter(pos);
                                        pos.sub(new Vector3f(pushBack, pushBack, pushBack));
                                        String newPosString = pos.toString();
                                        player.getPlayerState().getBuildModePosition().getWorldTransformOnClient().origin.set(pos);
                                        if (debugMode) {
                                            DebugFile.log("[DEBUG] Pushed client build mode camera out of debug sphere");
                                            DebugFile.log("OldPos: " + oldPosString);
                                            DebugFile.log("NewPos : " + newPosString);
                                        }
                                    }
                                }

                                if (player.getCurrentEntity() != null || player.getPlayerState().isSitting() || player.getPlayerState().isGodMode()) {
                                    Vector3f velocity = player.getCurrentEntity().getVelocity();
                                    velocity.scale(0.35f);
                                    player.getCurrentEntity().setVelocity(velocity);
                                } else {
                                    if (GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer()) {
                                        PacketUtil.sendPacketToServer(new ClientAtmoKillSendPacket(player.getSector().getCoordinates()));
                                    }
                                }
                                if (reduceDrawOnPlanets) {
                                    if (!EngineSettings.G_MAX_SEGMENTSDRAWN.getCurrentState().equals(planetChunkViewDistance)) {
                                        EngineSettings.G_MAX_SEGMENTSDRAWN.setCurrentState(planetChunkViewDistance);
                                        if (debugMode) DebugFile.log("[DEBUG] Set view distance to " + planetChunkViewDistance);
                                    }
                                }
                            } else if (outerBoundingSphere.isPositionInRadius(clientPos) || innerBoundingSphere.isPositionInRadius(clientPos)) {
                                event.getDodecahedron().draw();
                                planet.cleanUp();
                            } else if (!outerBoundingSphere.isPositionInRadius(clientPos) && !innerBoundingSphere.isPositionInRadius(clientPos)) {
                                event.getDodecahedron().cleanUp();
                                planet.draw();

                                if (!EngineSettings.G_MAX_SEGMENTSDRAWN.getCurrentState().equals(clientViewDistance)) {
                                    EngineSettings.G_MAX_SEGMENTSDRAWN.setCurrentState(clientViewDistance);
                                    if (debugMode) DebugFile.log("[DEBUG] Set view distance to " + clientViewDistance);
                                }
                            }
                            /*
                        } else {
                            if (planet != null) planet.cleanUp();
                            if (clouds != null) clouds.cleanUp();
                            if (outerBoundingSphere != null) outerBoundingSphere.cleanUp();
                            if (innerBoundingSphere != null) innerBoundingSphere.cleanUp();
                        }

                             */
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        DebugFile.log("Registered Listeners!", this);
    }

    private void initConfig() {
        this.config = getConfig("config");
        this.config.saveDefault(defaultConfig);

        this.debugMode = config.getBoolean("debug-mode");
        this.skyOffset = (float) config.getDouble("sky-offset");
        this.reduceDrawOnPlanets = config.getBoolean("reduce-draw-on-planets");
        this.planetChunkViewDistance = config.getInt("planet-chunk-view-distance");
    }

    public static ImmersivePlanets getInstance() {
        return inst;
    }
}
