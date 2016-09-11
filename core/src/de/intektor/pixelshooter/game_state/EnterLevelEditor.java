package de.intektor.pixelshooter.game_state;

import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.text_field.GuiNumberField;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;
import de.intektor.pixelshooter.world.EditingWorld;

import java.util.List;

/**
 * @author Intektor
 */
public class EnterLevelEditor extends Gui {

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case 0:
                List<GuiTextField> textFields = getTextFields();
                try {
                    String name = textFields.get(0).convertText();
                    int width = Integer.parseInt(textFields.get(1).convertText());
                    int height = Integer.parseInt(textFields.get(2).convertText());
                    if (width > 0 && height > 0 && !name.equals("")) {
                        PixelShooter.LEVEL_EDITOR_STATE.setEdit(new EditingWorld(name, width, height));
                        PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.LEVEL_EDITOR_SET_PROPERTIES;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiTextField(0, height / 5 * 4, width, height / 5, 0, true, 20, true, true, false, this, "My Level", "Level Name"));
        componentList.add(new GuiNumberField(0, height / 5 * 3, width, height / 5, 1, true, 6, "Level Width", this, "200", false));
        componentList.add(new GuiNumberField(0, height / 5 * 2, width, height / 5, 2, true, 6, "Level Height", this, "200", false));

        componentList.add(new GuiButton(0, height / 5, width, height / 5, "Ok", 0, true));
        componentList.add(new GuiButton(0, 0, width, height / 5, "Back", 1, true));
    }
}
