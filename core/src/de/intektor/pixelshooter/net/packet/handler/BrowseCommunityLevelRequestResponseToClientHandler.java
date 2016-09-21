package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.GuiBrowseCommunityLevelsFromMainServer;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelRequestResponseToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class BrowseCommunityLevelRequestResponseToClientHandler implements PacketHandler<BrowseCommunityLevelRequestResponseToClient> {

    @Override
    public void handlePacket(final BrowseCommunityLevelRequestResponseToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ((GuiBrowseCommunityLevelsFromMainServer)PixelShooter.getGuiByID(PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER)).setLevels(packet.levels, packet.order, packet.supply);
            }
        });
    }
}
