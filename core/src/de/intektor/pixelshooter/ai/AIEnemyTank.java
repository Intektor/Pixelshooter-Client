package de.intektor.pixelshooter.ai;

import de.intektor.pixelshooter.collision.RayTraceHelper;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.util.TickTimer;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public abstract class AIEnemyTank<T extends EntityEnemyTank> extends AI<T> {

    public EntityPlayer trackedPlayer;

    protected int trackingRange;
    protected float movementSpeed;
    protected boolean alreadyTrackedPlayer;
    protected int lastTimeSeenPlayer, shootingCooldown;

    protected final String TICK_TIMER_RANDOM_MOVEMENT = "RANDOM MOVEMENT " + hashCode();
    protected final String TICK_TIMER_FINISHED_RANDOM_MOVEMENT = "RANDOM MOVEMENT FINISH " + hashCode();
    protected final String TICK_TIMER_SHOOTING = "SHOOING " + hashCode();
    protected final String TICK_TIMER_NO_ATTACK_IDLE = "NO ATTACK IDLE " + hashCode();

    public AIEnemyTank(float movementSpeed, int trackingRange, int shootingCooldown) {
        this.movementSpeed = movementSpeed;
        this.trackingRange = trackingRange;
        this.shootingCooldown = shootingCooldown;
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
            List<WorldBorder> borders = getBorders();
            if (RayTraceHelper.hitRayEntity(RayTraceHelper.calcLookRay(entity, trackedPlayer), entity.getMid(), trackedPlayer, borders)) {
                lastTimeSeenPlayer = entity.worldObj.getWorldTime();
            }
            randomMovement();
        }
    }


    public abstract boolean attack();

    public void trackNearestPlayer() {
        if (!alreadyTrackedPlayer) {
            if (entity.getDistanceToEntitySq(entity.worldObj.thePlayer) <= trackingRange * trackingRange) {
                trackedPlayer = entity.worldObj.thePlayer;
                alreadyTrackedPlayer = true;
            }
        }
    }

    public void rotateBarrel() {
        List<WorldBorder> borders = getBorders();
        double dX;
        double dZ;
        if (RayTraceHelper.hitRayEntity(RayTraceHelper.calcLookRay(entity, trackedPlayer), entity.getMid(), trackedPlayer, borders)) {
            dX = (trackedPlayer.posX + trackedPlayer.getWidth() / 2) - (entity.posX + entity.getWidth() / 2);
            dZ = (trackedPlayer.posZ + trackedPlayer.getDepth() / 2) - (entity.posZ + entity.getDepth() / 2);
            float atan = (float) Math.atan2(dZ, dX);
            entity.setLookRotation(atan);
        } else {
            entity.setLookRotation(entity.getBaseRotation());
        }
    }

    protected void randomMovement() {
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_FINISHED_RANDOM_MOVEMENT)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_FINISHED_RANDOM_MOVEMENT);
            entity.isForward = entity.isBackward = entity.isLeft = entity.isRight = false;
            entity.motionX = entity.motionY = 0;
        }
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_RANDOM_MOVEMENT)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_RANDOM_MOVEMENT, 200 + random.nextInt(100));
            boolean b = random.nextBoolean();
            entity.isForward = b;
            entity.isBackward = !b;

            b = random.nextBoolean();
            entity.isRight = b;
            entity.isLeft = !b;
        }
    }

    @Override
    public void registerTickTimers() {
        TickTimerHandler.registerTickTimer(new TickTimer(shootingCooldown, TICK_TIMER_SHOOTING));
        TickTimerHandler.registerTickTimer(new TickTimer(100, TICK_TIMER_RANDOM_MOVEMENT));
        TickTimerHandler.registerTickTimer(new TickTimer(50, TICK_TIMER_FINISHED_RANDOM_MOVEMENT));
        TickTimerHandler.registerTickTimer(new TickTimer(5, TICK_TIMER_NO_ATTACK_IDLE));
    }

    public int getTrackingRange() {
        return trackingRange;
    }

    public int getShootingCooldown() {
        return shootingCooldown;
    }

    public List<WorldBorder> getBorders() {
        List<WorldBorder> borders = new ArrayList<WorldBorder>();
        borders.addAll(entity.worldObj.borders.getBorders());
        borders.addAll(trackedPlayer.getCollisionBox().getBorders());
        return borders;
    }
}
