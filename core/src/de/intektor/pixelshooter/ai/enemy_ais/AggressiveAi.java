package de.intektor.pixelshooter.ai.enemy_ais;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.ai.AIEnemyTank;
import de.intektor.pixelshooter.ai.BasicNode;
import de.intektor.pixelshooter.collision.RayTraceHelper;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.entity.EntityBullet;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.path.PathHelper;
import de.intektor.pixelshooter.util.TickTimerHandler;

import javax.vecmath.Point3f;
import java.util.List;

/**
 * @author Intektor
 */
public abstract class AggressiveAi<T extends EntityEnemyTank> extends AIEnemyTank<T> {

    protected final String TICK_TIMER_REFRESH_PATH_FINDING = "REFRESH_PATH_FINDING" + hashCode();

    public AggressiveAi(float movementSpeed, int trackingRange, int shootingCooldown) {
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

    public void move() {
        if (trackedPlayer != null) {
            if (!canShootPlayer()) {
                moveToPlayer();
            } else {
                entity.motionX = entity.motionZ = 0;
            }
        }
    }

    GraphPath<BasicNode> path;
    int currentStep = 1;

    public void moveToPlayer() {
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_REFRESH_PATH_FINDING)) {
            path = PathHelper.findAStarPathEntityToOtherEntityOrAround(entity, trackedPlayer, 5);
            TickTimerHandler.resetTickTimer(TICK_TIMER_REFRESH_PATH_FINDING);
            currentStep = 1;
        }
        if (path != null && path.getCount() > 1) {
            BasicNode basicNode = path.get(currentStep);
            PathHelper.setMotionToStep(basicNode, entity, movementSpeed);
            if (entity.getMid().distance(new Point3f(basicNode.x, entity.posY + entity.getHeight() / 2, basicNode.y)) < 1) {
                currentStep++;
            }
        }
    }

    public boolean canShootPlayer() {
        if (trackedPlayer != null) {
            Ray perfectRay = RayTraceHelper.calcLookRay(entity, trackedPlayer);
            boolean flag = true;
            Ray cpy = perfectRay.cpy();
            EntityBullet bullet = getBullet();
            List<WorldBorder> borders = getBorders();
            if (bullet != null) {
                float width = Math.max(bullet.getWidth(), bullet.getDepth());
                cpy.origin.x -= Math.cos(entity.getLookRotation() + Math.PI / 2) * width;
                cpy.origin.z -= Math.sin(entity.getLookRotation() + Math.PI / 2) * width;
                if (!RayTraceHelper.hitRayEntity(cpy, entity.getMid(), trackedPlayer, borders)) {
                    flag = false;
                }
                if (flag) {
                    cpy = perfectRay.cpy();
                    cpy.origin.x += Math.cos(entity.getLookRotation() + Math.PI / 2) * width;
                    cpy.origin.z += Math.sin(entity.getLookRotation() + Math.PI / 2) * width;
                    if (!RayTraceHelper.hitRayEntity(cpy, entity.getMid(), trackedPlayer, borders)) {
                        flag = false;
                    }
                }
            }
            return !(!RayTraceHelper.hitRayEntity(perfectRay, entity.getMid(), trackedPlayer, borders) || !flag);
        }
        return false;
    }

    public abstract EntityBullet getBullet();

    @Override
    public void registerTickTimers() {
        super.registerTickTimers();
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_REFRESH_PATH_FINDING);
    }

    @Override
    public abstract boolean attack();

}
