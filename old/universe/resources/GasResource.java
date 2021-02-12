package old.universe.resources;

public class GasResource {

    private short id;
    private int baseCount;
    private float minDepth;
    private float maxDepth;

    public GasResource(short id, int baseCount, float minDepth, float maxDepth) {
        this.id = id;
        this.baseCount = baseCount;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    public short getId() {
        return id;
    }

    public int getBaseCount() {
        return baseCount;
    }

    public float getMinDepth() {
        return minDepth;
    }

    public float getMaxDepth() {
        return maxDepth;
    }
}
