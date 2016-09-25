package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.levels.BasicPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.WorldUtils;
import de.intektor.pixelshooter.world.WorldUtils.FrameBufferTextureRegion;

import java.text.SimpleDateFormat;

/**
 * @author Intektor
 */
public class GuiFinishLevelToPublishLevel extends Gui {

    final int BUTTON_DONT_PUBLISH = 0, BUTTON_PUBLISH = 1;

    LevelFolder folder;
    int idToPublish;

    FrameBufferTextureRegion texture;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        int i = 50;
        RenderHelper.drawString(width / 2, height - i, "Are you sure you want to publish", font, b, true);
        LevelFolder.FolderFile file = folder.files.get(idToPublish);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        RenderHelper.drawString(width / 2, height - i - font.getLineHeight(), file.world.getName(), font, b, true);
        RenderHelper.drawString(width / 2, height - i - font.getLineHeight() * 2, "from " + format.format(file.world.timeSaved) + "?", font, b, true);
        RenderHelper.drawString(width / 2, height - i - font.getLineHeight() * 3, "You have to finish the level first, before you can publish it!", font, b, true);
        b.end();

        batch.begin();
        batch.draw(texture.texture, width / 2 - 125, height / 2 - 125, 250, 250);
        batch.end();
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_DONT_PUBLISH:
                PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                break;
            case BUTTON_PUBLISH:
                if (folder.files.get(idToPublish).world.checkConvert()) {
                    PixelShooter.PIXEL_SHOOTER_STATE.setTheWorld(folder.files.get(idToPublish).world);
                    PixelShooter.PIXEL_SHOOTER_STATE.setPlayInformation(new BasicPlayInformation(PlayStateStatus.PUBLISH_LEVEL));
                    PixelShooter.PIXEL_SHOOTER_STATE.setStart(true);
                    PixelShooter.enterGui(PixelShooter.PLAY_STATE);
                }
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.FINISH_LEVEL_TO_PUBLISH_LEVEL;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width / 2, 60 * 2, "Don't publish!", BUTTON_DONT_PUBLISH, true));
        componentList.add(new GuiButton(width / 2, 0, width / 2, 60 * 2, "Finish Level!", BUTTON_PUBLISH, true));
    }

    public void setFile(LevelFolder folder, int index) {
        this.folder = folder;
        this.idToPublish = index;
        texture = WorldUtils.getLevelEditorTexture(folder.files.get(index).world);
    }

    @Override
    public void exitGui() {
        texture.buffer.dispose();
    }

    public LevelFolder getFolder() {
        return folder;
    }

    public int getIdToPublish() {
        return idToPublish;
    }
}
