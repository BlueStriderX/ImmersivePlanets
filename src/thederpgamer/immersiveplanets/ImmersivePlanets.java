package thederpgamer.immersiveplanets;

import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.controller.planet.PlanetGenerateEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.graphics.planet.PlanetDrawer;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import thederpgamer.immersiveplanets.universe.generation.world.PlanetSpawnHandler;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ImmersivePlanets.java
 * ImmersivePlanets main class
 * ==================================================
 * Created 02/12/2021
 * @author TheDerpGamer
 */
public class ImmersivePlanets extends StarMod {

    public ImmersivePlanets() { }
    public static void main(String[] args) { }
    private static ImmersivePlanets instance;
    public static ImmersivePlanets getInstance() {
        return instance;
    }

    //Data
    public File playerDataFolder;
    public File planetDataFolder;
    public File chunkDataFolder;

    //Resources
    public PlanetDrawer planetDrawer;
    public GameResourceLoader resLoader;

    //Config
    private final String[] defaultConfig = {
            "debug-mode: false",
            "instanced-sector-dist: 10000",
            "max-planets-drawn: 5"
    };
    public boolean debugMode = false;
    public int instancedSectorDist = 10000;
    public int maxPlanetsDrawn = 5;

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        loadTextures();
        initialize();
        registerFastListeners();
        registerEventListeners();
    }

    @Override
    public void onLoadModels() {
        resLoader = (GameResourceLoader) Controller.getResLoader();
        String[] models = new String[] {
                "planet_debug_0"
        };

        for(final String model : models) {
            StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        resLoader.getMeshLoader().loadModMesh(ImmersivePlanets.getInstance(), model, getJarResource("thederpgamer/immersiveplanets/resources/models/planet/" + model + ".zip"), null);
                    } catch(ResourceException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void initConfig() {
        FileConfiguration config = getConfig("config");
        config.saveDefault(defaultConfig);

        debugMode = config.getConfigurableBoolean("debug-mode", false);
        instancedSectorDist = config.getConfigurableInt("instanced-sector-dist", 10000);
        if(instancedSectorDist < 5000) {
            config.set("instanced-sector-dist", 5000);
            instancedSectorDist = 5000;
        }
        maxPlanetsDrawn = config.getConfigurableInt("max-planets-drawn", 5);

        config.saveConfig();
    }

    private void loadTextures() {
        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
            @Override
            public void run() {
                synchronized(ImmersivePlanets.class) {
                    try {
                        for(WorldType worldType : WorldType.values()) {
                            ArrayList<Sprite> planetTextures = new ArrayList<>();
                            String typeName = worldType.toString().toLowerCase();
                            for(TextureUtils.PlanetTextureResolution res : TextureUtils.PlanetTextureResolution.values()) {
                                planetTextures.add(StarLoaderTexture.newSprite(ImageIO.read(ImmersivePlanets.getInstance().getJarResource("thederpgamer/immersiveplanets/resources/textures/planet/" + typeName + "_" + res.level + ".png")), ImmersivePlanets.getInstance(), typeName + "_" + res.level));
                            }
                            TextureUtils.planetTextures.put(worldType, planetTextures);
                        }
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initialize() {
        File dataFolder = new File(getSkeleton().getResourcesFolder() + "/data");
        if(!dataFolder.exists()) dataFolder.mkdirs();

        File instanceFolder = new File(getSkeleton().getResourcesFolder() + "/data/" + GameCommon.getUniqueContextId());
        if(!instanceFolder.exists()) instanceFolder.mkdirs();

        playerDataFolder = new File(getSkeleton().getResourcesFolder() + "/data/" + GameCommon.getUniqueContextId() + "/playerdata");
        if(!playerDataFolder.exists()) playerDataFolder.mkdirs();

        planetDataFolder = new File(getSkeleton().getResourcesFolder() + "/data/" + GameCommon.getUniqueContextId() + "/planetdata");
        if(!planetDataFolder.exists()) planetDataFolder.mkdirs();

        chunkDataFolder = new File(getSkeleton().getResourcesFolder() + "/data/" + GameCommon.getUniqueContextId() + "/chunkdata");
        if(!chunkDataFolder.exists()) chunkDataFolder.mkdirs();
    }

    private void registerFastListeners() {
        FastListenerCommon.planetDrawListeners.add(planetDrawer = new PlanetDrawer());
    }

    private void registerEventListeners() {
        StarLoader.registerListener(PlanetGenerateEvent.class, new Listener<PlanetGenerateEvent>() {
            @Override
            public void onEvent(PlanetGenerateEvent event) {
                PlanetSpawnHandler.handlePlanetCreation(event.getCreatorThread(), event.getRequestData(), event.getFactory(), event.getSegment());
            }
        }, this);
    }
}
