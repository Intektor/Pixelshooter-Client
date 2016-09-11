package de.intektor.pixelshooter.score.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.render.RenderHelper;

import static de.intektor.pixelshooter.PixelShooter.spriteBatch;

/**
 * @author Intektor
 */
public class HealthLostScore implements IScoreObject {

    float healthLost;

    public HealthLostScore(float healthLost) {
        this.healthLost = healthLost;
    }

    @Override
    public void renderScoreObject(ShapeRenderer renderer, int x, int y, int width, int height) {
        int scale = 10;

        renderer.identity();
        renderer.setColor(Color.WHITE);
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.line(x, y, x + width, y);
        renderer.line(x + height + 15, y, x + height + 15, y + height);

        int score = -MathHelper.getNextDivider((int) healthLost * 5000, 500);

        spriteBatch.begin();
        spriteBatch.draw(ImageStorage.dark_heart, x + 5, y + 5, height - scale, height - scale);
        RenderHelper.drawString(x + 20, y + 12, "x" + healthLost, PixelShooter.unScaledPerfectPixel12, spriteBatch, false);
        RenderHelper.drawString(x + 50, y + height / 2 + 4, score + " points", PixelShooter.unScaledPerfectPixel16, spriteBatch, false);
        spriteBatch.end();
    }

    @Override
    public int getScore() {
        return -MathHelper.getNextDivider((int) healthLost * 5000, 500);
    }
}
