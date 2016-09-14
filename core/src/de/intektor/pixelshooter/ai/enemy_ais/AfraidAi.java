package de.intektor.pixelshooter.ai.enemy_ais;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.abstrct.PositionHelper;
import de.intektor.pixelshooter.ai.*;
import de.intektor.pixelshooter.collision.Collision3D;
import de.intektor.pixelshooter.collision.RayTraceHelper;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.level.editor.LevelEditor;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityLiving;
import de.intektor.pixelshooter.path.PathHelper;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Intektor
 */
public abstract class AfraidAi<T extends EntityEnemyTank> extends AIEnemyTank<T> {

    protected final String TICK_TIMER_REFRESH_PATH_FINDING = "REFRESH_PATH_FINDING" + hashCode();

    public AfraidAi(float movementSpeed, int trackingRange, int shootingCooldown) {
        super(movementSpeed, trackingRange, shootingCooldown);
        this.movementSpeed = movementSpeed;

    }

    @Override
    public void startThinking() {
        if (!alreadyTrackedPlayer) {
            trackNearestPlayer();
        }
        if (trackedPlayer != null && !trackedPlayer.isDead) {
            rotateBarrel();
            if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_SHOOTING) && TickTimerHandler.hasTickTimerFinished(TICK_TIMER_NO_ATTACK_IDLE)) {
                if (attack()) {
                    TickTimerHandler.resetTickTimer(TICK_TIMER_SHOOTING);
                } else {
                    TickTimerHandler.resetTickTimer(TICK_TIMER_NO_ATTACK_IDLE);
                }
            }
        }
        move();
    }

    public GraphPath<BasicNode> path;
    public List<Point3f> posDebugCache;
    int currentStep = 1;

    public void move() {
        if (trackedPlayer != null) {
            List<WorldBorder> borders = new ArrayList<WorldBorder>();
            borders.addAll(entity.worldObj.borders.getBorders());
            borders.addAll(trackedPlayer.getCollisionBox().getBorders());
            Point3f mid = entity.getMid();
            Point3f trackedMid = trackedPlayer.getMid();
            Ray perfectRay;
            float atan = (float) Math.atan2(trackedMid.z - mid.z, trackedMid.x - mid.x);
            perfectRay = new Ray(new Vector3(mid.x, mid.y, mid.z), new Vector3((float) (Math.cos(atan) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2))));
            if (RayTraceHelper.hitRayEntity(perfectRay, mid, trackedPlayer, borders)) {
                if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_REFRESH_PATH_FINDING)) {
                    List<Point3f> placeToHideFromPlayer = findPlaceToHideFromPlayer(entity, trackedPlayer, 75);
                    posDebugCache = placeToHideFromPlayer;
                    for (Point3f point : placeToHideFromPlayer) {
                        BasicNode endNode = entity.worldObj.getNextNodeForPosition((int) point.x, (int) point.z, entity.getGraphPath().nodeTable, entity.getGraphPath().distance);
                        path = PathHelper.findAStarPathEntity(entity, endNode);
                        if (path != null) {
                            currentStep = 1;
                            TickTimerHandler.resetTickTimer(TICK_TIMER_REFRESH_PATH_FINDING);
                            break;
                        }
                    }
                }
            }
            if (path != null && path.getCount() > currentStep) {
                BasicNode basicNode = path.get(currentStep);
                PathHelper.setMotionToStep(basicNode, entity, movementSpeed);
                if (entity.getMid().distance(new Point3f(basicNode.x * LevelEditor.COLLISION_SIZE, entity.posY + entity.getHeight() / 2, basicNode.y * LevelEditor.COLLISION_SIZE)) < 1) {
                    currentStep++;
                }
            } else {
                path = null;
                entity.motionX = entity.motionZ = 0;
            }
        } else {
            path = null;
            entity.motionX = entity.motionZ = 0;
        }
    }


    public boolean couldEntityBeShotAtPosition(Entity attacker, Entity attacked, List<WorldBorder> borders, Point3f pos) {
        for (WorldBorder border : attacked.getCollisionBox()) {
            Collision3D copy = border.getCollisionBox().copy();
            copy.setPosition(pos.x - attacked.getWidth() / 2, pos.y, pos.z - entity.getDepth() / 2);
            Point3f[] corners = new Point3f[]{
                    new Point3f(pos.x, pos.y, pos.z),
                    new Point3f(pos.x + copy.width, pos.y, pos.z),
                    new Point3f(pos.x + copy.width, pos.y, pos.z + copy.depth),
                    new Point3f(pos.x, pos.y, pos.z + copy.depth)
            };
            for (Point3f corner : corners) {
                List<Collision3D> coll = new ArrayList<Collision3D>();
                for (WorldBorder borderx : borders) {
                    coll.add(borderx.getCollisionBox());
                }
                Ray ray = RayTraceHelper.calcLookRay(attacker.getMid(), corner);
                if (RayTraceHelper.hitRayCollision(ray, attacker.getMid(), copy, coll)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Point3f> findPlaceToHideFromPlayer(final EntityLiving hunted, final Entity hunter, float distance) {
        BasicNode node = hunted.worldObj.getNextNodeForEntityMid(hunted, hunted.getGraphPath().nodeTable, hunted.getGraphPath().distance);
        List<Point2f> positions = PositionHelper.getAllPointsInRadius(new Point2f(node.x * LevelEditor.COLLISION_SIZE, node.y * LevelEditor.COLLISION_SIZE), distance, LevelEditor.COLLISION_SIZE);
        Collections.sort(positions, new Comparator<Point2f>() {
            @Override
            public int compare(Point2f o1, Point2f o2) {
                Point2f mid = new Point2f(hunter.getMid().getX(), hunter.getMid().getZ());
                float d1 = o1.distance(mid);
                float d2 = o2.distance(mid);
                return -Float.compare(d1, d2);
            }
        });
        List<Point3f> list = new ArrayList<Point3f>();
        for (Point2f pos : positions) {
            float atan = (float) Math.atan2(hunter.posZ - pos.getY(), hunter.posX - pos.getX());
            Vector3 look = new Vector3((float) (Math.cos(atan) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2)));
            List<WorldBorder> borderList = new ArrayList<WorldBorder>();
            borderList.addAll(hunted.worldObj.borders.getBorders());
            Point3f p = new Point3f(pos.getX(), hunted.posY, pos.getY());
            if (hunted.worldObj.isValidPosition(p)) {
                if (!RayTraceHelper.hitRayEntity(new Ray(new Vector3(pos.getX() + hunted.getWidth() / 2, hunted.posY, pos.getY() + hunted.getDepth() / 2), look), new Point3f(pos.getX(), hunted.posY, pos.getY()), hunter, borderList)) {
                    list.add(p);
                }
            }
        }
        return list;
    }


    @Override
    public void registerTickTimers() {
        super.registerTickTimers();
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_REFRESH_PATH_FINDING);
    }

    @Override
    public abstract boolean attack();
}
