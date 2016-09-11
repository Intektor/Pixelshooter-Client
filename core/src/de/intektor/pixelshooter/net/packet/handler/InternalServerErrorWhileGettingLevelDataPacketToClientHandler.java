package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.WaitForServerToSendLevelInfo;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.InternalServerErrorWhileGettingLevelDataPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class InternalServerErrorWhileGettingLevelDataPacketToClientHandler implements PacketHandler<InternalServerErrorWhileGettingLevelDataPacketToClient> {

    @Override
    public void handlePacket(InternalServerErrorWhileGettingLevelDataPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ((WaitForServerToSendLevelInfo) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA)).setError();
            }
        });
    }
}
