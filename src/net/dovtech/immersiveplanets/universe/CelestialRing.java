package net.dovtech.immersiveplanets.universe;

public class CelestialRing extends CelestialBody {

    private CelestialBody parentBody;

    public CelestialRing(CelestialBody parentBody) {
        this.parentBody = parentBody;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void draw() {

    }

    @Override
    public void cleanUp() {

    }
}
