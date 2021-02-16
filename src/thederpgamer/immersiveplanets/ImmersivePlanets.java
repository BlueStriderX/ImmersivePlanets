package thederpgamer.immersiveplanets;

import api.listener.Listener;
import api.listener.events.controller.planet.PlanetGenerateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.graphics.planet.PlanetAtmosphereDrawer;
import thederpgamer.immersiveplanets.graphics.planet.PlanetSprite;
import thederpgamer.immersiveplanets.listeners.PlanetDrawHandler;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import thederpgamer.immersiveplanets.universe.generation.world.PlanetSpawnController;
import javax.imageio.ImageIO;
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
    public PlanetAtmosphereDrawer planetDrawer;
    public GameResourceLoader resLoader;

    //Config
    private final String[] defaultConfig = {
            "debug-mode: false",
            "instanced-sector-dist: 10000"
    };
    public boolean debugMode = false;
    public int instancedSectorDist = 10000;

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        initialize();
        loadTextures();
        registerFastListeners();
        registerEventListeners();
    }

    @Override
    public void onLoadModels() {
        final GameResourceLoader resLoader = (GameResourceLoader) Controller.getResLoader();
        String[] models = new String[] {
                "debug_planet_0"
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

        config.saveConfig();
    }

    private void initialize() {
        resLoader = (GameResourceLoader) Controller.getResLoader();
        TextureUtils.initialize();
    }

    private void loadTextures() {
        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
            @Override
            public void run() {
                synchronized(ImmersivePlanets.class) {
                    try {
                        for(WorldType worldType : WorldType.values()) {
                            ArrayList<Sprite> planetTextures = new ArrayList<>();
                            PlanetSprite planetSprite = new PlanetSprite(worldType);
                            String typeName = worldType.toString().toLowerCase().replaceAll("_", "-");
                            for(TextureUtils.PlanetTextureResolution res : TextureUtils.PlanetTextureResolution.values()) {
                                planetSprite.spriteMap.put(res.getRes(), StarLoaderTexture.newSprite(ImageIO.read(ImmersivePlanets.getInstance().getJarResource("thederpgamer/immersiveplanets/resources/sprites/planet/" + typeName + "_" + res.level + ".png")), ImmersivePlanets.getInstance(), typeName + "_" + res.level));
                                planetTextures.add(StarLoaderTexture.newSprite(ImageIO.read(ImmersivePlanets.getInstance().getJarResource("thederpgamer/immersiveplanets/resources/textures/planet/" + typeName + "_" + res.level + ".png")), ImmersivePlanets.getInstance(), typeName + "_" + res.level));
                            }
                            TextureUtils.planetSprites.put(worldType, planetSprite);
                            TextureUtils.getAllPlanetTextures().put(worldType, planetTextures);
                        }
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void registerFastListeners() {
        FastListenerCommon.planetDrawListeners.add(new PlanetDrawHandler());
    }

    private void registerEventListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(planetDrawer = new PlanetAtmosphereDrawer());
                for(PlanetSprite planetSprite : TextureUtils.planetSprites.values()) {
                    event.getModDrawables().add(planetSprite);
                }
            }
        }, this);

        StarLoader.registerListener(PlanetGenerateEvent.class, new Listener<PlanetGenerateEvent>() {
            @Override
            public void onEvent(PlanetGenerateEvent event) {
                PlanetSpawnController.handlePlanetCreation(event.getCreatorThread(), event.getRequestData(), event.getFactory(), event.getSegment());
            }
        }, this);
    }
}
