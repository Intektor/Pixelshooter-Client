package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.vecmath.Point2f;

/**
 * @author Intektor
 */
public class ThumbGameController {

    public final int originalX, originalY;
    int x, y, radius;
    int thumbX, thumbY;
    /**
     * Coordinates where to draw the thumb
     */
    float drawXT, drawYT;
    public float thumbCos, thumbSin;
    private boolean active;

    public ThumbGameController(int x, int y, int radius) {
        this.x = x - radius / 2;
        this.y = y - radius / 2;
        originalX = x;
        originalY = y;
        this.radius = radius;
        setThumb(x, y);
    }

    public void renderDPad(ShapeRenderer renderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(0x88888888));
        renderer.circle(x + radius / 2, y + radius / 2, radius);
        renderer.identity();
        renderer.setColor(Color.GRAY);
        renderer.circle(drawXT, drawYT, radius / 3);
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


    public float getStrength() {
        Point2f thumbPoint = new Point2f(thumbX, thumbY);
        double distance = thumbPoint.distance(getMid());
        if (distance < radius) {
            return (float) (distance / radius);
        } else {
            return 1;
        }
    }

    public Point2f getMid() {
        return new Point2f(x + radius / 2, y + radius / 2);
    }

    public int getRadius() {
        return radius;
    }

    public void setThumb(int x, int y) {
        this.thumbX = x;
        this.thumbY = y;
        Point2f thumbPoint = new Point2f(thumbX, thumbY);
        Point2f midPoint = getMid();
        int deltaX = (int) (thumbX - (midPoint.x));
        int deltaY = (int) (thumbY - (midPoint.y));
        float f = (float) Math.atan2(deltaY, deltaX);
        thumbSin = (float) Math.sin(f);
        thumbCos = (float) Math.cos(f);
        float distance = thumbPoint.distance(midPoint);
        if (distance >= radius) {
            drawXT = (int) (midPoint.x + radius * thumbCos);
            drawYT = (int) (midPoint.y + radius * thumbSin);
        } else {
            drawXT = (int) (midPoint.x + distance * thumbCos);
            drawYT = (int) (midPoint.y + distance * thumbSin);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.x = originalX;
            this.y = originalY;
            setThumb(x + radius / 2, y + radius / 2);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x - radius / 2;
        this.y = y - radius / 2;
        setThumb(x, y);
    }

    public boolean isActive() {
        return active;
    }
}
