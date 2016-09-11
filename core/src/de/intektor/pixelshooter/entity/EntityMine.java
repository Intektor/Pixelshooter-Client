package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.world.World;

import java.util.List;

/**
 * @author Intektor
 */
public class EntityMine extends Entity {

    Entity owner;

    public EntityMine(float posX, float posY, float posZ, World world, Entity owner) {
        super(posX, posY, posZ, world);
        this.owner = owner;
        model = PixelShooter.modelBuilder.createBox(1.5f, 1.5f, 1.5f, new Material(ColorAttribute.createDiffuse(owner instanceof EntityPlayer ? Color.GREEN : Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    private Model model;

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {
        batch.begin(camera);
        ModelInstance instance = new ModelInstance(model, posX, 0.2f, posZ);
        batch.render(instance);
        batch.end();
    }

    @Override
    public void move() {
        List<Entity> entity = worldObj.getEntitiesAt(posX, posY, posZ);
        if (!entity.isEmpty() && !entity.contains(owner)) {
            kill(new KillReason.Suicide());
        }
    }

    @Override
    public void onDeath(KillReason reason) {
        super.onDeath(reason);
        worldObj.addEntity(new EntityExplosion(posX, posY, posZ, worldObj, 1));
    }

    @Override
    public void onCollidedWithEntity(Entity entity) {
        worldObj.addEntity(new EntityExplosion(posX, posY, posZ, worldObj, 1));
    }

    @Override
    public boolean canBeCollidedWithEntity(Entity entity) {
        return false;
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public float getDepth() {
        return 1;
    }

    @Override
    public float getMaxHealth() {
        return 0;
    }

    @Override
    public long getMaxLifeTime() {
        return 400;
    }
}
