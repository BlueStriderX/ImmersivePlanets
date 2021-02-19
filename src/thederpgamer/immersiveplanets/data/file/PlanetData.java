package thederpgamer.immersiveplanets.data.file;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import java.io.File;
import java.io.FileNotFoundException;
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
        try {
            loadValues();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public PlanetData(Planet planet) {
        super(DataUtils.getPlanetDataFile(planet.planetId));
        initialize(planet);
    }

    public void initialize(Planet planet) {
        setValue("planetId", planet.planetId);
        setValue("factionId", planet.factionId);
        setValue("sector", planet.planetSector);
        setValue("radius", planet.radius);
        setValue("type", planet.worldType);
        try {
            saveValues();
            loadValues();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public long getPlanetId() {
        try {
            return Long.parseLong(getValue("planetId"));
        } catch(Exception e) {
            return -1;
        }
    }

    public int getFactionId() {
        try {
            return Integer.parseInt(getValue("factionId"));
        } catch(Exception e) {
            return 0;
        }
    }

    public Vector3i getSector() {
        try {
            String[] sectorString = getValue("sector").substring(1, getValue("sector").length() - 1).split(", ");
            return new Vector3i(Integer.parseInt(sectorString[0]), Integer.parseInt(sectorString[1]), Integer.parseInt(sectorString[2]));
        } catch(Exception e) {
            return new Vector3i();
        }
    }

    public int getRadius() {
        try {
            return Integer.parseInt(getValue("radius"));
        } catch(Exception e) {
            return 500;
        }
    }

    public WorldType getType() {
        try {
            return WorldType.parseWorldType(getValue("type"));
        } catch(Exception e) {
            return WorldType.PLANET_DEBUG;
        }
    }

    public Planet getPlanet() {
        return new Planet(this);
    }
}