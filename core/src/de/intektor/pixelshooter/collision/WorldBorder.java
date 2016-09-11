package de.intektor.pixelshooter.collision;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityBullet;
import de.intektor.pixelshooter.entity.KillReason;
import de.intektor.pixelshooter.enums.EnumSide;
import de.intektor.pixelshooter.world.World;

/**
 * @author Intektor
 */
public class WorldBorder {

    private Collision3D collisionBox;

    protected BorderType borderType;

    /**
     * How often the collision, if breakable, can be hit without breaking
     */
    protected int maxHitTimes = 2;

    protected World world;

    public static final float STANDARD_HEIGHT = 5;

    public WorldBorder(Collision3D collisionBox) {
        this.collisionBox = collisionBox;
    }

    public WorldBorder(Collision3D collisionBox, BorderType borderType, World world) {
        this.collisionBox = collisionBox;
        this.borderType = borderType;
        this.world = world;
        init();
    }

    public WorldBorder(float x, float y, float z, float width, float depth, BorderType borderType, World world) {
        this(x, y, z, width, STANDARD_HEIGHT, depth, borderType, world);
    }

    public WorldBorder(float x, float y, float z, float width, float height, float depth, BorderType borderType, World world) {
        collisionBox = new Collision3D(x, y, z, Math.max(1, width), height, Math.max(1, depth));
        this.borderType = borderType;
        this.world = world;
        init();
    }

    public void init() {
        if (borderType != null) {
            Texture texture = world.backGroundType.getCollisionTexture(borderType);
            Model box = PixelShooter.modelBuilder.createBox(collisionBox.width, collisionBox.height, collisionBox.depth, new Material(TextureAttribute.createDiffuse(texture)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
            modelInstance = new ModelInstance(box, collisionBox.x + collisionBox.width / 2, collisionBox.y + collisionBox.height / 2, collisionBox.z + collisionBox.depth / 2);
        }
    }

    private ModelInstance modelInstance;

    public void renderCollision3D(ModelBatch batch, Camera camera, Environment environment) {
        batch.begin(camera);
        batch.render(modelInstance, environment);
        batch.end();
    }

    public Collision3D getCollisionBox() {
        return collisionBox;
    }

    public void setMaxHitTimes(int maxHitTimes) {
        this.maxHitTimes = maxHitTimes;
    }

    public void collidedWithEntity(Entity collided, EnumSide side) {
        if (collided.isDead) return;
        if (collided instanceof EntityBullet) {
            if (borderType == BorderType.BREAKABLE) {
                if (maxHitTimes > 0) {
                    maxHitTimes--;
                } else {
                    PixelShooter.PIXEL_SHOOTER_STATE.getWorld().borders.getBorders().remove(this);
                }
                if (((EntityBullet) collided).diesAtCollisionBreakable()) {
                    collided.kill(new KillReason.Collision_Destroyed(this));
                }
            }
        }
    }

    public BorderType getBorderType() {
        return borderType;
    }

    public static class Collision3DEntity extends WorldBorder {

        protected Entity owner;

        public Collision3DEntity(float x, float y, float z, float width, float height, float depth, Entity entity) {
            super(x, y, z, width, height, depth, null, entity.worldObj);
            this.owner = entity;
        }

        public Entity getOwner() {
            return owner;
        }

        @Override
        public String toString() {
            return super.toString() + "(" + owner + ")";
        }
    }

    public enum BorderType {
        UNBREAKABLE(ImageStorage.border_texture_wooden),
        BREAKABLE(ImageStorage.border_breakable_wooden);

        Texture texture;

        BorderType(Texture texture) {
            this.texture = texture;
        }

        public Texture getTexture() {
            return texture;
        }

        @Override
        public String toString() {
            return name();
        }
    }
}
