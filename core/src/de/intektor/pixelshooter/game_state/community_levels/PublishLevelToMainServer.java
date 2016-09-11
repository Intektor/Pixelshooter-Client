package de.intektor.pixelshooter.game_state.community_levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.GuiTextBox;
import de.intektor.pixelshooter.level.editor.LevelEditor;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.net.packet.PublishLevelPacketToServer;
import de.intektor.pixelshooter_common.packet.PacketHelper;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Intektor
 */
public class PublishLevelToMainServer extends Gui {

    final int BUTTON_DONT_PUBLISH = 0, BUTTON_PUBLISH = 1, BUTTON_PRIVACY = 2, BUTTON_SHOW_NAME = 3;

    LevelFolder folder;
    int idToPublish;
    LevelFolder.FolderFile file;

    GuiTextBox infoBox;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
//        LevelEditor.renderRawWorld(file.world, camera, ownRenderer, this.batch, width - 250, height / 2 - 125, 250, 250);
        LevelEditor.renderRawWorld(file.world,  width - 250, height / 2 - 125, 250, 250);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        RenderHelper.drawString(0, height - 100, "Privacy:", PixelShooter.unScaledPerfectPixel22, b, false);
        b.end();
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_DONT_PUBLISH:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case BUTTON_PUBLISH:
                PixelShooter.enterGui(PixelShooter.WAIT_FOR_PUBLISH_LEVEL_RESPONSE);
                try {
                    PSTagCompound levelTag = new PSTagCompound();
                    levelTag.readFromStream(new DataInputStream(new FileInputStream(file.file)));
                    PSTagCompound infoTag = new PSTagCompound();
                    infoTag.setString("accessToken", PixelShooter.googleAccount.accessToken);
                    infoTag.setString("idToken", PixelShooter.googleAccount.idToken);
                    infoTag.setString("level_name", file.world.getName());
                    boolean isPrivate = ((GuiButton.GuiButtonSwitch) getButtonByID(BUTTON_PRIVACY)).getCurrentState() == 1;
                    boolean emailHidden = ((GuiButton.GuiButtonSwitch) getButtonByID(BUTTON_SHOW_NAME)).getCurrentState() == 1;
                    infoTag.setBoolean("private", isPrivate);
                    infoTag.setBoolean("show_email", emailHidden);
                    infoTag.setString("written_info", infoBox.convertText());
                    PublishLevelPacketToServer packet = new PublishLevelPacketToServer(levelTag, infoTag);
                    PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.PUBLISH_LEVEL_TO_MAIN_SERVER;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width / 2, 60, "Don't publish!", BUTTON_DONT_PUBLISH, true));
        componentList.add(new GuiButton(width / 2, 0, width / 2, 60, "Publish level!", BUTTON_PUBLISH, true));
        componentList.add(new GuiButton.GuiButtonSwitch(0, height - 180, 300, 50, BUTTON_PRIVACY, true, Arrays.asList("Public", "Private"), 0));
        componentList.add(new GuiButton.GuiButtonSwitch(0, height - 230, 300, 50, BUTTON_SHOW_NAME, true, Arrays.asList("Show your email", "Hide your email"), 0));
        infoBox = new GuiTextBox(width / 2 - 250, height / 2 - 250, 500, 500, true, 500, PixelShooter.unScaledPerfectPixel22);
        componentList.add(infoBox);
    }

    public void setFile(LevelFolder folder, int index) {
        this.folder = folder;
        this.idToPublish = index;
        file = folder.files.get(index);
    }
}
