package thederpgamer.immersiveplanets.graphics.planet;

import api.DebugFile;
import api.listener.fastevents.PlanetDrawListener;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.planetdrawer.PlanetInformations;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.world.SectorInformation;
import org.schema.schine.graphicsengine.forms.Mesh;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.universe.space.Planet;

/**
 * PlanetDrawer.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetDrawer implements PlanetDrawListener {

    private PlanetDrawData[] drawData;

    public PlanetDrawer() {
        drawData = new PlanetDrawData[ImmersivePlanets.getInstance().maxPlanetsDrawn];

        new StarRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTimer(ImmersivePlanets.getInstance(), 100);
    }

    private void update() {
        for(PlanetDrawData data : drawData) data.update();
    }

    public void addPlanet(Planet planet) {
        for(int i = 0; i < drawData.length; i ++) {
            if(drawData[i].drawMode == PlanetDrawData.MODE_NONE) {
                drawData[i] = new PlanetDrawData(planet);
                return;
            }
        }
        DebugFile.log("[WARNING]: Cannot draw planet " + planet.planetId + planet.planetSector.toString() +
                " as the max planets able to be drawn at once is currently set at " +
                ImmersivePlanets.getInstance().maxPlanetsDrawn + ".", ImmersivePlanets.getInstance());
    }

    public boolean contains(Vector3i sector) {
        for(PlanetDrawData data : drawData) {
            if(data.sector.equals(sector)) return true;
        }
        return false;
    }

    @Override
    public void onPlanetDraw(org.schema.game.client.view.planetdrawer.PlanetDrawer internalDrawer, Vector3i vector3i, PlanetInformations planetInformations, SectorInformation.PlanetType planetType, Mesh mesh, Dodecahedron dodecahedron) {
        for(PlanetDrawData data : drawData) data.draw();
    }
}
