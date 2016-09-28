package de.intektor.pixelshooter.game_state.worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.files.FileHelper;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder.FolderFile;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.levels.CampaignInformation.WorldInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter.world.WorldUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Intektor
 */
public class GuiWorldSelection extends Gui {

    final int BUTTON_BACK = 0, BUTTON_SELECT = 1, BUTTON_LEFT = 2, BUTTON_RIGHT = 3;

    List<CompactWorldInformation> worldList = new ArrayList<CompactWorldInformation>();
    Map<CompactWorldInformation, FrameBuffer> textureMap = new HashMap<CompactWorldInformation, FrameBuffer>();

    int currentWorld;

    @Override
    public void enterGui() {
        super.enterGui();
        File[] files = Gdx.files.local("c_worlds").file().listFiles();
        if (files != null) {
            for (File c_worlds : files) {
                if (c_worlds.isDirectory()) {
                    int worldID = Integer.parseInt(c_worlds.getName());
                    if (PixelShooter.campaign.getInformation(worldID) == null) {
                        PixelShooter.campaign.worlds.put(worldID, new WorldInformation(worldID, 30));
                    }
                    CompactWorldInformation e = loadWorld(worldID);
                    worldList.add(e);
                    EditingWorld world = e.folder.files.get(e.info.levelState).world;
                    FrameBuffer texture = WorldUtils.getPlayStateWorldTexture(world);
                    textureMap.put(e, texture);
                }
            }
        }
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case BUTTON_LEFT:
                currentWorld++;
                break;
            case BUTTON_RIGHT:
                currentWorld--;
                break;
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();

        batch.begin();
        Texture colorBufferTexture = textureMap.get(worldList.get(currentWorld)).getColorBufferTexture();
        TextureRegion region = new TextureRegion(colorBufferTexture);
        region.flip(false, true);
        batch.draw(region, width / 2 - 320, height / 2 - 180 + 40, 640, 360);

        RenderHelper.drawString(width / 2, height / 2 + 180 + 40 + (height - (height / 2 + 180 + 40)) / 2, "World-" + (currentWorld + 1), PixelShooter.unScaledPerfectPixel72, batch);

        batch.end();

        super.render(renderer, batch);
    }

    @Override
    public void update() {
        super.update();
        getButtonByID(BUTTON_LEFT).setShown(currentWorld != 0);
        getButtonByID(BUTTON_RIGHT).setShown(currentWorld != worldList.size() - 1);
    }

    @Override
    public int getID() {
        return PixelShooter.WORLD_SELECTION;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width, 60, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton(width / 2 - 320, height / 2 - 180 - 40, 640, 80, "Select", BUTTON_SELECT, true));
        int size = 100;
        int x = (width / 2 - 320) / 2 - size / 2;
        TextureRegion reg = new TextureRegion(ImageStorage.grayTriangle);
        reg.flip(true, false);
        componentList.add(new GuiButton(x, height / 2 - size / 2, size, size, BUTTON_LEFT, true, reg));
        componentList.add(new GuiButton(width - x - size, height / 2 - size / 2, size, size, BUTTON_RIGHT, true, ImageStorage.grayTriangle));
    }

    public CompactWorldInformation loadWorld(int worldID) {
        LevelFolder folder = new LevelFolder(String.format("World-%s", worldID));
        for (int i = 0; i < 30; i++) {
            FolderFile e = loadLevel(worldID, i - 1);
            if (e != null) {
                folder.files.add(e);
            }
        }
        return new CompactWorldInformation(PixelShooter.campaign.getInformation(worldID), folder);
    }

    public FolderFile loadLevel(int worldID, int id) {
        return FileHelper.getWorldFromFile(String.format("c_worlds/%s/Level%s.pssn", worldID, id));
    }

    public static class CompactWorldInformation {
        public final WorldInformation info;
        public final LevelFolder folder;

        public CompactWorldInformation(WorldInformation info, LevelFolder folder) {
            this.info = info;
            this.folder = folder;
        }
    }
}
