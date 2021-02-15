package thederpgamer.immersiveplanets.data.handler;

import api.DebugFile;
import api.common.GameClient;
import api.common.GameServer;
import api.utils.other.HashList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.controller.SectorSwitch;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.universe.space.Planet;
import javax.vecmath.Vector3f;

/**
 * AtmosphereTransitionWarpHandler.java
 * Handles the transition going in and out of atmosphere
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class AtmosphereTransitionWarpHandler {

    public static HashList<Planet, PlayerState> inAtmosphereMap = new HashList<>();

    public static void handleReentry(SegmentController entity, Planet planet) {
        if(entity.getType().equals(SimpleTransformableSendableObject.EntityType.ASTEROID) || entity.getType().equals(SimpleTransformableSendableObject.EntityType.ASTEROID_MANAGED)) {
            //Todo: Handle asteroid reentry
        } else if(!entity.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
            //Todo: Burnup entity (kill)
        } else {
            //Todo: Atmosphere reentry effect (to mask warp)
            //Todo: Overwrite galaxy map
            Vector3i realSector = planet.getRealSector();
            SectorSwitch sectorSwitch = GameServer.getServerState().getController().queueSectorSwitch(entity, realSector, SectorSwitch.TRANS_JUMP,  false, true, false);
            if(sectorSwitch == null) {
                DebugFile.log("[ERROR]: Failed to change sector for entity " + entity.getName() + " to " + realSector.toString() + "!", ImmersivePlanets.getInstance());
                //Todo: Don't allow ship to enter atmosphere
            } else {
                sectorSwitch.jumpSpawnPos = new Vector3f(entity.getWorldTransform().origin); //Todo: Map entry pos to surface pos
                sectorSwitch.executionGraphicsEffect = (byte) 0;
                sectorSwitch.keepJumpBasisWithJumpPos = true;
                inAtmosphereMap.add(planet, GameClient.getClientPlayerState());
            }
        }
    }

    public static void handleExit(SegmentController entity, Planet planet) {
        if(entity.getType().equals(SimpleTransformableSendableObject.EntityType.ASTEROID) || entity.getType().equals(SimpleTransformableSendableObject.EntityType.ASTEROID_MANAGED)) {
            //Todo: Handle asteroid exit
        } else if(!entity.getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
            //Todo: Burnup entity (kill)
        } else {
            //Todo: Atmosphere exit effect (to mask warp)
            //Todo: Switch back galaxy map
            SectorSwitch sectorSwitch = GameServer.getServerState().getController().queueSectorSwitch(entity, planet.planetSector, SectorSwitch.TRANS_JUMP,  false, true, false);
            if(sectorSwitch == null) {
                DebugFile.log("[ERROR]: Failed to change sector for entity " + entity.getName() + " to " + planet.planetSector.toString() + "!", ImmersivePlanets.getInstance());
                //Todo: Don't allow ship to exit atmosphere
            } else {
                sectorSwitch.jumpSpawnPos = new Vector3f(entity.getWorldTransform().origin); //Todo: Map surface pos to exit pos
                sectorSwitch.executionGraphicsEffect = (byte) 0;
                sectorSwitch.keepJumpBasisWithJumpPos = true;
                inAtmosphereMap.get(planet).remove(GameClient.getClientPlayerState());
            }
        }
    }
}
