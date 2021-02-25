package thederpgamer.immersiveplanets.universe.generation.world;

/**
 * WorldType.java
 * <Description>
 * ==================================================
 * Created 02/13/2021
 * @author TheDerpGamer
 */
public enum WorldType { //Todo: Make these settings configurable
    //Planets
    PLANET_DEBUG("planet_debug");

    public String name;

    WorldType(String name) {
        this.name = name;
    }

    public static WorldType parseWorldType(String string) {
        for(WorldType type : WorldType.values()) {
            if(string.equals(type.name)) return type;
        }
        return PLANET_DEBUG;
    }
}
