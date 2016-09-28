package de.intektor.pixelshooter.game_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.StringUtils;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter_common.common.Version;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.net.packet.CampaignWorldsUpdateRequestPacketToServer;
import de.intektor.pixelshooter_common.packet.Packet;
import de.intektor.pixelshooter_common.packet.PacketHelper;

import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * @author Intektor
 */
public class GuiWaitForMainServerCampaignWorldResponse extends Gui {

    final String TICK_TIMER_TICK_CONNECTION = "PUBLISH_LEVEL_GUI_TICK_CONNECTION_GUI";
    final String TICK_TIMER_JOIN_FOLLOWING_GUI = "JOIN_FOLLOWING_GUI_TICK_WAIT_FOR_RESPONSE_GUI";

    Response response;

    Version newVersion;

    public GuiWaitForMainServerCampaignWorldResponse() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_TICK_CONNECTION);
        TickTimerHandler.registerTickTimer(120, TICK_TIMER_JOIN_FOLLOWING_GUI);
    }

    @Override
    public void enterGui() {
        super.enterGui();
        reset();
        FileHandle versionFile = Gdx.files.local("c_worlds/.version");
        Version version = new Version(1, 0, 0);
        if (versionFile.file() != null) {
            try {
                FileInputStream in = new FileInputStream(versionFile.file());
                PSTagCompound tag = new PSTagCompound();
                tag.readFromStream(new DataInputStream(in));
                version.readFromTag(tag);
                System.out.println(String.format("%s.%s.%s", version.major, version.minor, version.little));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Packet packet = new CampaignWorldsUpdateRequestPacketToServer(version);
        PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
    }

    @Override
    public void onButtonTouched(int id) {

    }

    @Override
    public int getID() {
        return PixelShooter.WAIT_FOR_CAMPAIGN_WORLDS_RESPONSE;
    }

    String sendPacketToMainServerString = "";
    int connectMainServerTick;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        String text = null;
        if (response == null) {
            text = sendPacketToMainServerString;
        } else switch (response) {
            case NEW_LEVELS:
                text = "New levels received! New Version: " + newVersion.major + "." + newVersion.minor + "." + newVersion.little;
                break;
            case UP_TO_DATE:
                text = "Levels are up to date";
                break;
        }
        RenderHelper.drawString(width / 2, height / 2, text, font, b, true);
        b.end();
    }

    @Override
    public void update() {
        super.update();
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_TICK_CONNECTION)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_TICK_CONNECTION);
            sendPacketToMainServerString = "Asking for campaign update" + StringUtils.repeat(".", connectMainServerTick);
            if (connectMainServerTick == 3) {
                connectMainServerTick = 0;
            } else {
                connectMainServerTick++;
            }
        }
        if (response != null && TickTimerHandler.hasTickTimerFinished(TICK_TIMER_JOIN_FOLLOWING_GUI)) {
            PixelShooter.enterGui(PixelShooter.WORLD_SELECTION);
        }
    }

    public enum Response {
        NEW_LEVELS,
        UP_TO_DATE,
    }

    public void setResponse(Response response, Version newVersion) {
        this.response = response;
        TickTimerHandler.resetTickTimer(TICK_TIMER_JOIN_FOLLOWING_GUI);
        this.newVersion = newVersion;
    }

    public void reset() {
        response = null;
    }

    @Override
    public void addGuiComponents() {

    }
}
