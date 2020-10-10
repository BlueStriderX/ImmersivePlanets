package net.dovtech.immersiveplanets.graphics;

import net.dovtech.immersiveplanets.DataUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.core.DrawableScene;
import org.schema.schine.graphicsengine.shader.Shader;
import org.schema.schine.graphicsengine.shader.Shaderable;

public class CelestialBodyDrawer implements Drawable, Shaderable {

    public Vector3i sector;
    private boolean init;

    @Override
    public void cleanUp() {
        if(DataUtils.gasGiants.containsKey(sector)) {
            DataUtils.gasGiants.get(sector).cleanUp();
        }
        init = false;
    }

    @Override
    public void draw() {
        if(DataUtils.gasGiants.containsKey(sector)) {
            if(!init) onInit();
            DataUtils.gasGiants.get(sector).draw();
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void onInit() {
        if(DataUtils.gasGiants.containsKey(sector)) {
            DataUtils.gasGiants.get(sector).onInit();
            init = true;
        }
    }

    @Override
    public void onExit() {
        cleanUp();
    }

    @Override
    public void updateShader(DrawableScene drawableScene) {

    }

    @Override
    public void updateShaderParameters(Shader shader) {

    }
}
