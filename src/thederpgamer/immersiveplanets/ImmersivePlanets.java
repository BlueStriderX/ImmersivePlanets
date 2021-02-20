package thederpgamer.immersiveplanets;

import api.DebugFile;
import api.common.GameCommon;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.textures.StarLoaderTexture;
import org.apache.commons.io.IOUtils;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.utils.DataUtils;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
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

    //Data
    public File playerDataFolder;
    public File planetDataFolder;
    public File chunkDataFolder;

    //Resources
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
        if(className.endsWith("PlanetDrawer") || className.endsWith("PlanetCreatorThread") || className.endsWith("Planet")) {
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
                        resLoader.getMeshLoader().loadModMesh(ImmersivePlanets.getInstance(), model, getJarResource("thederpgamer/immersiveplanets/resources/models/planet/" + model + ".zip"), null);
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

        DataUtils.loadPlanets();
    }

    private void registerFastListeners() {
        //FastListenerCommon.planetDrawListeners.add(planetDrawerOld = new PlanetDrawerOld());
    }

    private void registerEventListeners() {
        /*
        StarLoader.registerListener(PlanetGenerateEvent.class, new Listener<PlanetGenerateEvent>() {
            @Override
            public void onEvent(PlanetGenerateEvent event) {
                PlanetSpawnHandler.handlePlanetCreation(event.getCreatorThread(), event.getRequestData(), event.getFactory(), event.getSegment());
            }
        }, this);
         */
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
}
