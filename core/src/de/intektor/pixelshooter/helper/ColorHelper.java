package de.intektor.pixelshooter.helper;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Intektor
 */
public class ColorHelper {

    public static Color toColor111(float r, float g, float b, float a) {
        return new Color(r / 255, g / 255, b / 255, a);
    }
}
