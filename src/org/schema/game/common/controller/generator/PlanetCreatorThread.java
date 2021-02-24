package org.schema.game.common.controller.generator;

import api.listener.events.controller.planet.PlanetGenerateEvent;
import api.mod.StarLoader;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.Planet;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.Segment;
import org.schema.game.common.data.world.SectorInformation.PlanetType;
import org.schema.game.server.controller.RequestData;
import org.schema.game.server.controller.RequestDataPlanet;
import org.schema.game.server.controller.world.factory.WorldCreatorFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetColumnyFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetDesertFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetEarthFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetIceFactory;
import org.schema.game.server.controller.world.factory.WorldCreatorPlanetMarsFactory;
import org.schema.game.server.data.ServerConfig;

/**
 * PlanetCreatorThread.java
 * PlanetOld Creation Handler (modified)
 * ==================================================
 * Modified 02/12/2021
 */
public class PlanetCreatorThread extends CreatorThread {
    public static final ObjectArrayFIFOQueue<RequestDataPlanet> dataPool;
    private static final float DEFAULT_RADIUS = 100.0F;
    public final Vector3f[] polygon;
    private final Dodecahedron s;
    public LongOpenHashSet locked = new LongOpenHashSet(128);
    public WorldCreatorFactory creator;

    public PlanetCreatorThread(Planet var1, PlanetType var2) {
        super(var1);

        assert var1.getCore() != null || var1.getPlanetCoreUID().equals("none");

        this.s = new Dodecahedron(var1.getCore() != null ? var1.getCore().getRadius() : 100.0F);
        this.s.create();
        Transform var3;
        (var3 = this.s.getTransform(Math.max(0, var1.fragmentId), new Transform(), -0.5F, -0.5F)).inverse();
        this.polygon = this.s.getPolygon(Math.max(0, var1.fragmentId));
        Matrix3f var4;
        (var4 = new Matrix3f()).rotY(3.1415927F);

        for(int var5 = 0; var5 < this.polygon.length; ++var5) {
            var3.transform(this.polygon[var5]);
            this.polygon[var5].y = 0.0F;
            var4.transform(this.polygon[var5]);
        }

        switch(var2) {
            case EARTH:
                this.creator = new WorldCreatorPlanetEarthFactory(var1.getSeed(), this.polygon, this.s.radius);
                return;
            case DESERT:
                this.creator = new WorldCreatorPlanetDesertFactory(var1.getSeed(), this.polygon, this.s.radius);
                return;
            case PURPLE:
                this.creator = new WorldCreatorPlanetColumnyFactory(var1.getSeed(), this.polygon, this.s.radius);
                return;
            case ICE:
                this.creator = new WorldCreatorPlanetIceFactory(var1.getSeed(), this.polygon, this.s.radius);
                return;
            case MARS:
            default:
                this.creator = new WorldCreatorPlanetMarsFactory(var1.getSeed(), this.polygon, this.s.radius);
        }
    }

    public int isConcurrent() {
        return 0;
    }

    public int loadFromDatabase(Segment var1) {
        return -1;
    }

    public void onNoExistingSegmentFound(Segment var1, RequestData var2) {
        //INSERTED CODE
        PlanetGenerateEvent event = new PlanetGenerateEvent(this, (RequestDataPlanet) var2, (WorldCreatorPlanetFactory) creator, var1);
        StarLoader.fireEvent(event, true);
        if(event.isCanceled()) return;
        //

        if (this.creator instanceof WorldCreatorPlanetFactory) {
            if (var1.pos.y < 0) {
                return;
            }

            if (!Dodecahedron.pnpoly(this.polygon, (float)var1.pos.x, (float)var1.pos.z, 48.0F)) {
                return;
            }
        }

        this.creator.createWorld(this.getSegmentController(), var1, var2);
    }

    public boolean predictEmpty(Vector3i var1) {
        return false;
    }

    public RequestData allocateRequestData(int var1, int var2, int var3) {
        synchronized(dataPool) {
            long var5 = ElementCollection.getIndex(var3, 0, var1);

            while(dataPool.isEmpty() || this.locked.contains(var5)) {
                try {
                    dataPool.wait();
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }
            }

            this.locked.add(var5);
            RequestDataPlanet var9;
            (var9 = (RequestDataPlanet)dataPool.dequeue()).cachePos.set(var1, var2, var3);
            return var9;
        }
    }

    public void freeRequestData(RequestData var1, int var2, int var3, int var4) {
        assert var1 != null;

        synchronized(dataPool) {
            ((RequestDataPlanet)var1).reset();
            this.locked.remove(ElementCollection.getIndex(var4, 0, var2));
            dataPool.enqueue((RequestDataPlanet)var1);
            dataPool.notify();
        }
    }

    public int margin(int var1) {
        return var1 < 0 ? Math.min(0, var1 + 48) : Math.max(0, var1 - 48);
    }

    static {
        dataPool = new ObjectArrayFIFOQueue((Integer)ServerConfig.CHUNK_REQUEST_THREAD_POOL_SIZE_CPU.getCurrentState());

        for(int var0 = 0; var0 < (Integer)ServerConfig.CHUNK_REQUEST_THREAD_POOL_SIZE_CPU.getCurrentState(); ++var0) {
            dataPool.enqueue(new RequestDataPlanet());
        }

    }
}