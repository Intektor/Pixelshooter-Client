package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.GuiServerError;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.ServerShutdownPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class ServerShutdownPacketToClientHandler implements PacketHandler<ServerShutdownPacketToClient> {

    @Override
    public void handlePacket(ServerShutdownPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                GuiServerError shutdownGui = (GuiServerError) PixelShooter.getGuiByID(PixelShooter.SERVER_CONNECTION_SHUTDOWN_MESSAGE);
                shutdownGui.setErrorMessage("Pixelshooter main server has been shutdown!");
                PixelShooter.enterGui(PixelShooter.SERVER_CONNECTION_SHUTDOWN_MESSAGE);
            }
        });
    }
}
