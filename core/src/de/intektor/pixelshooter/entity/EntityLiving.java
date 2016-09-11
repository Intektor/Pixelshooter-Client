package de.intektor.pixelshooter.entity;


import de.intektor.pixelshooter.ai.AI;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.path.PathTraveller;
import de.intektor.pixelshooter.path.WorldIndexedGraph;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point3f;


/**
 * @author Intektor
 */
public abstract class EntityLiving extends Entity implements PathTraveller {

    protected AI ai;

    public EntityLiving(float posX, float posY, World world, AI ai) {
        super(posX, posY, world);
        this.ai = ai;
        ai.setEntity(this);
        ai.init();
    }

    @Override
    public void initEntity() {
        if (ai != null) {
            ai.init();
        }
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
        if (ai != null) {
            ai.startThinking();
        }
    }

    @Override
    public void onDeath(KillReason reason) {
        super.onDeath(reason);
        ai.onEntityDeath();
    }

    @Override
    public WorldIndexedGraph getGraphPath() {
        return worldObj.worldPathFinderGraphDistance_5u;
    }

    @Override
    public Collisions getHitBoxes() {
        return getCollisionBox();
    }

    @Override
    public float getTravellerWidth() {
        return getWidth();
    }

    @Override
    public float getTravellerDepth() {
        return getHeight();
    }

    @Override
    public Point3f getTravellerPosMid() {
        return getMid();
    }

    @Override
    public World getTravellerWorld() {
        return worldObj;
    }
}
