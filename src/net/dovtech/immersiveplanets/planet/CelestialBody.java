package net.dovtech.immersiveplanets.planet;

import net.dovtech.immersiveplanets.data.shape.Shape;
import org.schema.schine.graphicsengine.core.Drawable;

public abstract class CelestialBody implements Drawable {

    private boolean visible;
    private Shape shape;

    @Override
    public void cleanUp() {
        shape.cleanUp();
    }

    @Override
    public void draw() {
        shape.draw();
    }

    @Override
    public boolean isInvisible() {
        return !visible;
    }

    @Override
    public void onInit() {
        shape.onInit();
    }
}
