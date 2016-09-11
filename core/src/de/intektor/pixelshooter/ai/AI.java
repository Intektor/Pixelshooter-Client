package de.intektor.pixelshooter.ai;

import de.intektor.pixelshooter.entity.EntityLiving;

import java.util.Random;

/**
 * @author Intektor
 */
public abstract class AI<T extends EntityLiving> {

    protected T entity;
    protected Random random = new Random();

    public void init() {
        registerTickTimers();
    }

    public abstract void startThinking();

    public void setEntity(T living) {
        entity = living;
    }

    public void onEntityDeath() {

    }

    public abstract void registerTickTimers();


}
