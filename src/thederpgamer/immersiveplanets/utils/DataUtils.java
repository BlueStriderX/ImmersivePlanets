package thederpgamer.immersiveplanets.utils;

import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.file.PlanetData;
import thederpgamer.immersiveplanets.universe.space.Planet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * DataUtils.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public class DataUtils {

    public static File getPlayerDataFile(String playerName) {
        File playerDataFile = new File(ImmersivePlanets.getInstance().planetDataFolder + "/" + playerName + ".smdat");
        try {
            if (!playerDataFile.exists()) playerDataFile.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return playerDataFile;
    }

    public static File getChunkDataFile(long planetId) {
        File chunkDataFile = new File(ImmersivePlanets.getInstance().chunkDataFolder + "/" + planetId + ".smdat");
        try {
            if(!chunkDataFile.exists()) chunkDataFile.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return chunkDataFile;
    }

    public static File getPlanetDataFile(long planetId) {
        File planetDataFile = new File(ImmersivePlanets.getInstance().planetDataFolder + "/" + planetId + ".smdat");
        try {
            if(!planetDataFile.exists()) planetDataFile.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return planetDataFile;
    }

    public static ArrayList<Planet> getAllPlanets() {
        ArrayList<Planet> planets = new ArrayList<>();
        for(File planetDataFile : Objects.requireNonNull(ImmersivePlanets.getInstance().planetDataFolder.listFiles())) {
            planets.add(new PlanetData(planetDataFile).getPlanet());
        }
        return planets;
    }

    public static Planet getFromSector(Vector3i sector) {
        ArrayList<Planet> planets = getAllPlanets();
        for(Planet planet : planets) {
            if(planet.planetSector.equals(sector)) return planet;
        }
        return null;
    }

    public static long getNewPlanetId() {
        return getAllPlanets().size();
    }
}
