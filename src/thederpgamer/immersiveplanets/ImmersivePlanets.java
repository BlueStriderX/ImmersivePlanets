package thederpgamer.immersiveplanets;

import api.DebugFile;
import api.listener.Listener;
import api.listener.events.world.PlanetCreateEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.StarRunnable;
import api.utils.textures.StarLoaderTexture;
import org.apache.commons.io.IOUtils;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.immersiveplanets.data.server.UniverseDatabase;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ImmersivePlanets.java
 * ImmersivePlanets main class
 * ==================================================
 * Created 02/12/2021
 *
 * @author TheDerpGamer
 */
public class ImmersivePlanets extends StarMod {

    public ImmersivePlanets() {
    }

    public static void main(String[] args) {
    }

    private static ImmersivePlanets instance;

    public static ImmersivePlanets getInstance() {
        return instance;
    }

    //Data
    private final String texturePath = "thederpgamer/immersiveplanets/resources/textures/planet/";
    public HashMap<String, Sprite> spriteMap = new HashMap<>();

    //Config
    private final String[] defaultConfig = {
            "debug-mode: false",
            "instanced-sector-dist: 10000"
    };
    public boolean debugMode = false;
    public int instancedSectorDist = 10000;

    @Override
    public void onLoad() {
        forceDefine("thederpgamer.immersiveplanets.ImmersivePlanets$1");
    }

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        initialize();
        registerEventListeners();
    }

    @Override
    public byte[] onClassTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) {
        if (className.endsWith("PlanetCreatorThread") || className.endsWith("PlanetCore") || className.endsWith("PlanetDrawer") || className.endsWith("PlanetShaderable") || className.endsWith("AtmoShaderable")) {
            return overwriteClass(className, byteCode);
        } else {
            return super.onClassTransform(loader, className, classBeingRedefined, protectionDomain, byteCode);
        }
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {
        String[] models = {
                "planet_sphere"
        };

        for (String model : models) {
            try {
                loader.getMeshLoader().loadModMesh(ImmersivePlanets.getInstance(), model, getJarResource("thederpgamer/immersiveplanets/resources/models/planet/" + model + ".zip"), null);
            } catch (ResourceException | IOException e) {
                e.printStackTrace();
            }
        }

        for (WorldType worldType : WorldType.values()) {
            try {
                String imageName = worldType.name + "_texture";
                spriteMap.put(imageName, StarLoaderTexture.newSprite(ImageIO.read(getJarResource(texturePath + imageName + ".png")), ImmersivePlanets.getInstance(), imageName));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void initConfig() {
        FileConfiguration config = getConfig("config");
        config.saveDefault(defaultConfig);

        debugMode = config.getConfigurableBoolean("debug-mode", false);
        instancedSectorDist = config.getConfigurableInt("instanced-sector-dist", 10000);
        if (instancedSectorDist < 5000) {
            config.set("instanced-sector-dist", 5000);
            instancedSectorDist = 5000;
        }

        config.saveConfig();
    }

    private void initialize() {
        UniverseDatabase.loadAllData();

        new StarRunnable() {
            @Override
            public void run() {
                UniverseDatabase.saveAllData();
            }
        }.runTimer(this, 3000);
    }

    private void registerEventListeners() {
        StarLoader.registerListener(PlanetCreateEvent.class, new Listener<PlanetCreateEvent>() {
            @Override
            public void onEvent(PlanetCreateEvent event) {
                UniverseDatabase.addNewWorld(event.getPlanetCore(), event.getSectorPos());
            }
        }, this);
    }

    private byte[] overwriteClass(String className, byte[] byteCode) {
        byte[] bytes = null;
        try {
            ZipInputStream file = new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
            while (true) {
                ZipEntry nextEntry = file.getNextEntry();
                if (nextEntry == null) break;
                if (nextEntry.getName().endsWith(className + ".class")) {
                    bytes = IOUtils.toByteArray(file);
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytes != null) {
            DebugFile.log("[ImmersivePlanets]: Overwrote Class " + className, this);
            return bytes;
        } else {
            return byteCode;
        }
    }
}
