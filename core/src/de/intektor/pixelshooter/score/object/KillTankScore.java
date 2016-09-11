package de.intektor.pixelshooter.score.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.render.RenderHelper;

/**
 * @author Intektor
 */
public class KillTankScore implements IScoreObject {

    EntityEnemyTank tank;
    int score;

    public KillTankScore(EntityEnemyTank tank, int score) {
        this.tank = tank;
        this.score = score;
    }

    @Override
    public void renderScoreObject(ShapeRenderer renderer, int x, int y, int width, int height) {
        RenderHelper.renderTank2D(renderer, x, y, height, height, 90 / 180 * Math.PI, tank, 0);
        renderer.identity();
        renderer.setColor(Color.WHITE);
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.line(x, y, x + width, y);
        renderer.line(x + height + 15, y, x + height + 15, y + height);
        PixelShooter.spriteBatch.begin();
        RenderHelper.drawString(x + 50, y + height / 2 + 4, "+" + score + " points", PixelShooter.unScaledPerfectPixel16, PixelShooter.spriteBatch, false);
        PixelShooter.spriteBatch.end();
    }

    @Override
    public int getScore() {
        return score;
    }
}
