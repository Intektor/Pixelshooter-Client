package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter.game_state.community_levels.ConnectToMainServerGui;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.GuiScrollBar;
import de.intektor.pixelshooter.levels.BasicPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Intektor
 */
public class UserLevelsFolder extends Gui {

    int scrollAmount;
    GuiScrollBar bar;
    private LevelFolder folder;

    @Override
    public void init() {
        super.init();
        if (folder.files.isEmpty()) {
            PixelShooter.enterGui(PixelShooter.USER_LEVEL_OVERVIEW);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (int i = 0; i < folder.files.size(); i++) {
            EditingWorld edit = folder.files.get(i).world;
            if (edit.timeSaved != null) {
                batch.begin();
                RenderHelper.drawString(615, scrollAmount + height - (i * 40 * 2 + 15), format.format(edit.timeSaved), PixelShooter.unScaledPerfectPixel12, batch, false);
                batch.end();
            }
        }
    }

    @Override
    public void update() {
        scrollAmount = (int) (bar.getScrollPercent() * (folder.files.size() * 40 * 2 - 40 * 2 * (720 / (40 * 2))));
        if (reInitNextUpdate) {
            scrollAmount = 0;
            reInitButtons();
            reInitNextUpdate = false;
        }
        for (int i = 0; i < folder.files.size(); i++) {
            int buttonID = i * 4 + 2;
            GuiButton button1 = getButtonByID(buttonID);
            GuiButton button2 = getButtonByID(buttonID + 1);
            GuiButton button3 = getButtonByID(buttonID + 2);
            GuiButton button4 = getButtonByID(buttonID + 3);

            button1.setX(0);
            button1.setY((int) (scrollAmount + height - (i + 1) * 40 * 2));

            button2.setX(button1.getWidth());
            button2.setY((int) (scrollAmount + height - ((i + 1) * 40 * 2)));

            button3.setX(width - button3.getWidth() - button4.getWidth() - 50);
            button3.setY((int) (scrollAmount + height - ((i + 1) * 40 * 2)));
            button3.enabled = PixelShooter.googleAccount != null;

            button4.setX(width - button4.getWidth() - 50);
            button4.setY((int) (scrollAmount + height - ((i + 1) * 40 * 2)));
        }
        super.update();
    }

    public boolean reInitNextUpdate = false;

    @Override
    public void onButtonTouched(int id) {
        if (id == 0) {
            PixelShooter.enterGui(PixelShooter.USER_LEVEL_OVERVIEW);
        } else if (id == 1) {
            reInitNextUpdate = true;
        } else {
            int rID = id - 2;
            int playID = rID / 4;
            int localID = rID % 4;
            switch (localID) {
                case 0:
                    if (folder.files.get(playID).world.checkConvert()) {
                        PixelShooter.PIXEL_SHOOTER_STATE.setTheWorld(folder.files.get(playID).world);
                        PixelShooter.PIXEL_SHOOTER_STATE.setPlayInformation(new BasicPlayInformation(PlayStateStatus.USER_LEVEL));
                        PixelShooter.PIXEL_SHOOTER_STATE.setStart(true);
                        PixelShooter.enterGui(PixelShooter.PLAY_STATE);
                    }
                    break;
                case 1:
                    PixelShooter.LEVEL_EDITOR_STATE.setEdit(folder.files.get(playID).world);
                    PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
                    break;
                case 2:
                    PixelShooter.enterGui(PixelShooter.CONNECT_TO_MAIN_SERVER);
                    ((ConnectToMainServerGui) PixelShooter.getGuiByID(PixelShooter.CONNECT_TO_MAIN_SERVER)).setInfo(PixelShooter.FINISH_LEVEL_TO_PUBLISH_LEVEL, getID());
                    ((FinishLevelToPublishLevel) PixelShooter.getGuiByID(PixelShooter.FINISH_LEVEL_TO_PUBLISH_LEVEL)).setFile(folder, playID);
                    break;
                case 3:
                    PixelShooter.enterGui(PixelShooter.SURE_DELETE_FOLDER_FILE);
                    ((SureDeleteLevel) PixelShooter.getGuiByID(PixelShooter.SURE_DELETE_FOLDER_FILE)).setFile(folder, playID);
                    break;

            }
        }
    }

    @Override
    public int getID() {
        return PixelShooter.USER_LEVELS_FOLDER;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(1280 - 250 - 75 * 2, 0, 75 * 2, 30 * 2, "Back", 0, true));
        bar = new GuiScrollBar(width - 50, 0, 50, height, true, GuiScrollBar.Direction.VERTICAL, (int) (folder.files.size() * 40 * 2), (int) (height - 75 * 2));
        componentList.add(bar);
        int id = 2;
        for (LevelFolder.FolderFile file : folder.files) {
            componentList.add(new GuiButton(0, 0, 300, 40 * 2, "Play: " + file.world.getName(), id++, true));
            componentList.add(new GuiButton(0, 0, 300, 40 * 2, "Edit: " + file.world.getName(), id++, true));
            componentList.add(new GuiButton(0, 0, 100, 40 * 2, "Publish!", id++, true));
            componentList.add(new GuiButton(0, 0, 100, 40 * 2, "Delete!", id++, true));
        }
    }

    public void setLevelFolder(LevelFolder folder) {
        Collections.sort(folder.files, new Comparator<LevelFolder.FolderFile>() {
            @Override
            public int compare(LevelFolder.FolderFile o1, LevelFolder.FolderFile o2) {
                if (o1.world.timeSaved != null && o2.world.timeSaved != null) {
                    return -o1.world.timeSaved.compareTo(o2.world.timeSaved);
                } else {
                    return 0;
                }
            }
        });
        this.folder = folder;
    }
}
