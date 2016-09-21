package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.GuiWaitForLevelPublishServerResponse;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.LevelPublishedPacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class LevelPublishedPacketToClientHandler implements PacketHandler<LevelPublishedPacketToClient> {
    @Override
    public void handlePacket(LevelPublishedPacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                GuiWaitForLevelPublishServerResponse wait = (GuiWaitForLevelPublishServerResponse) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_PUBLISH_LEVEL_RESPONSE);
                wait.setResponse(GuiWaitForLevelPublishServerResponse.Response.LEVEL_PUBLISHED);
            }
        });
    }
}
