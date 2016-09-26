package de.intektor.pixelshooter.sound;

import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;

import java.util.Random;

/**
 * @author Intektor
 */
public class SoundHelper {

    public static void playShootingSound(EntityPlayer player, EntityEnemyTank enemy, float pitch) {
        Random r = new Random();
        int dX = -Integer.signum((int) (player.getMid().x - enemy.getMid().x));
        SoundStorage.shootPlop.play(1, pitch + r.nextFloat() % 0.1f - 0.05f, dX);
    }

    public static void playHitSound(EntityPlayer player, Entity hit) {
        Random r = new Random();
        int dX = -Integer.signum((int) (player.getMid().x - hit.getMid().x));
        SoundStorage.shootPlop.play(1, 0.6f + r.nextFloat() % 0.1f - 0.05f, dX);
    }
}
