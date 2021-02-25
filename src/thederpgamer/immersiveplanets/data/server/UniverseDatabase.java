package thederpgamer.immersiveplanets.data.server;

import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.Planet;
import org.schema.game.common.controller.generator.PlanetCreatorThread;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetFactory;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.world.WorldData;
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

    public static Planet getFromId(long worldId) {
        return worldMap.get(worldId).toEntity();
    }

    public static Planet getFromSector(Vector3i sector) {
        for(WorldData data : worldMap.values()) {
            if(data.getSector().equals(sector)) return data.toEntity();
        }
        return null;
    }

    public static WorldData addNewWorld(PlanetCreatorThread creatorThread, WorldCreatorPlanetFactory factory) {
        long worldId = getNewWorldId();
        int entityId = creatorThread.getSegmentController().getId();
        float radius = factory.radius;
        WorldType worldType = WorldType.PLANET_DEBUG; //Todo: Convert world type
        Vector3i sector = creatorThread.getSegmentController().getSector(new Vector3i());
        WorldData worldData = new WorldData(worldId, entityId, radius, worldType, sector);
        worldMap.put(worldId, worldData);
        return worldData;
    }

    public static long getIdFromPlanet(Planet planet) {
        for(long id : worldMap.keySet()){
            if(worldMap.get(id).getSector().equals(planet.getSector(new Vector3i()))) return id;
        }
        return -1;
    }

    private static long getNewWorldId() {
        ArrayList<Long> toRemove = new ArrayList<>();
        long worldId = -1;
        if(worldMap.isEmpty()) return 0;
        for(long id : worldMap.keySet()) {
            int entityId = worldMap.get(id).getEntityId();
            if(!GameServer.getServerState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().containsKey(entityId)) {
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
        for(WorldData data : worldMap.values()) {
            PersistentObjectUtil.addObject(instance.getSkeleton(), data);
        }
        PersistentObjectUtil.save(instance.getSkeleton());
    }

    public static void saveData(Planet entity) {
        saveData(entity.toWorldData());
    }

    public static void saveData(WorldData data) {
        PersistentObjectUtil.addObject(instance.getSkeleton(), data);
        PersistentObjectUtil.save(instance.getSkeleton());
    }
}
