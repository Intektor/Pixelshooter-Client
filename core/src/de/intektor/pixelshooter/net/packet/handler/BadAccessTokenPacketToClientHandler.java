package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.WaitForLevelPublishServerResponse;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.BadAccessTokenPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class BadAccessTokenPacketToClientHandler implements PacketHandler<BadAccessTokenPacketToClient> {

    @Override
    public void handlePacket(BadAccessTokenPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                WaitForLevelPublishServerResponse wait = (WaitForLevelPublishServerResponse) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_PUBLISH_LEVEL_RESPONSE);
                wait.setResponse(WaitForLevelPublishServerResponse.Response.BAD_LOGIN);
            }
        });
    }
}
