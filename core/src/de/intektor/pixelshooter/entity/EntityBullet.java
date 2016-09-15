package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.PositionHelper;
import de.intektor.pixelshooter.ai.BasicNode;
import de.intektor.pixelshooter.collision.Collision3D;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.enums.EnumSide;
import de.intektor.pixelshooter.path.PathHelper;
import de.intektor.pixelshooter.path.PathTraveller;
import de.intektor.pixelshooter.path.WorldIndexedGraph;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point3f;


/**
 * @author Intektor
 */
public abstract class EntityBullet extends Entity {

    protected Entity shooter;
    protected Color renderColor;

    boolean hitByLaser;

    protected float damage;

    int timesCollided, maxBounces;

    protected Model model;

    public EntityBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float damage, int bounces) {
        super(posX, posY, posZ, world);
        this.shooter = shooter;
        this.renderColor = renderColor;
        this.motionMultiplier = (float) getMotionMultiplier();
        this.damage = damage;
        this.maxBounces = bounces;
        model = PixelShooter.modelBuilder.createBox(getWidth(), getHeight(), getDepth(), new Material(ColorAttribute.createDiffuse(new Color(1, 1, 1, 0.5f))), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
        batch.begin(camera);
        ModelInstance instance = new ModelInstance(model, posX + getWidth() / 2, posY + getHeight() / 2, posZ + getDepth() / 2);
        instance.materials.get(0).set(ColorAttribute.createDiffuse(renderColor.r, renderColor.g, renderColor.b, 1), new BlendingAttribute(opacity));
        batch.render(instance, environment);
        batch.end();
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
        if (hitByLaser) {
            opacity -= 0.05;
        }
        if (opacity <= 0) {
            kill(new KillReason.Suicide());
        }
    }

    @Override
    public void kill(KillReason reason) {
        super.kill(reason);
    }

    @Override
    public void onDeath(KillReason reason) {
        super.onDeath(reason);
    }

    @Override
    public float getWidth() {
        return 2;
    }

    @Override
    public float getHeight() {
        return 2;
    }

    @Override
    public float getDepth() {
        return 2;
    }

    @Override
    public long getMaxLifeTime() {
        return 500;
    }

    public boolean diesAtCollisionBreakable() {
        return true;
    }

    @Override
    public void onCollided(Collisions collidedWith, WorldBorder border, EnumSide side) {
        if (border.getBorderType() == WorldBorder.BorderType.BREAKABLE) return;
        if (timesCollided < maxBounces) {
            if (side == EnumSide.FRONT || side == EnumSide.BACK) {
                motionZ = -motionZ;
            } else if (side == EnumSide.LEFT || side == EnumSide.RIGHT) {
                motionX = -motionX;
            } else if (side == EnumSide.UP || side == EnumSide.DOWN) {
                motionY = -motionY;
            }
            timesCollided++;
        } else {
            kill(new KillReason.Too_Many_Bounces(timesCollided));
        }
    }

    @Override
    public void laserHit(EntityLaser laser, float charge, float maxCharge, float damage) {
        hitByLaser = true;
    }

    @Override
    public void damage(DamageSource source) {
        super.damage(source);
    }

    @Override
    public void onCollidedWithEntity(Entity entity) {
        if (entity == shooter) return;
        if (shooter instanceof EntityEnemyTank) {
            if (entity instanceof EntityEnemyTank) {
                kill(new KillReason.Suicide());
                return;
            }
        }
        float width = 7.5f;
        float height = 7.5f;
        worldObj.addEntity(new EntitySpriteExplosion(posX, posY + height / 2, posZ, worldObj, width, height));
        entity.damage(new DamageSource(damage, this));
        kill(new KillReason.Suicide());
    }

    @Override
    public boolean canBeCollidedWithEntity(Entity entity) {
        if (entity == shooter) return false;
        if (entity instanceof EntityBullet) {
            if (((EntityBullet) entity).shooter == shooter) {
                return false;
            }
        }
        return true;

    }

    @Override
    public float getMaxHealth() {
        return 1;
    }

    public abstract double getMotionMultiplier();

    public static class StandardBullet extends EntityBullet {

        public StandardBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float damage, int bounces) {
            super(posX, posY, posZ, world, shooter, renderColor, damage, bounces);
        }

        @Override
        public double getMotionMultiplier() {
            return 3;
        }
    }

    public abstract static class BulletFlying extends EntityBullet {

        protected float destX, destY, destZ, sX, sY, sZ;
        protected double maxDistance;

        public BulletFlying(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float destX, float destY, float destZ, float damage) {
            super(posX, posY, posZ, world, shooter, renderColor, damage, 0);
            this.destX = destX;
            this.destY = destY;
            this.destZ = destZ;
            this.sX = posX;
            this.sY = posY;
            this.sZ = posZ;
            maxDistance = new Point3f(sX, sY, sZ).distance(new Point3f(destX, destY, destZ));
        }

        @Override
        public void updateEntity(World world) {
            float currentDistance = getPosition().distance(PositionHelper.getMiddle(new Point3f(sX, 0, sZ), new Point3f(destX, 0, destZ)));
            float sourceDistance = new Point3f(sX, 0, sZ).distance(PositionHelper.getMiddle(new Point3f(sX, 0, sZ), new Point3f(destX, 0, destZ)));
            posY = ((1 - (currentDistance / sourceDistance)) * sourceDistance);
            if (posY < 0) {
                kill(new KillReason.Void_Death());
            }
            super.updateEntity(world);
        }

        @Override
        public double getMotionMultiplier() {
            return 1;
        }

        @Override
        public boolean canBeCollidedWithWorld() {
            return true;
        }

        @Override
        public void onCollidedWithEntity(Entity entity) {
            kill(new KillReason.Void_Death());
        }

        @Override
        public boolean canBeCollidedWithEntity(Entity entity) {
            return super.canBeCollidedWithEntity(entity);
        }

        @Override
        public void onCollided(Collisions collidedWith, WorldBorder border, EnumSide side) {
            kill(new KillReason.Void_Death());
        }
    }

    public static class ArtilleryBullet extends BulletFlying {

        public ArtilleryBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float destX, float destY, float destZ, float damage) {
            super(posX, posY, posZ, world, shooter, renderColor, destX, destY, destZ, damage);
        }

        @Override
        public void onDeath(KillReason reason) {
            super.onDeath(reason);
            worldObj.addEntity(new EntityExplosion(posX - 5, posY, posZ - 5, worldObj, damage));
            worldObj.addEntity(new EntitySpriteExplosion(destX, destY, destZ, worldObj, 10, 10));

        }
    }

    public static class MineBullet extends BulletFlying {

        Entity owner;

        public MineBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float destX, float destY, float destZ, Entity owner, float damage) {
            super(posX, posY, posZ, world, shooter, renderColor, destX, destY, destZ, damage);
            this.owner = owner;
        }

        @Override
        public void onCollidedWithEntity(Entity entity) {
            if (entity == shooter) return;
            kill(new KillReason.Void_Death());
        }

        @Override
        public void onDeath(KillReason reason) {
            super.onDeath(reason);
            worldObj.addEntity(new EntitySpriteExplosion(getMid().x, getMid().y, getMid().z, worldObj, 5, 5));
            if (posY > 9) return;
            worldObj.addEntity(new EntityMine(getMid().x, 0, getMid().z, worldObj, owner));
        }

        @Override
        public float getWidth() {
            return 1;
        }

        @Override
        public float getHeight() {
            return 1;
        }

        @Override
        public float getDepth() {
            return 1;
        }
    }

    public static class ChasingBullet extends EntityBullet implements PathTraveller {

        protected Entity chasingEntity;

        protected final String TICK_TIMER_REFRESH_PATH_FINDING;

        public ChasingBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float damage, int bounces) {
            super(posX, posY, posZ, world, shooter, renderColor, damage, bounces);
            TICK_TIMER_REFRESH_PATH_FINDING = "REFRESH_PATH_FINDING" + hashCode();
            TickTimerHandler.registerTickTimer(30, TICK_TIMER_REFRESH_PATH_FINDING);

        }

        @Override
        public void initEntity() {
        }

        @Override
        public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
            super.renderEntity(batch, camera, environment);
        }

        @Override
        public void updateEntity(World world) {
            super.updateEntity(world);

            if (chasingEntity != null && chasingEntity.isDead) {
                chasingEntity = null;
            }
            if (chasingEntity == null) {
                if (shooter instanceof EntityEnemyTank) {
                    chasingEntity = worldObj.thePlayer;
                } else if (shooter instanceof EntityPlayer) {
                    Entity nearestEntity = null;
                    double distanceToNearest = -1;
                    for (Entity entity : worldObj.getEntitiesInRegion(Collision3D.createX2Y2Z2(posX - 75, posY - 75, posZ - 75, posX + 75, posY + 75, posZ + 75))) {
                        if (entity instanceof EntityEnemyTank) {
                            if (nearestEntity == null) {
                                nearestEntity = entity;
                                distanceToNearest = Math.sqrt(worldObj.thePlayer.getDistanceToEntitySq(entity));
                            } else if (Math.sqrt(worldObj.thePlayer.getDistanceToEntitySq(entity)) < distanceToNearest) {
                                nearestEntity = entity;
                                distanceToNearest = Math.sqrt(worldObj.thePlayer.getDistanceToEntitySq(entity));
                            }
                        }
                    }
                    chasingEntity = nearestEntity;
                }
            }
        }

        GraphPath<BasicNode> path;
        int currentStep;

        @Override
        public void move() {
            if (chasingEntity == null) {
                super.move();
                return;
            }
            if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_REFRESH_PATH_FINDING)) {
                path = PathHelper.findAStarPathEntityToOtherEntityOrAround(this, chasingEntity, 5);
                /*
                PathSmoother<BasicNode, Vector3> smoother = new PathSmoother<BasicNode, Vector3>(new RaycastCollisionDetector<Vector3>() {
                    @Override
                    public boolean collides(Ray<Vector3> ray) {
                        Vector3 start = new Vector3(ray.start.x, 1, ray.start.y);
                        Vector3 end = new Vector3(ray.end.x, 1, ray.end.y);
                        Vector3 direction = start.cpy().sub(end);
                        com.badlogic.gdx.math.collision.Ray rRay = new com.badlogic.gdx.math.collision.Ray(start, direction);
                        List<WorldBorder> borders = new ArrayList<WorldBorder>();
                        Point3f start3f = new Point3f(start.x, start.y, start.z);
                        Point3f end3f = new Point3f(end.x, end.y, end.z);
                        borders.addAll(worldObj.getCollisionsWithMaxDistance(start3f, start3f.distance(end3f)));
                        return RayTraceHelper.rayTrace(rRay, false, borders).isEmpty();
                    }

                    @Override
                    public boolean findCollision(Collision<Vector3> outputCollision, Ray<Vector3> inputRay) {
                        List<WorldBorder> coll = worldObj.getCollisionsWithMaxDistance(new Point3f(inputRay.start.x, inputRay.start.y, inputRay.start.z), (float) Math.sqrt(worldObj.getWidth() * worldObj.getWidth() + worldObj.getHeight() * worldObj.getHeight()));
                        if (!coll.isEmpty()) {
                            Collision3D collisionBox = coll.get(0).getCollisionBox();
                            outputCollision.set(new Vector3(collisionBox.x, collisionBox.y, collisionBox.z), new Vector3(collisionBox.x + collisionBox.width, collisionBox.y + collisionBox.height, collisionBox.z + collisionBox.depth));
                            return true;
                        }
                        return false;
                    }
                });
                path = new WorldIndexedSmoothableGraphPath(this.path);
                System.out.println(smoother.smoothPath((SmoothableGraphPath<BasicNode, Vector3>) path));

                */
                TickTimerHandler.resetTickTimer(TICK_TIMER_REFRESH_PATH_FINDING);
                currentStep = 1;
            }
            if (path != null && path.getCount() > currentStep) {
                BasicNode basicNode = path.get(currentStep);
                PathHelper.setMotionToStep(basicNode, this, 1.5f);
                if (getMid().distance(new Point3f(basicNode.x, posY + getHeight() / 2, basicNode.y)) < 2f) {
                    currentStep++;
                }
            } else {
                path = null;
            }
            super.move();
        }

        @Override
        public double getMotionMultiplier() {
            return 0.85;
        }

        @Override
        public long getMaxLifeTime() {
            return 250;
        }

        @Override
        public WorldIndexedGraph getGraphPath() {
            return worldObj.worldPathFinderGraphDistance_1u;
        }

        @Override
        public Collisions getHitBoxes() {
            return getCollisionBox();
        }

        @Override
        public float getTravellerWidth() {
            return getWidth();
        }

        @Override
        public float getTravellerDepth() {
            return getHeight();
        }

        @Override
        public Point3f getTravellerPosMid() {
            return getMid();
        }

        @Override
        public World getTravellerWorld() {
            return worldObj;
        }
    }

    public static class HeavyBullet extends EntityBullet {

        public HeavyBullet(float posX, float posY, float posZ, World world, Entity shooter, Color renderColor, float damage, int bounces) {
            super(posX, posY, posZ, world, shooter, renderColor, damage, bounces);
        }

        @Override
        public void onCollided(Collisions collidedWith, WorldBorder border, EnumSide side) {
            if (border.getBorderType() == WorldBorder.BorderType.BREAKABLE) {
                border.setMaxHitTimes(-1);
            } else {
                super.onCollided(collidedWith, border, side);
            }
        }

        @Override
        public double getMotionMultiplier() {
            return 1;
        }

        @Override
        public boolean diesAtCollisionBreakable() {
            return false;
        }

        @Override
        public float getWidth() {
            return super.getWidth() * 1.5f;
        }

        @Override
        public float getHeight() {
            return super.getHeight() * 1.5f;
        }

        @Override
        public float getDepth() {
            return super.getDepth() * 1.5f;
        }
    }
}

