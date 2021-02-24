package thederpgamer.immersiveplanets.graphics.texture;

import org.schema.schine.graphicsengine.forms.Sprite;
import java.util.HashMap;

/**
 * TextureLoader.java
 * <Description>
 * ==================================================
 * Created 02/23/2021
 * @author TheDerpGamer
 */
public class TextureLoader {

    private static HashMap<String, Sprite> spriteMap = new HashMap<>();

    public static void addMaterial(String name, Sprite sprite) {
        spriteMap.put(name, sprite);
    }

    public static Sprite getSprite(String name) {
        return spriteMap.get(name);
    }
}
