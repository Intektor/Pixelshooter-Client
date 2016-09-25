package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.WorldUtils;
import de.intektor.pixelshooter.world.WorldUtils.FrameBufferTextureRegion;

import java.text.SimpleDateFormat;

/**
 * @author Intektor
 */
public class GuiSureDeleteLevel extends Gui {

    final int BUTTON_DONT_DELETE = 0, BUTTON_DELETE = 1;

    LevelFolder folder;
    int idToRemove;

    FrameBufferTextureRegion texture;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
        batch.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        RenderHelper.drawString(width / 2, height - 100, "Are you sure you want to delete", font, batch, true);
        LevelFolder.FolderFile file = folder.files.get(idToRemove);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        RenderHelper.drawString(width / 2, height - 100 - font.getLineHeight(), file.world.getName(), font, batch, true);
        RenderHelper.drawString(width / 2, height - 100 - font.getLineHeight() * 2, "from " + format.format(file.world.timeSaved) + "?", font, batch, true);
        batch.end();

        batch.begin();
        batch.draw(texture.texture, width / 2 - 125, height / 2 - 125, 250, 250);
        batch.end();
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_DONT_DELETE:
                PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                break;
            case BUTTON_DELETE:
                folder.files.get(idToRemove).file.delete();
                folder.files.remove(idToRemove);
                PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                ((GuiUserLevelsFolder) PixelShooter.getGuiByID(PixelShooter.USER_LEVELS_FOLDER)).reInitNextUpdate = true;
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.SURE_DELETE_FOLDER_FILE;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width - 140, 60 * 2, "DON'T DELETE!", BUTTON_DONT_DELETE, true));
        componentList.add(new GuiButton(width - 140, 0, 140, 60 * 2, "DELETE!", BUTTON_DELETE, true));
    }

    public void setFile(LevelFolder folder, int index) {
        this.folder = folder;
        this.idToRemove = index;
        texture = WorldUtils.getLevelEditorTexture(folder.files.get(index).world);
    }

    @Override
    public void exitGui() {
        texture.buffer.dispose();
    }
}
