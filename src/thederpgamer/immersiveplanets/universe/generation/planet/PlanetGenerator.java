package thederpgamer.immersiveplanets.universe.generation.planet;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.Chunk16SegmentData;
import org.schema.game.common.data.world.Segment;
import org.schema.game.common.data.world.SegmentDataWriteException;
import org.schema.game.server.controller.RequestData;
import org.schema.game.server.controller.RequestDataPlanet;
import org.schema.game.server.controller.world.factory.WorldCreatorFactory;
import org.schema.game.server.controller.world.factory.terrain.TerrainGenerator;
import thederpgamer.immersiveplanets.universe.generation.world.PlanetGenerationHandler;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.world.WorldEntity;

/**
 * PlanetGenerator.java
 * New planetOld generation base
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public abstract class PlanetGenerator extends WorldCreatorFactory {

    public boolean initialized;
    public float radius;
    public long seed;
    public Vector3i sector;
    public WorldType worldType;

    public PlanetGenerationHandler generator;
    public WorldEntity worldEntity;

    public PlanetGenerator(float radius, long seed, Vector3i sector, WorldType worldType) {
        this.initialized = false;
        this.radius = radius;
        this.seed = seed;
        this.sector = sector;
        this.worldType = worldType;
    }

    @Override
    public void createWorld(SegmentController planetEntity, Segment segment, RequestData requestData) {
        synchronized(this) {
            if(!initialized) {
                initialize(planetEntity);
            }
        }

        try {
            generate(segment, (RequestDataPlanet) requestData);
        } catch (SegmentDataWriteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean predictEmpty() {
        return false;
    }

    public void initialize(SegmentController world){
        generator = new PlanetGenerationHandler(((org.schema.game.common.controller.Planet) world).getSeed());
        generator.setPlanetGenerator(this);
        init(world);

        initialized = true;
    }

    public void generate(Segment segment, RequestDataPlanet requestData) throws SegmentDataWriteException {
        if(!requestData.done) {
            requestData.initWith(segment);

            for(int in = 0; in < requestData.segs.length; in ++){
                requestData.index = in;

                for(int i = 0; i < requestData.getR().segmentData.length; i ++){
                    Chunk16SegmentData c = requestData.getR().segmentData[i];

                    generator.generateSegment(c, c.getSegmentPos(),
                            64 + (c.getSegmentPos().x / TerrainGenerator.SEG),
                            (Math.abs(c.getSegmentPos().y) / TerrainGenerator.SEG),
                            64 + (c.getSegmentPos().z / TerrainGenerator.SEG), (c.getSegmentPos().y < 0), requestData);
                    generator.checkRegionHooks(segment, requestData);
                }
            }
            requestData.done = true;
        }
        requestData.applyTo(segment);
    }

    public void init(SegmentController worldEntity) {
        /*
        Random r = new Random(seed);
        if (r.nextInt(ServerConfig.PLANET_SPECIAL_REGION_PROBABILITY.getInt()) == 0 || world.forceSpecialRegion) {
            createAdditionalRegions(r);
        }

         */

    }
}
