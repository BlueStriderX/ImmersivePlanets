package thederpgamer.immersiveplanets.data.world;

import api.common.GameCommon;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.Planet;
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

    private WorldDrawData drawData;
    private long worldId;
    private int entityId;
    private float radius;
    private String worldType;
    private int[] sector;

    public WorldData(long worldId, int entityId, float radius, WorldType worldType, Vector3i sector) {
        this.worldId = worldId;
        this.entityId = entityId;
        this.radius = radius;
        this.worldType = worldType.toString();
        this.sector = new int[] {sector.x, sector.y, sector.z};
        this.drawData = new WorldDrawData(this);
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
        return new Vector3i(sector[0], sector[1], sector[2]);
    }

    public Planet toEntity() {
        return (Planet) GameCommon.getGameObject(getEntityId());
    }

    public WorldDrawData getDrawData() {
        return drawData;
    }
}
