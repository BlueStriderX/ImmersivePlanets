package thederpgamer.immersiveplanets;

import api.listener.Listener;
import api.listener.events.controller.planet.PlanetGenerateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import thederpgamer.immersiveplanets.graphics.planet.PlanetAtmosphereDrawer;
import thederpgamer.immersiveplanets.listeners.PlanetDrawHandler;
import thederpgamer.immersiveplanets.resources.textures.TextureLoader;
import thederpgamer.immersiveplanets.universe.generation.world.PlanetSpawnController;

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
        registerFastListeners();
        registerEventListeners();
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
        TextureLoader.initialize();
    }

    private void registerFastListeners() {
        FastListenerCommon.planetDrawListeners.add(new PlanetDrawHandler());
    }

    private void registerEventListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(planetDrawer = new PlanetAtmosphereDrawer());
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
