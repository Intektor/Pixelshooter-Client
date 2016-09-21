package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.world.EditingWorld;

import java.io.*;
import java.util.*;

/**
 * @author Intektor
 */
public class UserLevelsOverview extends Gui {

    public int scrollAmount;

    public List<LevelFolder> folders = new ArrayList<LevelFolder>();
    boolean reInitNextUpdate = false;

    final int BUTTON_REFRESH = 0, BUTTON_BACK = 1;

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                return;
            case BUTTON_REFRESH:
                reInitNextUpdate = true;
                return;
        }
        ((UserLevelsFolder) PixelShooter.getGuiByID(PixelShooter.USER_LEVELS_FOLDER)).setLevelFolder(folders.get(id - 2));
        PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
    }

    @Override
    public void update() {
        if (reInitNextUpdate) {
            scrollAmount = 0;
            reInitButtons();
            reInitNextUpdate = false;
        }

        for (int i = 0; i < folders.size(); i++) {
            GuiButton button = getButtonByID(i + 2);
            button.setY(scrollAmount + height - (i * 40 * 2 + 40 * 2));
            button.setX(0);
        }
        super.update();
    }

    @Override
    public int getID() {
        return PixelShooter.USER_LEVEL_OVERVIEW;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(width - 75 * 2, height - (30 * 2), 75 * 2, 30 * 2, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton(width - 75 * 2, height - (60 * 2), 75 * 2, 30 * 2, "Refresh", BUTTON_REFRESH, true));

        folders.clear();

        File folder = Gdx.files.local("saves/user").file();
        int id = 2;
        File[] x = folder.listFiles();
        if (x != null) {
            List<File> files = Arrays.asList(x);
            folders = new ArrayList<LevelFolder>();
            for (File file : files) {
                try {
                    PSTagCompound tag = new PSTagCompound();
                    tag.readFromStream(new DataInputStream(new FileInputStream(file)));
                    EditingWorld editingWorld = EditingWorld.readFromTag(tag);
                    boolean foundOne = false;
                    for (LevelFolder levelFolder : folders) {
                        if (levelFolder.levelName.equals(editingWorld.getName())) {
                            levelFolder.files.add(new LevelFolder.FolderFile(editingWorld, file));
                            foundOne = true;
                        }
                    }
                    if (!foundOne) {
                        LevelFolder lf = new LevelFolder(editingWorld.getName());
                        lf.files.add(new LevelFolder.FolderFile(editingWorld, file));
                        folders.add(lf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(folders, new Comparator<LevelFolder>() {
                @Override
                public int compare(LevelFolder o1, LevelFolder o2) {
                    return PixelShooter.NATURAL_SORT.compare(o1.levelName, o2.levelName);
                }
            });
            for (LevelFolder levelFolder : folders) {
                componentList.add(new GuiButton(0, 0, 300 * 2, 40 * 2, "Folder: " + levelFolder.levelName, id++, true));
            }
        }
    }

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        scrollAmount -= input.getDeltaY(pointer);
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        if (scrollAmount > folders.size() * 40 * 2 - 40 * 2 * (720 / (40 * 2))) {
            scrollAmount = folders.size() * 40 * 2 - 40 * 2 * (720 / (40 * 2));
        }
        if (folders.size() < 720 / (40 * 2)) {
            scrollAmount = 0;
        }
    }
}
