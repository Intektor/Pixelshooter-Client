package de.intektor.pixelshooter.abstrct;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

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
        String s = "";
        boolean first = true;
        for (Object o : objects) {
            if (!first) {
                s += " | ";
            }
            s += o;
            first = false;
        }
        return s;
    }

    public static boolean isTouchDevice() {
        Application.ApplicationType type = Gdx.app.getType();
        return type == Application.ApplicationType.Android || type == Application.ApplicationType.iOS;
    }
}
