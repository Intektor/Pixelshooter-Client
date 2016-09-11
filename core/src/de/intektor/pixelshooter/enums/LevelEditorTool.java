package de.intektor.pixelshooter.enums;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.render.RenderHelper;

/**
 * @author Intektor
 */
public enum LevelEditorTool {
    TOOL_SELECT(ImageStorage.pointer),
    TOOL_GRAB(ImageStorage.grab_cursor),
    TOOL_SQUARE_COLLISION(ImageStorage.collisionImage),
    TOOL_TRASH_CAN(ImageStorage.trashCan),
    TOOL_COPY(ImageStorage.copy_image),
    TOOL_SET_PLAYER(TankType.TANK_PLAYER),
    TOOL_SET_STANDARD_SHOOTER(TankType.TANK_STANDARD_ATTACKER),
    TOOL_SET_QUICK_ATTACKER(TankType.TANK_QUICK_SHOOTER),
    TOOL_SET_ARTILLERY_TANK(TankType.TANK_ARTILLERY),
    TOOL_SET_TRIPLE_ATTACKER(TankType.TANK_TRIPLE_ATTACKER),
    TOOL_SET_TANK_CHASE_SHOOTER(TankType.TANK_CHASE_SHOOTER),
    TOOL_SET_TANK_LASER_SHOOTER(TankType.TANK_LASER_SHOOTER),
    TOOL_SET_TANK_MINE_SHOOTER(TankType.TANK_MINE_SHOOTER),
    TOOL_SET_HEAVY_SHOOTER(TankType.TANK_HEAVY_SHOOTER);

    TankType tankType;

    Texture texture;

    LevelEditorTool(Texture texture) {
        this.texture = texture;
    }

    LevelEditorTool(TankType tankType) {
        this.tankType = tankType;
    }

    public TankType getTankType() {
        return tankType;
    }

    public void render(ShapeRenderer renderer, int x, int y, int width, int height) {
        if (texture != null) {
            SpriteBatch batch = PixelShooter.spriteBatch;
            batch.begin();
            batch.draw(texture, x, y, width, height);
            batch.end();
        } else {
            RenderHelper.renderTank2D(renderer, x, y, width, height, 0, tankType.tank, 0);
        }
    }
}
