package old.network.client;

import api.common.GameCommon;
import api.common.GameServer;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.universe.StarSector;
import api.universe.StarUniverse;
import net.dovtech.immersiveplanets.data.AtmosphereEntryDamager;
import org.newdawn.slick.Game;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import java.io.IOException;

public class ClientAtmoKillSendPacket extends Packet {

    private Vector3i sector;

    public ClientAtmoKillSendPacket() {

    }

    public ClientAtmoKillSendPacket(Vector3i sector) {
        this.sector = sector;
    }


    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
            sector = packetReadBuffer.readVector();
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        if(GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer()) {
            packetWriteBuffer.writeVector(sector);
        }
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        if(!playerState.isGodMode()) {
            StarSector starSector = StarUniverse.getUniverse().getSector(sector);
            playerState.announceKill(new AtmosphereEntryDamager(GameServer.getServerState(), starSector));
        }
    }
}
