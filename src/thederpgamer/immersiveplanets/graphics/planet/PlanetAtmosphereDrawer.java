package thederpgamer.immersiveplanets.graphics.planet;

import api.common.GameClient;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.Drawable;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.AtmosphereTransitionWarpHandler;
import thederpgamer.immersiveplanets.resources.textures.TextureLoader;
import thederpgamer.immersiveplanets.universe.world.Planet;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * PlanetAtmosphereDrawer.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetAtmosphereDrawer implements Drawable {

    private boolean initialized;
    public ArrayList<Planet> planetDrawQueue;

    public PlanetAtmosphereDrawer() {
        initialized = false;
        planetDrawQueue = new ArrayList<>();
    }

    public void queueDraw(Planet planet) {
        planet.initialize();
        planetDrawQueue.add(planet);
    }

    public void removeDraw(Planet planet) {
        planetDrawQueue.remove(planet);
    }

    @Override
    public void onInit() {
        for(Planet planet : planetDrawQueue) planet.initialize();
        initialized = true;
    }

    @Override
    public void draw() {
        if(!initialized) onInit();
        Vector3i clientSector = GameClient.getClientPlayerState().getCurrentSector();
        for(Planet planet : planetDrawQueue) {
            if(clientSector.equals(planet.planetSector)) {
                if(PlayerUtils.getCurrentControl(GameClient.getClientPlayerState()) != null) {
                    SegmentController entity = (SegmentController) PlayerUtils.getCurrentControl(GameClient.getClientPlayerState());
                    if(entity.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
                        if(runPositionCheck(planet)) {
                            if(AtmosphereTransitionWarpHandler.inAtmosphereMap.getList(planet).contains(GameClient.getClientPlayerState())) {
                                //Todo: Handle atmosphere exit
                            } else {
                                AtmosphereTransitionWarpHandler.handleReentry(entity, planet);
                            }
                        }
                    }
                }
            } else {
                int distance = (int) Math.abs(Math.floor(Vector3i.getDisatance(clientSector, planet.planetSector)));
                drawPlanet(planet, distance);
            }
        }
    }

    @Override
    public void cleanUp() {
        if(initialized) {
            for(Planet planet : planetDrawQueue) {
                planet.planetSprite.cleanUp();
                planet.outerSphere.cleanUp();
                planet.innerSphere.cleanUp();
            }
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    public boolean runPositionCheck(Planet planet) {
        Transform transform = new Transform();
        GameClient.getClientPlayerState().getWordTransform(transform);
        Vector3f clientPos = transform.origin;
        if(planet.outerSphere.isPositionInRadius(clientPos)) {
            if(planet.innerSphere.isPositionInRadius(clientPos)) {
                //Todo: Kill player
            } else {
                return true;
            }
        }
        return false;
    }

    private void drawPlanet(Planet planet, int distance) {
        boolean shouldDraw = true;
        if(distance == 0) { //full draw
            planet.planetSprite.setCurrentRes(TextureLoader.planetTextureResolutions[0]);
        } else if(distance == 1) {
            planet.planetSprite.setCurrentRes(TextureLoader.planetTextureResolutions[1]);
        } else if(distance == 2) {
            planet.planetSprite.setCurrentRes(TextureLoader.planetTextureResolutions[2]);
        } else if(distance == 3) {
            planet.planetSprite.setCurrentRes(TextureLoader.planetTextureResolutions[3]);
        } else if(distance == 4) {
            planet.planetSprite.setCurrentRes(TextureLoader.planetTextureResolutions[4]);
        } else if(distance >= 5) {
            planet.planetSprite.cleanUp();
            planet.outerSphere.cleanUp();
            planet.innerSphere.cleanUp();
            planetDrawQueue.remove(planet);
            shouldDraw = false;
        }
        if(shouldDraw) {
            planet.planetSprite.draw();
            if(ImmersivePlanets.getInstance().debugMode) {
                planet.outerSphere.draw();
                planet.innerSphere.draw();
            } else {
                planet.outerSphere.cleanUp();
                planet.innerSphere.cleanUp();
            }
        }
    }
}
