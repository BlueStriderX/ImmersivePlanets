package thederpgamer.immersiveplanets.universe.space;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.file.PlanetData;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;

/**
 * Planet.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class Planet {

    public long planetId;
    public int radius;
    public Vector3i planetSector;

    public WorldType worldType;
    public PlanetSegment[] segments;
    public int factionId;

    public Planet(PlanetData planetData) {
        this(planetData.getRadius(), planetData.getType(), planetData.getPlanetId(), planetData.getFactionId(), planetData.getSector());
    }

    public Planet(int radius, WorldType worldType, long planetId, int factionId, Vector3i planetSector) {
        this.planetId = planetId;
        this.radius = radius;
        this.segments = new PlanetSegment[20];
        this.worldType = worldType;
        //for(TextureUtils.PlanetTextureResolution resolution : TextureUtils.PlanetTextureResolution.values()) {
            //String meshName = worldType.toString().toLowerCase() + "_" + resolution.level;
            //atmosphereMeshes.put(resolution.level, ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), meshName));
        //}

        this.planetSector = planetSector;
        this.factionId = factionId;
    }

    public Planet(int radius, WorldType worldType, long planetId, Vector3i planetSector) {
        this(radius, worldType, planetId, 0, planetSector);
    }

    public void initialize() {
        new PlanetData(this);
    }

    public Vector3i getRealSector() {
        return new Vector3i(addDistance(planetSector.x), addDistance(planetSector.y), addDistance(planetSector.z));
    }

    private int addDistance(int i) {
        if(i >= 0) {
            return i + ImmersivePlanets.getInstance().instancedSectorDist;
        } else {
            return i + (ImmersivePlanets.getInstance().instancedSectorDist * -1);
        }
    }
}
