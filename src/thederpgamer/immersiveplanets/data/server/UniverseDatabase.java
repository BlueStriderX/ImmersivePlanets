package thederpgamer.immersiveplanets.data.server;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.space.PlanetCore;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.world.WorldData;
import thederpgamer.immersiveplanets.graphics.universe.WorldDrawData;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UniverseDatabase.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class UniverseDatabase {

    private static ImmersivePlanets instance = ImmersivePlanets.getInstance();
    private static HashMap<Long, WorldData> worldMap = new HashMap<>();
    public static HashMap<Long, WorldDrawData> drawMap = new HashMap<>();

    public static Sector getSectorFromId(int sectorId) {
        return GameServer.getUniverse().getSector(sectorId);
    }

    public static WorldData getFromId(long worldId) {
        return worldMap.get(worldId);
    }

    public static WorldData getFromEntityId(int entityId) {
        for(WorldData worldData : worldMap.values()) {
            if(worldData.getEntityId() == entityId) return worldData;
        }
        return null;
    }

    public static WorldData getFromSector(Vector3i sector) {
        for(WorldData worldData : worldMap.values()) {
            if(worldData.getSector().equals(sector)) return worldData;
        }
        return null;
    }

    public static void addNewWorld(PlanetCore planetCore, Vector3i sector) {
        WorldData worldData = new WorldData(getNewWorldId(), planetCore.getId(), planetCore.getRadius(), WorldType.PLANET_DEBUG, sector);
        planetCore.setWorldData(worldData);
        worldMap.put(worldData.getWorldId(), worldData);
    }

    private static long getNewWorldId() {
        ArrayList<Long> toRemove = new ArrayList<>();
        long worldId = -1;
        if(worldMap.isEmpty()) return 0;
        for(long id : worldMap.keySet()) {
            int entityId = worldMap.get(id).getEntityId();
            if(GameCommon.getGameObject(entityId) == null || !GameCommon.getGameObject(entityId).isOnServer()) {
                toRemove.add(id);
                if(toRemove.size() == 1) worldId = id;
            }
        }
        for(long id : toRemove) worldMap.keySet().remove(id);
        if(worldId == -1) worldId = worldMap.keySet().size();
        return worldId;
    }

    public static void loadAllData() {
        ArrayList<Object> objectList = PersistentObjectUtil.getObjects(instance.getSkeleton(), WorldData.class);
        for(Object object : objectList) {
            WorldData worldData = (WorldData) object;
            worldMap.put(worldData.getWorldId(), worldData);
        }
    }

    public static void saveAllData() {
        for(WorldData worldData : worldMap.values()) {
            PersistentObjectUtil.addObject(instance.getSkeleton(), worldData);
        }
        PersistentObjectUtil.save(instance.getSkeleton());
    }

    public static void saveData(PlanetCore planetCore) {
        saveData(planetCore.getWorldData());
    }

    public static void saveData(WorldData worldData) {
        PersistentObjectUtil.addObject(instance.getSkeleton(), worldData);
        PersistentObjectUtil.save(instance.getSkeleton());
    }
}
