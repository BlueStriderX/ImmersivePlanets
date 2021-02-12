package old;

import api.DebugFile;
import api.common.GameClient;
import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.draw.PlanetDrawEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.network.packets.PacketUtil;
import net.dovtech.immersiveplanets.commands.DebugCreateGasGiantCommand;
import net.dovtech.immersiveplanets.commands.DebugSphereCommand;
import net.dovtech.immersiveplanets.graphics.CelestialBodyDrawer;
import net.dovtech.immersiveplanets.network.client.ClientAtmoKillSendPacket;
import net.dovtech.immersiveplanets.universe.BodyType;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.planetdrawer.PlanetDrawer;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import java.util.Random;

public class ImmersivePlanets extends StarMod {

    static ImmersivePlanets inst;

    public ImmersivePlanets() {
        inst = this;
    }

    //Other
    private int clientViewDistance = 1000;
    private GameClientState clientState;
    private StarPlayer player;
    public boolean drawDebugSpheres;
    public CelestialBodyDrawer bodyDrawer;

    //Config
    private FileConfiguration config;
    private String[] defaultConfig = {
            "debug-mode: false",
            "sky-offset: 1.3",
            "reduce-draw-on-planets: true",
            "universe-chunk-view-distance: 250",
            "gas-giant-generation-chance: 0.15",
            "gas-giant-min-radius: 450",
            "gas-giant-max-radius: 800",
            "gas-giant-max-rings: 5",
            "gas-giant-ring-generation-chance: 0.2",
            "gas-giant-max-moons: 2",
            "gas-giant-moon-generation-chance: 0.15"
    };

    //Config Settings
    public boolean debugMode = true;
    public float skyOffset = 1.3f;
    public boolean reduceDrawOnPlanets = true;
    public int planetChunkViewDistance = 250;
    public float gasGiantGenerationChance = 0.15f;
    public int gasGiantMinRadius = 450;
    public int gasGiantMaxRadius = 800;
    public int gasGiantMaxRings = 5;
    public float gasGiantRingGenerationChance = 0.2f;
    public int gasGiantMaxMoons = 2;
    public float gasGiantMoonGenerationChance = 0.15f;

    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        inst = this;
        setModName("ImmersivePlanets");
        setModAuthor("Dovtech");
        setModVersion("0.6.1");
        setModDescription("Adds larger and more immersive planets with their own atmospheres and features.");

        if (GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) initConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer())
            clientViewDistance = (int) EngineSettings.G_MAX_SEGMENTSDRAWN.getCurrentState();

        registerOverwrites();
        registerListeners();
        registerCommands();
        registerPackets();

        DebugFile.log("Enabled", this);
    }

    /*
    @Override
    public void onBlockConfigLoad(BlockConfig config) {

    }
     */

    private void registerOverwrites() {
        overwriteClass(PlanetDrawer.class);
    }

    private void registerPackets() {
        PacketUtil.registerPacket(ClientAtmoKillSendPacket.class);
    }

    private void registerCommands() {
        if (debugMode) {
            StarLoader.registerCommand(new DebugSphereCommand());
            StarLoader.registerCommand(new DebugCreateGasGiantCommand());
        }
    }

    private void registerListeners() {

        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                bodyDrawer = new CelestialBodyDrawer(GameClient.getClientState());
                event.getModDrawables().add(bodyDrawer);
            }
        });

        StarLoader.registerListener(PlanetDrawEvent.class, new Listener<PlanetDrawEvent>() {
            @Override
            public void onEvent(PlanetDrawEvent event) {
                if (clientState == null || player == null) {
                    clientState = GameClient.getClientState();
                    player = new StarPlayer(GameClient.getClientPlayerState());
                }
                Random random = new Random();
                boolean spawnGasGiant = false;
                if (!DataUtils.gasGiants.containsKey(event.getSector())) {
                    if (gasGiantGenerationChance > 0 && gasGiantGenerationChance <= 1) {
                        int generationChance = random.nextInt(100 - 1) + 1;
                        if (gasGiantGenerationChance <= (float) generationChance / 100) {
                            spawnGasGiant = true;
                        }

                    }
                }

                if (spawnGasGiant) {
                    PlanetDrawer.getInstance().sector = event.getSector();
                    PlanetDrawer.getInstance().bodyType = BodyType.GAS_GIANT_ORANGE;
                    /*
                    int radius = random.nextInt(gasGiantMaxRadius = gasGiantMinRadius) + gasGiantMinRadius;

                    int ringCount = 0;
                    int ringTurns = 0;
                    int ringsRandom = random.nextInt(gasGiantMaxRings);
                    while (ringTurns <= gasGiantMaxRings) {
                        if (gasGiantRingGenerationChance <= (float) ringsRandom / 100) {
                            ringCount++;
                        }
                        ringTurns++;
                    }

                    int moonCount = 0;
                    int moonTurns = 0;
                    int moonRandom = random.nextInt(gasGiantMaxMoons);
                    while (moonTurns <= gasGiantMaxMoons) {
                        if (gasGiantMoonGenerationChance <= (float) moonRandom / 100) {
                            moonCount++;
                        }
                        moonTurns++;
                    }

                    BodyType bodyType = BodyType.GAS_GIANT_ORANGE;

                    Color4f atmosphereColor = new Color4f(0.204f, 0.140f, 0.88f, 1.0f);
                    GasGiant gasGiant = new GasGiant(event.getSector(), bodyType, radius, moonCount, ringCount, 4.5f, radius * 100, atmosphereColor);
                    DataUtils.gasGiants.put(event.getSector(), gasGiant);
                    spawnGasGiant = false;
                    forceGasGiant = false;
                    event.getSphere().setVisibility(2);
                    event.getSphere().cleanUp();
                    event.getDodecahedron().cleanUp();

                     */
                    /*
                    gasGiantOld = new GasGiantOld(event.getSector(), radius, ringCount, moonCount, 1, atmosphereColor);
                    gasGiantOld.onInit();

                     */
                        /*
                        event.getPlanetInfo().setRadius(radius);
                        event.getPlanetInfo().setHasCloud(true);
                        event.getPlanetInfo().setAtmosphereDensity(3.5f);
                        event.getPlanetInfo().setAtmosphereColor(atmosphereColor);
                        event.getPlanetInfo().setAtmosphereSizeInUnity(radius * 1.15f);'
                         */
                }

            }
        });

        DebugFile.log("Registered Listeners!", this);
    }

    private void initConfig() {
        this.config = getConfig("config");
        this.config.saveDefault(defaultConfig);

        this.debugMode = Boolean.parseBoolean(config.getString("debug-mode"));
        this.skyOffset = (float) config.getDouble("sky-offset");
        this.reduceDrawOnPlanets = Boolean.parseBoolean(config.getString("reduce-draw-on-planets"));
        this.planetChunkViewDistance = config.getInt("universe-chunk-view-distance");
        this.gasGiantGenerationChance = (float) config.getDouble("gas-giant-generation-chance");
        this.gasGiantMinRadius = config.getInt("gas-giant-min-radius");
        this.gasGiantMaxRadius = config.getInt("gas-giant-max-radius");
        this.gasGiantMaxRings = config.getInt("gas-giant-max-rings");
        this.gasGiantRingGenerationChance = (float) config.getDouble("gas-giant-ring-generation-chance");
        this.gasGiantMaxMoons = config.getInt("gas-giant-max-moons");
        this.gasGiantMoonGenerationChance = (float) config.getDouble("gas-giant-moon-generation-chance");
    }

    public static ImmersivePlanets getInstance() {
        return inst;
    }
}
