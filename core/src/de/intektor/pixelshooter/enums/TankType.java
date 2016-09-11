package de.intektor.pixelshooter.enums;

import com.badlogic.gdx.graphics.Color;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;
import de.intektor.pixelshooter.entity.Tank;

/**
 * @author Intektor
 */
public enum TankType {
    TANK_PLAYER(new EntityPlayer(0, 0, null, null, 1, 3, 3, 50, 3, 45, 1, 1)),
    TANK_STANDARD_ATTACKER(new EntityEnemyTank.TankStandardAttacker(0, 0, null, 1, 50, 100, 1, 1, 1)),
    TANK_QUICK_SHOOTER(new EntityEnemyTank.TankQuickShooter(0, 0, null, 1, 100, 100, 1, 1, 1)),
    TANK_ARTILLERY(new EntityEnemyTank.TankArtillery(0, 0, null, 3, 300, 300, 2, 1)),
    TANK_TRIPLE_ATTACKER(new EntityEnemyTank.TankTripleAttacker(0, 0, null, 3, 100, 50, 3, 45, 1, 0, 1)),
    TANK_CHASE_SHOOTER(new EntityEnemyTank.TankChaseShooter(0, 0, null, 3, 100, 100, 1, 0, 1)),
    TANK_LASER_SHOOTER(new EntityEnemyTank.TankLaserShooter(0, 0, null, 3, 100, 1.75f, 1, 500, 50 * 5)),
    TANK_MINE_SHOOTER(new EntityEnemyTank.TankMineShooter(0, 0, null, 3, 300, 300, 1, 1)),
    TANK_HEAVY_SHOOTER(new EntityEnemyTank.TankHeavyShooter(0, 0, null, 3, 75, 250, 3, 0, 1));

    Tank tank;

    TankType(Tank tank) {
        this.tank = tank;
    }

    public Tank getTank() {
        return tank;
    }

    public Color getTankColor() {
        return tank.getTankColor();
    }

    public Color getBarrelColor() {
        return tank.getBarrelColor();
    }

    public boolean isEnemy() {
        return this != TANK_PLAYER;
    }
}
