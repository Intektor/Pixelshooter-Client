package de.intektor.pixelshooter.enums;

import com.badlogic.gdx.graphics.Texture;
import de.intektor.pixelshooter.abstrct.ImageStorage;

import java.io.Serializable;

/**
 * @author Intektor
 */
public enum BulletType implements Serializable {
    STANDARD_BULLET(ImageStorage.standard_ammo),
    ARTILLERY_BULLET(ImageStorage.tankExplosion),
    TRIPLE_BULLET(ImageStorage.triple_ammo),
    CHASING_BULLET(ImageStorage.chasing_ammo),
    MINE_BULLET(ImageStorage.mine_bullet),
    HEAVY_BULLET(ImageStorage.heavy_ammo);

    Texture texture;

    BulletType(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
