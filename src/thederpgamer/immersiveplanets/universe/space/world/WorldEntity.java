package thederpgamer.immersiveplanets.universe.space.world;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.Salvager;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.ai.stateMachines.AIConfigurationInterface;
import org.schema.schine.network.StateInterface;
import thederpgamer.immersiveplanets.data.world.WorldData;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;

/**
 * WorldEntity.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class WorldEntity extends ManagedUsableSegmentController<WorldEntity> {

    private long worldId;
    private float radius;
    private WorldType worldType;

    public WorldEntity(StateInterface state) {
        super(state);
    }

    @Override
    public void onDetachPlayer(PlayerState playerState, boolean b, Vector3i vector3i) {

    }

    @Override
    protected String getSegmentControllerTypeString() {
        return null;
    }

    @Override
    protected void onCoreDestroyed(Damager damager) {

    }

    @Override
    public boolean isSalvagableFor(Salvager salvager, String[] strings, Vector3i vector3i) {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isMoved() {
        return false;
    }

    @Override
    public void setMoved(boolean b) {

    }

    @Override
    public ManagerContainer<WorldEntity> getManagerContainer() {
        return null;
    }

    @Override
    public SegmentController getSegmentController() {
        return null;
    }

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public AIConfigurationInterface getAiConfiguration() {
        return null;
    }

    public long getWorldId() {
        return worldId;
    }

    public float getRadius() {
        return radius;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public WorldData toWorldData() {
        return new WorldData(getWorldId(), getId(), getRadius(), getWorldType(), getSector(new Vector3i()));
    }
}
