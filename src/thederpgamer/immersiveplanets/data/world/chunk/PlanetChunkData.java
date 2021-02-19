package thederpgamer.immersiveplanets.data.world.chunk;

import thederpgamer.immersiveplanets.data.other.vector.Vector2i;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

/**
 * PlanetChunkData.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public class PlanetChunkData implements Serializable {

    private Vector2i chunkPos;
    private DataOutputStream dataStream;

    public PlanetChunkData(DynamicChunk chunk, Planet planet) {
        this.chunkPos = chunk.getChunkPos();
        this.dataStream = getChunkDataStream(planet.planetId);
    }

    public Vector2i getChunkPos() {
        return chunkPos;
    }

    private DataOutputStream getChunkDataStream(long planetId) {
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(DataUtils.getChunkDataFile(planetId)));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return outputStream;
    }
}
