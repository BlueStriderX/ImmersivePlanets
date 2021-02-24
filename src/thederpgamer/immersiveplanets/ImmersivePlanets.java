package thederpgamer.immersiveplanets;

import api.DebugFile;
import api.listener.Listener;
import api.listener.events.controller.planet.PlanetGenerateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.textures.StarLoaderTexture;
import org.apache.commons.io.IOUtils;
import org.schema.game.client.view.GameResourceLoader;
import thederpgamer.immersiveplanets.data.server.UniverseDatabase;
import thederpgamer.immersiveplanets.graphics.texture.TextureLoader;
import thederpgamer.immersiveplanets.graphics.universe.WorldEntityDrawer;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    //Resources
    public GameResourceLoader resLoader;
    public WorldEntityDrawer worldEntityDrawer;

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
    public void onLoad() {
        forceDefine("thederpgamer.immersiveplanets.ImmersivePlanets$1");
    }

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
    public byte[] onClassTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) {
        if(className.endsWith("PlanetCreatorThread") || className.endsWith("PlanetOld") || className.endsWith("PlanetDrawer") || className.endsWith("PlanetShaderable") || className.endsWith("AtmoShaderable")) {
            return overwriteClass(className, byteCode);
        } else {
            return super.onClassTransform(loader, className, classBeingRedefined, protectionDomain, byteCode);
        }
    }

    /*
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
                        resLoader.getMeshLoader().loadModMesh(ImmersivePlanets.getInstance(), model, getJarResource("thederpgamer/immersiveplanets/resources/models/planetOld/" + model + ".zip"), null);
                    } catch(ResourceException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
     */

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
                            String[] imageNames = getImageNames(worldType);
                            for(String imageName : imageNames) {
                                TextureLoader.addMaterial(imageName, StarLoaderTexture.newSprite(ImageIO.read(getJarResource("thederpgamer/immersiveplanets/resources/textures/planet/" + imageName + ".png")), ImmersivePlanets.getInstance(), imageName));
                            }
                        }
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initialize() {
        UniverseDatabase.loadAllData();
    }

    private void registerFastListeners() {
        //FastListenerCommon.planetDrawListeners.add(planetDrawerOld = new PlanetDrawerOld());
    }

    private void registerEventListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(worldEntityDrawer = new WorldEntityDrawer());
            }
        }, this);

        StarLoader.registerListener(PlanetGenerateEvent.class, new Listener<PlanetGenerateEvent>() {
            @Override
            public void onEvent(PlanetGenerateEvent event) {
                UniverseDatabase.addNewWorld(event.getCreatorThread(), event.getFactory());
            }
        }, this);
    }

    private byte[] overwriteClass(String className, byte[] byteCode) {
        byte[] bytes = null;
        try {
            ZipInputStream file = new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
            while (true) {
                ZipEntry nextEntry = file.getNextEntry();
                if(nextEntry == null) break;
                if(nextEntry.getName().endsWith(className + ".class")) {
                    bytes = IOUtils.toByteArray(file);
                }
            }
            file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes != null) {
            DebugFile.log("[ImmersivePlanets]: Overwrote Class " + className, this);
            return bytes;
        } else {
            return byteCode;
        }
    }

    private String[] getImageNames(WorldType worldType) {
        String typeString = worldType.toString().toLowerCase();
        return new String[] {
                typeString + "_atmosphere",
                typeString + "_clouds",
                typeString + "_64",
                typeString + "_256",
                typeString + "_512"
        };
    }
}
