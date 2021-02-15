package thederpgamer.immersiveplanets.data.geometry;

import org.schema.game.common.data.Icosahedron;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * MappedIcosahedron.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class MappedIcosahedron extends Icosahedron implements FaceMapper {

    private float radius;

    public MappedIcosahedron(float radius) {
        this.radius = radius;
    }

    @Override
    public Vector3f[] getVertices() {
        Vector3f[] vertices = new Vector3f[60];
        int v = 0;
        Polygon[] faces = getFaces();
        for(Polygon face : faces) {
            vertices[v] = face.vertices[0];
            vertices[v + 1] = face.vertices[1];
            vertices[v + 2] = face.vertices[2];
            v += 3;
        }
        return vertices;
    }

    @Override
    public Polygon[] getFaces() {
        Polygon[] faces = new Polygon[20];
        for(int i = 0; i < 20; i ++) {
            Matrix3f matrix = getSideTransform((byte) i).basis;
            Vector3f a = new Vector3f(matrix.m00, matrix.m01, matrix.m02);
            Vector3f b = new Vector3f(matrix.m10, matrix.m11, matrix.m12);
            Vector3f c = new Vector3f(matrix.m20, matrix.m21, matrix.m22);
            faces[i] = new Polygon(a, b, c);
        }
        return faces;
    }
}
