package thederpgamer.immersiveplanets.graphics.universe;

import api.DebugFile;
import org.schema.schine.graphicsengine.core.Drawable;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.graphics.model.WorldDrawData;

/**
 * WorldEntityDrawer.java
 * <Description>
 * ==================================================
 * Created 02/22/2021
 * @author TheDerpGamer
 */
public class WorldEntityDrawer implements Drawable {

    private WorldDrawData[] worlds;

    public WorldEntityDrawer() {
        worlds = new WorldDrawData[ImmersivePlanets.getInstance().maxPlanetsDrawn];
    }

    @Override
    public void onInit() {
        for(WorldDrawData world : worlds) if(world != null) world.onInit();
    }

    @Override
    public void draw() {
        for(WorldDrawData world : worlds) if(world != null) world.draw();
    }

    @Override
    public void cleanUp() {
        for(WorldDrawData world : worlds) if(world != null) world.cleanUp();
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    public void addDrawData(WorldDrawData drawData) {
        for(WorldDrawData world : worlds) {
            if(world != null && world.getWorldId() == drawData.getWorldId()) return;
        }

        for(int i = 0; i < worlds.length; i ++) {
            if(worlds[i] == null || worlds[i].getDrawMode().equals(WorldDrawMode.NONE)) {
                worlds[i] = drawData;
                worlds[i].onInit();
                return;
            }
        }
        DebugFile.log("[WARNING]: Cannot add DrawData for planet at " + drawData.getSector() + " as the draw list is already full!", ImmersivePlanets.getInstance());
    }
}