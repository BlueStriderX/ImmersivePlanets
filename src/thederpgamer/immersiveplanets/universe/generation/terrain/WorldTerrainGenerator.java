package thederpgamer.immersiveplanets.universe.generation.terrain;

import org.schema.game.common.data.world.Segment;
import org.schema.game.common.data.world.SegmentDataWriteException;
import org.schema.game.server.controller.RequestData;
import org.schema.game.server.controller.world.factory.planet.structures.TerrainStructureList;
import org.schema.game.server.controller.world.factory.planet.terrain.TerrainGenerator;

/**
 * WorldTerrainGenerator.java
 * New terrain generation base
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class WorldTerrainGenerator extends TerrainGenerator {

    public WorldTerrainGenerator(int i, float v) {
        super(i, v);
    }

    @Override
    public TerrainStructureList generateSegment(Segment segment, RequestData requestData) throws SegmentDataWriteException {
        return null;
    }

    @Override
    public void generateLOD() {

    }
}
