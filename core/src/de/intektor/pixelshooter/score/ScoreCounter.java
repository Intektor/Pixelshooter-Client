package de.intektor.pixelshooter.score;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.enums.Medals;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.score.object.IScoreObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public class ScoreCounter {

    private List<IScoreObject> scoreInformation;
    int lastPublishedScoreObject = -1;
    private List<IScoreObject> publishedObjects = new ArrayList<IScoreObject>();
    private boolean success, counterFinished;

    public ScoreCounter(List<IScoreObject> scoreInformation, boolean success) {
        this.scoreInformation = scoreInformation;
        this.success = success;
    }

    int currentScoreCounter;
    int nextMaxScore;
    int scrollAmount;


    public void renderScoreCounter(ShapeRenderer renderer, int offsetX, int offsetY) {
        renderer.begin();
        renderer.identity();
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);
        int scoreBoardWidth = MathHelper.getNextDivider(1280 / 2, 31);
        int scoreBoardHeight = MathHelper.getNextDivider(720 / 3, 31);
        int x = 1280 / 2 - scoreBoardWidth / 2 + offsetX;
        int y = 720 / 3 * 2 - scoreBoardHeight / 2 + offsetY;
        int x2 = 1280 / 2 + scoreBoardWidth / 2 + offsetX;
        int y2 = 720 / 3 * 2 + scoreBoardHeight / 2 + offsetY;
        int midX = x + (x2 - x) / 2;
        renderer.rect(x, y2, x2 - x, y - y2);
        renderer.identity();
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.line(x + (x2 - x) / 2, y, x + (x2 - x) / 2, y2);

        renderer.end();

        PixelShooter.spriteBatch.begin();
        RenderHelper.drawString(midX + (x2 - midX) / 2, y + ((y2 - y) / 4 * 3), "Points:", PixelShooter.unScaledPerfectPixel32, PixelShooter.spriteBatch);
        RenderHelper.drawString(midX + (x2 - midX) / 2, y + ((y2 - y) / 2), currentScoreCounter + "", PixelShooter.unScaledPerfectPixel64, PixelShooter.spriteBatch);

        MedalInfo medalInfo = PixelShooter.PIXEL_SHOOTER_STATE.getWorldBackup().medalInfo;

        RenderHelper.drawString(midX + (x2 - midX) / 2, y + ((y2 - y) / 4), "Gold: " + medalInfo.minGold, PixelShooter.unScaledPerfectPixel12, PixelShooter.spriteBatch);
        RenderHelper.drawString(midX + (x2 - midX) / 2, y + ((y2 - y) / 4) - 12, "Silver: " + medalInfo.minSilver, PixelShooter.unScaledPerfectPixel12, PixelShooter.spriteBatch);
        RenderHelper.drawString(midX + (x2 - midX) / 2, y + ((y2 - y) / 4) - 24, "Bronze: " + medalInfo.minBronze, PixelShooter.unScaledPerfectPixel12, PixelShooter.spriteBatch);
        PixelShooter.spriteBatch.end();

        if (counterFinished && success) {
            SpriteBatch batch = PixelShooter.spriteBatch;
            Texture texture = currentScoreCounter >= medalInfo.minGold ? ImageStorage.gold_medal : currentScoreCounter >= medalInfo.minSilver ? ImageStorage.silver_medal : currentScoreCounter >= medalInfo.minBronze ? ImageStorage.bronze_medal : null;
            if (texture != null) {
                batch.begin();
                batch.draw(texture, midX + (x2 - midX) / 2, y + ((y2 - y) / 2) - 120, 400 / 2.5f, 300 / 2.5f);
                batch.end();
            }
        }
        int i = 0;
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        renderer.begin();
        for (IScoreObject publishedObject : publishedObjects) {
            publishedObject.renderScoreObject(renderer, x, y2 - 31 * (i + 1) + scrollAmount, scoreBoardWidth / 2, 31);
            i++;
        }
        renderer.end();
        float scaleX = Gdx.graphics.getWidth() / (float) 1280;
        float scaleY = Gdx.graphics.getHeight() / (float) 720;

        Gdx.gl.glScissor((int) (x * scaleX), (int) (y * scaleY), (int) (scoreBoardWidth * scaleX / 2), (int) (scoreBoardHeight * scaleY));
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    public void update() {
        if (currentScoreCounter == nextMaxScore) {
            int index = ++lastPublishedScoreObject;
            if (scoreInformation.size() > index) {
                IScoreObject next = scoreInformation.get(index);
                publishedObjects.add(next);
                scrollAmount += 31;
                nextMaxScore = nextMaxScore + next.getScore();
                checkScroll();
            } else {
                counterFinished = true;
            }
        }
        if (currentScoreCounter > nextMaxScore) {
            currentScoreCounter -= 100;
        }
        if (currentScoreCounter < nextMaxScore) {
            currentScoreCounter += 100;
        }
    }

    public void checkScroll() {
        if (currentScoreCounter < nextMaxScore) {
            currentScoreCounter += 100;
        }
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        int scoreBoardHeight = MathHelper.getNextDivider(720 / 3, 31);
        if (publishedObjects.size() > scoreBoardHeight / 31) {
            if (scrollAmount > publishedObjects.size() * 31 - scoreBoardHeight) {
                scrollAmount = publishedObjects.size() * 31 - scoreBoardHeight;
            }
        } else {
            scrollAmount = 0;
        }
    }

    public void mouseDragged(int mouseX, int mouseY, int width, int height) {
        int scoreBoardWidth = MathHelper.getNextDivider(width / 2, 31);
        int scoreBoardHeight = MathHelper.getNextDivider(height / 3, 31);
        int x = width / 2 - scoreBoardWidth / 2;
        int x2 = width / 2 + scoreBoardWidth / 2;
        int midX = x + (x2 - x) / 2;
        if (mouseX > x && mouseX < midX) {
            scrollAmount -= Gdx.input.getDeltaY();
        }
        checkScroll();
    }

    public int calcFinalScore() {
        int score = 0;
        for (IScoreObject publishedObject : publishedObjects) {
            score += publishedObject.getScore();
        }
        return score;
    }

    public Medals getMedal() {
        MedalInfo medalInfo = PixelShooter.PIXEL_SHOOTER_STATE.getWorldBackup().medalInfo;
        int score = calcFinalScore();
        return score >= medalInfo.minGold ? Medals.GOLD : score >= medalInfo.minSilver ? Medals.SILVER : score >= medalInfo.minBronze ? Medals.BRONZE : Medals.NONE;
    }
}
