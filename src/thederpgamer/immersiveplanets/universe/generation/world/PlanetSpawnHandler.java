package thederpgamer.immersiveplanets.universe.generation.world;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.generator.PlanetCreatorThread;
import org.schema.game.common.data.world.Segment;
import org.schema.game.server.controller.RequestDataPlanet;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetFactory;
import thederpgamer.immersiveplanets.universe.space.planet.DebugPlanet;
import thederpgamer.immersiveplanets.utils.DataUtils;

/**
 * PlanetSpawnHandler.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class PlanetSpawnHandler {

    public static void handlePlanetCreation(PlanetCreatorThread creatorThread, RequestDataPlanet requestData, WorldCreatorPlanetFactory factory, Segment segment) {
        //Todo: Debug type is only for testing purposes, should generate actual planet type based on factors like temperature and location
        if(DataUtils.getFromSector(creatorThread.getSegmentController().getSector(new Vector3i())) == null) {
            DebugPlanet planet = new DebugPlanet(500, DataUtils.getNewPlanetId(), creatorThread.getSegmentController().getSector(new Vector3i()));
            planet.initialize();

            //creatorThread.getSegmentController().setMarkedForDeletePermanentIncludingDocks(true);
        }
    }
}
