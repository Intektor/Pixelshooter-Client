package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.ServerErrorGui;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.UnsupportedClientVersionPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class UnsupportedClientVersionPacketToClientHandler implements PacketHandler<UnsupportedClientVersionPacketToClient> {

    @Override
    public void handlePacket(UnsupportedClientVersionPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ServerErrorGui shutdownGui = (ServerErrorGui) PixelShooter.getGuiByID(PixelShooter.SERVER_CONNECTION_SHUTDOWN_MESSAGE);
                shutdownGui.setErrorMessage("Unsupported client version, please update the game!");
                PixelShooter.enterGui(PixelShooter.SERVER_CONNECTION_SHUTDOWN_MESSAGE);
            }
        });
    }
}
