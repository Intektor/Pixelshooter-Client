package de.intektor.pixelshooter.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import de.intektor.pixelshooter.enums.EnumSide;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

import javax.vecmath.Point2f;

/**
 * A class just used for collision detection and for the Level Editor. Previously named Region
 *
 * @author Intektor
 */
public class Collision2D {

    private float x, y, x2, y2;

    public Collision2D(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        x2 = x + width;
        y2 = y + height;
        update();
    }

    public static boolean isPointInRegion(Point2f point, Collision2D collision2D) {
        return isRegionInRegion(new Collision2D(point.x, point.y, point.x, point.y), collision2D);
    }

    public static boolean isRegionInRegion(Collision2D reg1, Collision2D reg2) {
        return reg1.collidingWith(reg2) || reg2.collidingWith(reg1);
    }

    private void update() {
        if (x2 < x) {
            float xB = x;
            x = x2;
            x2 = xB;
        }
        if (y2 < y) {
            float yB = y;
            y = y2;
            y2 = yB;
        }
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    public Collision2D copy() {
        return new Collision2D(x, y, getWidth(), getHeight());
    }

    public Collision2D translate(float x, float y) {
        this.x += x;
        this.y += y;
        this.x2 += x;
        this.y2 += y;
        update();
        return this;
    }

    public Collision2D setPosition(float x, float y) {
        this.x2 -= this.x - x;
        this.y2 -= this.y - y;
        this.x = x;
        this.y = y;
        update();
        return this;
    }

    public boolean collidingWith(Collision2D c) {
        return Intersector.intersectRectangles(c.getRectangle(), getRectangle(), new Rectangle());
    }

    public void writeToTag(PSTagCompound tag) {
        tag.setFloat("x", x);
        tag.setFloat("y", y);
        tag.setFloat("x2", x2);
        tag.setFloat("y2", y2);
    }

    public static Collision2D readFromTag(PSTagCompound tag) {
        return Collision2D.createX2Y2(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("x2"), tag.getFloat("y2"));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getWidth() {
        return x2 - x;
    }

    public float getHeight() {
        return y2 - y;
    }

    public static Collision2D createX2Y2(float x, float y, float x2, float y2) {
        return new Collision2D(x, y, x2 - x, y2 - y);
    }

    public void stretchInDirection(EnumSide side, float amt) {
        switch (side) {
            case UP:
                if (y2 + amt - y > 0) {
                    y2 += amt;
                }
                break;
            case RIGHT:
                if (x2 + amt - x > 0) {
                    x2 += amt;
                }
                break;
            case DOWN:
                if (y2 - (y - amt) > 0) {
                    y -= amt;
                }
                break;
            case LEFT:
                if (x2 - (x - amt) > 0) {
                    x -= amt;
                }
                break;
            default:
                throw new IllegalArgumentException("Only UP, RIGHT, DOWN, LEFT is allowed");
        }
    }

    public Collision2D invertY() {
        float height = getHeight();
        return new Collision2D(x, y - 720, getWidth(), height);
    }
}
