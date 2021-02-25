package thederpgamer.immersiveplanets.data.world;

import api.common.GameCommon;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.space.PlanetCore;
import thederpgamer.immersiveplanets.data.server.UniverseDatabase;
import thederpgamer.immersiveplanets.graphics.model.WorldDrawData;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import java.io.Serializable;

/**
 * WorldData.java
 * <Description>
 * ==================================================
 * Created 02/23/2021
 * @author TheDerpGamer
 */
public class WorldData implements Serializable {

    private long worldId;
    private int entityId;
    private float radius;
    private String worldType;
    private Vector3i sector;

    public WorldData(long worldId, int entityId, float radius, WorldType worldType, Vector3i sector) {
        this.worldId = worldId;
        this.entityId = entityId;
        this.radius = radius;
        this.worldType = worldType.toString();
        this.sector = sector;
        UniverseDatabase.drawMap.put(worldId, new WorldDrawData(this));
    }

    public WorldType getWorldType() {
        return WorldType.parseWorldType(worldType);
    }

    public long getWorldId() {
        return worldId;
    }

    public int getEntityId() {
        return entityId;
    }

    public float getRadius() {
        return radius;
    }

    public Vector3i getSector() {
        return sector;
    }

    public PlanetCore toPlanet() {
        PlanetCore planetCore;
        (planetCore = (PlanetCore) GameCommon.getGameObject(getEntityId())).setWorldData(this);
        return planetCore;
    }

    public WorldDrawData getDrawData() {
        return UniverseDatabase.drawMap.get(worldId);
    }
}
