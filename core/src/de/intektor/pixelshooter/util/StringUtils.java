package de.intektor.pixelshooter.util;

/**
 * @author Intektor
 */
public class StringUtils {

    public static String repeat(String s, int r) {
        String string = "";
        for (int i = 0; i < r; i++) {
            string += s;
        }
        return string;
    }
}
