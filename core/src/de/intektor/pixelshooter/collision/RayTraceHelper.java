package de.intektor.pixelshooter.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Intektor
 */
public class RayTraceHelper {

    public static RayTrace rayTraceWorldEntity(Entity entity, float stepsLength, int steps, boolean checkForCollisions, boolean checkForEntities) {
        return rayTraceWorld(entity.worldObj, entity.posX + entity.getWidth() / 2, entity.posY + entity.getHeight() / 2, entity.posZ + entity.getDepth() / 2, entity.getLookVector3(), stepsLength, steps, false, checkForCollisions, checkForEntities, Collections.singletonList(entity));
    }

    public static RayTrace rayTraceWorld(final float sX, final float sY, final float sZ, Vector3 direction, float stepLength, float maxDistance, World world, boolean stopAtNegY, boolean checkForCollisions, boolean checkForEntities) {
        return rayTraceWorld(sX, sY, sZ, direction, stepLength, maxDistance, world, stopAtNegY, checkForCollisions, checkForEntities, new ArrayList());
    }

    public static RayTrace rayTraceWorld(final float sX, final float sY, final float sZ, Vector3 direction, float stepLength, float maxDistance, World world, boolean stopAtNegY, boolean checkForCollisions, boolean checkForEntities, List ignoreObjects) {
        RayTrace ray = new RayTrace();

        //Distance already checked as a square
        double currDSq = 0;
        //Maximal distance as a square, so we don't have to calc it every loop agains
        double maxDistanceSq = maxDistance * maxDistance;

        //Prev positions
        float prevX = sX;
        float prevY = sY;
        float prevZ = sZ;

        while (currDSq <= maxDistanceSq && (!stopAtNegY || prevY >= 0)) {
            //Current position
            float cX = prevX;
            float cY = prevY;
            float cZ = prevZ;

            //Add step length to the position
            cX += direction.x * stepLength;
            cY += direction.y * stepLength;
            cZ += direction.z * stepLength;

            //Potential entity hit
            Entity potEntHit = world.getEntityAt(cX, cY, cZ);
            if (potEntHit != null && !ignoreObjects.contains(potEntHit) && checkForEntities) {
                ray.entityHit = potEntHit;
                ray.hitX = cX;
                ray.hitY = cY;
                ray.hitZ = cZ;
                ray.hasHit = true;
                return ray;
            }

            //Potential collision hit
            WorldBorder potColHit = world.borders.getCollisionAt(cX, cY, cZ);
            if (potColHit != null && !ignoreObjects.contains(potColHit) && checkForCollisions) {
                ray.collisionHit = potColHit;
                ray.hitX = cX;
                ray.hitY = cY;
                ray.hitZ = cZ;
                ray.hasHit = true;
                return ray;
            }
            currDSq += (cX - prevX) * (cX - prevX) + (cY - prevY) * (cY - prevY) + (cZ - prevZ) * (cZ - prevZ);
            prevX = cX;
            prevY = cY;
            prevZ = cZ;
        }
        ray.hitX = prevX;
        ray.hitY = prevY;
        ray.hitZ = prevZ;
        return ray;
    }

    public static RayTrace rayTraceWorld(World world, float sX, float sY, float sZ, Vector3 direction, float stepLength, int steps, boolean stopAtNegY, boolean checkForCollisions, boolean checkForEntities, List exclude) {
        RayTrace ray = new RayTrace();
        for (int i = 0; i < steps && (!stopAtNegY || sY > 0); i++) {
            sX += direction.x * stepLength;
            sY += direction.y * stepLength;
            sZ += direction.z * stepLength;
            if (checkForCollisions) {
                //Potential Entity hit
                WorldBorder potColHit = world.borders.isPointInCollision(sX, sY, sZ);
                if (potColHit != null && !exclude.contains(potColHit)) {
                    ray.collisionHit = potColHit;
                    ray.hitX = sX;
                    ray.hitY = sY;
                    ray.hitZ = sZ;
                    ray.hasHit = true;
                    return ray;
                }
            }
            if (checkForEntities) {
                //Potential entity hit
                Entity potEntHit = world.getEntityAt(sX, sY, sZ);
                if (potEntHit != null && !exclude.contains(potEntHit)) {
                    ray.entityHit = potEntHit;
                    ray.hitX = sX;
                    ray.hitY = sY;
                    ray.hitZ = sZ;
                    ray.hasHit = true;
                    return ray;
                }
            }
        }
        ray.hitX = sX;
        ray.hitY = sY;
        ray.hitZ = sZ;
        return ray;
    }

    public static List<RayTrace> simpleRayTrace(Ray ray, boolean quick, List<Collision3D> collisions) {
        List<RayTrace> l = new ArrayList<RayTrace>();
        for (Collision3D collision : collisions) {
            RayTrace rayTrace = new RayTrace();
            if (quick) {
                if (Intersector.intersectRayBoundsFast(ray, collision.getBoundingBox())) {
                    rayTrace.collisionHit = new WorldBorder(collision);
                    rayTrace.hasHit = true;
                    l.add(rayTrace);
                }
            } else {
                Vector3 intersection = new Vector3();
                if (Intersector.intersectRayBounds(ray, collision.getBoundingBox(), intersection)) {
                    rayTrace.hasHit = true;
                    rayTrace.collisionHit = new WorldBorder(collision);
                    rayTrace.hitX = intersection.x;
                    rayTrace.hitY = intersection.y;
                    rayTrace.hitZ = intersection.z;
                    l.add(rayTrace);
                }
            }
        }
        return l;
    }


    public static List<RayTrace> rayTrace(Ray ray, boolean quick, List<WorldBorder> borders) {
        List<RayTrace> l = new ArrayList<RayTrace>();
        for (WorldBorder collision3D : borders) {
            RayTrace rayTrace = new RayTrace();
            if (quick) {
                if (Intersector.intersectRayBoundsFast(ray, collision3D.getCollisionBox().getBoundingBox())) {
                    rayTrace.collisionHit = collision3D;
                    rayTrace.hasHit = true;
                    if (collision3D instanceof WorldBorder.Collision3DEntity) {
                        rayTrace.entityHit = ((WorldBorder.Collision3DEntity) collision3D).getOwner();
                    }
                    l.add(rayTrace);
                }
            } else {
                Vector3 intersection = new Vector3();
                if (Intersector.intersectRayBounds(ray, collision3D.getCollisionBox().getBoundingBox(), intersection)) {
                    rayTrace.hasHit = true;
                    rayTrace.collisionHit = collision3D;
                    rayTrace.hitX = intersection.x;
                    rayTrace.hitY = intersection.y;
                    rayTrace.hitZ = intersection.z;
                    if (collision3D instanceof WorldBorder.Collision3DEntity) {
                        rayTrace.entityHit = ((WorldBorder.Collision3DEntity) collision3D).getOwner();
                    }
                    l.add(rayTrace);
                }
            }

        }
        return l;
    }

    public static RayTrace getFirstRayCollided(List<RayTrace> rays, Point3f pos) {
        RayTrace sRay = null;
        double shortestDistance = Double.MAX_VALUE;
        for (RayTrace trace : rays) {
            Point3f hitPoint = new Point3f(trace.hitX, trace.hitY, trace.hitZ);
            double d = pos.distance(hitPoint);
            if (d < shortestDistance) {
                shortestDistance = d;
                sRay = trace;
            }
        }
        return sRay;
    }

    public static boolean hitRayEntity(Ray ray, Point3f src, Entity entityToHit, List<WorldBorder> borders) {
        borders.addAll(entityToHit.getCollisionBox().getBorders());
        RayTrace firstRayCollided = getFirstRayCollided(rayTrace(ray, false, borders), src);
        return firstRayCollided != null && firstRayCollided.entityHit != null && firstRayCollided.entityHit == entityToHit;
    }

    public static boolean hitRayCollision(Ray ray, Point3f src, Collision3D collision, List<Collision3D> borders) {
        borders.add(collision);
        RayTrace firstRayCollided = getFirstRayCollided(simpleRayTrace(ray, false, borders), src);
        return firstRayCollided != null && firstRayCollided.collisionHit != null && firstRayCollided.collisionHit.getCollisionBox() == collision;
    }

    public static Ray calcLookRay(Entity main, Entity lookAt) {
        return calcLookRay(main.getMid(), lookAt.getMid());
    }

    public static Ray calcLookRay(Point3f point1, Point3f point2) {
        float atan = (float) Math.atan2(point2.z - point1.z, point2.x - point1.x);
        return new Ray(new Vector3(point1.x, point1.y, point1.z), new Vector3((float) (Math.cos(atan) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2))));
    }
}
