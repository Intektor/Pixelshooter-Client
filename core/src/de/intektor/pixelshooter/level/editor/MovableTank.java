package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;
import de.intektor.pixelshooter.enums.MovableObjects;
import de.intektor.pixelshooter.enums.TankType;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;

/**
 * @author Intektor
 */
public class MovableTank extends MovableObject {

    TankType tankType;

    public float health = -1, damage = -1, speed = -1;
    public int trackingRange = -1, shootingCooldown = -1, bulletBounces = -1;

    public int triple_attacker_player_amtOfBullets = -1, triple_attacker_player_field_of_shooting = -1;

    public int player_shotsBeforeCooldown = -1, player_cooldownInTicks = -1;

    public int laser_shooter_chargeTime = -1, laser_shooter_laserTime = -1;

    public MovableTank() {
    }

    public MovableTank(float x, float y, float width, float height, TankType tanktype) {
        super(new Collision2D(x, y, width, height), MovableObjects.MOVABLE_TANK);
        this.tankType = tanktype;
        initTank(tanktype);
    }

    public MovableTank(Collision2D collision2D, TankType tanktype) {
        super(collision2D, MovableObjects.MOVABLE_TANK);
        this.tankType = tanktype;
    }

    public void initTank(TankType tanktype) {
        if (tanktype.getTank() instanceof Entity) {
            this.health = ((Entity) tanktype.getTank()).getHealth();
            this.damage = tanktype.getTank().getDamage();
            this.speed = ((Entity) tanktype.getTank()).motionMultiplier;
            this.bulletBounces = tanktype.getTank().getBulletBounces();
            if (tanktype.getTank() instanceof EntityEnemyTank) {
                this.trackingRange = ((EntityEnemyTank) tanktype.getTank()).getTrackingRange();
                this.shootingCooldown = ((EntityEnemyTank) tanktype.getTank()).getShootingCooldown();
                if (tanktype == TankType.TANK_TRIPLE_ATTACKER) {
                    triple_attacker_player_amtOfBullets = ((EntityEnemyTank.TankTripleAttacker) tanktype.getTank()).amtOfBullets;
                    triple_attacker_player_field_of_shooting = ((EntityEnemyTank.TankTripleAttacker) tanktype.getTank()).fieldOfShooting;
                }
                if (tanktype == TankType.TANK_LASER_SHOOTER) {
                    laser_shooter_laserTime = ((EntityEnemyTank.TankLaserShooter) tanktype.getTank()).maxAttackTime;
                    laser_shooter_chargeTime = ((EntityEnemyTank.TankLaserShooter) tanktype.getTank()).maxLaserCharge;
                }
            }
            if (tanktype == TankType.TANK_PLAYER) {
                player_shotsBeforeCooldown = ((EntityPlayer) tanktype.getTank()).shotsBeforeCooldown;
                player_cooldownInTicks = ((EntityPlayer) tanktype.getTank()).cooldownInTicks;
                triple_attacker_player_amtOfBullets = ((EntityPlayer) tanktype.getTank()).amtOfBullets;
                triple_attacker_player_field_of_shooting = ((EntityPlayer) tanktype.getTank()).fieldOfShooting;
            }
        }
    }

    @Override
    public boolean isResizeAble() {
        return false;
    }

    @Override
    public boolean canBeSelected() {
        return true;
    }

    @Override
    public boolean canBeRemoved() {
        return true;
    }

    @Override
    public boolean canBeCopied() {
        return tankType != TankType.TANK_PLAYER;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void writeToTag(PSTagCompound tag) {
        type = MovableObjects.MOVABLE_TANK;
        super.writeToTag(tag);
        tag.setInteger("tank_type", tankType.ordinal());
        tag.setFloat("health", health);
        tag.setFloat("damage", damage);
        tag.setFloat("speed", speed);

        tag.setInteger("tracking_range", trackingRange);
        tag.setInteger("shooting_cooldown", shootingCooldown);
        tag.setInteger("bullet_bounces", bulletBounces);

        tag.setInteger("triple_attacker_player_amt_of_bullets", triple_attacker_player_amtOfBullets);
        tag.setInteger("triple_attacker_player_radius_of_shooting", triple_attacker_player_field_of_shooting);

        tag.setInteger("player_shots_before_cooldown", player_shotsBeforeCooldown);
        tag.setInteger("player_cooldown_in_ticks", player_cooldownInTicks);

        tag.setInteger("laser_shooter_charge_time", laser_shooter_chargeTime);
        tag.setInteger("laser_shooter_laser_time", laser_shooter_laserTime);
    }

    public void readFromTag(PSTagCompound tag) {
        tankType = TankType.values()[tag.getInteger("tank_type")];

        super.readFromTag(tag);

        health = tag.getFloat("health");
        damage = tag.getFloat("damage");
        speed = tag.getFloat("speed");

        trackingRange = tag.getInteger("tracking_range");
        shootingCooldown = tag.getInteger("shooting_cooldown");
        bulletBounces = tag.getInteger("bullet_bounces");

        triple_attacker_player_amtOfBullets = tag.getInteger("triple_attacker_player_amt_of_bullets");
        triple_attacker_player_field_of_shooting = tag.getInteger("triple_attacker_player_radius_of_shooting");

        player_shotsBeforeCooldown = tag.getInteger("player_shots_before_cooldown");
        player_cooldownInTicks = tag.getInteger("player_cooldown_in_ticks");

        laser_shooter_chargeTime = tag.getInteger("laser_shooter_charge_time");
        laser_shooter_laserTime = tag.getInteger("laser_shooter_laser_time");

        if (speed == 0) {
            initTank(tankType);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, Camera camera, EditingWorld world) {
        RenderHelper.renderTank2D(renderer, getX(), getY(), getWidth(), getHeight(), 0, tankType.getTank(), 0);

        if (isSelected) {
            RenderHelper.renderSquare(renderer, Color.GREEN, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public MovableTank copy() {
        return new MovableTank(getX(), getY(), getWidth(), getHeight(), tankType);
    }

    @Override
    public boolean hasToFitStandardSize() {
        return false;
    }

    public TankType getTankType() {
        return tankType;
    }

}
