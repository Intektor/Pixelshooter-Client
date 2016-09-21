package de.intektor.pixelshooter.game_state;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;

/**
 * @author Intektor
 */
public class Options extends Gui {

    final int BUTTON_BACK = 0;
    final int TEXT_FIELD_SCALE_AMT = 1;

    @Override
    public void onButtonTouched(int id) {
        if (id == 0) {
            PixelShooter.enterGui(PixelShooter.MAIN_MENU);
        } else if (id == 1) {
        }
    }

    @Override
    public int getID() {
        return PixelShooter.OPTIONS;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, height / 5 * 4, width, height / 5, "Back", BUTTON_BACK, true));
    }

    @Override
    public void textFieldDeactivated(GuiTextField field) {
        switch (field.getId()) {
            case TEXT_FIELD_SCALE_AMT:
                break;
        }
    }

    @Override
    public void update() {
        super.update();

    }
}
