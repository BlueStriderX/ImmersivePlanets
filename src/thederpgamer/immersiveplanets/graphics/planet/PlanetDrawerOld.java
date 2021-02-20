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
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.DataUtils;
import thederpgamer.immersiveplanets.utils.TextureUtils;

/**
 * PlanetDrawerOld.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetDrawerOld implements PlanetDrawListener {

    private PlanetDrawData[] drawData;

    public PlanetDrawerOld() {
        drawData = new PlanetDrawData[ImmersivePlanets.getInstance().maxPlanetsDrawn];

        new StarRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTimer(ImmersivePlanets.getInstance(), 100);
    }

    private void update() {
        for(PlanetDrawData data : drawData) {
            if(data != null) data.update();
        }
    }

    public void addPlanet(Planet planet) {
        for(int i = 0; i < drawData.length; i ++) {
            if(drawData[i] != null) {
                if(drawData[i].drawMode == PlanetDrawData.MODE_NONE) {
                    drawData[i] = new PlanetDrawData(planet);
                    return;
                }
            } else {
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
            if(data != null && data.sector.equals(sector)) return true;
        }
        return false;
    }

    @Override
    public void onPlanetDraw(org.schema.game.client.view.planetdrawer.PlanetDrawer internalDrawer, Vector3i sector, PlanetInformations planetInformations, SectorInformation.PlanetType planetType, Mesh sphere, Dodecahedron dodecahedron) {
        if(!contains(sector) && DataUtils.getFromSector(sector) != null) addPlanet(DataUtils.getFromSector(sector));
        for(PlanetDrawData data : drawData) {
            if(data != null) {
                if(!data.loaded) {
                    sphere.setVertCount(872);
                    sphere.setFaceCount(900);
                    sphere.getScale().scale(5);
                    sphere.setMaterial(TextureUtils.getPlanetTexture(WorldType.PLANET_DEBUG, 0).getMaterial());
                    sphere.updateBound();
                    sphere.draw();
                    data.loaded = true;
                }

                if(data.drawMode == PlanetDrawData.MODE_SPHERE_DEBUG) {
                    data.outerSphere.draw();
                    data.innerSphere.draw();
                } else {
                    data.outerSphere.cleanUp();
                    data.innerSphere.cleanUp();
                }
            }
        }
    }
}
