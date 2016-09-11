package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.enums.MovableObjects;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;

/**
 * @author Intektor
 */
public class MovableCollision extends MovableObject {

    public static final int collisionSize = 4;

    private boolean canBeSelected = true;

    protected WorldBorder.BorderType borderType = WorldBorder.BorderType.UNBREAKABLE;

    public MovableCollision() {
    }

    public MovableCollision(Collision2D collision, boolean canBeSelected) {
        super(collision, MovableObjects.MOVABLE_COLLISION);
        this.canBeSelected = canBeSelected;
    }

    public MovableCollision(Collision2D collision) {
        this(collision, true);
    }

    public WorldBorder.BorderType getBorderType() {
        return borderType;
    }

    @Override
    public boolean isResizeAble() {
        return true;
    }

    @Override
    public boolean canBeSelected() {
        return canBeSelected;
    }

    @Override
    public boolean canBeRemoved() {
        return canBeSelected;
    }

    @Override
    public boolean canBeCopied() {
        return true;
    }

    @Override
    public void writeToTag(PSTagCompound tag) {
        super.writeToTag(tag);
        type = MovableObjects.MOVABLE_COLLISION;
        tag.setBoolean("can_be_selected", canBeSelected);
        tag.setInteger("border_type", borderType.ordinal());
    }

    public void readFromFile(PSTagCompound tag) {
        super.readFromTag(tag);
        canBeSelected = tag.getBoolean("can_be_selected");
        borderType = WorldBorder.BorderType.values()[tag.getInteger("border_type")];
    }

    @Override
    public void render(ShapeRenderer renderer, Camera camera, EditingWorld world) {
        RenderHelper.renderCollision(renderer.getProjectionMatrix(), world.background, borderType, collision);
        if (isSelected)
            RenderHelper.renderSquare(renderer, Color.GREEN, collision.getX(), collision.getY(), collision.getWidth(), collision.getHeight());
    }

    public MovableObject copy() {
        MovableCollision movableCollision = new MovableCollision(collision.copy(), canBeSelected);
        movableCollision.borderType = borderType;
        return movableCollision;
    }

    @Override
    public boolean hasToFitStandardSize() {
        return true;
    }

    public void switchType() {
        switch (borderType) {
            case UNBREAKABLE:
                borderType = WorldBorder.BorderType.BREAKABLE;
                break;
            case BREAKABLE:
                borderType = WorldBorder.BorderType.UNBREAKABLE;
                break;
        }
    }
}
