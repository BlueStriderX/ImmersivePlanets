package thederpgamer.immersiveplanets.graphics.planet;

import api.common.GameClient;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.Drawable;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.data.handler.AtmosphereTransitionWarpHandler;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import thederpgamer.immersiveplanets.universe.space.Planet;
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
        ArrayList<Planet> toDraw = new ArrayList<>(planetDrawQueue);
        for(Planet planet : toDraw) planet.initialize();
        initialized = true;
    }

    @Override
    public void draw() {
        if(!initialized) onInit();
        Vector3i clientSector = GameClient.getClientPlayerState().getCurrentSector();
        ArrayList<Planet> toDraw = new ArrayList<>(planetDrawQueue);
        for(Planet planet : toDraw) {
            if(clientSector != null) {
                if (clientSector.equals(planet.planetSector)) {
                    if (GameClient.getClientPlayerState().isControllingCore()) {
                        SegmentController entity = (SegmentController) PlayerUtils.getCurrentControl(GameClient.getClientPlayerState());
                        if (entity.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
                            if (runPositionCheck(planet)) {
                                if (AtmosphereTransitionWarpHandler.inAtmosphereMap.getList(planet).contains(GameClient.getClientPlayerState())) {
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
    }

    @Override
    public void cleanUp() {
        if(initialized) {
            ArrayList<Planet> toDraw = new ArrayList<>(planetDrawQueue);
            for(Planet planet : toDraw) {
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
        if(distance != 0) {
            planet.outerSphere.cleanUp();
            planet.innerSphere.cleanUp();
        }
        if(distance >= 5) {
            planet.planetSprite.doDraw = false;
            planet.planetSprite.cleanUp();
            shouldDraw = false;
        } else if(distance >= 4) {
            planet.planetSprite.setCurrentRes(TextureUtils.planetTextureResolutions[4]);
        } else if(distance >= 3) {
            planet.planetSprite.setCurrentRes(TextureUtils.planetTextureResolutions[3]);
        } else if(distance >= 2) {
            planet.planetSprite.setCurrentRes(TextureUtils.planetTextureResolutions[2]);
        } else if(distance >= 1) {
            planet.planetSprite.setCurrentRes(TextureUtils.planetTextureResolutions[1]);
        } else {
            planet.planetSprite.setCurrentRes(TextureUtils.planetTextureResolutions[0]);
        }

        if(shouldDraw) {
            //planet.planetSprite.doDraw = true;
            planet.planetSprite.doDraw = false;
            planet.planetSprite.draw();
            if(ImmersivePlanets.getInstance().debugMode && distance == 0) {
                planet.outerSphere.draw();
                planet.innerSphere.draw();
            } else {
                planet.outerSphere.cleanUp();
                planet.innerSphere.cleanUp();
            }
        }
    }
}
