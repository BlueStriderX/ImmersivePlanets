package thederpgamer.immersiveplanets.universe.world;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.Chunk16SegmentData;
import org.schema.game.server.controller.world.factory.terrain.TerrainGenerator;
import java.util.Random;

/**
 * PlanetSegment.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetSegment {

    public Chunk16SegmentData[] segmentData = new Chunk16SegmentData[8];

    public PlanetSegment() {
        for(int i = 0; i < segmentData.length; i ++) {
            segmentData[i] = new Chunk16SegmentData();
        }
    }


    public final Vector3i cachePos = new Vector3i();
    public short[] data = new short[32768 / (8 / TerrainGenerator.plateauHeight)];
    public boolean created = false;
    public Random rand = new Random();
    public double noiseSmall8[];
    public double noise1Big16[];
    public double noise2Big16[];
    public double noise2DMid16[];
    public final Vector3i p = new Vector3i();
    public final Vector3i pFac = new Vector3i();
    public double noiseArray[];
    public float mar;
    public int miniblock;
    public double rScale = 1;
    public float normMax = 4;
    public float normMin = -0.55f;
    public float radius;

    public void reset() {
        created = false;
        for(int i = 0; i < segmentData.length; i ++) {
            segmentData[i].resetFast();
        }
    }

    public void set(int x, int y, int z) {
        cachePos.set(x, y, z);
        for(int i = 0; i < segmentData.length; i ++) {
            segmentData[i].segmentPos.set(x, i * 16, z);
            segmentData[i].setBlockAddedForced(false);
        }
    }
}
