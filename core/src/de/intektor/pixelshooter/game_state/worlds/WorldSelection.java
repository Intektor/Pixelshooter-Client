package de.intektor.pixelshooter.game_state.worlds;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;

/**
 * @author Intektor
 */
public class WorldSelection extends Gui {

    final int BUTTON_BACK = 0, BUTTON_WORLD_1 = 1;

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case BUTTON_WORLD_1:
                PixelShooter.enterGui(PixelShooter.WORLD_1);
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.WORLD_SELECTION;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(width - 300, height - 60, 300, 60, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton(0, height - 60, 300, 60, "World 1", BUTTON_WORLD_1, true));
    }
}
