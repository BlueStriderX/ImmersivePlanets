package thederpgamer.immersiveplanets.graphics.planet;

import org.schema.schine.graphicsengine.core.Drawable;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import java.util.ArrayList;

/**
 * PlanetDrawer.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetDrawer implements Drawable {

    private boolean initialized;
    private ArrayList<Planet> drawList;

    public PlanetDrawer() {
        initialized = false;
        drawList = new ArrayList<>();
    }

    @Override
    public void onInit() {
        updateQueue();
        for(Planet planet : drawList) {
            planet.outerSphere.onInit();
            planet.innerSphere.onInit();
            planet.atmosphereMesh.onInit();
        }
    }

    @Override
    public void draw() {
        updateQueue();
        boolean doCleanup = false;
        for(Planet planet : drawList) {
            int level = TextureUtils.getCurrentLevel(planet);
            if(ImmersivePlanets.getInstance().debugMode && level == 0) {
                planet.outerSphere.draw();
                planet.innerSphere.draw();
            } else {
                planet.outerSphere.cleanUp();
                planet.innerSphere.cleanUp();
            }

            if(level == 0) {
                planet.atmosphereMesh.draw();
            //} else if(level > 0 && level < 5) {
                //planet.getPlanetSprite(level).draw();
            } else {
                doCleanup = true;
            }
        }
        if(doCleanup) cleanUp();
    }

    @Override
    public void cleanUp() {
        updateQueue();
        for(Planet planet : drawList) {
            planet.outerSphere.cleanUp();
            planet.innerSphere.cleanUp();
            planet.atmosphereMesh.cleanUp();
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    private void updateQueue() {
        ArrayList<Planet> planetList = DataUtils.getAllPlanets();
        ArrayList<Planet> toRemove = new ArrayList<>();
        for(Planet planet : planetList) {
            int currentLevel = TextureUtils.getCurrentLevel(planet);
            if(drawList.contains(planet)) {
                if(currentLevel == -1) toRemove.add(planet);
            } else {
                if(currentLevel != -1) drawList.add(planet);
            }
        }

        for(Planet planet : toRemove) drawList.remove(planet);
    }
}
