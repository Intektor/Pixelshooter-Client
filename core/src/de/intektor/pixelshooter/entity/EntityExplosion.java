package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import de.intektor.pixelshooter.collision.Collision3D;
import de.intektor.pixelshooter.world.World;

import java.util.List;

/**
 * @author Intektor
 */
public class EntityExplosion extends Entity {

    float damage;

    public EntityExplosion(float posX, float posY, float posZ, World world, float damage) {
        super(posX, posY, posZ, world);
        this.damage = damage;
        forceMotionCheck = true;
    }

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
    }

    @Override
    public void updateEntity(World world) {
        if (lifeTime == 0) {
            List<Entity> entitiesInRegion = world.getEntitiesInRegion(new Collision3D(posX - getWidth(), posY - getHeight(), posZ - getDepth(), getWidth() * 2, getHeight() * 2, getDepth() * 2));
            for (Entity entity : entitiesInRegion) {
                entity.damage(new DamageSource(damage, this));
            }
        }
        kill(new KillReason.Suicide());
        super.updateEntity(world);
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
    public long getMaxLifeTime() {
        return 0;
    }

    @Override
    public float getMaxHealth() {
        return 1;
    }
}
