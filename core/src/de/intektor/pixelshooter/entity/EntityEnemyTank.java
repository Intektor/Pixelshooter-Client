package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.ai.AIEnemyTank;
import de.intektor.pixelshooter.ai.enemy_ais.AfraidAi;
import de.intektor.pixelshooter.ai.enemy_ais.AggressiveAi;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.collision.RayTrace;
import de.intektor.pixelshooter.collision.RayTraceHelper;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter.helper.ColorHelper;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.score.object.KillTankScore;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public abstract class EntityEnemyTank extends EntityLiving implements Tank {

    private Model modelTank, modelUpperTank, modelBarrel;

    public float damage;

    public int bulletBounces;

    public EntityEnemyTank(float posX, float posY, World world, AIEnemyTank ai, float health, float damage, int bulletBounces, float speed) {
        super(posX, posY, world, ai);
        motionMultiplier = speed;
        this.health = health;
        this.damage = damage;
        this.bulletBounces = bulletBounces;
        modelTank = PixelShooter.modelBuilder.createBox(getWidth(), getHeight() / 5f * 2f, getDepth() / 6f * 5f, new Material(ColorAttribute.createDiffuse(getTankColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelUpperTank = PixelShooter.modelBuilder.createBox(getWidth() / 2, getHeight() / 2, getDepth() / 2, new Material(ColorAttribute.createDiffuse(getTankColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelBarrel = PixelShooter.modelBuilder.createBox(2, 2, getBarrelLength(), new Material(ColorAttribute.createDiffuse(getBarrelColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        setLookRotation(0);
    }

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
        RenderHelper.renderTank3D(batch, camera, this, environment, this);
//        RenderHelper.renderPath(rawWorldCamera, PathHelper.findAStarPathEntityToOtherEntityOrAround(this, worldObj.thePlayer, 5), Color.ORANGE);
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
    }

    public abstract Color getTankColor();

    public abstract Color getBarrelColor();

    public abstract float getMaxHealth();

    public int getTrackingRange() {
        return ((AIEnemyTank) ai).getTrackingRange();
    }

    public int getShootingCooldown() {
        return ((AIEnemyTank) ai).getShootingCooldown();
    }

    @Override
    public void laserHit(EntityLaser laser, float charge, float maxCharge, float damage) {
        super.laserHit(laser, charge, maxCharge, damage);
    }

    @Override
    public float getWidth() {
        return 10;
    }

    @Override
    public float getHeight() {
        return 10;
    }

    @Override
    public float getDepth() {
        return 10;
    }

    @Override
    public void onCollidedWithEntity(Entity entity) {

    }

    @Override
    public boolean canBeCollidedWithEntity(Entity entity) {
        return true;
    }

    @Override
    public void onDeath(KillReason reason) {
        super.onDeath(reason);
        if (!isDead) {
            worldObj.scoreObjects.add(new KillTankScore(this, getScoreOnKilled()));
        }
    }

    public abstract int getScoreOnKilled();

    @Override
    public ModelInstance[] getModelInstance() {
        ModelInstance[] instances = new ModelInstance[3];
        instances[0] = new ModelInstance(modelTank, posX + getWidth() / 2, posY + getHeight() / 3, posZ + getDepth() / 2);
        instances[1] = new ModelInstance(modelUpperTank, posX + getWidth() / 2, posY + getHeight() / 3 * 2, posZ + getHeight() / 2);
        instances[2] = new ModelInstance(modelBarrel);
        return instances;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public float getBarrelLength() {
        return 8.5f;
    }

    @Override
    public boolean isStandardBulletShooter() {
        return true;
    }

    @Override
    public int getBulletBounces() {
        return bulletBounces;
    }

    @Override
    public boolean collidesWithPathFinder() {
        return true;
    }

    public static class TankStandardAttacker extends EntityEnemyTank {

        public TankStandardAttacker(float posX, float posY, final World world, float health, int trackingRange, int shootingCooldown, float damage, int bulletBounces, float speed) {
            super(posX, posY, world, new AIEnemyTank<TankStandardAttacker>(1, trackingRange, shootingCooldown) {
                @Override
                public boolean attack() {
                    Collisions collisions = new Collisions(entity.worldObj.getCollisionsWithMaxDistance(entity.getPosition(), (float) Math.sqrt(entity.getDistanceToEntitySq(trackedPlayer))));
                    List<WorldBorder> borders = new ArrayList<WorldBorder>();
                    borders.addAll(collisions.getBorders());
                    borders.addAll(entity.worldObj.thePlayer.collisionBox.getBorders());
                    List<RayTrace> l = RayTraceHelper.rayTrace(entity.getLookRay(), false, borders);
                    if (l.isEmpty()) return false;
                    if (l.get(0).collisionHit == entity.worldObj.thePlayer.collisionBox.getBorders().get(0)) {
                        EntityBullet bullet = new EntityBullet.StandardBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                        double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);
                        double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);

                        bullet.motionZ = (float) (sin * 1);
                        bullet.motionX = (float) (cos * 1);

                        entity.worldObj.addEntity(bullet);
                        return true;
                    }
                    return false;
                }
            }, health, damage, bulletBounces, speed);
        }


        @Override
        public Color getTankColor() {
            return Color.BLUE;
        }

        @Override
        public Color getBarrelColor() {
            return ColorHelper.toColor111(30, 144, 255, 1);
        }

        @Override
        public float getMaxHealth() {
            return 1;
        }

        @Override
        public int getScoreOnKilled() {
            return 500;
        }
    }

    public static class TankQuickShooter extends EntityEnemyTank {

        public TankQuickShooter(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, float damage, int bulletBounces, float speed) {
            super(posX, posY, world, new AggressiveAi<TankQuickShooter>(2.5f, trackingRange, shootingCooldown) {
                @Override
                public EntityBullet getBullet() {
                    return new EntityBullet.StandardBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);
                }

                @Override
                public boolean attack() {
                    List<WorldBorder> borders = new ArrayList<WorldBorder>();
                    borders.addAll(entity.worldObj.borders.getBorders());
                    borders.addAll(trackedPlayer.getCollisionBox().getBorders());
                    Point3f mid = entity.getMid();
                    Point3f trackedMid = trackedPlayer.getMid();
                    float atan = (float) Math.atan2(trackedMid.z - mid.z, trackedMid.x - mid.x);
                    Ray perfectRay = new Ray(new Vector3(mid.x, mid.y, mid.z), new Vector3((float) (Math.cos(atan) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2))));
                    if (RayTraceHelper.hitRayEntity(perfectRay, entity.getMid(), trackedPlayer, borders)) {
                        EntityBullet bullet = new EntityBullet.StandardBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                        double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);
                        double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);

                        bullet.motionZ = (float) (sin * 1);
                        bullet.motionX = (float) (cos * 1);

                        entity.worldObj.addEntity(bullet);
                        return true;
                    }
                    return false;
                }
            }, health, damage, bulletBounces, speed);
        }

        @Override
        public Color getTankColor() {
            return Color.YELLOW;
        }

        @Override
        public Color getBarrelColor() {
            return ColorHelper.toColor111(128, 128, 0, 1);
        }

        @Override
        public float getMaxHealth() {
            return 1;
        }

        @Override
        public int getScoreOnKilled() {
            return 2000;
        }
    }

    public static class TankArtillery extends EntityEnemyTank {

        public TankArtillery(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, float damage, float speed) {
            super(posX, posY, world, new AfraidAi<TankArtillery>(1, trackingRange, shootingCooldown) {

                @Override
                public void rotateBarrel() {
                    double dX;
                    double dZ;
                    dX = (trackedPlayer.posX + trackedPlayer.getWidth() / 2) - (entity.posX + entity.getWidth() / 2);
                    dZ = (trackedPlayer.posZ + trackedPlayer.getDepth() / 2) - (entity.posZ + entity.getDepth() / 2);
                    float atan = (float) Math.atan2(dZ, dX);
                    entity.setLookRotation(atan);
                }

                @Override
                public boolean attack() {
                    EntityBullet bullet = new EntityBullet.ArtilleryBullet(entity.posX + entity.getWidth() / 2, 0, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, trackedPlayer.posX, trackedPlayer.posY, trackedPlayer.posZ, entity.damage);

                    double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.05);
                    double cos = Math.cos(entity.getLookRotation());

                    bullet.motionZ = (float) (sin);
                    bullet.motionX = (float) (cos);

                    entity.worldObj.addEntity(bullet);
                    return true;
                }
            }, health, damage, 0, speed);
        }

        @Override
        public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
            super.renderEntity(batch, camera, environment);
        }

        @Override
        public Color getTankColor() {
            return Color.RED;
        }

        @Override
        public Color getBarrelColor() {
            return ColorHelper.toColor111(128, 0, 0, 1);
        }

        @Override
        public float getMaxHealth() {
            return 3;
        }

        @Override
        public int getScoreOnKilled() {
            return 3000;
        }

        @Override
        public boolean isStandardBulletShooter() {
            return false;
        }
    }

    public static class TankTripleAttacker extends EntityEnemyTank {

        public int amtOfBullets, radiusOfShooting;

        public TankTripleAttacker(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, int amtOfBullets, int radiusOfShooting, float damage, int bulletBounces, float speed) {
            super(posX, posY, world, new AggressiveAi<TankTripleAttacker>(2, trackingRange, shootingCooldown) {

                double attackX, attackY, attackZ;

                final String TICK_TIMER_GO_FOR_ATTACK = "TripleAttackerAttackTimer" + hashCode();

                boolean allowGoForAttack;

                @Override
                public void startThinking() {
                    trackNearestPlayer();
                    if (trackedPlayer != null) {
                        final boolean isInPositionToShootPlayer = canShootPlayer();
                        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_GO_FOR_ATTACK)) {
                            if (isInPositionToShootPlayer) {
                                TickTimerHandler.resetTickTimer(TICK_TIMER_GO_FOR_ATTACK);
                                allowGoForAttack = true;
                                attackX = trackedPlayer.getMid().x;
                                attackY = trackedPlayer.getMid().y;
                                attackZ = trackedPlayer.getMid().z;
                            }
                        }
                        if (!isInPositionToShootPlayer) {
                            allowGoForAttack = false;
                        }
                        if (allowGoForAttack) {
                            if (attack()) {
                                rotateBarrel();
                                allowGoForAttack = false;
                            }
                        }
                        if (!allowGoForAttack) {
                            if (!isInPositionToShootPlayer) {
                                rotateBarrel();
                                TickTimerHandler.resetTickTimer(TICK_TIMER_GO_FOR_ATTACK);
                            }
                            move();
                        } else {
                            entity.motionX = entity.motionZ = 0;
                        }
                    }
                }

                @Override
                public EntityBullet getBullet() {
                    return new EntityBullet.StandardBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);
                }

                @Override
                public boolean attack() {
                    double radius = entity.radiusOfShooting;
                    for (int i = 0; i < entity.amtOfBullets; i++) {
                        EntityBullet bullet = new EntityBullet.StandardBullet(entity.posX + entity.getWidth() / 2, 0, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                        double sin = Math.sin(entity.getLookRotation() + Math.toRadians(-radius / entity.amtOfBullets + radius / entity.amtOfBullets * i));
                        double cos = Math.cos(entity.getLookRotation() + Math.toRadians(-radius / entity.amtOfBullets + radius / entity.amtOfBullets * i));

                        bullet.motionZ = (float) (sin * 1);
                        bullet.motionX = (float) (cos * 1);

                        entity.worldObj.addEntity(bullet);
                    }
                    return true;
                }

                @Override
                public void registerTickTimers() {
                    super.registerTickTimers();
                    TickTimerHandler.registerTickTimer(shootingCooldown, TICK_TIMER_GO_FOR_ATTACK);
                }
            }, health, damage, bulletBounces, speed);
            this.amtOfBullets = amtOfBullets;
            this.radiusOfShooting = radiusOfShooting;
        }

        @Override
        public Color getTankColor() {
            return Color.ORANGE;
        }

        @Override
        public Color getBarrelColor() {
            return ColorHelper.toColor111(255, 215, 0, 1);
        }

        @Override
        public float getMaxHealth() {
            return 3;
        }

        @Override
        public int getScoreOnKilled() {
            return 4000;
        }
    }

    public static class TankChaseShooter extends EntityEnemyTank {

        public TankChaseShooter(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, float damage, int bulletBounces, float speed) {
            super(posX, posY, world, new AggressiveAi<TankChaseShooter>(1, trackingRange, shootingCooldown) {
                @Override
                public EntityBullet getBullet() {
                    EntityBullet bullet = new EntityBullet.ChasingBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                    double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);
                    double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);

                    bullet.motionZ = (float) (sin * 1);
                    bullet.motionX = (float) (cos * 1);
                    return bullet;
                }

                @Override
                public boolean attack() {
                    Collisions collisions = new Collisions(entity.worldObj.getCollisionsWithMaxDistance(entity.getPosition(), (float) Math.sqrt(entity.getDistanceToEntitySq(trackedPlayer))));
                    List<WorldBorder> borders = new ArrayList<WorldBorder>();
                    borders.addAll(collisions.getBorders());
                    borders.addAll(entity.worldObj.thePlayer.collisionBox.getBorders());
                    Point3f mid = entity.getMid();
                    Point3f trackedMid = trackedPlayer.getMid();
                    float atan = (float) Math.atan2(trackedMid.z - mid.z, trackedMid.x - mid.x);
                    Ray perfectRay = new Ray(new Vector3(mid.x, mid.y, mid.z), new Vector3((float) (Math.cos(atan) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2))));

                    List<RayTrace> l = RayTraceHelper.rayTrace(perfectRay, false, borders);
                    if (l.isEmpty()) return false;
                    if (l.get(0).collisionHit == entity.worldObj.thePlayer.collisionBox.getBorders().get(0)) {
                        EntityBullet bullet = new EntityBullet.ChasingBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                        double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.35);
                        double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.35);

                        bullet.motionZ = (float) (sin * 1);
                        bullet.motionX = (float) (cos * 1);

                        entity.worldObj.addEntity(bullet);
                        return true;
                    }
                    return false;
                }
            }, health, damage, bulletBounces, speed);
        }

        @Override
        public Color getTankColor() {
            return new Color(0.1f, 0.1f, 0.1f, 1);
        }

        @Override
        public Color getBarrelColor() {
            return Color.DARK_GRAY;
        }

        @Override
        public float getMaxHealth() {
            return 3;
        }

        @Override
        public int getScoreOnKilled() {
            return 6000;
        }
    }

    public static class TankLaserShooter extends EntityEnemyTank {

        public int maxLaserCharge = 500;

        public int maxAttackTime;

        protected float laserCharge = -1;

        public TankLaserShooter(final float posX, float posY, final World world, float health, int trackingRange, float damage, float speed, int maxLaserCharge, int maxAttackTime) {
            super(posX, posY, world, new AggressiveAi<TankLaserShooter>(1, trackingRange, 0) {

                EntityLaser laser;
                int attackTime;
                boolean isAttacking;

                @Override
                public void startThinking() {
                    trackNearestPlayer();
                    if (trackedPlayer != null) {
                        double dX = (trackedPlayer.posX + trackedPlayer.getWidth() / 2) - (entity.posX + entity.getWidth() / 2);
                        double dZ = (trackedPlayer.posZ + trackedPlayer.getDepth() / 2) - (entity.posZ + entity.getDepth() / 2);

                        float atan = (float) Math.atan2(dZ, dX);

                        Vector3 targetLookVec = new Vector3((float) (Math.cos(atan) * Math.PI / 2), 0, (float) (Math.sin(atan) * Math.PI / 2));

                        Collisions collisions = new Collisions(entity.worldObj.getCollisionsWithMaxDistance(entity.getMid(), (float) Math.sqrt(entity.getDistanceToEntitySq(trackedPlayer))));
                        List<WorldBorder> borders = new ArrayList<WorldBorder>();
                        borders.addAll(collisions.getBorders());
                        borders.addAll(entity.worldObj.thePlayer.collisionBox.getBorders());


                        List<RayTrace> l = RayTraceHelper.rayTrace(entity.getLookRay(), false, borders);
                        if (isAttacking) {
                            if (l.size() == 1 && l.get(0).collisionHit == entity.worldObj.thePlayer.collisionBox.getBorders().get(0)) {
                                rotateBarrel();
                            }
                        } else {
                            rotateBarrel();
                        }
                        if (entity.laserCharge >= entity.maxLaserCharge - 200) {
                            //Some more check so we are close to the target when shooting
                            Vector3f rLookVec = entity.getLookVector3f();
                            Vector3f targetLookVec3f = new Vector3f(targetLookVec.x, targetLookVec.y, targetLookVec.z);
                            float angle = rLookVec.angle(targetLookVec3f);

                            if ((angle < Math.toRadians(20) && l.size() == 1 && l.get(0).collisionHit == entity.worldObj.thePlayer.collisionBox.getBorders().get(0)) || isAttacking) {
                                if (attack()) {
                                    attackTime++;
                                    isAttacking = true;
                                }
                                if (attackTime > entity.maxAttackTime) {
                                    attackTime = 0;
                                    laser.kill(new KillReason.Suicide());
                                    entity.laserCharge = 0;
                                    isAttacking = false;
                                }
                            }
                        }
                    }
                    move();
                }

                @Override
                public EntityBullet getBullet() {
                    return null;
                }

                @Override
                public boolean attack() {
                    //Create a laser if there is not already one there
                    if (laser == null) {
                        laser = new EntityLaser(entity.posX + entity.getWidth() / 2, entity.posY + entity.getHeight() / 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity.getLookRotation(), entity, entity.damage);
                        entity.worldObj.addEntity(laser);
                    }
                    if (laser.isDead) {
                        laser.revive();
                        entity.worldObj.addEntity(laser);
                    }
                    laser.setPosition(entity.posX + entity.getWidth() / 2, entity.posY + entity.getHeight() / 2, entity.posZ + entity.getDepth() / 2);
                    laser.setLookRotation(entity.getLookRotation());
                    return true;
                }

                @Override
                public void rotateBarrel() {
                    double dX = (trackedPlayer.posX + trackedPlayer.getWidth() / 2) - (entity.posX + entity.getWidth() / 2);
                    double dZ = (trackedPlayer.posZ + trackedPlayer.getDepth() / 2) - (entity.posZ + entity.getDepth() / 2);

                    float atan = (float) Math.atan2(dZ, dX);

                    float srcLookRotation = MathHelper.toDegree360((float) (entity.getLookRotation() * 180 / Math.PI));
                    float ratan = MathHelper.toDegree360((float) (atan * 180 / Math.PI));

                    Vector3f vec1 = new Vector3f(entity.getLookVector3().x, entity.getLookVector3().y, entity.getLookVector3().z);

                    Vector3f test1 = new Vector3f((float) (Math.cos(atan + Math.toRadians(1)) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2)));
                    Vector3f test2 = new Vector3f((float) (Math.cos(atan - Math.toRadians(1)) * (Math.PI / 2)), 0, (float) (Math.sin(atan) * (Math.PI / 2)));

                    float angle1 = test1.angle(vec1);
                    float angle2 = test2.angle(vec1);

                    boolean positive = MathHelper.getClosestToNumber(angle1, angle2, 0) == angle2;

                    double addition = laser != null && !laser.isDead ? 0.2 : 0.45;

                    if (!MathHelper.isInsideNumberRegion(srcLookRotation, ratan, 1)) {
                        entity.setLookRotation(Math.toRadians(srcLookRotation + (positive ? addition : -addition)) % (Math.PI * 2));
                    }
                }

                @Override
                public void onEntityDeath() {
                    super.onEntityDeath();
                    if (laser != null) {
                        laser.isDead = true;
                        laser = null;
                    }
                }
            }, health, damage, 0, speed);
            this.maxLaserCharge = maxLaserCharge;
            this.maxAttackTime = maxAttackTime;
        }

        @Override
        public void updateEntity(World world) {
            super.updateEntity(world);
            laserCharge++;
        }

        @Override
        public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
            super.renderEntity(batch, camera, environment);
        }

        @Override
        public Color getTankColor() {
            return Color.FIREBRICK;
        }

        @Override
        public Color getBarrelColor() {
            return new Color(laserCharge != -1 ? laserCharge / maxLaserCharge : 1, laserCharge != -1 ? 1 - (laserCharge / maxLaserCharge) : 1, 0, 1);
        }

        @Override
        public float getMaxHealth() {
            return 2;
        }

        @Override
        public int getScoreOnKilled() {
            return 10000;
        }

        @Override
        public boolean isStandardBulletShooter() {
            return false;
        }
    }

    public static class TankMineShooter extends EntityEnemyTank {

        public TankMineShooter(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, float damage, float speed) {
            super(posX, posY, world, new AfraidAi<TankMineShooter>(0.75f, trackingRange, shootingCooldown) {
                @Override
                public boolean attack() {
                    EntityBullet bullet = new EntityBullet.MineBullet(entity.posX + entity.getWidth() / 2, 0, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, trackedPlayer.posX, trackedPlayer.posY, trackedPlayer.posZ, entity, entity.damage);

                    double sin = Math.sin(entity.getLookRotation());
                    double cos = Math.cos(entity.getLookRotation());

                    bullet.motionZ = (float) (sin);
                    bullet.motionX = (float) (cos);

                    entity.worldObj.addEntity(bullet);
                    return true;
                }

                @Override
                public void rotateBarrel() {
                    double dX = (trackedPlayer.posX + trackedPlayer.getWidth() / 2) - (entity.posX + entity.getWidth() / 2);
                    double dZ = (trackedPlayer.posZ + trackedPlayer.getDepth() / 2) - (entity.posZ + entity.getDepth() / 2);

                    float atan = (float) Math.atan2(dZ + ((random.nextFloat() % 10) - 10), dX + ((random.nextFloat() % 10) - 10));

                    entity.setLookRotation(atan);
                }
            }, health, damage, 0, speed);
        }

        @Override
        public Color getTankColor() {
            return Color.NAVY;
        }

        @Override
        public Color getBarrelColor() {
            return Color.SKY;
        }

        @Override
        public float getMaxHealth() {
            return 2;
        }

        @Override
        public int getScoreOnKilled() {
            return 3000;
        }

        @Override
        public boolean isStandardBulletShooter() {
            return false;
        }
    }

    public static class TankHeavyShooter extends EntityEnemyTank {

        public TankHeavyShooter(float posX, float posY, World world, float health, int trackingRange, int shootingCooldown, float damage, int bulletBounces, float speed) {
            super(posX, posY, world, new AggressiveAi<TankHeavyShooter>(0.5f, trackingRange, shootingCooldown) {

                @Override
                public EntityBullet getBullet() {
                    EntityBullet bullet = new EntityBullet.HeavyBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                    double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);
                    double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);

                    bullet.motionZ = (float) (sin * 1);
                    bullet.motionX = (float) (cos * 1);
                    return bullet;
                }

                @Override
                public boolean attack() {
                    Collisions collisions = new Collisions(entity.worldObj.getCollisionsWithMaxDistance(entity.getPosition(), (float) Math.sqrt(entity.getDistanceToEntitySq(trackedPlayer))));
                    List<WorldBorder> actualBorders = new ArrayList<WorldBorder>();
                    for (WorldBorder collision : collisions) {
                        if (collision.getBorderType() != WorldBorder.BorderType.BREAKABLE) {
                            actualBorders.add(collision);
                        }
                    }
                    List<WorldBorder> borders = new ArrayList<WorldBorder>();
                    borders.addAll(actualBorders);
                    borders.addAll(entity.worldObj.thePlayer.collisionBox.getBorders());
                    List<RayTrace> l = RayTraceHelper.rayTrace(entity.getLookRay(), false, borders);
                    if (l.isEmpty()) return false;
                    if (l.get(0).collisionHit == entity.worldObj.thePlayer.collisionBox.getBorders().get(0)) {
                        EntityBullet bullet = new EntityBullet.HeavyBullet(entity.posX + entity.getWidth() / 2, 2, entity.posZ + entity.getDepth() / 2, entity.worldObj, entity, Color.RED, entity.damage, entity.bulletBounces);

                        double sin = Math.sin(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);
                        double cos = Math.cos(entity.getLookRotation() + (random.nextFloat() - 0.5) % 0.2);

                        bullet.motionZ = (float) (sin * 1);
                        bullet.motionX = (float) (cos * 1);

                        entity.worldObj.addEntity(bullet);
                        return true;
                    }
                    return false;
                }

                @Override
                public List<WorldBorder> getBorders() {
                    List<WorldBorder> borders = new ArrayList<WorldBorder>();
                    for (WorldBorder worldBorder : super.getBorders()) {
                        if (worldBorder.getBorderType() != WorldBorder.BorderType.BREAKABLE) {
                            borders.add(worldBorder);
                        }
                    }
                    return borders;
                }
            }, health, damage, bulletBounces, speed);
        }

        @Override
        public Color getTankColor() {
            return new Color(0x888888ff);
        }

        @Override
        public Color getBarrelColor() {
            return new Color(0xaaaaaaff);
        }

        @Override
        public float getMaxHealth() {
            return 3;
        }

        @Override
        public int getScoreOnKilled() {
            return 8000;
        }
    }
}
