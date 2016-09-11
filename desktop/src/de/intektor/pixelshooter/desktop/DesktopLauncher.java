package de.intektor.pixelshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.intektor.pixelshooter.PixelShooter;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;
        config.foregroundFPS = 0;
        config.backgroundFPS = 18;
		config.width = 1280;
        config.height = 720;
        new LwjglApplication(new PixelShooter(), config);
    }
}
