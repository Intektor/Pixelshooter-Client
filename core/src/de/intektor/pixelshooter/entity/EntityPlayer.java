package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.enums.BulletType;
import de.intektor.pixelshooter.helper.ColorHelper;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.score.object.HealthLostScore;
import de.intektor.pixelshooter.world.World;


/**
 * @author Intektor
 */
public class EntityPlayer extends Entity implements Tank {

    public BulletType bulletType;

    public boolean keepMotion;

    public int shotsBeforeCooldown, cooldownInTicks;

    public int amtOfBullets, fieldOfShooting, bulletBounces;

    public float damage;

    private Model modelTank, modelUpperTank, modelBarrel;

    public EntityPlayer(float posX, float posY, World world, BulletType bulletType, float damage, float health, int shotsBeforeCooldown, int cooldownInTicks, int amtOfBullets, int fieldOfShooting, int bulletBounces, float speed) {
        super(posX, posY, world);
        this.bulletType = bulletType;
        this.motionMultiplier = speed;
        this.shotsBeforeCooldown = shotsBeforeCooldown;
        this.cooldownInTicks = cooldownInTicks;
        this.amtOfBullets = amtOfBullets;
        this.fieldOfShooting = fieldOfShooting;
        this.bulletBounces = bulletBounces;
        this.health = health;
        this.damage = damage;
        modelTank = PixelShooter.modelBuilder.createBox(getWidth(), getHeight() / 5f * 2f, getDepth() / 6f * 5f, new Material(ColorAttribute.createDiffuse(getTankColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelUpperTank = PixelShooter.modelBuilder.createBox(getWidth() / 2, getHeight() / 2, getDepth() / 2, new Material(ColorAttribute.createDiffuse(getTankColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelBarrel = PixelShooter.modelBuilder.createBox(2, 2, getBarrelLength(), new Material(ColorAttribute.createDiffuse(getBarrelColor())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
        RenderHelper.renderTank3D(batch, camera, this, environment, this);
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
        if (!keepMotion) motionX = motionY = motionZ = 0;
    }

    @Override
    public void laserHit(EntityLaser laser, float charge, float maxCharge, float damage) {
        super.laserHit(laser, charge, maxCharge, damage);
        opacity = health / getMaxHealth() * 2;
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
    public void damage(DamageSource source) {
        super.damage(source);
        worldObj.scoreObjects.add(new HealthLostScore(source.damage));
        Gdx.input.vibrate(200);
    }

    @Override
    public boolean canBeCollidedWithEntity(Entity entity) {
        return !((entity instanceof EntityBullet) && ((EntityBullet) entity).shooter == this);
    }

    @Override
    public float getMaxHealth() {
        return 3;
    }

    @Override
    public Color getTankColor() {
        return ColorHelper.toColor111(34, 139, 34, 1);
    }

    @Override
    public Color getBarrelColor() {
        return Color.GREEN;
    }

    @Override
    public float getBarrelLength() {
        return 10f;
    }

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
    public void kill(KillReason reason) {
        super.kill(reason);
    }

    @Override
    public boolean isStandardBulletShooter() {
        return true;
    }

    @Override
    public int getBulletBounces() {
        return bulletBounces;
    }
}
