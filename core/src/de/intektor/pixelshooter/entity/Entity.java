package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.collision.Collision3D;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.enums.EnumSide;
import de.intektor.pixelshooter.sound.SoundHelper;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Intektor
 */
public abstract class Entity {

    public float posX, posY, posZ, motionX, motionY, motionZ, motionMultiplier = 1.4f;

    public float prevX, prevY, prevZ;

    public float distanceTravelled, opacity = 1f;

    public boolean isUp, isDown, isLeft, isRight, isForward, isBackward, canDie = true, forceMotionCheck = false;

    protected WorldBorder.Collision3DEntity hitBox;
    protected Collisions collisionBox;

    public World worldObj;

    protected long lifeTime;

    public boolean isDead = false;

    private float lookRotation, baseRotation;
    public float lookSin;
    public float lookCos;

    protected float health;

    /**
     * Causes Confusion, it is actually the 2D edition after adding 3D.
     * This actually supports only 2 vars, the x and the z to be used, and set the y to a standard of 0
     * <p>
     * The problem is that I've added 3D afterwards, and now in the Level Editor the x and y are equivalent to 3D x 0 y what means x: x; y; 0; z: y
     * <p>
     * The level editors y is calculated by libgdx from the bottom of the screen, so we have to subtract the worlds height by the y what actually is the z
     * </p>
     * </p>
     *
     * @param posX  the x
     * @param posY  the z
     * @param world the worlds
     */
    public Entity(float posX, float posY, World world) {
        this(posX, 0, world != null ? world.getHeight() - posY : posY, world);
    }

    public Entity(float posX, float posY, float posZ, World world) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        worldObj = world;
        hitBox = new WorldBorder.Collision3DEntity(posX, posY, posZ, getWidth(), getHeight(), getDepth(), this);
        collisionBox = new Collisions(hitBox);
        health = getMaxHealth();
        initEntity();
    }

    public void initEntity() {
        setLookRotation(0.00);
    }

    public void updateEntity(World world) {
        prevX = posX;
        prevY = posY;
        prevZ = posZ;
        //Add one tick to the life time
        lifeTime++;

        if (lifeTime > getMaxLifeTime()) {
            kill(new KillReason.Time_Limit());
        }

        move();
        hitBox.getCollisionBox().setPosition(posX, posY, posZ);

        distanceTravelled += new Point3f(prevX, prevY, prevZ).distanceSquared(getPosition());
    }

    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
    }

    public void move() {
        if (motionX == 0 && motionY == 0 && motionZ == 0 && !isUp && !isDown && !isLeft && !isRight && !isForward && !isBackward)
            return;

        motionY = isUp ? 1 : isDown ? -1 : motionY;
        motionX = isLeft ? -1 : isRight ? 1 : motionX;
        motionZ = isForward ? -1 : isBackward ? 1 : motionZ;

        boolean movementHorizontal = isLeft || isRight;
        boolean movementVertical = isForward || isBackward;

        if (movementHorizontal && movementVertical) {
            motionX /= 2;
            motionZ /= 2;
        }

        setBaseRotation((float) Math.atan2(motionZ, motionX));

        //If we used the fields motion they would multiply themselves
        float motionX = this.motionX * motionMultiplier;
        float motionY = this.motionY * motionMultiplier;
        float motionZ = this.motionZ * motionMultiplier;

        //Box for checking the x axis
        Collision3D copyX = hitBox.getCollisionBox().copy();
        copyX.setPosition(posX + motionX, posY, posZ);

        //Box for checking the y axis
        Collision3D copyY = hitBox.getCollisionBox().copy();
        copyY.setPosition(posX, posY + motionY, posZ);

        //Box for checking the z axis
        Collision3D copyZ = hitBox.getCollisionBox().copy();
        copyZ.setPosition(posX, posY, posZ + motionZ);

        //Check for collisions separately, so movement on edges is still possible
        WorldBorder collidingWallX = canBeCollidedWithWorld() ? copyX.collidingWith(worldObj.borders) : null;
        WorldBorder collidingWallY = canBeCollidedWithWorld() ? copyY.collidingWith(worldObj.borders) : null;
        WorldBorder collidingWallZ = canBeCollidedWithWorld() ? copyZ.collidingWith(worldObj.borders) : null;

        //Same with entity collisions
        boolean collidingEntityX = false;
        boolean collidingEntityY = false;
        boolean collidingEntityZ = false;

        //Only loop one time through the EntityList
        for (int i = 0; i < worldObj.getEntityList().size(); i++) {
            Entity entity = worldObj.getEntityList().get(i);
            boolean localCheckX = false;
            boolean localCheckY = false;
            boolean localCheckZ = false;
            if (entity != this && canBeCollidedWithEntity(entity) && entity.canBeCollidedWithEntity(this)) {
                if (copyX.collidingWith(entity.hitBox.getCollisionBox())) {
                    localCheckX = true;
                }
                if (copyY.collidingWith(entity.hitBox.getCollisionBox())) {
                    localCheckY = true;
                }
                if (copyZ.collidingWith(entity.hitBox.getCollisionBox())) {
                    localCheckZ = true;
                }

                if (localCheckX || localCheckY || localCheckZ) {
                    collidingEntityX = localCheckX || collidingEntityX;
                    collidingEntityY = localCheckY || collidingEntityY;
                    collidingEntityZ = localCheckZ || collidingEntityZ;

                    onCollidedWithEntity(entity);
                }
            }
        }

        EnumSide side;
        //Tell the entity it got collided in x
        if (collidingWallX != null) {
            side = motionX < 0 ? EnumSide.LEFT : EnumSide.RIGHT;
            collidingWallX.collidedWithEntity(this, side);
            onCollided(worldObj.borders, collidingWallX, side);
        }
        //Tell the entity it got collided in y
        if (collidingWallY != null) {
            side = motionY < 0 ? EnumSide.UP : EnumSide.DOWN;
            collidingWallY.collidedWithEntity(this, side);
            onCollided(worldObj.borders, collidingWallY, side);
        }
        //Tell the entity it got collided in z
        if (collidingWallZ != null) {
            side = motionZ < 0 ? EnumSide.FRONT : EnumSide.BACK;
            collidingWallZ.collidedWithEntity(this, motionZ < 0 ? EnumSide.FRONT : EnumSide.BACK);
            onCollided(worldObj.borders, collidingWallZ, side);
        }

        if (collidingWallX == null && !collidingEntityX) {
            posX += motionX;
        }
        if (collidingWallY == null && !collidingEntityY) {
            posY += motionY;
        }
        if (collidingWallZ == null && !collidingEntityZ) {
            posZ += motionZ;
        }
    }

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract float getDepth();

    public void resetMotion() {
        motionX = 0;
        motionY = 0;
    }

    public void kill(KillReason reason) {
        if (canDie) {
            onDeath(reason);
            isDead = true;
        }
    }

    public void onDeath(KillReason reason) {

    }

    public void revive() {
        isDead = false;
        lifeTime = 0;
    }

    public long getMaxLifeTime() {
        return 1000000L;
    }

    public void onCollided(Collisions collidedWith, WorldBorder border, EnumSide side) {

    }

    public void onCollidedWithEntity(Entity entity) {

    }

    public boolean canBeCollidedWithEntity(Entity entity) {
        return true;
    }

    public void setPosition(float x, float y, float z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    /**
     * @return the distance as a square
     */
    public float getDistanceToEntitySq(Entity entity) {
        return getDistanceToSq(entity.posX, entity.posY, entity.posZ);
    }

    public float getDistanceToSq(float x, float y, float z) {
        float dX = posX - x;
        float dY = posY - y;
        float dZ = posZ - z;
        return dX * dX + dY * dY + dZ * dZ;
    }

    public Collisions getCollisionBox() {
        return collisionBox;
    }

    public WorldBorder.Collision3DEntity getHitBox() {
        return hitBox;
    }

    public boolean isHorizontal() {
        return isUp || isDown;
    }

    public boolean isVertical() {
        return isLeft || isRight;
    }

    public void setWorldObj(World worldObj) {
        this.worldObj = worldObj;
    }

    public void damage(DamageSource source) {
        float prevHealth = this.health;
        this.health -= source.damage;
        if (health <= 0) {
            kill(new KillReason.Health_Zero(source.damage, prevHealth, source.damager));
        }
        SoundHelper.playHitSound(worldObj.thePlayer, this);
    }

    /**
     * Called when the Entity is hit by a laser
     *
     * @param laser     the laser
     * @param charge    the current charge
     * @param maxCharge the max charge
     * @param damage    how much damage is dealt to the entity
     */
    public void laserHit(EntityLaser laser, float charge, float maxCharge, float damage) {
        damage(new DamageSource(damage, laser));
    }

    /**
     * Called when a laser hits an entity an it wasn't hit by the laser the tick before
     *
     * @param laser     the laser
     * @param charge    the current laser charge
     * @param maxCharge the laser's max charge
     * @param damage    the damage dealt
     */
    public void laserHitStart(EntityLaser laser, float charge, float maxCharge, float damage) {

    }

    /**
     * Called when the entity was hit by a laser the tick before and isn't hit this tick
     */
    public void laserHitStop(EntityLaser laser) {

    }

    public abstract float getMaxHealth();

    public boolean canBeCollidedWithWorld() {
        return true;
    }

    public Point3f getPosition() {
        return new Point3f(posX, posY, posZ);
    }

    public Point2f getPosition2D() {
        return new Point2f(posX, posZ);
    }

    public Vector3 getPosition3() {
        return new Vector3(posX, posY, posZ);
    }

    public Point3f getMid() {
        return new Point3f(posX + getWidth() / 2, posY + getHeight() / 2, posZ + getDepth() / 2);
    }

    public float getHealth() {
        return health;
    }

    public void setLookRotation(double lookRotation) {
        this.lookRotation = (float) (lookRotation);
        lookSin = (float) Math.sin(lookRotation);
        lookCos = (float) Math.cos(lookRotation);
    }

    public float getLookRotation() {
        return lookRotation;
    }

    public Vector3 getLookVector3() {
        return new Vector3((float) (lookCos * (Math.PI / 2)), 0, (float) (lookSin * (Math.PI / 2)));
    }

    public Vector3f getLookVector3f() {
        return new Vector3f((float) (lookCos * (Math.PI / 2)), 0, (float) (lookSin * (Math.PI / 2)));
    }

    public float getBaseRotation() {
        return baseRotation;
    }

    public void setBaseRotation(float baseRotation) {
        this.baseRotation = baseRotation;
    }


    public Ray getLookRay() {
        return new Ray(getPosition3(), getLookVector3());
    }

    public boolean collidesWithPathFinder() {
        return false;
    }

    public void lookAt(float x, float z) {
        setLookRotation(Math.atan2(-(getMid().z - z), -(getMid().x - x)));
    }
}
