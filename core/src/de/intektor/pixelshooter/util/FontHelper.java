package de.intektor.pixelshooter.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public class FontHelper {

    public static float stringWidth(String string, BitmapFont font) {
//        GlyphLayout.GlyphRun run = new GlyphLayout.GlyphRun();
//        font.getData().getGlyphs(run, string, 0, string.length(), false);
//        for (float f : run.xAdvances.items) {
//            run.width += f;
//        }
//        return run.width;
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, string);
        return layout.width;
    }


    public static float stringHeight(String string, BitmapFont font) {
        return font.getLineHeight();
    }

    public static List<String> splitString(String string, float width, BitmapFont font) {
        List<String> list = new ArrayList<String>();
        int currentIndex = 1;
        while (stringWidth(string, font) > width) {
            while (stringWidth(string.substring(0, currentIndex), font) < width) {
                currentIndex++;
            }
            CharSequence local = string.substring(0, currentIndex);
            list.add((String) local);
            string = string.substring(currentIndex, string.length());
            currentIndex = 0;
        }
        if (string.trim().length() > 0) {
            list.add(string);
        }
        return list;
    }

    public static float getStringIndexPosX(String string, float width, BitmapFont font, int index) {
        List<String> strings = splitString(string.substring(0, index), width, font);
        return !strings.isEmpty() ? stringWidth(strings.get(strings.size() - 1), font) : 0;
    }

    public static float getStringIndexPosY(String string, float width, BitmapFont font, int index) {
        List<String> strings = splitString(string.substring(0, index), width, font);
        return Math.max(strings.size() - 1, 0) * stringHeight(string, font);
    }
}
