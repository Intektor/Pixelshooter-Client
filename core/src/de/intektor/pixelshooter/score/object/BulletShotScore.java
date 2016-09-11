package de.intektor.pixelshooter.score.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.render.RenderHelper;

import static de.intektor.pixelshooter.PixelShooter.spriteBatch;

/**
 * @author Intektor
 */
public class BulletShotScore implements IScoreObject {

    int score;

    public BulletShotScore(int score) {
        this.score = score;
    }

    @Override
    public void renderScoreObject(ShapeRenderer renderer, int x, int y, int width, int height) {
        renderer.identity();
        renderer.setColor(Color.WHITE);
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.line(x, y, x + width, y);
        renderer.line(x + height + 15, y, x + height + 15, y + height);

        spriteBatch.begin();
        spriteBatch.draw(ImageStorage.standard_ammo, x, y, height, height);
        RenderHelper.drawString(x + 50, y + height / 2 + 4, score + " points", PixelShooter.unScaledPerfectPixel16, spriteBatch, false);
        spriteBatch.end();
    }

    @Override
    public int getScore() {
        return score;
    }
}
