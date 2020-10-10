package net.dovtech.immersiveplanets.data.shape;

import org.lwjgl.opengl.GL11;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.texture.Material;
import org.schema.schine.graphicsengine.texture.Texture;
import javax.vecmath.Vector3f;

public abstract class Shape implements Drawable {

    public Mesh mesh;
    private ShapeType type;

    public Shape(ShapeType type) {
        this.type = type;
        this.mesh = (Mesh) Controller.getResLoader().getMesh(type.meshId).getChilds().get(0);
    }

    @Override
    public void cleanUp() {
        mesh.cleanUp();
    }

    @Override
    public void draw() {
        mesh.draw();
    }

    @Override
    public boolean isInvisible() {
        return mesh.isInvisible();
    }

    @Override
    public void onInit() {
        mesh.onInit();
    }

    public enum ShapeType {
        SPHERE("Sphere"),
        SPHERE_LOW_POLY("SphereLowPoly");

        public String meshId;

        ShapeType(String meshId) {
            this.meshId = meshId;
        }
    }

    public void setScale(float scale) {
        mesh.setScale(scale, scale, scale);
    }

    public void setScale(Vector3f scale) {
        mesh.setScale(scale.x, scale.y, scale.z);
    }

    public Vector3f getScale() {
        return mesh.getScale();
    }

    public void setPosition(Vector3f position) {
        mesh.setPos(position);
    }

    public void setPosition(float x, float y, float z) {
        mesh.setPos(x, y, z);
    }

    public Vector3f getPosition() {
        return mesh.getPos();
    }

    public void setTexture(Texture texture, boolean wrap) {
        GlUtil.glEnable(GL11.GL_TEXTURE_2D);
        mesh.getMaterial().setTexture(texture);
        GlUtil.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
        if(wrap) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }
    }

    public Texture getTexture() {
        return mesh.getMaterial().getTexture();
    }

    public Material getMaterial() {
        return mesh.getMaterial();
    }

    public boolean isPhysical() {
        return mesh.isCollisionObject();
    }

    public void setPhysical(boolean physical) {
        mesh.setCollisionObject(physical);
    }

    public boolean isVisible() {
        return mesh.getVisibility() == 1;
    }

    public void setVisible(boolean visible) {
        if(visible) {
            mesh.setVisibility(1);
        } else {
            mesh.setVisibility(2);
        }
    }
}
