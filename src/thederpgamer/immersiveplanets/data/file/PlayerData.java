package thederpgamer.immersiveplanets.data.file;

import api.common.GameClient;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.immersiveplanets.utils.DataUtils;

import java.io.IOException;

/**
 * PlayerData.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public class PlayerData extends DataFile {

    public PlayerData() {
        this(GameClient.getClientPlayerState());
    }

    public PlayerData(PlayerState playerState) {
        super(DataUtils.getPlayerDataFile(playerState.getName()));
        initialize(playerState);
    }

    private void initialize(PlayerState playerState) {
        setValue("name", playerState.getName());
        setValue("factionId", playerState.getFactionId());
        setValue("lastSector", playerState.getCurrentSector());
        try {
            saveValues();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
