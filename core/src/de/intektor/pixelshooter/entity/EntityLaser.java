package de.intektor.pixelshooter.entity;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.PositionHelper;
import de.intektor.pixelshooter.world.World;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.collision.RayTrace;
import de.intektor.pixelshooter.collision.RayTraceHelper;

import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Intektor
 */
public class EntityLaser extends Entity {

    protected float hitX, hitY, hitZ;
    protected Entity owner;
    protected float charge, maxCharge = 200, damage;


    public EntityLaser(float posX, float posY, float posZ, World world, float direction, Entity owner, float damage) {
        super(posX, posY, posZ, world);
        setLookRotation(direction);
        this.owner = owner;
        this.damage = damage;
    }

    private static Model laserModel = PixelShooter.modelBuilder.createBox(1, 0.5f, 0.5f, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position);

    @Override
    public void renderEntity(ModelBatch batch, Camera camera, Environment environment) {

        Point3f hitPoint = new Point3f(hitX, hitY, hitZ);
        Point3f mid = PositionHelper.getMiddle(getMid(), hitPoint);
        ModelInstance instance = new ModelInstance(laserModel, mid.x, mid.y, mid.z);

        instance.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED), new BlendingAttribute(opacity));

        float dX = hitPoint.x - getMid().x;
        float dZ = getMid().z - hitPoint.z;

        instance.transform.rotateRad(0, 1, 0, (float) Math.atan2(dZ, dX));

        instance.transform.scale(getMid().distance(hitPoint), 1, 1);

        batch.begin(camera);
        batch.render(instance, environment);
        batch.end();
    }

    @Override
    public void updateEntity(World world) {
        super.updateEntity(world);
        if (charge < maxCharge) {
            charge++;
            opacity = charge / maxCharge;
        }
    }

    @Override
    public void setLookRotation(double lookRotation) {
        super.setLookRotation(lookRotation);
        List<RayTrace> rays = RayTraceHelper.rayTrace(new Ray(new Vector3(posX, posY, posZ), getLookVector3()), false, worldObj.borders.getBorders());
        double shortestDistance = Double.MAX_VALUE;
        //The rayTrace with the shortest distance
        RayTrace sRay = null;
        for (RayTrace trace : rays) {
            Point3f hitPoint = new Point3f(trace.hitX, trace.hitY, trace.hitZ);
            double d = getPosition().distance(hitPoint);
            if (d < shortestDistance) {
                shortestDistance = d;
                sRay = trace;
            }
        }
        if (sRay != null) {
            hitX = sRay.hitX;
            hitY = sRay.hitY;
            hitZ = sRay.hitZ;
        }
    }

    @Override
    public void revive() {
        super.revive();
        charge = 0;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public float getDepth() {
        return 0;
    }

    @Override
    public float getMaxHealth() {
        return 1;
    }

    @Override
    public long getMaxLifeTime() {
        return 50 * 5;
    }

    @Override
    public void move() {
        if (isDead) return;
        if (opacity < 0.15) return;
        //Used to check for hits in laser to kill
        List<WorldBorder> borders = new ArrayList<WorldBorder>();
        borders.addAll(worldObj.borders.getBorders());
        borders.addAll(worldObj.getEntityCollisions());
        List<RayTrace> rays = RayTraceHelper.rayTrace(new Ray(new Vector3(posX, posY, posZ), getLookVector3()), false, borders);
        Collections.sort(rays, new Comparator<RayTrace>() {
            @Override
            public int compare(RayTrace o1, RayTrace o2) {
                float distance1 = new Point3f(o1.hitX, o1.hitY, o1.hitZ).distance(owner.getMid());
                float distance2 = new Point3f(o2.hitX, o2.hitY, o2.hitZ).distance(owner.getMid());
                return Float.compare(distance1, distance2);
            }
        });
        for (RayTrace traces : rays) {
            if (new Point3f(traces.hitX, traces.hitY, traces.hitZ).distance(new Point3f(hitX, hitY, hitZ)) <= getMid().distance(new Point3f(hitX, hitY, hitZ))) {
                if (traces.collisionHit instanceof WorldBorder.Collision3DEntity) {
                    if (((WorldBorder.Collision3DEntity) traces.collisionHit).getOwner() != owner && ((WorldBorder.Collision3DEntity) traces.collisionHit).getOwner() != this) {
                        ((WorldBorder.Collision3DEntity) traces.collisionHit).getOwner().laserHit(this, charge, maxCharge, charge / maxCharge / damage);
                    }
                } else {
                    break;
                }
            }
        }
    }
}
