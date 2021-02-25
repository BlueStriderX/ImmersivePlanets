package org.schema.game.common.data.world.space;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import org.schema.common.util.StringTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.PlayerControllable;
import org.schema.game.client.view.gui.shiphud.newhud.ColorPalette;
import org.schema.game.common.controller.Planet;
import org.schema.game.common.controller.SegmentBuffer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.damage.Hittable;
import org.schema.game.common.controller.damage.beam.DamageBeamHitHandler;
import org.schema.game.common.controller.damage.beam.DamageBeamHitHandlerPlanetCore;
import org.schema.game.common.controller.damage.beam.DamageBeamHittable;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.physics.CollisionType;
import org.schema.game.common.data.physics.CubeRayCastResult;
import org.schema.game.common.data.physics.DodecahedronShapeExt;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionRelation.RType;
import org.schema.game.common.data.world.RemoteSector;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.StellarSystem;
import org.schema.game.common.data.world.SectorInformation.SectorType;
import org.schema.game.network.objects.NetworkPlanetCore;
import org.schema.game.server.data.FactionState;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.core.settings.EngineSettings;
import org.schema.schine.graphicsengine.forms.DebugBox;
import org.schema.schine.graphicsengine.forms.debug.DebugDrawer;
import org.schema.schine.network.StateInterface;
import org.schema.schine.network.TopLevelType;
import org.schema.schine.network.objects.NetworkObject;
import org.schema.schine.network.objects.Sendable;
import org.schema.schine.network.objects.container.TransformTimed;
import org.schema.schine.network.server.ServerMessage;
import org.schema.schine.resource.tag.FinishTag;
import org.schema.schine.resource.tag.Tag;
import org.schema.schine.resource.tag.Tag.Type;
import thederpgamer.immersiveplanets.data.world.WorldData;

/**
 * PlanetCore.java
 * PlanetCore entity
 * ==================================================
 * Modified 02/25/2021
 */
public class PlanetCore extends FixedSpaceEntity implements Hittable, DamageBeamHittable {
    public static final int MAX_HP = 10000000;
    public static final int MAX_HP_RECHARGE_PER_SEC = 5000;
    int tmpFaction;
    private NetworkPlanetCore networkPlanetCore;
    private float radius = 200.0F;
    private float hitPoints = 1.0E7F;
    private short[] ores = new short[0];
    private short[] top = new short[0];
    private short[] rock = new short[0];
    private short[] flowers = new short[0];
    private short[] fill = new short[0];
    private boolean destroyed;
    private String realName = "";
    private Dodecahedron h;
    private Vector3f minOut = new Vector3f();
    private Vector3f maxOut = new Vector3f();
    private Vector3f minOutC = new Vector3f();
    private Vector3f maxOutC = new Vector3f();
    private Vector3f[] maxOutCorners = new Vector3f[8];
    Transform aTemp = new Transform();
    Transform bTemp = new Transform();
    CubeRayCastResult r = new CubeRayCastResult(new Vector3f(), new Vector3f(), (Object)null, new SegmentController[0]);
    Vector3f tPos;
    private DamageBeamHitHandler damageBeamHitHandler;

    private WorldData worldData;

    public PlanetCore(StateInterface var1) {
        super(var1);

        for(int var2 = 0; var2 < this.maxOutCorners.length; ++var2) {
            this.maxOutCorners[var2] = new Vector3f();
        }

        this.aTemp.setIdentity();
        this.bTemp.setIdentity();
        this.tPos = new Vector3f();
        this.damageBeamHitHandler = new DamageBeamHitHandlerPlanetCore();
    }

    public void setWorldData(WorldData worldData) {
        this.worldData = worldData;
    }

    public WorldData getWorldData() {
        return worldData;
    }

    public EntityType getType() {
        return EntityType.PLANET_CORE;
    }

    public NetworkPlanetCore getNetworkObject() {
        return this.networkPlanetCore;
    }

    public CollisionType getCollisionType() {
        return CollisionType.PLANET_CORE;
    }

    public void initFromNetworkObject(NetworkObject var1) {
        super.initFromNetworkObject(var1);
        NetworkPlanetCore var2 = (NetworkPlanetCore)var1;
        this.radius = var2.radius.getFloat();
        this.setUniqueIdentifier((String)var2.uid.get());
        this.hitPoints = this.getNetworkObject().hp.getFloat();
    }

    public void updateFromNetworkObject(NetworkObject var1, int var2) {
        super.updateFromNetworkObject(var1, var2);
        if (!this.isOnServer()) {
            this.hitPoints = this.getNetworkObject().hp.getFloat();
        }

    }

    public void updateLocal(Timer var1) {
        super.updateLocal(var1);
        if (this.getHitPoints() < 1.0E7F) {
            this.setHitPoints(Math.min(1.0E7F, this.getHitPoints() + 5000.0F * var1.getDelta()));
        }

        if (this.isDestroyed() && this.isOnServer()) {
            this.setFactionAll(0);
            StellarSystem var2;
            Sector var10;
            if ((var10 = ((GameServerState)this.getState()).getUniverse().getSector(this.getSectorId())) != null) {
                try {
                    if ((var2 = ((GameServerState)this.getState()).getUniverse().getStellarSystemFromSecPos(var10.pos)) != null) {
                        int var3 = var2.getLocalCoordinate(var10.pos.x);
                        int var4 = var2.getLocalCoordinate(var10.pos.y);
                        int var5 = var2.getLocalCoordinate(var10.pos.z);
                        var3 = var2.getIndex(var3, var4, var5);
                        var2.setSectorType(var3, SectorType.ASTEROID);
                        var10.setChangedForDb(true);
                        var10.setTransientSector(false);
                        Iterator var11 = ((GameServerState)this.getState()).getPlayerStatesByName().values().iterator();

                        while(var11.hasNext()) {
                            ((PlayerState)var11.next()).updateProximitySectors();
                        }
                    } else {
                        try {
                            ((GameServerState)this.getState()).getController().broadcastMessageAdmin(new Object[]{445}, 3);
                            throw new IllegalArgumentException("[SERVER] " + this.getUniqueIdentifier() + " " + this + " System of destroyed planed not loaded: " + this.getSectorId());
                        } catch (Exception var8) {
                            var8.printStackTrace();
                        }
                    }
                } catch (IOException var9) {
                    var2 = null;
                    var9.printStackTrace();
                    ((GameServerState)this.getState()).getController().broadcastMessageAdmin(new Object[]{446}, 3);
                }
            } else {
                try {
                    ((GameServerState)this.getState()).getController().broadcastMessageAdmin(new Object[]{447}, 3);
                    throw new IllegalArgumentException("[SERVER] " + this.getUniqueIdentifier() + " " + this + " Sector of destroyed planed not loaded: " + this.getSectorId());
                } catch (Exception var7) {
                    var2 = null;
                    var7.printStackTrace();
                }
            }

            this.markForPermanentDelete(true);
            this.setMarkedForDeleteVolatile(true);
            synchronized(this.getState().getLocalAndRemoteObjectContainer().getLocalObjects()) {
                Iterator var12 = this.getState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values().iterator();

                while(true) {
                    if (!var12.hasNext()) {
                        break;
                    }

                    Sendable var13;
                    if ((var13 = (Sendable)var12.next()) instanceof Planet && ((Planet)var13).getCore() == this) {
                        ((Planet)var13).setPlanetCore((PlanetCore)null);
                        ((Planet)var13).setPlanetCoreUID("none");
                        ((Planet)var13).setBlownOff(new Vector3f(this.getWorldTransform().origin));
                    }
                }
            }

            ((GameServerState)this.getState()).getController().broadcastMessageAdmin(new Object[]{448, this}, 3);
        }

    }

    public void updateToFullNetworkObject() {
        super.updateToFullNetworkObject();
        NetworkPlanetCore var1;
        (var1 = this.getNetworkObject()).radius.set(this.radius);
        var1.uid.set(this.getUniqueIdentifier());
        var1.hp.set(this.hitPoints);
    }

    public void updateToNetworkObject() {
        super.updateToNetworkObject();
        if (this.isOnServer()) {
            this.getNetworkObject().hp.set(this.hitPoints);
        }

    }

    public void fromTagStructure(Tag var1) {
        Tag[] var3;
        Tag[] var2 = (Tag[])(var3 = (Tag[])var1.getValue())[0].getValue();
        this.setUniqueIdentifier((String)var2[0].getValue());
        this.setRadius((Float)var2[1].getValue());
        this.ores = Tag.shortArrayFromTagStruct(var2[2]);
        this.top = Tag.shortArrayFromTagStruct(var2[3]);
        this.rock = Tag.shortArrayFromTagStruct(var2[4]);
        this.flowers = Tag.shortArrayFromTagStruct(var2[5]);
        this.fill = Tag.shortArrayFromTagStruct(var2[6]);
        super.fromTagStructure(var3[1]);
    }

    public Tag toTagStructure() {
        Tag var1 = new Tag(Type.STRUCT, "PlanetCore", new Tag[]{new Tag(Type.STRING, (String)null, this.getUniqueIdentifier()), new Tag(Type.FLOAT, (String)null, this.radius), Tag.listToTagStruct(this.ores, (String)null), Tag.listToTagStruct(this.top, (String)null), Tag.listToTagStruct(this.rock, (String)null), Tag.listToTagStruct(this.flowers, (String)null), Tag.listToTagStruct(this.fill, (String)null), super.toTagStructure(), FinishTag.INST});
        return new Tag(Type.STRUCT, (String)null, new Tag[]{var1, super.toTagStructure(), FinishTag.INST});
    }

    public void getRelationColor(RType var1, boolean var2, Vector4f var3, float var4, float var5) {
        switch(var1) {
            case ENEMY:
                var3.set(ColorPalette.enemyOther);
                break;
            case FRIEND:
                var3.set(ColorPalette.allyOther);
                break;
            case NEUTRAL:
                var3.set(ColorPalette.neutralOther);
        }

        if (var2) {
            var3.set(ColorPalette.factionOther);
        }

        var3.x += var4;
        var3.y += var4;
        var3.z += var4;
    }

    protected boolean hasVirtual() {
        return false;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealNameToAll(String var1) {
        this.realName = var1;
        synchronized(this.getState().getLocalAndRemoteObjectContainer().getLocalObjects()) {
            Iterator var2 = this.getState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values().iterator();

            while(var2.hasNext()) {
                Sendable var3;
                if ((var3 = (Sendable)var2.next()) instanceof Planet && ((Planet)var3).getPlanetCoreUID().equals(this.getUniqueIdentifier())) {
                    ((Planet)var3).setRealName(this.realName);
                }
            }

        }
    }

    public String toString() {
        return "PlanetCore( id " + this.getId() + "; hp " + this.hitPoints + ")";
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float var1) {
        this.radius = var1;
    }

    public short[] getOres() {
        return this.ores;
    }

    public void setOres(short[] var1) {
        this.ores = var1;
    }

    public short[] getTop() {
        return this.top;
    }

    public void setTop(short[] var1) {
        this.top = var1;
    }

    public short[] getRock() {
        return this.rock;
    }

    public void setRock(short[] var1) {
        this.rock = var1;
    }

    public short[] getFlowers() {
        return this.flowers;
    }

    public void setFlowers(short[] var1) {
        this.flowers = var1;
    }

    public short[] getFill() {
        return this.fill;
    }

    public void setFill(short[] var1) {
        this.fill = var1;
    }

    public boolean isVulnerable() {
        return true;
    }

    public boolean checkAttack(Damager var1, boolean var2, boolean var3) {
        return this.canHit(var1);
    }

    private boolean canHit(Damager var1) {
        synchronized(this.getState().getLocalAndRemoteObjectContainer().getLocalObjects()){}

        label204: {
            Throwable var10000;
            label203: {
                Iterator var3;
                boolean var10001;
                try {
                    var3 = this.getState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values().iterator();
                } catch (Throwable var10) {
                    var10000 = var10;
                    var10001 = false;
                    break label203;
                }

                while(true) {
                    try {
                        if (!var3.hasNext()) {
                            break label204;
                        }

                        Sendable var4;
                        if ((var4 = (Sendable)var3.next()) instanceof Planet && ((Planet)var4).getPlanetCoreUID().equals(this.getUniqueIdentifier()) && ((Planet)var4).isHomeBase()) {
                            return false;
                        }
                    } catch (Throwable var9) {
                        var10000 = var9;
                        var10001 = false;
                        break;
                    }
                }
            }

            //Throwable var11 = var10000;
            //throw var11;
        }

        if (this.isOnServer()) {
            Sector var2;
            if ((var2 = ((GameServerState)this.getState()).getUniverse().getSector(this.getSectorId())) != null && var2.isProtected()) {
                if (var1 != null && var1 instanceof PlayerControllable) {
                    List var14 = ((PlayerControllable)var1).getAttachedPlayers();

                    for(int var15 = 0; var15 < var14.size(); ++var15) {
                        PlayerState var12 = (PlayerState)var14.get(var15);
                        if (System.currentTimeMillis() - var12.lastSectorProtectedMsgSent > 5000L) {
                            var12.lastSectorProtectedMsgSent = System.currentTimeMillis();
                            var12.sendServerMessage(new ServerMessage(new Object[]{449}, 2, var12.getId()));
                        }
                    }
                }

                return false;
            }
        } else {
            Sendable var13;
            if ((var13 = (Sendable)this.getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(this.getSectorId())) != null && var13 instanceof RemoteSector && ((RemoteSector)var13).isProtectedClient()) {
                return false;
            }
        }

        return true;
    }

    public float getHitPoints() {
        return this.hitPoints;
    }

    public void setHitPoints(float var1) {
        this.hitPoints = var1;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setDestroyed(boolean var1) {
        this.destroyed = var1;
    }

    public void setFactionAll(int var1) {
        if (var1 != this.getFactionId()) {
            super.setFactionId(var1);
            synchronized(this.getState().getLocalAndRemoteObjectContainer().getLocalObjects()) {
                Iterator var3 = this.getState().getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values().iterator();

                while(true) {
                    Sendable var4;
                    do {
                        do {
                            do {
                                if (!var3.hasNext()) {
                                    return;
                                }
                            } while(!((var4 = (Sendable)var3.next()) instanceof Planet));
                        } while(!((Planet)var4).getPlanetCoreUID().equals(this.getUniqueIdentifier()));
                    } while(((Planet)var4).getElementClassCountMap().get((short)291) != 0 && ((FactionState)this.getState()).getFactionManager().existsFaction(((Planet)var4).getFactionId()));

                    ((Planet)var4).setFactionId(var1);
                }
            }
        }
    }

    public void sendHitConfirm(byte var1) {
    }

    public boolean isSegmentController() {
        return false;
    }

    public String getName() {
        return this.toNiceString();
    }

    public AbstractOwnerState getOwnerState() {
        return null;
    }

    public void destroyPersistent() {
        super.destroyPersistent();
        Sector var1 = ((GameServerState)this.getState()).getUniverse().getSector(this.getSectorId());
        Vector3i var2 = StellarSystem.getPosFromSector(new Vector3i(var1.pos), new Vector3i());
        ((GameServerState)this.getState()).getGameMapProvider().updateMapForAllInSystem(var2);
    }

    public void newNetworkObject() {
        this.networkPlanetCore = new NetworkPlanetCore(this.getState());
    }

    public void initPhysics() {
        super.initPhysics();
        if (this.getPhysicsDataContainer().getObject() == null) {
            Transform var1 = this.getRemoteTransformable().getInitialTransform();
            this.h = new Dodecahedron(this.getRadius());
            this.h.create();
            ObjectArrayList var2 = new ObjectArrayList();

            for(int var3 = 0; var3 < 12; ++var3) {
                Vector3f[] var4 = this.h.getPolygon(var3);

                for(int var5 = 0; var5 < var4.length; ++var5) {
                    var2.add(var4[var5]);
                }
            }

            DodecahedronShapeExt var6;
            (var6 = new DodecahedronShapeExt(var2, this)).dodecahedron = this.h;
            (new Transform()).setIdentity();
            this.getPhysicsDataContainer().setShape(var6);
            this.getPhysicsDataContainer().setInitial(var1);
            RigidBody var7;
            (var7 = this.getPhysics().getBodyFromShape(var6, this.getMass(), this.getPhysicsDataContainer().initialTransform)).setUserPointer(this.getId());
            this.getPhysicsDataContainer().setObject(var7);
            this.getWorldTransform().set(var1);

            assert this.getPhysicsDataContainer().getObject() != null;
        } else {
            System.err.println("[SegmentController][WARNING] not adding initial physics object. it already exists");
        }

        this.setFlagPhysicsInit(true);
    }

    public String toNiceString() {
        return StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_DATA_WORLD_SPACE_PLANETCORE_6, new Object[]{this.realName, (int)this.radius, StringTools.formatSeperated((int)this.getHitPoints())});
    }

    public boolean occludes(SegmentBuffer var1, Vector3f var2, Vector3f var3, Vector3f var4, Vector3f var5) {
        TransformTimed var7 = var1.getSegmentController().getWorldTransformOnClient();
        this.minOut.set((float)(var1.regionBlockStart.x - 8), (float)(var1.regionBlockStart.y - 8), (float)(var1.regionBlockStart.z - 8));
        this.maxOut.set((float)(var1.regionBlockEnd.x - 8), (float)(var1.regionBlockEnd.y - 8), (float)(var1.regionBlockEnd.z - 8));
        this.maxOutCorners[0].set(this.minOut.x, this.minOut.y, this.minOut.z);
        this.maxOutCorners[1].set(this.maxOut.x, this.minOut.y, this.minOut.z);
        this.maxOutCorners[2].set(this.minOut.x, this.maxOut.y, this.minOut.z);
        this.maxOutCorners[3].set(this.minOut.x, this.minOut.y, this.maxOut.z);
        this.maxOutCorners[4].set(this.maxOut.x, this.maxOut.y, this.minOut.z);
        this.maxOutCorners[5].set(this.maxOut.x, this.minOut.y, this.maxOut.z);
        this.maxOutCorners[6].set(this.minOut.x, this.maxOut.y, this.maxOut.z);
        this.maxOutCorners[7].set(this.maxOut.x, this.maxOut.y, this.maxOut.z);
        this.tPos.set(this.getWorldTransformOnClient().origin);
        if (var1.getSegmentController() instanceof Planet && EngineSettings.P_PHYSICS_DEBUG_ACTIVE.isOn()) {
            DebugDrawer.boxes.add(new DebugBox(new Vector3f(this.maxOutCorners[0]), new Vector3f(this.maxOutCorners[7]), var7, 1.0F, 1.0F, 0.0F, 1.0F));
            Dodecahedron.debug = true;
        }

        for(int var6 = 0; var6 < this.maxOutCorners.length; ++var6) {
            var7.transform(this.maxOutCorners[var6]);
            var3 = Controller.getCamera().getPos();
            if (!this.h.testRay(this.tPos, var3, this.maxOutCorners[var6])) {
                return false;
            }
        }

        Dodecahedron.debug = false;
        return true;
    }

    public void sendClientMessage(String var1, int var2) {
    }

    public TopLevelType getTopLevelType() {
        return TopLevelType.OTHER_SPACE;
    }

    public InterEffectSet getAttackEffectSet(long var1, DamageDealerType var3) {
        return null;
    }

    public byte getFactionRights() {
        return 0;
    }

    public void sendServerMessage(Object[] var1, int var2) {
    }

    public byte getOwnerFactionRights() {
        return 0;
    }

    public MetaWeaponEffectInterface getMetaWeaponEffect(long var1, DamageDealerType var3) {
        return null;
    }

    public DamageBeamHitHandler getDamageBeamHitHandler() {
        return this.damageBeamHitHandler;
    }

    public boolean canBeDamagedBy(Damager var1, DamageDealerType var2) {
        return true;
    }
}