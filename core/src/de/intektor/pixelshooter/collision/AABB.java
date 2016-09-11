package de.intektor.pixelshooter.collision;

import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

/**
 * @author Intektor
 */
public class AABB {
    protected Point2f pos;
    protected Vector2f size;

    public AABB(Point2f pos, Vector2f size) {
        this.pos = pos;
        this.size = size;
    }

    public static boolean collides(AABB a, AABB b) {
        if (Math.abs(a.pos.x - b.pos.x) < a.size.x + b.size.x) {
            if (Math.abs(a.pos.y - b.pos.y) < a.size.y + b.size.y) {
                return true;
            }
        }

        return false;
    }

    public static boolean inside(AABB a, Vector2f b) {
        if (Math.abs(a.pos.x - b.x) < a.size.x) {
            if (Math.abs(a.pos.y - b.y) < a.size.y) {
                return true;
            }
        }
        return false;
    }
}
