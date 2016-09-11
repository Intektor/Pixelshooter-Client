package de.intektor.pixelshooter.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import de.intektor.pixelshooter.abstrct.AbstractHelper;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

/**
 * @author Intektor
 */
public class Collision3D {

    public float x, y, z, width, height, depth;

    public Collision3D(float x, float y, float z, float width, float height, float depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;

    }

    public boolean collidingWith(Collision3D c) {
        return colliding(c);
    }

    private boolean colliding(Collision3D c) {
        return getBoundingBox().intersects(c.getBoundingBox());
    }

    public WorldBorder collidingWith(Collisions c) {
        return c.collidingWith(this);
    }

    public Collision3D setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Collision3D translate(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Collision3D grow(float amt) {
        x -= amt;
        y -= amt;
        z -= amt;
        width += amt;
        height += amt;
        depth += amt;
        return this;
    }

    public Collision3D stretch(float x, float y, float z) {
        this.width += x;
        this.height += y;
        this.depth += z;
        return this;
    }

    public boolean contains(float x, float y, float z) {
        return getBoundingBox().contains(new Vector3(x, y, z));
    }

    public Point3f getMid() {
        return new Point3f(x + width / 2, y + height / 2, z + depth / 2);
    }

    public Collision3D copy() {
        return new Collision3D(x, y, z, width, height, depth);
    }

    @Override
    public String toString() {
        Rectangle bounding = getRectangle();
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "( " + AbstractHelper.format(bounding.getX(), bounding.getY(), bounding.getWidth(), bounding.getHeight() + " )");
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public static Collision3D createX2Y2Z2(float x, float y, float z, float x2, float y2, float z2) {
        return new Collision3D(x, y, z, x2 - x, y2 - y, z2 - z);
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(new Vector3(x, y, z), new Vector3(x + width, y + height, z + depth));
    }

    public float getDistance(float x, float z) {
        BoundingBox box = getBoundingBox();
        Point2f[] oulines = new Point2f[]{
                new Point2f(box.min.x, box.min.z), new Point2f(box.max.x, box.min.z),
                new Point2f(box.max.x, box.min.z), new Point2f(box.max.x, box.max.z),
                new Point2f(box.min.x, box.max.z), new Point2f(box.max.x, box.max.z),
                new Point2f(box.min.x, box.min.z), new Point2f(box.min.x, box.max.z),
        };
        float distance = Float.MAX_VALUE;
        for (int i = 0; i < oulines.length; i += 2) {
            Point2f p1 = oulines[i];
            Point2f p2 = oulines[i + 1];
            float v = Intersector.distanceSegmentPoint(p1.x, p1.y, p2.x, p2.y, x, z);
            distance = Math.min(distance, v);
        }
        return distance;
    }

    public Point2f conv(Vector3 vec) {
        return new Point2f(vec.x, vec.z);
    }


}
