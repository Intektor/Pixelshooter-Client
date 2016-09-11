package de.intektor.pixelshooter.abstrct;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Intektor
 */
public class PositionHelper {

    public static List<Point2f> getAllPointsInRadius(final Point2f src, float radius, float jumpSize) {
        List<Point2f> list = new ArrayList<Point2f>();
        for (float x = src.getX() - radius; x < src.getX() + radius; x += jumpSize) {
            for (float y = src.getY() - radius; y < src.getY() + radius; y += jumpSize) {
                Point2f point = new Point2f(x, y);
                if (point.distance(src) <= radius) {
                    list.add(point);
                }
            }
        }
        Collections.sort(list, new Comparator<Point2f>() {
            @Override
            public int compare(Point2f o1, Point2f o2) {
                return Float.compare(src.distance(o1), src.distance(o2));
            }
        });
        return list;
    }

    public static List<Point3f> convert2to3(List<Point2f> point2fs, float y) {
        List<Point3f> list = new ArrayList<Point3f>(point2fs.size());
        for (Point2f point2f : point2fs) {
            list.add(new Point3f(point2f.x, y, point2f.y));
        }
        return list;
    }

    public static Point3f getMiddle(Point3f a, Point3f b) {
        return new Point3f((a.x + b.x) / 2, (a.y + b.y) / 2, (a.z + b.z) / 2);
    }
}
