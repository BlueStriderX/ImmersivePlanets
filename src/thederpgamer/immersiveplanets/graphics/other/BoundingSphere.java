package thederpgamer.immersiveplanets.graphics.other;

import org.lwjgl.opengl.GL11;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.world.Segment;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.BoundingBox;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class BoundingSphere implements Drawable {

    private float radius;
    private Vector3f position;
    private Vector4f color;
    private boolean init;

    public BoundingSphere(float radius) {
        this.radius = radius;
        this.position = new Vector3f();
        this.init = false;
    }

    public BoundingSphere(float radius, Vector3f position) {
        this.radius = radius;
        this.position = position;
        this.init = false;
    }

    public BoundingSphere(BoundingBox boundingBox) {
        this.radius = 0;
        this.radius = Math.max(boundingBox.max.length(), boundingBox.min.length());
        this.position = boundingBox.getCenter(new Vector3f());
        this.init = false;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    public void cleanUp() {
        ImmersivePlanets.getInstance().debugMode = false;
    }

    @Override
    public void draw() {
        if(!init) onInit();
        if(ImmersivePlanets.getInstance().debugMode) {
            GlUtil.glColor4f(color);
            GlUtil.translateModelview(position);
            GlUtil.glDepthMask(false);
            if (isPositionInRadius(Controller.getCamera().getPos())) {
                GL11.glCullFace(GL11.GL_FRONT);
            } else {
                GL11.glCullFace(GL11.GL_BACK);
            }

            GlUtil.drawSphere(radius, 20);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

            GlUtil.drawSphere(radius, 20);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

            GlUtil.glDepthMask(true);
            GL11.glCullFace(GL11.GL_BACK);
        }
    }

    public boolean isPositionInRadius(Vector3f pos) {
        return getDistanceToCenter(pos) <= radius;
    }

    public float getDistanceToCenter(Vector3f pos) {
        float halfX = position.x - Segment.HALF_DIM;
        float halfY = position.y - Segment.HALF_DIM;
        float halfZ = position.z - Segment.HALF_DIM;
        return Math.abs(Vector3fTools.distance(pos.x, pos.y, pos.z, halfX, halfY, halfZ));
    }

    public boolean isPositionInRadius(Vector3i pos) {
        return getDistanceToCenter(pos) <= radius;
    }

    public float getDistanceToCenter(Vector3i pos) {
        float halfX = position.x - Segment.HALF_DIM;
        float halfY = position.y - Segment.HALF_DIM;
        float halfZ = position.z - Segment.HALF_DIM;
        Vector3i newPos = new Vector3i(halfX, halfY, halfZ);
        return Math.abs(Vector3i.getDisatance(newPos, pos));
    }

    @Override
    public boolean isInvisible() {
        return !ImmersivePlanets.getInstance().debugMode;
    }

    @Override
    public void onInit() {
        if(color == null) color = new Vector4f(150,150,150,0.1f);
        init = true;
    }
}
