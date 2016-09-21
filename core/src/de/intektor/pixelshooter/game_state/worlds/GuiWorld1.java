package de.intektor.pixelshooter.game_state.worlds;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter.files.FileHelper;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.levels.PlayInformation;
import de.intektor.pixelshooter.levels.WorldPlayInformation;
import de.intektor.pixelshooter.world.EditingWorld;

/**
 * @author Intektor
 */
public class GuiWorld1 extends Gui {

    final int BUTTON_BACK = 0;

    int scrollAmount;

    public static final int WORLD_ID = 0;

    @Override
    public void onButtonTouched(int id) {
        if (id == BUTTON_BACK) {
            PixelShooter.enterGui(PixelShooter.WORLD_SELECTION);
        }
        loadLevel(id);
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
    }

    @Override
    public void update() {
        for (int i = 0; i < 40; i++) {
            GuiButton button = getButtonByID(i + 1);
            button.setY(scrollAmount + height - ((i + 1) * height / 6));
            button.setX(0);
            button.enabled = PixelShooter.campaign.levelPlayable(WORLD_ID, i);
        }
        super.update();
    }

    @Override
    public int getID() {
        return PixelShooter.WORLD_1;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(width - 300, height - 60, 300, 60, "Back", BUTTON_BACK, true));
        for (int i = 0; i < 40; i++) {
            GuiButton e = new GuiButton(0, 0, width / 2, height / 6, "Level " + (i + 1), i + 1, true);
            e.enabled = PixelShooter.campaign.levelPlayable(WORLD_ID, i);
            componentList.add(e);
        }
    }

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        scrollAmount -= input.getDeltaY(pointer);
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        if (scrollAmount > height / 6 * height / 6 - height) {
            scrollAmount = height / 6 * height / 6 - height;
        }
    }

    public static void loadLevel(int id) {
        EditingWorld world = FileHelper.getWorldFromFile("saves/worlds/world1/Level" + id + ".pssn");
        if (world != null) {
            if (world.checkConvert()) {
                PixelShooter.PIXEL_SHOOTER_STATE.setTheWorld(world);
                PixelShooter.PIXEL_SHOOTER_STATE.setPlayInformation(new WorldPlayInformation(WORLD_ID, id));
                PixelShooter.PIXEL_SHOOTER_STATE.setStart(true);
                PixelShooter.enterGui(PixelShooter.PLAY_STATE);
            }
        }
    }
}
