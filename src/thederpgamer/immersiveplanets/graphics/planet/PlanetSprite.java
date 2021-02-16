package thederpgamer.immersiveplanets.graphics.planet;

import org.schema.schine.graphicsengine.core.Drawable;
import org.schema.schine.graphicsengine.forms.Sprite;
import thederpgamer.immersiveplanets.utils.TextureUtils;
import thederpgamer.immersiveplanets.universe.generation.world.WorldType;

import java.util.HashMap;

/**
 * PlanetSprite.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public class PlanetSprite implements Drawable {

    public boolean initialized;
    public HashMap<Integer, Sprite> spriteMap;
    private int currentRes = TextureUtils.PlanetTextureResolution.RES_2048.getRes();
    public WorldType worldType;
    public boolean doDraw;

    public PlanetSprite(WorldType worldType) {
        this.worldType = worldType;
        spriteMap = new HashMap<>();
        initialized = false;
        doDraw = false;
    }

    @Override
    public void onInit() {
        for(TextureUtils.PlanetTextureResolution res : TextureUtils.PlanetTextureResolution.values()) {
            try {
                if(spriteMap.get(res.getRes()) != null) {
                    Sprite sprite = spriteMap.get(res.getRes());
                    sprite.onInit();
                    sprite.setBillboard(true);
                    scaleSprite(sprite, res.getRes());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void draw() {
        for(TextureUtils.PlanetTextureResolution res : TextureUtils.PlanetTextureResolution.values()) {
            try {
                if(res.getRes() == currentRes && doDraw) {
                    spriteMap.get(res.getRes()).draw();
                } else {
                    spriteMap.get(res.getRes()).cleanUp();
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
        return !doDraw;
    }

    public Sprite getSprite(int res) {
        return spriteMap.get(res);
    }

    public void setCurrentRes(int currentRes) {
        for(TextureUtils.PlanetTextureResolution res : TextureUtils.PlanetTextureResolution.values()) {
            if(currentRes == res.getRes()) {
                this.currentRes = currentRes;
                return;
            }
        }
        this.currentRes = TextureUtils.PlanetTextureResolution.RES_2048.getRes();
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
