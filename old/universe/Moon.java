package old.universe;

import org.schema.common.util.linAlg.Vector3i;

public class Moon extends CelestialBody {

    private CelestialBody parentBody;

    public Moon(Vector3i sector, BodyType bodyType, CelestialBody parentBody) {
        super(sector, bodyType);
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
