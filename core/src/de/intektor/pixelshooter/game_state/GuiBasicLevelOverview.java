package de.intektor.pixelshooter.game_state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.game_state.community_levels.GuiConnectToMainServer;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;

/**
 * @author Intektor
 */
public class GuiBasicLevelOverview extends Gui {

    final int BUTTON_WORLDS = 0, BUTTON_USER_LEVELS = 1, BUTTON_COMMUNITY_LEVELS = 2, BUTTON_CREATE_LEVEL = 3, BUTTON_BACK = 4;

    @Override
    public void onButtonTouched(int buttonID) {
        switch (buttonID) {
            case BUTTON_WORLDS:
                PixelShooter.enterGui(PixelShooter.WORLD_SELECTION);
                break;
            case BUTTON_USER_LEVELS:
                PixelShooter.enterGui(PixelShooter.USER_LEVEL_OVERVIEW);
                break;
            case BUTTON_CREATE_LEVEL:
                PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR_SET_PROPERTIES);
                break;
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.MAIN_MENU);
                break;
            case BUTTON_COMMUNITY_LEVELS:
                ((GuiConnectToMainServer)PixelShooter.getGuiByID(PixelShooter.CONNECT_TO_MAIN_SERVER)).setInfo(PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER, getID());
                PixelShooter.enterGui(PixelShooter.CONNECT_TO_MAIN_SERVER);
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.BASIC_LEVEL_OVERVIEW;
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, height / 5 * 4, width, height / 5, "Worlds", BUTTON_WORLDS, true));
        componentList.add(new GuiButton(0, height / 5 * 3, width, height / 5, "Your Levels", BUTTON_USER_LEVELS, true));
        componentList.add(new GuiButton(0, height / 5 * 2, width, height / 5, "Community Levels", BUTTON_COMMUNITY_LEVELS, true));
        componentList.add(new GuiButton(0, height / 5 * 1, width, height / 5, "Create a Level", BUTTON_CREATE_LEVEL, true));
        componentList.add(new GuiButton(0, height / 5 * 0, width, height / 5, "Back", BUTTON_BACK, true));
    }
}
