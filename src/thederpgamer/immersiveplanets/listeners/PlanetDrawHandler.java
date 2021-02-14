package thederpgamer.immersiveplanets.listeners;

import api.listener.fastevents.PlanetDrawListener;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.planetdrawer.PlanetInformations;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.world.SectorInformation;
import org.schema.schine.graphicsengine.forms.Mesh;

/**
 * PlanetDrawHandler.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class PlanetDrawHandler implements PlanetDrawListener {

    @Override
    public void onPlanetDraw(Vector3i sector, PlanetInformations planetInfo, SectorInformation.PlanetType planetType, Mesh sphere, Dodecahedron core) {

    }
}
