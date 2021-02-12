package old.data.shape;

public class SphereLowPoly extends Shape {

    public SphereLowPoly(float radius) {
        super(ShapeType.SPHERE_LOW_POLY);
        setRadius(radius);
    }

    public void setRadius(float radius) {
        setScale(radius / 100);
    }

    public float getRadius() {
        return getScale().x * 100;
    }
}
