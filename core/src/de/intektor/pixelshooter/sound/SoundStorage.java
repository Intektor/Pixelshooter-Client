package de.intektor.pixelshooter.sound;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import de.intektor.pixelshooter.abstrct.ImageStorage;

import java.net.URL;

/**
 * @author Intektor
 */
public class SoundStorage {

    public static Sound shootPlop;

    public static void init() {
        URL resource = ImageStorage.class.getResource("ImageStorage.class");
        boolean inJar = resource != null && resource.toString().startsWith("jar");
        String domain = Gdx.app.getType() == Application.ApplicationType.Desktop && !inJar ? "assets/" : "";
        shootPlop = Gdx.audio.newSound(Gdx.files.local(domain + "sounds/shootPop.wav"));
    }
}
