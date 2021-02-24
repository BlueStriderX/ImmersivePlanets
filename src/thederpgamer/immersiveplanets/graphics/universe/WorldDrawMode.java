package thederpgamer.immersiveplanets.graphics.universe;

/**
 * WorldDrawMode.java
 * <Description>
 * ==================================================
 * Created 02/23/2021
 * @author TheDerpGamer
 */
public enum WorldDrawMode {
    NONE(-1),
    SPHERE_FULL(0),
    SPHERE_HALF(1),
    SPRITE_512(2),
    SPRITE_256(3),
    SPRITE_64(4);

    public int drawLevel;

    WorldDrawMode(int drawLevel) {
        this.drawLevel = drawLevel;
    }

    public static WorldDrawMode getFromDistance(float distance) {
        if(distance < 6) {
            if(distance >= 5) {
                return SPRITE_64;
            } else if(distance >= 4) {
                return SPRITE_256;
            } else if(distance >= 3) {
                return SPRITE_512;
            } else if(distance >= 2) {
                return SPHERE_HALF;
            } else {
                return SPHERE_FULL;
            }
        } else {
            return NONE;
        }
    }
}
