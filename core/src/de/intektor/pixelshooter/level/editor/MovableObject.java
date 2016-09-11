package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.enums.MovableObjects;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.world.EditingWorld;

/**
 * @author Intektor
 *         A movable Object in level editor
 */
public abstract class MovableObject {

    protected Collision2D collision;

    MovableObjects type;

    public boolean isSelected;

    public MovableObject() {
    }

    public MovableObject(Collision2D collision, MovableObjects type) {
        this.collision = collision;
        this.type = type;
    }

    public abstract boolean isResizeAble();

    public abstract boolean canBeSelected();

    public abstract boolean canBeRemoved();

    public abstract boolean canBeCopied();

    public abstract void render(ShapeRenderer renderer, Camera camera, EditingWorld world);

    public void update() {
    }

    public void setSelected(boolean selected) {
        isSelected = selected && canBeSelected();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public enum SquareSides {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public abstract MovableObject copy();


    public float getX() {
        return collision.getX();
    }

    public float getY() {
        return collision.getY();
    }

    public float getWidth() {
        return collision.getWidth();
    }

    public float getHeight() {
        return collision.getHeight();
    }

    public void translate(float x, float y) {
        collision.translate(x, y);
    }

    public void setPosition(float x, float y) {
        collision.setPosition(x, y);
    }

    public boolean isPointInside(float x, float y) {
        return collision.collidingWith(new Collision2D(x, y, 0, 0));
    }

    public boolean collidingWith(MovableObject object) {
        return collision.collidingWith(object.collision);
    }

    public Collision2D getCollision2D() {
        return collision;
    }

    public abstract boolean hasToFitStandardSize();

    protected void writeToTag(PSTagCompound tag) {
        tag.setInteger("movable_object_type", type.ordinal());
        PSTagCompound collisionTag = new PSTagCompound();
        collision.writeToTag(collisionTag);
        tag.setTag("collision", collisionTag);
    }

    protected void readFromTag(PSTagCompound tag) {
        type = MovableObjects.values()[tag.getInteger("movable_object_type")];
        PSTagCompound collisionTag = tag.getTag("collision");
        collision = Collision2D.readFromTag(collisionTag);
    }

    public static Collision2D calculateValidSize(float x1, float y1, float x2, float y2) {
        int min = MovableCollision.collisionSize;
        int rx1 = MathHelper.getNextDivider(Math.round(x1), min);
        int ry1 = MathHelper.getNextDivider(Math.round(y1), min);
        int rx2 = MathHelper.getNextDivider(Math.round(x2), min);
        int ry2 = MathHelper.getNextDivider(Math.round(y2), min);
        Collision2D rect = Collision2D.createX2Y2(rx1, ry1, rx2, ry2);
        return rect.getWidth() > 0 && rect.getHeight() > 0 ? rect : null;
    }

    public MovableObjects getType() {
        return type;
    }
}
