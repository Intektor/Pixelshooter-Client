package de.intektor.pixelshooter.files;

import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.world.EditingWorld;

import java.io.DataInputStream;

/**
 * @author Intektor
 */
public class FileHelper {

    public static EditingWorld getWorldFromFile(String path) {
        try {
            PSTagCompound tag = new PSTagCompound();
            tag.readFromStream(new DataInputStream(Gdx.files.local(path).read()));
            return EditingWorld.readFromTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
