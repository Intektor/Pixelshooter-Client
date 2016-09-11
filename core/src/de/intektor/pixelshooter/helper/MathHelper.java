package de.intektor.pixelshooter.helper;

import de.intektor.pixelshooter.collision.Collision2D;

import javax.vecmath.Point2f;

/**
 * @author Intektor
 */
public class MathHelper {

    public static boolean isInsideNumberRegion(float toTest, float aimingPoint, float tolerance) {
        return (aimingPoint + tolerance) > toTest && (aimingPoint - tolerance) < toTest;
    }

    public static float getClosestToNumber(float test1, float test2, float aimingPoint) {
        float d1 = Math.abs(test1 - aimingPoint);
        float d2 = Math.abs(test2 - aimingPoint);
        return d1 < d2 ? test1 : test2;
    }

    public static float toDegree360(float degree) {
        return degree >= 0 ? degree % 360 : (360 + degree);
    }

    /**
     * Returns the next divider of the given number, if the divider is 4 and the number is 15, 16 is returned, if the divider is 4 and the number is 13, 12 is returned.
     *
     * @param dividing the number
     * @param divider  the number to aim for
     */
    public static int getNextDivider(int dividing, int divider) {
        int a = dividing % divider;
        if (a < divider / 2) {
            return dividing - a;
        } else {
            return divider + (dividing - a);
        }
    }

    /**
     * Returns the next divider of the given number, but always down, so if the divider
     */
    public static int getNextDividerDown(int dividing, int divider) {
        int a = dividing % divider;
        if (a < divider) {
            return dividing - a;
        } else {
            return divider + (dividing - a);
        }
    }

    public static boolean isPointInsideCircle(int pX, int pY, int cX, int cY, int cRadius) {
        return new Point2f(cX, cY).distance(new Point2f(pX, pY)) <= cRadius;
    }

}
