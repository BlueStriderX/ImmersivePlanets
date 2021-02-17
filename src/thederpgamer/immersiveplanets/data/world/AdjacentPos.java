package thederpgamer.immersiveplanets.data.world;

import thederpgamer.immersiveplanets.data.other.vector.Vector2i;

/**
 * AdjacentPos.java
 * <Description>
 * ==================================================
 * Created 02/16/2021
 * @author TheDerpGamer
 */
public enum AdjacentPos {

    CENTER(-1),
    TOP_LEFT(0),
    TOP_MIDDLE(1),
    TOP_RIGHT(2),
    MID_LEFT(3),
    MID_RIGHT(4),
    BOTTOM_LEFT(5),
    BOTTOM_MIDDLE(6),
    BOTTOM_RIGHT(7);

    public int pos;

    AdjacentPos(int pos) {
        this.pos = pos;
    }

    public static AdjacentPos getNext(Vector2i direction) {
        int x = direction.x;
        int y = direction.y;

        if(x < 0) {
            if(y < 0) {
                return BOTTOM_LEFT;
            } else if(y > 0) {
                return TOP_LEFT;
            } else {
                return MID_LEFT;
            }
        } else if(x > 0) {
            if(y < 0) {
                return BOTTOM_RIGHT;
            } else if(y > 0) {
                return TOP_RIGHT;
            } else {
                return MID_RIGHT;
            }
        } else {
            if(y < 0) {
                return BOTTOM_MIDDLE;
            } else if(y > 0) {
                return TOP_MIDDLE;
            } else {
                return CENTER;
            }
        }
    }
}
