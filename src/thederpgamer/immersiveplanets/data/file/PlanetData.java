package thederpgamer.immersiveplanets.data.file;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.data.other.vector.Vector2i;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import java.io.File;
import java.io.IOException;

/**
 * PlanetData.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public class PlanetData extends DataFile {

    public PlanetData(File dataFile) {
        super(dataFile);
        initialize(getPlanet());
    }

    public PlanetData(Planet planet) {
        super(DataUtils.getPlanetDataFile(planet.planetId));
        initialize(planet);
    }

    private void initialize(Planet planet) {
        setValue("planetId", planet.planetId);
        setValue("factionId", planet.factionId);
        setValue("name", planet.name);
        setValue("sector", planet.planetSector);
        setValue("radius", planet.radius);
        setValue("type", planet.worldType);
        try {
            saveValues();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public long getPlanetId() {
        return Long.parseLong(getValue("planetId"));
    }

    public int getFactionId() {
        return Integer.parseInt(getValue("factionId"));
    }

    public String getName() {
        return getValue("name");
    }

    public Vector3i getSector() {
        return Vector3i.parseVector3i(getValue("sector"));
    }

    public float getRadius() {
        return Float.parseFloat(getValue("radius"));
    }

    public WorldType getType() {
        return WorldType.parseWorldType(getValue("type"));
    }

    public Planet getPlanet() {
        return new Planet(this);
    }
}
