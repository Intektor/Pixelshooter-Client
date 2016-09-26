package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.text_field.GuiNumberField;
import de.intektor.pixelshooter.score.MedalInfo;
import de.intektor.pixelshooter.world.EditingWorld;

/**
 * @author Intektor
 */
public class GuiSetWorldAttributes extends Gui {

    final int BUTTON_ACCEPT = 0, BUTTON_SET_WORLD_TYPE = 1, BUTTON_AUTO_CALC_MEDAL_POINTS = 2;
    final int TEXT_FIELD_BRONZE_MEDAL = 0, TEXT_FIELD_SILVER_MEDAL = 1, TEXT_FIELD_GOLD_MEDAL = 2;

    @Override
    public void enterGui() {
        super.enterGui();
        MedalInfo medalInfo = GuiLevelEditor.edit.medalInfo;
        if (medalInfo != null) {
            getTextFieldByID(TEXT_FIELD_BRONZE_MEDAL).setText(medalInfo.minBronze + "");
            getTextFieldByID(TEXT_FIELD_SILVER_MEDAL).setText(medalInfo.minSilver + "");
            getTextFieldByID(TEXT_FIELD_GOLD_MEDAL).setText(medalInfo.minGold + "");
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        GuiButton button = getButtonByID(BUTTON_SET_WORLD_TYPE);
        batch.begin();
        batch.draw(GuiLevelEditor.edit.background.getTinyTexture(), button.getX() + button.getWidth() + 5, button.getY(), button.getHeight(), button.getHeight());
        batch.end();
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_ACCEPT:
                try {
                    int b = Integer.parseInt(getTextFieldByID(TEXT_FIELD_BRONZE_MEDAL).convertText());
                    int s = Integer.parseInt(getTextFieldByID(TEXT_FIELD_SILVER_MEDAL).convertText());
                    int g = Integer.parseInt(getTextFieldByID(TEXT_FIELD_GOLD_MEDAL).convertText());
                    GuiLevelEditor.edit.medalInfo = new MedalInfo(b, s, g);
                } catch (Exception ignored) {

                }
                PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
                break;
            case BUTTON_SET_WORLD_TYPE:
                GuiButton button = getButtonByID(BUTTON_SET_WORLD_TYPE);
                int index = GuiLevelEditor.edit.background.ordinal();
                if (index + 1 != EditingWorld.BackGroundType.values().length) {
                    index++;
                } else {
                    index = 0;
                }
                GuiLevelEditor.edit.background = EditingWorld.BackGroundType.values()[index];
                button.description = "World Type: " + GuiLevelEditor.edit.background.name();
                break;
            case BUTTON_AUTO_CALC_MEDAL_POINTS:
                MedalInfo medalInfo = GuiLevelEditor.edit.calcMedalInfo();
                if (medalInfo != null) {
                    getTextFieldByID(TEXT_FIELD_BRONZE_MEDAL).setText(medalInfo.minBronze + "");
                    getTextFieldByID(TEXT_FIELD_SILVER_MEDAL).setText(medalInfo.minSilver + "");
                    getTextFieldByID(TEXT_FIELD_GOLD_MEDAL).setText(medalInfo.minGold + "");
                }
                break;
        }
    }

    @Override
    public int getID() {
        return PixelShooter.LE_SET_WORLD_ATTRIBUTES;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(width / 2 - 100, 0, 200, 50, "Accept", BUTTON_ACCEPT, true));
        componentList.add(new GuiButton(width / 2 - 200, height - 50, 400, 50, "World Type: " + GuiLevelEditor.edit.background.name(), BUTTON_SET_WORLD_TYPE, true));
        int w = (int) (width / 3.5f);
        componentList.add(new GuiNumberField(0, height - 100, w, 50, TEXT_FIELD_BRONZE_MEDAL, true, 10, "Min Points for Bronze Medal", this, "", false));
        componentList.add(new GuiNumberField(w, height - 100, w, 50, TEXT_FIELD_SILVER_MEDAL, true, 10, "Min Points for Silver Medal", this, "", false));
        componentList.add(new GuiNumberField(w * 2, height - 100, w, 50, TEXT_FIELD_GOLD_MEDAL, true, 10, "Min Points for Gold Medal", this, "", false));

        componentList.add(new GuiButton(width - w / 2, height - 100, w / 2, 50, "Auto calc", BUTTON_AUTO_CALC_MEDAL_POINTS, true));
    }
}
