package de.intektor.pixelshooter.entity;

/**
 * @author Intektor
 */
public class DamageSource {
    public final float damage;
    public final Entity damager;

    public DamageSource(float damage, Entity damager) {
        this.damage = damage;
        this.damager = damager;
    }
}
