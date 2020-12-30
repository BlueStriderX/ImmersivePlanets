package net.dovtech.immersiveplanets.universe;

public enum BodyType {
    PLANET_ORGANIC("universe/universe-organic.png", 0),
    PLANET_ROCKY("universe/universe-rocky.png", 1),
    PLANET_ARID("universe/universe-arid.png", 2),
    PLANET_DESERT("universe/universe-desert.png", 3),
    PLANET_FROZEN("universe/universe-frozen.png", 4),
    PLANET_ALIEN("universe/universe-alien.png", 5),
    GAS_GIANT_ORANGE("gas-giant/gas-giant-orange.png", 6),
    GAS_GIANT_RED("gas-giant/gas-giant-red.png", 7),
    GAS_GIANT_BLUE("gas-giant/gas-giant-blue.png", 8),
    MOON_ORGANIC("moon/moon-organic.png", 9),
    MOON_ROCKY("moon/moon-rocky.png", 10),
    MOON_ARID("moon/moon-arid.png", 11),
    MOON_DESERT("moon/moon-desert.png", 12),
    MOON_FROZEN("moon/moon-frozen.png", 13),
    MOON_ALIEN("moon/moon-alien.png", 14),
    RING("other/ring.png", 15);


    private String texturePath;
    private int id;

    BodyType(String texturePath, int id) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getId() {
        return id;
    }

    public static BodyType getFromID(int ID) {
        for(BodyType b : values()) {
            if(b.getId() == ID) return b;
        }
        return null;
    }
}