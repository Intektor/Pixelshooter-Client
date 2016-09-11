package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * @author Intektor
 */
public interface Tank {

    Color getTankColor();

    Color getBarrelColor();

    float getBarrelLength();

    /**
     * index:
     * 0: the tank model
     * 1: the upper tank model
     * 2: the barrel model
     * @return the instance of how to render in 3D
     */
    ModelInstance[] getModelInstance();

    float getDamage();

    boolean isStandardBulletShooter();

    int getBulletBounces();
}
