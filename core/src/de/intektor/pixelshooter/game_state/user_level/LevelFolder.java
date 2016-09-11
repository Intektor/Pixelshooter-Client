package de.intektor.pixelshooter.game_state.user_level;

import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

import java.io.*;
import java.util.*;

/**
 * @author Intektor
 */
public class LevelFolder {

    public List<FolderFile> files = new ArrayList<FolderFile>();
    final String levelName;

    public LevelFolder(String levelName) {
        this.levelName = levelName;
    }

    public static class FolderFile {

        public final EditingWorld world;
        public final File file;

        public FolderFile(EditingWorld world, File file) {
            this.world = world;
            this.file = file;
        }
    }

    public static LevelFolder createFolderByLevelName(String levelName) {
        LevelFolder levelFolder = new LevelFolder(levelName);
        File folder = Gdx.files.local("saves/user").file();
        File[] x = folder.listFiles();
        if (x != null) {
            List<File> files = Arrays.asList(x);
            for (File file : files) {
                try {
                    PSTagCompound tag = new PSTagCompound();
                    tag.readFromStream(new DataInputStream(new FileInputStream(file)));
                    EditingWorld editingWorld = EditingWorld.readFromTag(tag);
                    if (editingWorld.getName().equals(levelName)) {
                        levelFolder.files.add(new FolderFile(editingWorld, file));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return levelFolder;
    }
}
