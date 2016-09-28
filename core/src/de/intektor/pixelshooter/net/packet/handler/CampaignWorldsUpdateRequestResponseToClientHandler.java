package de.intektor.pixelshooter.net.packet.handler;

import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.GuiWaitForMainServerCampaignWorldResponse;
import de.intektor.pixelshooter.game_state.GuiWaitForMainServerCampaignWorldResponse.Response;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.net.packet.CampaignWorldsUpdateRequestResponseToClient;
import de.intektor.pixelshooter_common.net.packet.CampaignWorldsUpdateRequestResponseToClient.CampaignWorld;
import de.intektor.pixelshooter_common.packet.PacketHandler;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * @author Intektor
 */
public class CampaignWorldsUpdateRequestResponseToClientHandler implements PacketHandler<CampaignWorldsUpdateRequestResponseToClient> {

    @Override
    public void handlePacket(final CampaignWorldsUpdateRequestResponseToClient packet, Socket socketFrom, Side from) {
        PixelShooter.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                GuiWaitForMainServerCampaignWorldResponse gui = (GuiWaitForMainServerCampaignWorldResponse) PixelShooter.getGuiByID(PixelShooter.WAIT_FOR_CAMPAIGN_WORLDS_RESPONSE);
                gui.setResponse(packet.update ? Response.NEW_LEVELS : Response.UP_TO_DATE, packet.newVersion);
                if (packet.update) {
                    Gdx.files.local("c_worlds").deleteDirectory();
                    Gdx.files.local("c_worlds").mkdirs();
                    Gdx.files.local("c_worlds/.version").delete();
                    PSTagCompound versionTag = new PSTagCompound();
                    packet.newVersion.writeToTag(versionTag);
                    try {
                        versionTag.writeToStream(new DataOutputStream(new FileOutputStream(Gdx.files.local("c_worlds/.version").file())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (CampaignWorld world : packet.worlds) {
                        Gdx.files.local("c_worlds/" + world.worldID).mkdirs();
                        List<PSTagCompound> levels = world.levels;
                        for (int i = 0; i < levels.size(); i++) {
                            PSTagCompound level = levels.get(i);
                            try {
                                DataOutputStream out = new DataOutputStream(new FileOutputStream(Gdx.files.local(String.format("c_worlds/%s/Level%s.pssn", world.worldID, i)).file()));
                                level.writeToStream(out);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
}
