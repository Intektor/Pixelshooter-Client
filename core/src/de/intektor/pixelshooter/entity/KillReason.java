package de.intektor.pixelshooter.entity;

import de.intektor.pixelshooter.collision.WorldBorder;

/**
 * @author Intektor
 */
public abstract class KillReason {

    public static class Time_Limit extends KillReason {
    }

    public static class Health_Zero extends KillReason {
        public final float damage;
        public final float prevHealth;
        public final Entity killer;

        public Health_Zero(float damage, float prevHealth, Entity killer) {
            this.damage = damage;
            this.prevHealth = prevHealth;
            this.killer = killer;
        }
    }

    public static class Suicide extends KillReason {

    }

    public static class Too_Many_Bounces extends KillReason {
        public final int bounces;

        public Too_Many_Bounces(int bounces) {
            this.bounces = bounces;
        }
    }

    public static class Void_Death extends KillReason {

    }

    public static class Collision_Destroyed extends KillReason {

        public final WorldBorder destroyed;

        public Collision_Destroyed(WorldBorder destroyed) {
            this.destroyed = destroyed;
        }
    }
}
