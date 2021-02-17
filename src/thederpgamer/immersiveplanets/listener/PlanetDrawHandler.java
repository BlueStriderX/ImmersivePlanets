package thederpgamer.immersiveplanets.listener;

import api.common.GameClient;
import api.listener.fastevents.PlanetDrawListener;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.planetdrawer.PlanetDrawer;
import org.schema.game.client.view.planetdrawer.PlanetInformations;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.world.SectorInformation;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.immersiveplanets.data.handler.AtmosphereTransitionWarpHandler;
import thederpgamer.immersiveplanets.graphics.other.BoundingSphere;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.universe.space.planet.DebugPlanet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import javax.vecmath.Vector3f;

/**
 * PlanetDrawHandler.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public class PlanetDrawHandler implements PlanetDrawListener {

    private Planet planet;
    private Vector3f playerPos;

    private void initPlanet(Vector3i sector, PlanetInformations planetInfo, SectorInformation.PlanetType planetType) {
        if(DataUtils.getFromSector(sector) == null) {
            //Todo
            planet = new DebugPlanet(500, DataUtils.getNewPlanetId());
            planet.getData();
        } else {
            planet = DataUtils.getFromSector(sector);
        }
    }

    @Override
    public void onPlanetDraw(PlanetDrawer planetDrawer, Vector3i sector, PlanetInformations planetInfo, SectorInformation.PlanetType planetType, Mesh sphere, Dodecahedron core) {
        if(planet == null) initPlanet(sector, planetInfo, planetType);
        if(planet.planetSector.equals(GameClient.getClientPlayerState().getCurrentSector())) {
            BoundingSphere outerSphere = planet.outerSphere;
            BoundingSphere innerSphere = planet.innerSphere;

            Vector3f lastPos = new Vector3f(playerPos);
            updatePlayerPos();
            if(innerSphere.isPositionInRadius(playerPos)) {
                //Todo: Move player out of inner sphere
            } else if(outerSphere.isPositionInRadius(playerPos) && !outerSphere.isPositionInRadius(lastPos)) { //Entering Sphere
                AtmosphereTransitionWarpHandler.handleReentry(GameClient.getCurrentControl(), planet);
            } else if(outerSphere.isPositionInRadius(lastPos) && !outerSphere.isPositionInRadius(playerPos)) { //Exiting Sphere
                AtmosphereTransitionWarpHandler.handleExit(GameClient.getCurrentControl(), planet);
            }
        }
    }

    private void updatePlayerPos() {
        Transform transform = new Transform();
        GameClient.getClientPlayerState().getWordTransform(transform);
        playerPos = transform.origin;
    }
}
