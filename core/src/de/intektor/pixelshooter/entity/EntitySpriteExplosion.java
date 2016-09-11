package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.world.World;

/**
 * @author Intektor
 */
public class EntitySpriteExplosion extends Entity {

    protected final float width, height;

    public EntitySpriteExplosion(float posX, float posY, float posZ, World world, float width, float height) {
        super(posX, posY, posZ, world);
        this.width = width;
        this.height = height;
    }

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
        Decal decal = Decal.newDecal(getWidth(), getHeight(), new TextureRegion(ImageStorage.tankExplosion), true);
        decal.setPosition(posX, posY + 3, posZ);
        decal.lookAt(camera.position, camera.up);
        worldObj.decalBatch.add(decal);
    }

    @Override
    public float getWidth() {
        return (long) width;
    }

    @Override
    public float getHeight() {
        return (long) height;
    }

    @Override
    public float getDepth() {
        return 1;
    }

    @Override
    public boolean canBeCollidedWithEntity(Entity entity) {
        return false;
    }

    @Override
    public float getMaxHealth() {
        return 1;
    }

    @Override
    public long getMaxLifeTime() {
        return 5;
    }
}
