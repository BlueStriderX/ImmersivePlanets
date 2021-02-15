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
            if(planet.planetSector.equals(sector) && planet.planetSprite.doDraw) {
                float distance = Math.abs(Vector3i.getDisatance(GameClient.getClientPlayerState().getCurrentSector(), sector));
                String meshName;
                Sprite planetTexture;
                if(distance >= 5) {
                    //Don't draw
                    meshName = null;
                    planetTexture = null;
                } else if(distance >= 4) {
                    meshName = "planet-sphere_4";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 4);
                } else if(distance >= 3) {
                    meshName = "planet-sphere_3";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 3);
                } else if(distance >= 2) {
                    meshName = "planet-sphere_2";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 2);
                } else if(distance >= 1) {
                    meshName = "planet-sphere_1";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 1);
                } else {
                    meshName = "planet-sphere_0";
                    planetTexture = TextureUtils.getPlanetTexture(planet.type, 0);
                }

                if(meshName != null && planetTexture != null && !sphere.getName().equals(meshName)) {
                    String typeName = planet.type.toString().toLowerCase().replaceAll("_", "-");
                    Mesh newMesh = ImmersivePlanets.getInstance().resLoader.getMeshLoader().getModMesh(ImmersivePlanets.getInstance(), meshName);
                    newMesh.getMaterial().texturePathFull = planetTexture.getMaterial().texturePathFull;
                    newMesh.getMaterial().setTexture(planetTexture.getMaterial().getTexture());
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
