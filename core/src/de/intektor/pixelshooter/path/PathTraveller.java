package de.intektor.pixelshooter.path;

import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point3f;

/**
 * @author Intektor
 */
public interface PathTraveller {

    WorldIndexedGraph getGraphPath();

    Collisions getHitBoxes();

    float getTravellerWidth();

    float getTravellerDepth();

    Point3f getTravellerPosMid();

    World getTravellerWorld();
}
