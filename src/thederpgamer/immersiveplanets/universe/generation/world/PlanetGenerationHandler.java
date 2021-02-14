package thederpgamer.immersiveplanets.universe.generation.world;

import org.schema.game.server.controller.world.factory.terrain.TerrainGenerator;
import thederpgamer.immersiveplanets.universe.generation.planet.PlanetGenerator;

/**
 * PlanetGenerationHandler.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetGenerationHandler extends TerrainGenerator {

    private PlanetGenerator planetGenerator;

    public PlanetGenerationHandler(long seed) {
        super(seed);
    }

    public void setPlanetGenerator(PlanetGenerator planetGenerator) {
        this.planetGenerator = planetGenerator;
    }
}
