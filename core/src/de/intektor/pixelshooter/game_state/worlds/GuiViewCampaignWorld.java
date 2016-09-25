package de.intektor.pixelshooter.game_state.worlds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.game_state.GuiPlayState;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.game_state.worlds.GuiWorldSelection.CompactWorldInformation;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.GuiScrollBar;
import de.intektor.pixelshooter.gui.GuiScrollBar.Direction;
import de.intektor.pixelshooter.levels.WorldPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.WorldUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Intektor
 */
public class GuiViewCampaignWorld extends Gui {

    final int BUTTON_BACK = 0;

    int scrollAmount;

    GuiScrollBar scrollBar;

    CompactWorldInformation worldInfo;
    Map<LevelFolder.FolderFile, FrameBuffer> textureMap = new HashMap<LevelFolder.FolderFile, FrameBuffer>();

    @Override
    public void onButtonTouched(int id) {
        if (id == BUTTON_BACK) {
            PixelShooter.enterGui(PixelShooter.WORLD_SELECTION);
            return;
        }
        startLevel(id - 1);
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        if (componentList.size() == 0) return;
        int x = -scrollAmount + width / 2 - 408 / 2;
        int i = 1;
        for (LevelFolder.FolderFile file : worldInfo.folder.files) {
            renderer.begin();
            renderer.setColor(Color.WHITE);
            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.rect(x, height / 2 + 140, 408, 40);
            renderer.end();

            batch.begin();
            RenderHelper.drawString(x + 408 / 2, height / 2 + 140 + 20, String.format("Level-%s", i), PixelShooter.unScaledPerfectPixel32, batch);
            Texture texture1;
            if (worldInfo.info.levelState >= i - 1) {
                texture1 = textureMap.get(file).getColorBufferTexture();
            } else {
                texture1 = ImageStorage.main_menu_wooden;
            }
            TextureRegion region = new TextureRegion(texture1);
            region.flip(false, worldInfo.info.levelState >= i - 1);
            batch.draw(region, x, height / 2 - 140, 408, 280);
            batch.end();

            if (worldInfo.info.levelState < i - 1) {
                batch.begin();
                RenderHelper.drawString(x + 408 / 2, height / 2, "?", PixelShooter.unScaledPerfectPixel128, batch);
                batch.end();
            }

            getButtonByID(i).setX(x);
            getButtonByID(i).setY(height / 2 - 140 - 50);

            x += 458;
            i++;
        }
        x = -scrollAmount + width / 2 - 408 / 2;
        i = 1;
        super.render(renderer, batch);
        for (LevelFolder.FolderFile file : worldInfo.folder.files) {
            batch.begin();
            Texture texture = null;
            switch (worldInfo.info.getLevel(i - 1).medal) {
                case NONE:
                    texture = null;
                    break;
                case BRONZE:
                    texture = ImageStorage.bronze_medal;
                    break;
                case SILVER:
                    texture = ImageStorage.silver_medal;
                    break;
                case GOLD:
                    texture = ImageStorage.gold_medal;
                    break;
            }
            if (texture != null) {
                float width = 400 / 2.5f;
                float height = 300 / 2.5f;
                batch.draw(texture, x + 408 - width / 2, this.height / 2 - 140 - height / 2, width, height);
            }
            batch.end();
            x += 458;
            i++;
        }
    }

    @Override
    public void update() {
        if (scrollBar.justScrolled) {
            scrollAmount = (int) ((worldInfo.folder.files.size() * 458 - width / 2 + 204 - 20) * (1 - scrollBar.getScrollPercent()));
        }
        super.update();
    }

    @Override
    public int getID() {
        return PixelShooter.VIEW_CAMPAIGN_WORLD;
    }

    @Override
    public void addGuiComponents() {
        if (worldInfo == null) return;
        componentList.add(new GuiButton(width / 2 - 200, 0, 400, 100, "Back", BUTTON_BACK, true));
        scrollBar = new GuiScrollBar(0, height - 50, width, 50, true, Direction.HORIZONTAL, worldInfo.folder.files.size() * 458 - width / 2 + 204 - 20, width);
        componentList.add(scrollBar);
        int id = BUTTON_BACK + 1;
        List<LevelFolder.FolderFile> files = worldInfo.folder.files;
        for (int i = 0; i < files.size(); i++) {
            componentList.add(new GuiButton(0, 0, 408, 50, "Play!", id, true));
            id++;
        }
    }

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        super.pointerDragged(x, y, prevX, prevY, pointer);
        if (hoveredOverComponent(x, y) || scrollBar.currentlyClicked) return;
        scrollAmount -= input.getDeltaX(pointer);
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        int i = worldInfo.folder.files.size() * 458 - width / 2 + 204 - 20;
        if (scrollAmount > i) {
            scrollAmount = i;
        }
        scrollBar.setScrollPercent(scrollAmount / (float) (worldInfo.folder.files.size() * 458 - width / 2 + 204 - 20));
    }

    public void setWorld(CompactWorldInformation info) {
        this.worldInfo = info;
        for (LevelFolder.FolderFile file : info.folder.files) {
            textureMap.put(file, WorldUtils.getPlayStateWorldTexture(file.world));
        }
        reInitButtons();
    }

    public void startLevel(int levelID) {
        GuiPlayState playState = (GuiPlayState) PixelShooter.getGuiByID(PixelShooter.PLAY_STATE);
        playState.setPlayInformation(new WorldPlayInformation(worldInfo.info.worldID, levelID));
        playState.setTheWorld(worldInfo.folder.files.get(levelID).world);
        playState.setStart(true);
        PixelShooter.enterGui(PixelShooter.PLAY_STATE);
    }

    @Override
    public void exitGui() {
        for (FrameBuffer frameBuffer : textureMap.values()) {
            frameBuffer.dispose();
        }
    }
}
