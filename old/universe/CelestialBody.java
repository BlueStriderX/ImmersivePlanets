package old.universe;

import net.dovtech.immersiveplanets.data.shape.Shape;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Drawable;

public abstract class CelestialBody implements Drawable {

    public Shape shape;
    public Vector3i sector;
    private boolean visible;
    private boolean init;

    public CelestialBody(Vector3i sector) {
        this.sector = sector;
        this.init = false;
    }

    @Override
    public void cleanUp() {
        if(shape != null) {
            shape.cleanUp();
        }
    }

    @Override
    public void draw() {
        if(!init) onInit();
        if(shape != null) {
            shape.draw();
        }
    }

    @Override
    public boolean isInvisible() {
        return !visible;
    }

    @Override
    public void onInit() {
        if(shape != null) {
            shape.onInit();
            init = true;
        }
    }

    public Vector3i getSector() {
        return sector;
    }
}
