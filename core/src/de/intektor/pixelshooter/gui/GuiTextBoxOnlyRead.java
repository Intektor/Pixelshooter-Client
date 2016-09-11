package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.render.RenderHelper;

/**
 * @author Intektor
 */
public class GuiTextBoxOnlyRead extends GuiComponent {

    private String text;

    public GuiTextBoxOnlyRead(int x, int y, int width, int height, boolean isShown, String text) {
        super(x, y, width, height, isShown);
        this.text = text;
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
        BitmapFont font = PixelShooter.unScaledPerfectPixel22;
        RenderHelper.drawSplitString(x + 2, y + height - 3, width - 15, text, font, b, 32);
        b.end();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
