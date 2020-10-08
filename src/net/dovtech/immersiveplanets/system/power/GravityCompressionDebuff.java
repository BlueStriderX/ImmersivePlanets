package net.dovtech.immersiveplanets.system.power;

import org.schema.game.common.controller.elements.power.reactor.PowerConsumer;
import org.schema.schine.graphicsengine.core.Timer;

public class GravityCompressionDebuff implements PowerConsumer {

    private double totalRegen;
    private float compressionPercentage;
    private boolean active;

    public GravityCompressionDebuff(double totalRegen) {
        this.totalRegen = totalRegen;
        this.compressionPercentage = 0.0f;
        this.active = false;
    }

    public float getCompressionPercentage() {
        return compressionPercentage;
    }

    public void setCompressionPercentage(float compressionPercentage) {
        this.compressionPercentage = compressionPercentage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return compressionPercentage * totalRegen;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return getPowerConsumedPerSecondResting();
    }

    @Override
    public boolean isPowerCharging(long l) {
        return false;
    }

    @Override
    public void setPowered(float v) {

    }

    @Override
    public float getPowered() {
        return 0;
    }

    @Override
    public PowerConsumerCategory getPowerConsumerCategory() {
        return PowerConsumerCategory.OTHERS;
    }

    @Override
    public void reloadFromReactor(double v, Timer timer, float v1, boolean b, float v2) {

    }

    @Override
    public boolean isPowerConsumerActive() {
        return active;
    }

    @Override
    public String getName() {
        return "Gravity Compression";
    }

    @Override
    public void dischargeFully() {

    }
}
