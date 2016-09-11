package de.intektor.pixelshooter.collision;

import de.intektor.pixelshooter.entity.Entity;

/**
 * @author Intektor
 */
public class RayTrace {

    public float hitX, hitY, hitZ;
    public boolean hasHit;
    public Entity entityHit;
    public WorldBorder collisionHit;

    @Override
    public String toString() {
        return "Hit Coordinates: ( " + hitX + " | " + hitY + " | " + hitZ + " ) " + (collisionHit != null ? "Collision3D: (" + collisionHit + ")" : "Entity hit: (" + entityHit + ")");
    }
}
