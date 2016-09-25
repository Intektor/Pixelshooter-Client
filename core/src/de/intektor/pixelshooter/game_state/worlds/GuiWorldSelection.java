package de.intektor.pixelshooter.game_state.worlds;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.files.FileHelper;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder.FolderFile;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.levels.CampaignInformation.WorldInformation;

/**
 * @author Intektor
 */
public class GuiWorldSelection extends Gui {

    final int BUTTON_BACK = 0, BUTTON_WORLD_1 = 1;

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case BUTTON_WORLD_1:
                PixelShooter.enterGui(PixelShooter.VIEW_CAMPAIGN_WORLD);
                ((GuiViewCampaignWorld) PixelShooter.getGuiByID(PixelShooter.VIEW_CAMPAIGN_WORLD)).setWorld(loadWorld(0));
                break;
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
    }

    @Override
    public int getID() {
        return PixelShooter.WORLD_SELECTION;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(width - 300, height - 60, 300, 60, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton(0, height - 60, 300, 60, "World 1", BUTTON_WORLD_1, true));
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
        return FileHelper.getWorldFromFile(String.format("assets/levels/world%s/Level%s.pssn", worldID, id));
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
