package thederpgamer.immersiveplanets.data.world.chunk;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.data.other.vector.Vector2i;

/**
 * DynamicChunkQueue.java
 * <Description>
 * ==================================================
 * Created 02/16/2021
 * @author TheDerpGamer
 */
public class DynamicChunkQueue {

    private DynamicChunk current;

    public DynamicChunkQueue(DynamicChunk current) {
        this.current = current;
    }

    public DynamicChunk getCurrent() {
        return current;
    }

    public DynamicChunk cycleNext(Vector2i direction) {
        current = current.getNext(direction);
        return getCurrent();
    }

    public DynamicChunk cycleNext(Vector3i direction) {
        return cycleNext(new Vector2i(direction.x, direction.z));
    }

    public DynamicChunk peekNext(Vector2i direction) {
        return current.getNext(direction);
    }

    public DynamicChunk peekNext(Vector3i direction) {
        return current.getNext(new Vector2i(direction.x, direction.z));
    }
}
