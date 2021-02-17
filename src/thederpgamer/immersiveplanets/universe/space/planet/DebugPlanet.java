package thederpgamer.immersiveplanets.universe.space.planet;

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

    public DebugPlanet(float radius, long worldId) {
        super(radius, WorldType.PLANET_DEBUG, worldId);
    }
}
