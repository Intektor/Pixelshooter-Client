package de.intektor.pixelshooter.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.level.editor.MovableObject;

/**
 * @author Intektor
 */
public class WorldUtils {

    static OrthographicCamera rawWorldCamera;
    static SpriteBatch rawWorldSpriteBatch;
    static ShapeRenderer rawWorldShapeRenderer;


    static {
        rawWorldCamera = new OrthographicCamera(1280, 720);
        rawWorldSpriteBatch = new SpriteBatch();
        rawWorldShapeRenderer = new ShapeRenderer();
        rawWorldShapeRenderer.setAutoShapeType(true);
    }

    public static FrameBuffer getPlayStateWorldTexture(EditingWorld world) {
        return getPlayStateWorldTexture(world.convertToWorld());
    }

    public static FrameBuffer getPlayStateWorldTexture(World world) {
        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
        buffer.begin();
        ModelBatch batch = new ModelBatch();
        world.renderWorld(batch, true);
        buffer.end();
        batch.dispose();
        return buffer;
    }

    public static FrameBufferTextureRegion getLevelEditorTexture(EditingWorld world) {
        Graphics g = Gdx.graphics;
        FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGB565, g.getWidth(), g.getHeight(), false, true);

        buffer.begin();

        rawWorldCamera.zoom = Math.min(world.getWidth() + 100, world.getHeight() + 100) / Math.min(rawWorldCamera.viewportHeight, rawWorldCamera.viewportHeight);

        rawWorldCamera.position.set(world.getWidth() / 2, world.getHeight() / 2, 0);

        rawWorldCamera.update();

        rawWorldSpriteBatch.setProjectionMatrix(rawWorldCamera.combined);

        rawWorldSpriteBatch.begin();
        rawWorldSpriteBatch.draw(world.background.getBackgroundTexture(), -50, -50, world.getWidth() + 100, world.getHeight() + 100);
        rawWorldSpriteBatch.end();

        rawWorldShapeRenderer.setProjectionMatrix(rawWorldCamera.combined);

        rawWorldShapeRenderer.begin();

        for (MovableObject mObject : world.getMovableObjects()) {
            mObject.render(rawWorldShapeRenderer, rawWorldCamera, world);
        }

        rawWorldShapeRenderer.end();

        buffer.end();

        int rX = (g.getWidth() - g.getHeight()) / 2;

        TextureRegion region = new TextureRegion(buffer.getColorBufferTexture(), rX, 0, g.getHeight(), g.getHeight());

        region.flip(false, true);

        return new FrameBufferTextureRegion(buffer, region);
    }

    public static class FrameBufferTextureRegion {

        public final FrameBuffer buffer;
        public final TextureRegion texture;

        public FrameBufferTextureRegion(FrameBuffer buffer, TextureRegion texture) {
            this.buffer = buffer;
            this.texture = texture;
        }
    }
}
