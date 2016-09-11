package de.intektor.pixelshooter.gui.text_field;

import de.intektor.pixelshooter.gui.Gui;

/**
 * @author Intektor
 */
public class GuiNumberField extends GuiTextField {

    protected boolean decimal;

    public GuiNumberField(int x, int y, int width, int height, int id, boolean isShown, int maxChars, String info, Gui gui, String standard, boolean decimal) {
        super(x, y, width, height, id, isShown, maxChars, false, true, false, gui, standard, info);
        this.decimal = decimal;
    }

    @Override
    public void addCharacter(char c) {
        if (text.size() < maxChars) {
            boolean isDigit = (c >= '0' && c <= '9');
            if (isDigit || (decimal && c == '.' && !convertText().contains("."))) {
                text.add(c);
            }
        }
    }
}
