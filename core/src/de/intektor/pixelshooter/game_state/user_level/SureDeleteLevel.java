package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.level.editor.LevelEditor;
import de.intektor.pixelshooter.render.RenderHelper;

import java.text.SimpleDateFormat;

/**
 * @author Intektor
 */
public class SureDeleteLevel extends Gui {

    final int BUTTON_DONT_DELETE = 0, BUTTON_DELETE = 1;

    LevelFolder folder;
    int idToRemove;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        RenderHelper.drawString(width / 2, height - 100, "Are you sure you want to delete", font, b, true);
        LevelFolder.FolderFile file = folder.files.get(idToRemove);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        RenderHelper.drawString(width / 2, height - 100 - font.getLineHeight(), file.world.getName(), font, b, true);
        RenderHelper.drawString(width / 2, height - 100 - font.getLineHeight() * 2, "from " + format.format(file.world.timeSaved) + "?", font, b, true);
        b.end();

        LevelEditor.renderRawWorld(file.world, width / 2 - 125, height / 2 - 125, 250, 250);
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
                ((UserLevelsFolder) PixelShooter.getGuiByID(PixelShooter.USER_LEVELS_FOLDER)).reInitNextUpdate = true;
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.SURE_DELETE_FOLDER_FILE;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width - 140, 60 * scale, "DON'T DELETE!", BUTTON_DONT_DELETE, true));
        componentList.add(new GuiButton(width - 140, 0, 140, 60 * scale, "DELETE!", BUTTON_DELETE, true));
    }

    public void setFile(LevelFolder folder, int index) {
        this.folder = folder;
        this.idToRemove = index;
    }
}
