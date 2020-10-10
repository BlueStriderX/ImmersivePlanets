package net.dovtech.immersiveplanets.planet;

import net.dovtech.immersiveplanets.ImmersivePlanets;

public enum BodyType {
    PLANET_ORGANIC("planet/planet-organic.png"),
    PLANET_ROCKY("planet/planet-rocky.png"),
    PLANET_ARID("planet/planet-arid.png"),
    PLANET_DESERT("planet/planet-desert.png"),
    PLANET_FROZEN("planet/planet-frozen.png"),
    PLANET_ALIEN("planet/planet-alien.png"),
    GAS_GIANT_ORANGE("gas-giant/gas-giant-orange.png"),
    GAS_GIANT_RED("gas-giant/gas-giant-red.png"),
    GAS_GIANT_BLUE("gas-giant/gas-giant-blue.png");
    /*
    MOON_ORGANIC,
    MOON_ROCKY,
    MOON_ARID,
    MOON_DESERT,
    MOON_FROZEN,
    MOON_ALIEN;
     */

    private String texturePath;

    BodyType(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }
}