package thederpgamer.immersiveplanets.graphics.planet;

import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.ImmersivePlanets;
import thederpgamer.immersiveplanets.resources.textures.TextureLoader;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Locale;

/**
 * PlanetSprite.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetSprite implements Drawable {

    public boolean initialized;
    private HashMap<Integer, Sprite> spriteMap;
    private int currentRes = TextureLoader.planetTextureResolutions[0];

    public PlanetSprite(WorldType worldType) {
        spriteMap = new HashMap<>();
        String typeName = worldType.name().toLowerCase(Locale.ROOT).replaceAll("_", "-");
        for(int res : TextureLoader.planetTextureResolutions) {
            try {
                spriteMap.put(res, StarLoaderTexture.newSprite(ImageIO.read(TextureLoader.class.getResourceAsStream(typeName + "_" + res + ".png")), ImmersivePlanets.getInstance(), typeName + "_" + res));
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        initialized = false;
    }

    @Override
    public void onInit() {
        for(int res : TextureLoader.planetTextureResolutions) {
            try {
                if(spriteMap.get(res) != null) {
                    Sprite sprite = spriteMap.get(res);
                    sprite.onInit();
                    sprite.setBillboard(true);
                    scaleSprite(sprite, res);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void draw() {
        for(int res : TextureLoader.planetTextureResolutions) {
            try {
                if(res == currentRes) {
                    spriteMap.get(res).draw();
                } else {
                    spriteMap.get(res).cleanUp();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void cleanUp() {
        for(Sprite sprite : spriteMap.values()) {
            try {
                sprite.cleanUp();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    public Sprite getSprite(int res) {
        return spriteMap.get(res);
    }

    public void setCurrentRes(int currentRes) {
        for(int r : TextureLoader.planetTextureResolutions) {
            if(currentRes == r) {
                this.currentRes = currentRes;
                return;
            }
        }
        this.currentRes = TextureLoader.planetTextureResolutions[0];
    }

    private void scaleSprite(Sprite sprite, int res) {
        //float scaleX = (float) sprite.getWidth() / res;
        //float scaleY = (float) sprite.getHeight() / res;
        sprite.setWidth(res);
        sprite.setHeight(res);
        //sprite.setScale(scaleX, scaleY, 0);
        //Todo: Properly scale sprite
    }
}
