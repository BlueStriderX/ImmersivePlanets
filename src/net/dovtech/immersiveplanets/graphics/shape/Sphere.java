package net.dovtech.immersiveplanets.graphics.shape;

import api.common.GameClient;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.Segment;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Mesh;
import javax.vecmath.Vector3f;

public class Sphere extends Mesh implements Drawable {

    private Vector3i sector;
    private float radius = 0;

    public boolean isPositionInRadius(Vector3f pos) {
        return getDistanceToCenter(pos) <= radius;
    }

    public float getDistanceToCenter(Vector3f pos) {
        float halfX = getPos().x - Segment.HALF_DIM;
        float halfY = getPos().y - Segment.HALF_DIM;
        float halfZ = getPos().z - Segment.HALF_DIM;
        return Math.abs(Vector3fTools.distance(pos.x, pos.y, pos.z, halfX, halfY, halfZ));
    }

    @Override
    public void onInit() {
        if(radius <= 0) {
            if(getBoundingSphereRadius() <= 0) {
                setBoundingSphereRadius(300);
                radius = 300;
            } else {
                radius = getBoundingSphereRadius();
            }
        }
        if(sector == null) sector = GameClient.getClientPlayerState().getCurrentSector();
    }

    public Vector3i getSector() {
        return sector;
    }

    public void setSector(Vector3i sector) {
        this.sector = sector;
    }

    public float getRadius() {
        if(radius <= 0) {
            if(getBoundingSphereRadius() <= 0) {
                setBoundingSphereRadius(300);
                radius = 300;
            } else {
                radius = getBoundingSphereRadius();
            }
        }
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        setBoundingSphereRadius(this.radius);
    }
}