package thederpgamer.immersiveplanets.data.other.vector;

import javax.vecmath.*;
import java.io.Serializable;

/**
 * Vector2i.java
 * <Description>
 * ==================================================
 * Created 02/16/2021
 * @author TheDerpGamer
 */
public class Vector2i extends Tuple2i implements Serializable {

    public Vector2i() {
        super(0, 0);
    }

    public Vector2i(int x, int y) {
        super(x, y);
    }

    public Vector2i(int[] array) {
        super(array);
    }

    public Vector2i(Vector2f vector) {
        super((int) vector.x, (int) vector.y);
    }

    public Vector2i(float[] array) {
        super((int) array[0], (int) array[1]);
    }

    public int dot(Vector2i vector) {
        return this.x * vector.x + this.y * vector.y;
    }

    public int length() {
        return (int) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public int lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public void normalize(Vector2i vector) {
        int i = (int) (1 / Math.sqrt(vector.x * vector.x + vector.y * vector.y));
        this.x = vector.x * i;
        this.y = vector.y * i;
    }

    public void normalize() {
        int i = (int) (1 / Math.sqrt(this.x * this.x + this.y * this.y));
        this.x *= i;
        this.y *= i;
    }

    public int angle(Vector2i vector) {
        int i = this.dot(vector) / (this.length() * vector.length());
        if(i < -1) i = -1;
        if(i > 1) i = 1;
        return (int) Math.acos(i);
    }
}
