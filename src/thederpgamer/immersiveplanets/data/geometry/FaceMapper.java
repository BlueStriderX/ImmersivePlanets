package thederpgamer.immersiveplanets.data.geometry;

import javax.vecmath.Vector3f;

/**
 * FaceMapper.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public interface FaceMapper {
    Vector3f[] getVertices();
    Polygon[] getFaces();
}
