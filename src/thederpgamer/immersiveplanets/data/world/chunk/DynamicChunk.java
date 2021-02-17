package thederpgamer.immersiveplanets.data.world.chunk;

import org.schema.game.common.data.world.Chunk16SegmentData;
import thederpgamer.immersiveplanets.data.other.vector.Vector2i;
import thederpgamer.immersiveplanets.data.world.AdjacentPos;

/**
 * DynamicChunk.java
 * <Description>
 * ==================================================
 * Created 02/16/2021
 * @author TheDerpGamer
 */
public class DynamicChunk {

    private Chunk16SegmentData chunkData;
    private DynamicChunk[] adjacentChunks;
    private Vector2i chunkPos;

    public DynamicChunk() {
        this(new Chunk16SegmentData(), new Vector2i());
    }

    public DynamicChunk(Chunk16SegmentData chunkData, Vector2i chunkPos) {
        this.chunkData = chunkData;
        this.adjacentChunks = new DynamicChunk[8];
        this.chunkPos = chunkPos;
    }

    public Chunk16SegmentData getChunkData() {
        return chunkData;
    }

    public DynamicChunk[] getAdjacentChunks() {
        return adjacentChunks;
    }

    public DynamicChunk getAdjacent(int i) {
        return adjacentChunks[i];
    }

    public DynamicChunk getNext(Vector2i direction) {
        return getAdjacent(AdjacentPos.getNext(direction).pos);
    }

    public Vector2i getChunkPos() {
        return chunkPos;
    }
}
