package de.intektor.pixelshooter.net.packet.handler;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.ViewCommunityLevel;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.WaitForServerToSendLevelInfo;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.RequestLevelDataPacketResponsePacketToClient;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.net.Socket;

/**
 * @author Intektor
 */
public class RequestLevelDataPacketResponsePacketToClientHandler implements PacketHandler<RequestLevelDataPacketResponsePacketToClient> {

    @Override
    public void handlePacket(final RequestLevelDataPacketResponsePacketToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                try {
                    EditingWorld world = EditingWorld.readFromTag(packet.worldTag);
                    ((ViewCommunityLevel) PixelShooter.getGuiByID(PixelShooter.BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL)).setInfo(world, packet.info, packet.writtenInfo, packet.prevRated, packet.ratedStars);
                    PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL);
                } catch (Exception e) {
                    e.printStackTrace();
                    ((WaitForServerToSendLevelInfo) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA)).setError();
                }
            }
        });
    }
}
