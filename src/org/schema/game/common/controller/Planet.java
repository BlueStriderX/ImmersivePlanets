package org.schema.game.common.controller;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.Transform;
import java.util.Iterator;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.data.PlayerControllable;
import org.schema.game.client.data.gamemap.requests.GameMapRequest;
import org.schema.game.client.view.gui.shiphud.newhud.ColorPalette;
import org.schema.game.common.Starter;
import org.schema.game.common.controller.ai.AIGameConfiguration;
import org.schema.game.common.controller.ai.AIPlanetConfiguration;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.elements.PlanetManagerContainer;
import org.schema.game.common.controller.generator.PlanetCreatorThread;
import org.schema.game.common.data.physics.RigidBodySegmentController;
import org.schema.game.common.data.player.AbstractCharacter;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerCharacter;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.common.data.player.faction.FactionRelation.RType;
import org.schema.game.common.data.world.*;
import org.schema.game.common.data.world.SectorInformation.PlanetType;
import org.schema.game.common.data.world.space.PlanetCore;
import org.schema.game.network.objects.NetworkPlanet;
import org.schema.game.server.ai.PlanetAIEntity;
import org.schema.game.server.data.FactionState;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.network.StateInterface;
import org.schema.schine.network.objects.NetworkObject;
import org.schema.schine.network.objects.Sendable;
import org.schema.schine.physics.Physical;
import org.schema.schine.resource.tag.FinishTag;
import org.schema.schine.resource.tag.Tag;
import org.schema.schine.resource.tag.Tag.Type;

/**
 * PlanetOld.java
 * PlanetOld entity class (modified)
 * ==================================================
 * Modified 02/12/2021
 */
public class Planet extends ManagedUsableSegmentController<Planet> {
    private final PlanetManagerContainer planetManagerContainer;
    public int fragmentId = -1;
    private AIPlanetConfiguration aiConfiguration;
    private PlanetType planetType;
    private PlanetCore core;
    private String planetCoreUID;
    private Vector3f blownOff;
    private boolean clientBlownOff;
    private boolean hasClientBlownOff;
    private boolean blownOffDebug;
    private boolean transientMoved;
    private boolean checkEmpty;

    public Planet(StateInterface var1) {
        super(var1);
        this.planetType = PlanetType.EARTH;
        this.planetCoreUID = "none";
        this.planetManagerContainer = new PlanetManagerContainer(var1, this);
        this.aiConfiguration = new AIPlanetConfiguration(var1, this);
    }

    public EntityType getType() {
        return EntityType.PLANET_SEGMENT;
    }

    protected boolean affectsGravityOf(SimpleTransformableSendableObject<?> var1) {
        if (var1 instanceof AbstractCharacter && ((AbstractCharacter)var1).getOwnerState().isSitting()) {
            return false;
        } else if (FactionManager.isNPCFaction(var1.getFactionId())) {
            return false;
        } else {
            return var1.getSectorId() == this.getSectorId() && (var1.getMass() > 0.0F || var1 instanceof AbstractCharacter) && this.checkGravityDownwards(var1);
        }
    }

    public void getGravityAABB(Vector3f var1, Vector3f var2) {
        super.getGravityAABB(var1, var2);
        var2.y += 32.0F;
    }

    public void getGravityAABB(Transform var1, Vector3f var2, Vector3f var3) {
        super.getGravityAABB(var1, var2, var3);
        var3.y += 32.0F;
    }

    public void getRelationColor(RType var1, boolean var2, Vector4f var3, float var4, float var5) {
        switch(var1) {
            case ENEMY:
                var3.set(ColorPalette.enemyPlanet);
                break;
            case FRIEND:
                var3.set(ColorPalette.allyPlanet);
                break;
            case NEUTRAL:
                var3.set(ColorPalette.neutralPlanet);
        }

        if (var2) {
            var3.set(ColorPalette.factionPlanet);
        }

        var3.x += var4;
        var3.y += var4;
        var3.z += var4;
    }

    public void initialize() {
        super.initialize();
        this.setMass(0.0F);
        this.setRealName("PlanetOld");
    }

    public boolean isGravitySource() {
        return true;
    }

    public boolean isHomeBase() {
        return super.isHomeBase() || this.isAnyPlanetSegmentHomebase();
    }

    public void onSectorInactiveClient() {
        super.onSectorInactiveClient();
        this.getManagerContainer().getShoppingAddOn().onSectorInactiveClient();
    }

    public void cleanUpOnEntityDelete() {
        super.cleanUpOnEntityDelete();
        this.getManagerContainer().getShoppingAddOn().cleanUp();
    }

    public void destroyPersistent() {
        super.destroyPersistent();
        Sector var1 = ((GameServerState)this.getState()).getUniverse().getSector(this.getSectorId());
        Vector3i var2 = StellarSystem.getPosFromSector(new Vector3i(var1.pos), new Vector3i());
        ((GameServerState)this.getState()).getGameMapProvider().updateMapForAllInSystem(var2);
    }

    public String toNiceString() {
        String var1 = "PlanetSegment(" + this.getRealName() + ");";
        if (this.getFactionId() != 0) {
            var1 = var1 + "[";
            Faction var2;
            if ((var2 = ((FactionState)this.getState()).getFactionManager().getFaction(this.getFactionId())) != null) {
                var1 = var1 + var2.getName();
            } else {
                var1 = var1 + "factionUnknown";
                var1 = var1 + this.getFactionId();
            }

            var1 = var1 + "]";
        }

        return var1;
    }

    public void initFromNetworkObject(NetworkObject var1) {
        super.initFromNetworkObject(var1);
        if (!this.isOnServer()) {
            this.planetCoreUID = (String)this.getNetworkObject().planetUid.get();
            this.setSeed(this.getNetworkObject().seed.getLong());
        }

    }

    public void updateFromNetworkObject(NetworkObject var1, int var2) {
        super.updateFromNetworkObject(var1, var2);
        if (!this.isOnServer()) {
            this.clientBlownOff = (Boolean)((NetworkPlanet)super.getNetworkObject()).blownOff.get();
            this.planetCoreUID = (String)this.getNetworkObject().planetUid.get();
        }

    }

    public void updateToFullNetworkObject() {
        super.updateToFullNetworkObject();
        if (this.isOnServer()) {
            this.getNetworkObject().planetUid.set(this.planetCoreUID);
            this.getNetworkObject().seed.set(this.getSeed());
        }

    }

    public void updateToNetworkObject() {
        super.updateToNetworkObject();
        if (this.isOnServer()) {
            this.getNetworkObject().planetUid.set(this.planetCoreUID);
        }

    }

    public void onRename(String var1, String var2) {
        Sector var5 = ((GameServerState)this.getState()).getUniverse().getSector(this.getSectorId());
        Vector3i var6 = StellarSystem.getPosFromSector(new Vector3i(var5.pos), new Vector3i());
        Vector3i var7 = new Vector3i();
        if (this.isOnServer()) {
            Iterator var3 = ((GameServerState)this.getState()).getPlayerStatesByName().values().iterator();

            while(var3.hasNext()) {
                PlayerState var4 = (PlayerState)var3.next();
                StellarSystem.getPosFromSector(new Vector3i(var4.getCurrentSector()), var7);
                if (var7.equals(var6)) {
                    ((GameServerState)this.getState()).getGameMapProvider().addRequestServer(new GameMapRequest((byte)2, var6), var4.getClientChannel());
                }
            }
        }

    }

    public boolean hasStructureAndArmorHP() {
        return false;
    }

    public boolean isHomeBaseFor(int var1) {
        return super.isHomeBaseFor(var1) || this.isAnyPlanetSegmentHomebase();
    }

    public void fromTagStructure(Tag var1) {
        assert var1.getName().equals("PlanetOld");

        Tag[] var2;
        if ((var2 = (Tag[])var1.getValue())[0].getType() == Type.BYTE && var2[1].getType() == Type.STRING) {
            this.fragmentId = (Byte)var2[0].getValue() - 1;
            this.setPlanetCoreUID((String)var2[1].getValue());
            super.fromTagStructure(var2[2]);
        } else {
            super.fromTagStructure(var2[1]);
        }
    }

    public void setFactionId(int var1) {
        if (this.blownOffDebug) {
            System.err.println("[PLANET] Cannot set faction on blown up planetOld");
            var1 = 0;
        }

        super.setFactionId(var1);
        if (this.core != null) {
            this.core.setFactionAll(var1);
        }

    }

    public int getCreatorId() {
        return this.planetType.ordinal();
    }

    public void onRemovedElementSynched(short var1, int var2, byte var3, byte var4, byte var5, byte var6, Segment var7, boolean var8, long var9) {
        this.getManagerContainer().onRemovedElementSynched(var1, var2, var3, var4, var5, var7, var8);
        super.onRemovedElementSynched(var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    public void setCreatorId(int var1) {
        this.planetType = PlanetType.values()[var1];
    }

    public Tag toTagStructure() {
        return new Tag(Type.STRUCT, "PlanetOld", new Tag[]{new Tag(Type.BYTE, (String)null, (byte)(this.fragmentId + 1)), new Tag(Type.STRING, (String)null, this.getPlanetCoreUID()), super.toTagStructure(), FinishTag.INST});
    }

    public boolean isRankAllowedToChangeFaction(int var1, PlayerState var2, byte var3) {
        return var1 == 0 && ((FactionState)this.getState()).getFactionManager().existsFaction(this.getFactionId()) && this.isHomeBase() && ((FactionState)this.getState()).getFactionManager().existsFaction(var2.getFactionId()) && !((FactionState)this.getState()).getFactionManager().getFaction(var2.getFactionId()).getRoles().hasHomebasePermission(var3) ? false : super.isRankAllowedToChangeFaction(var1, var2, var3);
    }

    public AIGameConfiguration<PlanetAIEntity, Planet> getAiConfiguration() {
        return this.aiConfiguration;
    }

    public void onAttachPlayer(PlayerState var1, Sendable var2, Vector3i var3, Vector3i var4) {
        super.onAttachPlayer(var1, var2, var3, var4);
        GameClientState var5;
        if (!this.isOnServer() && ((GameClientState)this.getState()).getPlayer() == var1 && (var5 = (GameClientState)this.getState()).getPlayer() == var1) {
            var5.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getSegmentControlManager().setActive(true);
            System.err.println("Entering space stationc ");
        }

    }

    public void onDetachPlayer(PlayerState var1, boolean var2, Vector3i var3) {
        GameClientState var4;
        if (!this.isOnServer() && (var4 = (GameClientState)this.getState()).getPlayer() == var1 && ((GameClientState)this.getState()).getPlayer() == var1) {
            var4.getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getSegmentControlManager().setActive(false);
        }

        Starter.modManager.onSegmentControllerPlayerDetached(this);
    }

    protected short getCoreType() {
        return 65;
    }

    public NetworkPlanet getNetworkObject() {
        return (NetworkPlanet)super.getNetworkObject();
    }

    protected String getSegmentControllerTypeString() {
        return "PlanetOld";
    }

    public void newNetworkObject() {
        this.setNetworkObject(new NetworkPlanet(this.getState(), this));
    }

    public void onAddedElementSynched(short var1, byte var2, byte var3, byte var4, byte var5, Segment var6, boolean var7, long var8, long var10, boolean var12) {
        this.getManagerContainer().onAddedElementSynched(var1, var6, var8, var10, var12);
        super.onAddedElementSynched(var1, var2, var3, var4, var5, var6, var7, var8, var10, var12);
    }

    protected void onCoreDestroyed(Damager var1) {
    }

    public void onDamageServerRootObject(float var1, Damager var2) {
        super.onDamageServerRootObject(var1, var2);
        this.aiConfiguration.onDamageServer(var1, var2);
        this.getManagerContainer().getShoppingAddOn().onHit(var2);
    }

    public void startCreatorThread() {
        if (this.getCreatorThread() == null) {
            this.setCreatorThread(new PlanetCreatorThread(this, this.planetType));
        }
    }

    public String toString() {
        return "PlanetOld(" + this.getId() + ")[s" + this.getSectorId() + "]" + this.getPlanetInfo();
    }

    protected boolean canObjectOverlap(Physical var1) {
        boolean var2 = super.canObjectOverlap(var1);
        if (var1 instanceof PlayerCharacter) {
            Iterator var3 = this.getAttachedPlayers().iterator();

            while(var3.hasNext()) {
                PlayerState var4;
                if ((var4 = (PlayerState)var3.next()).getAssingedPlayerCharacter() != null && var4.getAssingedPlayerCharacter().equals(var1)) {
                    return false;
                }
            }
        }

        return var2;
    }

    public void updateLocal(Timer var1) {
        super.updateLocal(var1);
        if (this.isOnServer() && this.getTotalElements() <= 0 && System.currentTimeMillis() - this.getTimeCreated() > 50000L && this.isEmptyOnServer() && this.getSegmentBuffer().isFullyLoaded()) {
            System.err.println("[SERVER][PlanetOld] Empty planetOld section: deleting " + this);
            this.setMarkedForDeleteVolatile(true);
        }

        Sendable var2;
        if (!this.isOnServer() && this.core == null && !this.getPlanetCoreUID().equals("none") && (var2 = (Sendable)this.getState().getLocalAndRemoteObjectContainer().getUidObjectMap().get(this.getPlanetCoreUID())) != null) {
            this.core = (PlanetCore)var2;
        }

        if (this.core != null && !this.getRealName().equals("PlanetOld") && !this.core.getRealName().equals(this.getRealName())) {
            this.core.setRealNameToAll(this.getRealName());
        }

        if (this.isOnServer() && this.blownOff != null) {
            ((NetworkPlanet)super.getNetworkObject()).blownOff.set(true);
            this.doBlowOff();
            this.blownOff = null;
            System.err.println("SERVER PLANET CORE EXPLOSION: " + this);
        } else if (this.clientBlownOff && !this.hasClientBlownOff) {
            this.doBlowOff();
            System.err.println("CLIENT PLANET CORE EXPLOSION: " + this);
            this.hasClientBlownOff = true;
        }

        if (this.blownOffDebug) {
            assert ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).isCollisionException();

            if ((double)((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).getLinearVelocity(new Vector3f()).lengthSquared() <= 0.01D) {
                this.blownOffDebug = false;
                if (this.isOverlapping()) {
                    System.err.println("STILL OVERLAPPING: PUSHING FURTHER");
                    this.doBlowOff();
                } else {
                    this.reinstate();
                }
            }
        }

        if (this.isOnServer() && this.checkEmpty) {
            if (this.getTotalElements() <= 0) {
                this.destroy();
            }

            this.checkEmpty = false;
        }

        Starter.modManager.onSegmentControllerUpdate(this);
    }

    public PlanetManagerContainer getManagerContainer() {
        return this.planetManagerContainer;
    }

    public SegmentController getSegmentController() {
        return this;
    }

    public PlanetType getPlanetType() {
        return this.planetType;
    }

    public boolean isSalvagableFor(Salvager var1, String[] var2, Vector3i var3) {
        AbstractOwnerState var4 = var1.getOwnerState();
        if (var1.getFactionId() != this.getFactionId() || (var4 == null || !(var4 instanceof PlayerState) || this.allowedToEdit((PlayerState)var4)) && var1.getOwnerFactionRights() >= this.getFactionRights()) {
            if (var1.getFactionId() == this.getFactionId()) {
                return true;
            } else if (!this.isHomeBase() && (this.getDockingController().getDockedOn() == null || !this.getDockingController().getDockedOn().to.getSegment().getSegmentController().isHomeBaseFor(this.getFactionId()))) {
                return true;
            } else {
                var2[0] = Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_PLANET_1;
                return false;
            }
        } else {
            var2[0] = Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_PLANET_0;
            return false;
        }
    }

    public boolean isHomebaseSingle(int var1) {
        return super.isHomeBaseFor(var1);
    }

    private boolean isAnyPlanetSegmentHomebase() {
        FactionManager var10000 = ((FactionState)this.getState()).getFactionManager();
        Object var1 = null;
        if (var10000.existsFaction(this.getFactionId())) {
            synchronized(this.getState().getLocalAndRemoteObjectContainer().getLocalObjects()){}

            Throwable var12;
            label178: {
                boolean var10001;
                Iterator var2;
                try {
                    var2 = this.getState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values().iterator();
                } catch (Throwable var9) {
                    var12 = var9;
                    var10001 = false;
                    break label178;
                }

                while(true) {
                    try {
                        if (!var2.hasNext()) {
                            return false;
                        }

                        Sendable var3;
                        Planet var11;
                        if ((var3 = (Sendable)var2.next()) instanceof Planet && (var11 = (Planet)var3).getCore() != null && this.getCore() != null) {
                            assert var11.getCore().getUniqueIdentifier() != null : var11;

                            assert this.getCore().getUniqueIdentifier() != null : this;

                            if (var11.getCore().getUniqueIdentifier().equals(this.getCore().getUniqueIdentifier()) && this.getFactionId() == var11.getFactionId() && var11.isHomebaseSingle(var11.getFactionId())) {
                                return true;
                            }
                        }
                    } catch (Throwable var8) {
                        var12 = var8;
                        var10001 = false;
                        break;
                    }
                }
            }

            //Throwable var10 = var12;
            //throw var10;
        } else {
            return false;
        }
        return false;
    }

    private String getPlanetInfo() {
        if ("none".equals(this.getPlanetCoreUID())) {
            return "none";
        } else {
            Sendable var1;
            return (var1 = (Sendable)this.getState().getLocalAndRemoteObjectContainer().getUidObjectMap().get(this.getPlanetCoreUID())) != null ? ((PlanetCore)var1).toNiceString() : this.getPlanetCoreUID() + "(unloaded)";
        }
    }

    private void doBlowOff() {
        assert !this.blownOffDebug;

        System.err.println("[PLANET] " + this.getState() + " DO BLOW OFF " + this);
        if (this.isOnServer()) {
            this.setFactionId(0);
        }

        this.onPhysicsRemove();
        this.getPhysicsDataContainer().setObject((CollisionObject)null);
        this.setMass(0.1F);
        this.initPhysics();
        ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).setCollisionException(true);
        this.onPhysicsAdd();
        Vector3f var1;
        (var1 = new Vector3f(this.getWorldTransform().origin)).normalize();
        var1.scale(15.0F);
        ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).applyCentralImpulse(var1);
        var1.scale(2.0F);
        ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).applyTorqueImpulse(var1);
        this.blownOffDebug = true;

        assert ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).isCollisionException();

        this.setMass(0.0F);
    }

    private void reinstate() {
        System.err.println("[PLANET] REINSTATE");
        this.onPhysicsRemove();
        this.getPhysicsDataContainer().setObject((CollisionObject)null);
        this.setMass(0.0F);
        this.initPhysics();
        ((RigidBodySegmentController)this.getPhysicsDataContainer().getObject()).setCollisionException(false);
        this.onPhysicsAdd();
        this.setMass(0.0F);
        this.blownOffDebug = false;
    }

    public PlanetCore getCore() {
        return this.core;
    }

    public String getPlanetCoreUID() {
        return this.planetCoreUID;
    }

    public void setPlanetCoreUID(String var1) {
        this.planetCoreUID = var1;
    }

    public void setPlanetCore(PlanetCore var1) {
        this.core = var1;
    }

    public void setBlownOff(Vector3f var1) {
        this.blownOff = var1;
    }

    public boolean isMoved() {
        return this.transientMoved;
    }

    public void setMoved(boolean var1) {
        if (var1 != this.transientMoved) {
            this.setChangedForDb(true);
        }

        this.transientMoved = var1;
        this.setMoved(var1);
    }

    public void onPlayerDetachedFromThis(PlayerState var1, PlayerControllable var2) {
    }

    public boolean isStatic() {
        return true;
    }
}