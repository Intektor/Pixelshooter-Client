package de.intektor.pixelshooter.collision;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Intektor
 */
public class Collisions implements Iterable<WorldBorder> {

    private List<WorldBorder> borders = new ArrayList<WorldBorder>();

    public Collisions(List<WorldBorder> borders) {
        this.borders = borders;
    }

    public Collisions(WorldBorder... worldBorder) {
        if (worldBorder.length > 0) {
            this.borders = Arrays.asList(worldBorder);
        }
    }

    public WorldBorder collidingWith(Collision3D collision3D) {
        for (WorldBorder col : borders) {
            if (col.getCollisionBox().collidingWith(collision3D)) return col;
        }
        return null;
    }

    public WorldBorder collidingWith(WorldBorder collision3D) {
        for (WorldBorder col : borders) {
            if (col.getCollisionBox().collidingWith(collision3D.getCollisionBox())) return col;
        }
        return null;
    }

    public WorldBorder collidingWith(Collisions collisions) {
        for (WorldBorder col : collisions) {
            if (collidingWith(col) != col) {
                return col;
            }
        }
        return null;
    }

    public WorldBorder getClosestBorder(int x, int y) {
        WorldBorder closestBorder = null;
        float closestDistance = Float.MAX_VALUE;
        for (WorldBorder border : borders) {
            float distanceToWorldBorder = getDistanceToWorldBorder(x, y, border);
            if (distanceToWorldBorder < closestDistance) {
                closestDistance = distanceToWorldBorder;
                closestBorder = border;
            }
        }
        return closestBorder;
    }

    public static float getDistanceToWorldBorder(int x, int y, WorldBorder border) {
        return border.getCollisionBox().getDistance(x, y);
    }

    public static WorldBorder collidingWith(Collisions collisions1, Collisions collisions2) {
        return collisions1.collidingWith(collisions2);
    }


    public List<WorldBorder> getBorders() {
        return borders;
    }

    @Override
    public Iterator<WorldBorder> iterator() {
        return borders.iterator();
    }

    public WorldBorder isPointInCollision(float x, float y, float z) {
        Collision3D checkC = Collision3D.createX2Y2Z2(x, y, z, x, y, z);
        for (WorldBorder c : this) {
            if (c.getCollisionBox().collidingWith(checkC)) {
                return c;
            }
        }
        return null;
    }

    public WorldBorder isPointInCollision(Point3f pos) {
        return isPointInCollision(pos.x, pos.y, pos.z);
    }

    public WorldBorder getCollisionAt(float x, float y, float z) {
        Collision3D checkC = Collision3D.createX2Y2Z2(x, y, z, x, y, z);
        for (WorldBorder c : this) {
            if (c.getCollisionBox().collidingWith(checkC)) {
                return c;
            }
        }
        return null;
    }

    public void renderCollisions(ModelBatch batch, Camera camera, Environment environment) {
        for (WorldBorder c : this) {
            c.renderCollision3D(batch, camera, environment);
        }
    }
}
