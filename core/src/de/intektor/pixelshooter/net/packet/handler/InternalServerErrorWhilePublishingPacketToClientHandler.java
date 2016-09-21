package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.GuiWaitForLevelPublishServerResponse;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.InternalServerErrorWhilePublishingPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class InternalServerErrorWhilePublishingPacketToClientHandler implements PacketHandler<InternalServerErrorWhilePublishingPacketToClient> {

    @Override
    public void handlePacket(InternalServerErrorWhilePublishingPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                GuiWaitForLevelPublishServerResponse wait = (GuiWaitForLevelPublishServerResponse) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_PUBLISH_LEVEL_RESPONSE);
                wait.setResponse(GuiWaitForLevelPublishServerResponse.Response.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
