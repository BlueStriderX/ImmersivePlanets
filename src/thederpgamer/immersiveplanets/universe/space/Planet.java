package thederpgamer.immersiveplanets.universe.space;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.file.PlanetData;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import javax.vecmath.Vector4f;

/**
 * Planet.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class Planet {

    public long planetId;
    public float radius;
    public Vector3i planetSector;

    public WorldType worldType;
    public PlanetSegment[] segments;

    public Mesh atmosphereMesh;
    public BoundingSphere outerSphere;
    public BoundingSphere innerSphere;

    public int factionId;
    public String name;

    public Planet(PlanetData planetData) {
        this(planetData.getRadius(), planetData.getType(), planetData.getPlanetId(), planetData.getFactionId(), planetData.getName());
    }

    public Planet(float radius, WorldType worldType, long planetId, int factionId, String name) {
        this.planetId = planetId;
        this.radius = radius;
        this.segments = new PlanetSegment[20];
        this.worldType = worldType;
        //for(TextureUtils.PlanetTextureResolution resolution : TextureUtils.PlanetTextureResolution.values()) {
            //String meshName = worldType.toString().toLowerCase() + "_" + resolution.level;
            //atmosphereMeshes.put(resolution.level, ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), meshName));
        //}
        atmosphereMesh = ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), "planet_debug_0");
        (outerSphere = new BoundingSphere(radius)).setColor(new Vector4f(0.75f, 0.75f, 0.75f, 0.3f));
        (innerSphere = new BoundingSphere(radius * 0.9f)).setColor(new Vector4f(0.35f, 0.35f, 0.35f, 0.5f));

        this.factionId = factionId;
        this.name = name;
        initialize();
    }

    public Planet(float radius, WorldType worldType, long planetId) {
        this(radius, worldType, planetId, 0, "Planet");
    }

    public void initialize() {
        atmosphereMesh.onInit();
        outerSphere.onInit();
        innerSphere.onInit();
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

    public PlanetData getData() {
        return new PlanetData(this);
    }
}
