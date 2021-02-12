package old.data.shape;

public class Sphere extends Shape {

    public Sphere(float radius) {
        super(ShapeType.SPHERE);
        setRadius(radius);
    }

    public void setRadius(float radius) {
        setScale(radius / 100);
    }

    public float getRadius() {
        return getScale().x * 100;
    }
}