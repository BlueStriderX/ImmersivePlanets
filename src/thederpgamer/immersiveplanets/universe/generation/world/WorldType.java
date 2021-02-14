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
    PLANET_DEBUG(0, 0, 500, 1000, false);

    /*
    PLANET_EARTH(8, 16, 650, 1500, false),
    PLANET_AQUATIC(8, 16, 650, 1300, false),
    PLANET_ICE(8, 16, 650, 1500, false),
    PLANET_DESERT(8, 16, 650, 1500, false),
    PLANET_MESA(8, 16, 650, 1500, false),
    PLANET_ALIEN(8, 16, 650, 1300, false),
    PLANET_BARREN(4, 16, 300, 1300, false),

    //Gas Giants
    GAS_GIANT_RED(12, 24, 1500, 5000, true),
    GAS_GIANT_ORANGE(12, 24, 1500, 5000, true),
    GAS_GIANT_BLUE(12, 24, 1500, 5000, true),
    GAS_GIANT_GREEN(12, 24, 1500, 5000, true);
     */

    public int minSegments;
    public int maxSegments;
    public float minRadius;
    public float maxRadius;
    public boolean hasCompression;

    WorldType(int minSegments, int maxSegments, float minRadius, float maxRadius, boolean hasCompression) {
        this.minSegments = minSegments;
        this.maxSegments = maxSegments;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.hasCompression = hasCompression;
    }
}
