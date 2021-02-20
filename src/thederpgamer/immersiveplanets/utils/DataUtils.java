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

    private static ArrayList<Planet> planets = new ArrayList<>();

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

    public static void loadPlanets() {
        planets.clear();
        ArrayList<File> toDelete = new ArrayList<>();
        ArrayList<PlanetData> dataList = new ArrayList<>();
        ArrayList<Vector3i> sectorList = new ArrayList<>();
        for(File planetDataFile : Objects.requireNonNull(ImmersivePlanets.getInstance().planetDataFolder.listFiles())) {
            PlanetData pData = new PlanetData(planetDataFile);
            if(!sectorList.contains(pData.getSector())) {
                sectorList.add(pData.getSector());
                dataList.add(pData);
            } else {
                toDelete.add(planetDataFile);
            }
        }

        for(PlanetData pData : dataList) planets.add(new Planet(pData));
        for(File file : toDelete) file.delete();
    }

    public static ArrayList<Planet> getPlanets() {
        return planets;
    }

    public static Planet getFromSector(Vector3i sector) {
        for(Planet planet : planets) {
            if(planet.planetSector.equals(sector)) return planet;
        }
        return null;
    }

    public static long getNewPlanetId() {
        loadPlanets();
        return getPlanets().size();
    }
}
