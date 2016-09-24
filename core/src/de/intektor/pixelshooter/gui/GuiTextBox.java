package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.FontHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public class GuiTextBox extends GuiComponent {

    private int maxChars;
    boolean isActive;
    protected List<Character> text = new ArrayList<Character>();

    int cursorIndex;
    long lastCursorTickTime;
    boolean cursorTickActive;
    BitmapFont font;

    public GuiTextBox(int x, int y, int width, int height, boolean isShown, int maxChars, BitmapFont font) {
        super(x, y, width, height, isShown);
        this.maxChars = maxChars;
        this.font = font;
    }

    @Override
    protected void renderComponent(ShapeRenderer renderer) {
        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLUE);
        renderer.rect(x, y, width, height);
        renderer.end();

        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        String text = convertText();
        RenderHelper.drawSplitString(x + 2, y + height - 3, width - 15, text, font, b, 32);
        if (cursorTickActive && isActive) {
            RenderHelper.drawString(x + FontHelper.getStringIndexPosX(text, width - 15, font, cursorIndex) + 4, y + height - Math.max((FontHelper.splitString(text.substring(0, cursorIndex), width - 15, font).size() - 1), 0) * 32 - 12, "|", font, b);
        }
        b.end();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        super.updateComponent(mouseX, mouseY);
        if (System.currentTimeMillis() - lastCursorTickTime >= 500) {
            cursorTickActive = !cursorTickActive;
            lastCursorTickTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onClickedAt(int x, int y) {
        Gdx.input.setOnscreenKeyboardVisible(true);
        if (isActive) {
            String text = convertText();
            BitmapFont font = PixelShooter.unScaledPerfectPixel22;
            List<String> lines = FontHelper.splitString(text, width - 15, font);
            if (!lines.isEmpty()) {
                int line = 0;
                int localY = y - this.y;
                for (int i = 0; i < lines.size(); i++) {
                    if (localY > i * font.getLineHeight()) {
                        line = i;
                    } else {
                        break;
                    }
                }
                int localX = x - this.x;
                String cLine = lines.get(line);
                List<Character> charList = new ArrayList<Character>();
                for (char c : cLine.toCharArray()) {
                    charList.add(c);
                }
                charList.add(' ');
                int lineIndex = 0;
                for (int i = 0; i < charList.size(); i++) {
                    String t = cLine.substring(0, i);
                    if (localX > FontHelper.stringWidth(t, font)) {
                        lineIndex = i;
                    } else {
                        break;
                    }
                }

                cursorIndex = 0;

                for (int i = 0; i < line; i++) {
                    cursorIndex += lines.get(i).length();
                }
                cursorIndex += lineIndex;
            } else {
                cursorIndex = 0;
            }
        }
    }

    @Override
    public void onClicked(int x, int y) {
        isActive = isHoveredOver(x, y);
        cursorTickActive = true;
        lastCursorTickTime = System.currentTimeMillis() + 500;
    }

    @Override
    public void keyTyped(char character) {
        if (isActive) {
            addCharacter(character);
        }
    }

    @Override
    public void keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            Gdx.input.setOnscreenKeyboardVisible(false);
        }
    }

    public void addCharacter(char c) {
        if (text.size() < maxChars) {
            if (Character.isLetter(c) || c == '.' || c == ',' || c == '!' || c == '?' || c == ' ') {
                text.add(cursorIndex, c);
                cursorIndex++;
                lastCursorTickTime = System.currentTimeMillis();
                cursorTickActive = true;
            }
        }
        if (c == '\b') {
            removeChar();
        }
    }

    public void removeChar() {
        if (text.size() > 0) {
            text.remove(text.size() - 1);
            cursorIndex--;
            lastCursorTickTime = System.currentTimeMillis();
            cursorTickActive = true;
        }
    }

    public String convertText() {
        String s = "";
        for (Character c : text) {
            s += c;
        }
        return s;
    }
}
