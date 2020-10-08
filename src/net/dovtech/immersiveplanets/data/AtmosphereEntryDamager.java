package net.dovtech.immersiveplanets.data;

import api.universe.StarSector;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.controller.damage.effects.MetaWeaponEffectInterface;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.network.StateInterface;

public class AtmosphereEntryDamager implements Damager {


    private InterEffectSet damageSet = new InterEffectSet();
    private StateInterface state;
    private StarSector sector;

    public AtmosphereEntryDamager(StateInterface state, StarSector sector) {
        this.damageSet.setStrength(InterEffectHandler.InterEffectType.HEAT, 3.0f);
        this.state = state;
        this.sector = sector;
    }

    @Override
    public void sendServerMessage(Object[] astr, int msgType) {
    }

    @Override
    public StateInterface getState() {
        return state;
    }

    @Override
    public void sendHitConfirm(byte damageType) {
    }

    @Override
    public boolean isSegmentController() {
        return false;
    }

    @Override
    public SimpleTransformableSendableObject<?> getShootingEntity() {
        return null;
    }

    @Override
    public int getFactionId() {
        return 0;
    }

    @Override
    public String getName() {
        return "Atmospheric Heat Damage";
    }

    @Override
    public AbstractOwnerState getOwnerState() {
        return null;
    }

    @Override
    public void sendClientMessage(String str, int type) {

    }

    @Override
    public float getDamageGivenMultiplier() {
        return 3;
    }

    @Override
    public InterEffectSet getAttackEffectSet(long weaponId, DamageDealerType damageDealerType) {
        return damageSet;
    }

    @Override
    public MetaWeaponEffectInterface getMetaWeaponEffect(long weaponId, DamageDealerType damageDealerType) {
        return null;
    }

    @Override
    public int getSectorId() {
        return sector.getSectorId();
    }
}
