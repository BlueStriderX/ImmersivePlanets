package thederpgamer.immersiveplanets.listeners;

import api.common.GameClient;
import api.listener.fastevents.PlanetDrawListener;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.planetdrawer.PlanetDrawer;
import org.schema.game.client.view.planetdrawer.PlanetInformations;
import org.schema.game.common.data.Dodecahedron;
import org.schema.game.common.data.world.SectorInformation;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.universe.space.Planet;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import javax.vecmath.Vector3f;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * PlanetDrawHandler.java
 * <Description>
 * ==================================================
 * Created 02/14/2021
 * @author TheDerpGamer
 */
public class PlanetDrawHandler implements PlanetDrawListener {

    @Override
    public void onPlanetDraw(PlanetDrawer planetDrawer, Vector3i sector, PlanetInformations planetInfo, SectorInformation.PlanetType planetType, Mesh sphere, Dodecahedron core) {
        ArrayList<Planet> toDraw = new ArrayList<>(ImmersivePlanets.getInstance().planetDrawer.planetDrawQueue);
        for(Planet planet : toDraw) {
            //if(planet.planetSector.equals(sector) && planet.planetSprite.doDraw) {
            if(planet.planetSector.equals(sector)) {
                float distance = Math.abs(Vector3i.getDisatance(GameClient.getClientPlayerState().getCurrentSector(), sector));
                String meshName;
                Sprite planetTexture;
                Vector3f scale;
                if(distance >= 5) {
                    //Don't draw
                    meshName = null;
                    planetTexture = null;
                    scale = null;
                } else if(distance >= 4) {
                    meshName = "planet-sphere_4";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 4);
                    scale = new Vector3f(1, 1, 1);
                } else if(distance >= 3) {
                    meshName = "planet-sphere_3";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 3);
                    scale = new Vector3f(2, 2, 2);
                } else if(distance >= 2) {
                    meshName = "planet-sphere_2";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 2);
                    scale = new Vector3f(4, 4, 4);
                } else if(distance >= 1) {
                    meshName = "planet-sphere_1";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 1);
                    scale = new Vector3f(8, 8, 8);
                } else {
                    meshName = "planet-sphere_0";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 0);
                    scale = new Vector3f(16, 16, 16);
                }

                if(meshName != null && planetTexture != null && !sphere.getName().equals(meshName)) {
                    Mesh newMesh = ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), meshName);
                    newMesh.getMaterial().texturePathFull = planetTexture.getMaterial().texturePathFull;
                    newMesh.getMaterial().setTexture(planetTexture.getMaterial().getTexture());
                    newMesh.setScale(scale);
                    try {
                        Field meshField = planetDrawer.getClass().getDeclaredField("sphere");
                        meshField.setAccessible(true);
                        meshField.set(planetDrawer, newMesh);
                    } catch(NullPointerException | NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                //ArrayList<Sprite> sprites = new ArrayList<>(planet.planetSprite.spriteMap.values());
                //for(Sprite sprite : sprites) sprite.setPos(sphere.getPos());
            }
        }
    }
}
