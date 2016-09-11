package de.intektor.pixelshooter.gui.text_field;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiComponent;
import de.intektor.pixelshooter.render.RenderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public class GuiTextField extends GuiComponent {

    protected int maxChars, id;
    protected boolean allowLetters, allowNumbers, allowDots;

    protected List<Character> text;
    protected String info = "";

    private boolean isActive;

    private Gui gui;

    public GuiTextField(int x, int y, int width, int height, int id, boolean isShown, int maxChars, boolean allowLetters, boolean allowNumbers, boolean allowDots, Gui gui, String standard, String info) {
        super(x, y, width, height, isShown);
        this.id = id;
        this.maxChars = maxChars;
        this.allowLetters = allowLetters;
        this.allowNumbers = allowNumbers;
        this.allowDots = allowDots;
        this.info = info;
        this.gui = gui;
        text = new ArrayList<Character>();
        for (char c : standard.toCharArray()) {
            text.add(c);
        }
    }

    @Override
    public void renderComponent(ShapeRenderer renderer) {
        if (isShown()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderer.begin();
            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.setColor(isHovered ? Color.RED : Color.ORANGE);
            renderer.rect(x, y, width, height);
            renderer.identity();
            renderer.set(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(0x88888888));
            renderer.rect(x + 1, y + 1, width - 2, height - 2);
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            PixelShooter.spriteBatch.begin();
            if (!info.equals("")) {
                RenderHelper.drawString(x + width / 2, y + height / 4 * 3, info, PixelShooter.unScaledPerfectPixel22, PixelShooter.spriteBatch);
                RenderHelper.drawString(x + width / 2, y + height / 4, convertText(), PixelShooter.unScaledPerfectPixel22, PixelShooter.spriteBatch);
            } else {
                RenderHelper.drawString(x + width / 2, y + height / 2, convertText(), PixelShooter.unScaledPerfectPixel22, PixelShooter.spriteBatch, true);
            }
            PixelShooter.spriteBatch.end();
        }
    }

    public void addCharacter(char c) {
        if (text.size() < maxChars) {
            boolean isDigit = (c >= '0' && c <= '9');
            if ((isDigit && allowNumbers) || ((Character.isLetter(c) || c == ' ' || c == '@' || (allowDots && c == '.')) && allowLetters)) {
                text.add(c);
            }
        }
        if (c == '\b') {
            removeChar();
        }
    }

    @Override
    public void onClicked(int x, int y) {
        super.onClicked(x, y);
        if (!isHoveredOver(x, y)) {
            isActive = false;
        }
    }

    public void removeChar() {
        if (text.size() > 0) {
            text.remove(text.size() - 1);
        }
    }

    public String convertText() {
        String s = "";
        for (Character c : text) {
            s += c;
        }
        return s;
    }

    @Override
    public void onClickedAt(int x, int y) {
        Gdx.input.setOnscreenKeyboardVisible(true);
        for (GuiComponent component : gui.componentList) {
            if (component instanceof GuiTextField && component != this) {
                ((GuiTextField) component).isActive = false;
            }
        }
        isActive = true;
    }

    public void setText(String text) {
        this.text.clear();
        for (char c : text.toCharArray()) {
            this.text.add(c);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void keyTyped(char character) {
        if (isActive) {
            addCharacter(character);
        }
    }

    @Override
    public GuiComponent setShown(boolean shown) {
        if (!shown) {
            setActive(false);
        }
        return super.setShown(shown);
    }

    @Override
    public void keyDown(int keycode) {
        if (isActive) {
            if (keycode == Input.Keys.ENTER) {
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        }
    }

    public void setActive(boolean active) {
        isActive = active;
        if (!active) {
            gui.textFieldDeactivated(this);
        }
    }

    public int getId() {
        return id;
    }
}
