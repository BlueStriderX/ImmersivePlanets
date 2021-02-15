package thederpgamer.immersiveplanets.universe.generation.world;

import api.common.GameClient;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.generator.PlanetCreatorThread;
import org.schema.game.common.data.world.Segment;
import org.schema.game.server.controller.RequestDataPlanet;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetFactory;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import thederpgamer.immersiveplanets.universe.space.planet.DebugPlanet;

/**
 * PlanetSpawnController.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class PlanetSpawnController {

    public static void handlePlanetCreation(PlanetCreatorThread creatorThread, RequestDataPlanet requestData, WorldCreatorPlanetFactory factory, Segment segment) {
        //Todo: Debug type is only for testing purposes, should generate actual planet type based on factors like temperature and location
        WorldType type = WorldType.PLANET_DEBUG;
        DebugPlanet planet = new DebugPlanet(500);

        planet.planetSector = creatorThread.getSegmentController().getSector(new Vector3i());
        planet.planetSprite = TextureUtils.planetSprites.get(type);
        planet.initialize();

        Vector3i clientSector = GameClient.getClientPlayerState().getCurrentSector();
        int distance = (int) Math.abs(Math.floor(Vector3i.getDisatance(clientSector, planet.planetSector)));
        if(distance < 5) {
            ImmersivePlanets.getInstance().planetDrawer.queueDraw(planet);
        }

        //creatorThread.getSegmentController().setMarkedForDeletePermanentIncludingDocks(true);
    }
}
