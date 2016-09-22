package de.intektor.pixelshooter.game_state.worlds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.game_state.worlds.GuiWorldSelection.CompactWorldInformation;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Intektor
 */
public class GuiViewCampaignWorld extends Gui {

    final int BUTTON_BACK = 0;

    int scrollAmount;

    CompactWorldInformation worldInfo;
    Map<LevelFolder.FolderFile, Texture> textureMap = new HashMap<LevelFolder.FolderFile, Texture>();

    @Override
    public void onButtonTouched(int id) {
        if (id == BUTTON_BACK) {
            PixelShooter.enterGui(PixelShooter.WORLD_SELECTION);
            return;
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        if (componentList.size() == 0) return;
        int x = -scrollAmount;
        int i = 1;
        for (LevelFolder.FolderFile file : worldInfo.folder.files) {
            renderer.begin();
            renderer.setColor(Color.WHITE);
            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.rect(x, height / 2 + 140, 408, 40);
            renderer.end();
            batch.begin();
            RenderHelper.drawString(x + 408 / 2, height / 2+ 140 + 20, String.format("Level-%s", i), PixelShooter.unScaledPerfectPixel32, batch);
            TextureRegion region = new TextureRegion(textureMap.get(file));
            region.flip(false, true);
            batch.draw(region, x, height / 2 - 140, 408, 280);
            batch.end();
            getButtonByID(i).setX(x);
            getButtonByID(i).setY(height / 2 - 140 - 50);
            x += 458;
            i++;
        }
        super.render(renderer, batch);
    }

    @Override
    public void update() {

        super.update();
    }

    @Override
    public int getID() {
        return PixelShooter.VIEW_CAMPAIGN_WORLD;
    }

    @Override
    public void addGuiComponents() {
        if (worldInfo == null) return;
        componentList.add(new GuiButton(width / 2 - 100, 0, 200, 50, "Back", BUTTON_BACK, true));
        int id = BUTTON_BACK + 1;
        for (LevelFolder.FolderFile file : worldInfo.folder.files) {
            componentList.add(new GuiButton(0, 0, 408, 50, "Play!", id, true));
            id++;
        }
    }

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        scrollAmount -= input.getDeltaX(pointer);
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        if (scrollAmount > height / 6 * height / 6 - height) {
            scrollAmount = height / 6 * height / 6 - height;
        }
    }

    public void setWorld(CompactWorldInformation info) {
        this.worldInfo = info;
        for (LevelFolder.FolderFile file : info.folder.files) {
            World world = file.world.convertToWorld();
            world.updateWorld();
            ModelBatch batch = new ModelBatch();
            FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGB565, width, height, false, true);
            buffer.begin();
            world.renderWorld(batch, false);
            buffer.end();
            textureMap.put(file, buffer.getColorBufferTexture());
            batch.dispose();
        }
        reInitButtons();
    }

    public void startLevel(int levelID) {

    }
}
