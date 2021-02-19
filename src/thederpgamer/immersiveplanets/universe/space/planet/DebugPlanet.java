package thederpgamer.immersiveplanets.universe.space.planet;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;

/**
 * DebugPlanet.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class DebugPlanet extends Planet {

    public DebugPlanet(int radius, long worldId, Vector3i planetSector) {
        super(radius, WorldType.PLANET_DEBUG, worldId, planetSector);
    }
}
