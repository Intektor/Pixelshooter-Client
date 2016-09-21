package de.intektor.pixelshooter.abstrct;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter.util.StringUtils;

/**
 * @author Intektor
 */
public class AbstractHelper {

    /**
     * Very great for debugging
     *
     * @param objects the objects to print
     */
    public static void print(Object... objects) {
        System.out.println(format(objects));
    }

    public static String format(Object... objects) {
        return String.format("%s " + StringUtils.repeat("| %s ", objects.length - 1), objects);
    }

    public static boolean isTouchDevice() {
        Application.ApplicationType type = Gdx.app.getType();
        return type == Application.ApplicationType.Android || type == Application.ApplicationType.iOS;
    }
}
