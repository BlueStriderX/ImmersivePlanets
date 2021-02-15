package thederpgamer.immersiveplanets.universe.space;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.graphics.planet.PlanetSprite;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;

import javax.vecmath.Vector4f;

/**
 * Planet.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public abstract class Planet {

    public float radius;
    public Vector3i planetSector;

    public WorldType type;
    public PlanetSegment[] segments;
    public PlanetSprite planetSprite;

    public BoundingSphere outerSphere;
    public BoundingSphere innerSphere;

    public Planet(float radius, WorldType type) {
        this(radius,12, type);
    }

    public Planet(float radius, int segmentCount, WorldType type) {
        this.radius = radius;
        this.segments = new PlanetSegment[segmentCount];
        this.type = type;
        (outerSphere = new BoundingSphere(radius * 1.15f)).setColor(new Vector4f(0.75f, 0.75f, 0.75f, 0.3f));
        (innerSphere = new BoundingSphere(radius)).setColor(new Vector4f(0.35f, 0.35f, 0.35f, 0.5f));
    }

    public void initialize() {
        planetSprite.onInit();
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
}
