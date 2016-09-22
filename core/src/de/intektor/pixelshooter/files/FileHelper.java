package de.intektor.pixelshooter.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder.FolderFile;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

import java.io.DataInputStream;

/**
 * @author Intektor
 */
public class FileHelper {

    public static FolderFile getWorldFromFile(String path) {
        try {
            PSTagCompound tag = new PSTagCompound();
            FileHandle local = Gdx.files.local(path);
            tag.readFromStream(new DataInputStream(local.read()));
            return new FolderFile(EditingWorld.readFromTag(tag), local.file());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
