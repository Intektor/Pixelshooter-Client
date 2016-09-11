package de.intektor.pixelshooter.game_state.community_levels.browse_user_levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter_common.net.packet.RequestLevelDataPacketToServer;
import de.intektor.pixelshooter_common.packet.PacketHelper;

/**
 * @author Intektor
 */
public class LookupOfficialID extends Gui {

    final int BUTTON_BACK = 0, BUTTON_LOOKUP = 1;

    final int TEXT_FIELD_LOOKUP_OFFICIAL_ID = 0;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        batch.begin();
        RenderHelper.drawString(width / 2, height - 100, "Enter Code:", PixelShooter.unScaledPerfectPixel64, batch, true);
        batch.end();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER);
                break;
            case BUTTON_LOOKUP:
                RequestLevelDataPacketToServer request = new RequestLevelDataPacketToServer(getTextFieldByID(TEXT_FIELD_LOOKUP_OFFICIAL_ID).convertText(), BrowseCommunityLevelsFromMainServer.getUserData());
                PacketHelper.sendPacket(request, PixelShooter.mainServerClient.connection);
                PixelShooter.enterGui(PixelShooter.WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA);
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.LOOKUP_OFFICIAL_ID;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width, 100, "Back", BUTTON_BACK, true));
        componentList.add(new GuiTextField(0, height / 2, width, 100, TEXT_FIELD_LOOKUP_OFFICIAL_ID, true, 300, true, false, false, this, "", "Enter Code:"));
        componentList.add(new GuiButton(0, height / 2 - 100, width, 100, "Lookup", BUTTON_LOOKUP, true));
    }
}
