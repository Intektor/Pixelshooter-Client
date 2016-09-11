package de.intektor.pixelshooter.collision;

/**
 * @author Intektor
 */
public enum TankRotation {

    UP_LEFT((float)Math.toRadians(0)),
    UP((float)Math.toRadians(0)),
    UP_RIGHT((float)Math.toRadians(0)),
    RIGHT((float)Math.toRadians(0)),
    DOWN_RIGHT((float)Math.toRadians(0)),
    DOWN((float)Math.toRadians(0)),
    DOWN_LEFT((float)Math.toRadians(0)),
    LEFT((float)Math.toRadians(0));

    float bodyRotation;

    TankRotation(float bodyRotation) {
        this.bodyRotation = bodyRotation;
    }

    public float getBodyRotation() {
        return bodyRotation;
    }
}
