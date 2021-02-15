package thederpgamer.immersiveplanets.data.geometry;

import javax.vecmath.Vector3f;

/**
 * Polygon.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class Polygon {

    public Vector3f[] vertices;

    public Polygon(Vector3f... vertices) {
        this.vertices = vertices;
    }
}
