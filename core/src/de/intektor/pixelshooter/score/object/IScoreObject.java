package de.intektor.pixelshooter.score.object;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * @author Intektor
 */
public interface IScoreObject {

    void renderScoreObject(ShapeRenderer renderer, int x, int y, int width, int height);

    int getScore();
}
