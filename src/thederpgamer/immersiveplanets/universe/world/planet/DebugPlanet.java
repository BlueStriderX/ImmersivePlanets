package thederpgamer.immersiveplanets.universe.world.planet;

import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.world.Planet;

/**
 * DebugPlanet.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class DebugPlanet extends Planet {

    public DebugPlanet(float radius) {
        super(radius, WorldType.PLANET_DEBUG);
    }
}
