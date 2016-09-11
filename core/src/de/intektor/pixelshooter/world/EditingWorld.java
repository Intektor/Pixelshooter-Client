package de.intektor.pixelshooter.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;
import de.intektor.pixelshooter.enums.BulletType;
import de.intektor.pixelshooter.enums.MovableObjects;
import de.intektor.pixelshooter.enums.TankType;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.level.editor.MovableCollision;
import de.intektor.pixelshooter.level.editor.MovableObject;
import de.intektor.pixelshooter.level.editor.MovableTank;
import de.intektor.pixelshooter.score.MedalInfo;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Intektor
 */
public class EditingWorld {

    protected final String name;
    protected final int width, height;

    public BulletType bulletType = BulletType.STANDARD_BULLET;

    public BackGroundType background = BackGroundType.WOODEN;

    protected List<MovableObject> movableObjects = new ArrayList<MovableObject>();

    public MedalInfo medalInfo;

    public Date timeSaved = new Date();

    public EditingWorld(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        init();
    }

    public void init() {
        //World Border Up
        movableObjects.add(new MovableCollision(new Collision2D(0, -10, width, 10), false));
        //World Border Down
        movableObjects.add(new MovableCollision(new Collision2D(0, height, width, 10), false));
        //World Border Left
        movableObjects.add(new MovableCollision(new Collision2D(-10, 0, 10, height), false));
        //World Border Right
        movableObjects.add(new MovableCollision(new Collision2D(width, 0, 10, height), false));

    }

    public MovableObject getObjectAtPosition(float x, float y) {
        for (MovableObject o : movableObjects) {
            if (o.isPointInside(x, y)) {
                return o;
            }
        }
        return null;
    }

    public List<MovableObject> getMovableObjects() {
        return movableObjects;
    }

    public void addMoveableObject(MovableObject object) {
        movableObjects.add(object);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public EditingWorld resetCopy() {
        return new EditingWorld(name, width, height);
    }

    public void selectObject(MovableObject ob, boolean unselectAllFirst) {
        if (unselectAllFirst) {
            for (MovableObject obj : movableObjects) {
                obj.setSelected(false);
            }
        }
        if (ob != null) {
            ob.setSelected(true);
        }
    }

    public List<MovableObject> getSelectedObjects() {
        List<MovableObject> objects = new ArrayList<MovableObject>();
        for (MovableObject obj : movableObjects) {
            if (obj.isSelected) {
                objects.add(obj);
            }
        }
        return objects;
    }

    public List<MovableObject> getObjectsInRange(float x, float y, float x2, float y2) {
        List<MovableObject> objects = new ArrayList<MovableObject>();
        for (MovableObject o : movableObjects) {
            if (Collision2D.isRegionInRegion(o.getCollision2D(), Collision2D.createX2Y2(x, y, x2, y2))) {
                objects.add(o);
            }
        }
        return objects;
    }

    public void unselectAll() {
        for (MovableObject o : movableObjects) {
            o.isSelected = false;
        }
    }

    public boolean checkConvert() {
        boolean hasPlayer = false;
        for (MovableObject object : getMovableObjects()) {
            if (object instanceof MovableTank) {
                if (((MovableTank) object).getTankType() == TankType.TANK_PLAYER) {
                    hasPlayer = true;
                }
            }
        }
        return hasPlayer;
    }


    public World convertToWorld() {
        World world = new World(new Collisions(), width, height, background);
        List<WorldBorder> cList = new ArrayList<WorldBorder>();
        List<Entity> eList = new ArrayList<Entity>();
        if (medalInfo == null) medalInfo = calcMedalInfo();

        for (MovableObject object : getMovableObjects()) {
            object.update();
            if (object instanceof MovableCollision) {
                MovableCollision c = (MovableCollision) object;
                cList.add(new WorldBorder(c.getX(), 0, height - c.getY() - c.getHeight(), c.getWidth(), c.getHeight(), c.getBorderType(), world));
            } else if (object instanceof MovableTank) {
                MovableTank tank = (MovableTank) object;
                TankType typeOfTank = ((MovableTank) object).getTankType();
                if (typeOfTank == TankType.TANK_PLAYER) {
                    EntityPlayer player = new EntityPlayer(object.getX(), object.getY() + object.getHeight(), world, bulletType, tank.damage, tank.health, tank.player_shotsBeforeCooldown, tank.player_cooldownInTicks, tank.triple_attacker_player_amtOfBullets, tank.triple_attacker_player_radiusOfShooting, tank.bulletBounces, tank.speed);
                    eList.add(player);
                    world.thePlayer = player;
                } else if (typeOfTank == TankType.TANK_STANDARD_ATTACKER) {
                    eList.add(new EntityEnemyTank.TankStandardAttacker(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.bulletBounces, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_QUICK_SHOOTER) {
                    eList.add(new EntityEnemyTank.TankQuickShooter(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.bulletBounces, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_ARTILLERY) {
                    eList.add(new EntityEnemyTank.TankArtillery(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_TRIPLE_ATTACKER) {
                    eList.add(new EntityEnemyTank.TankTripleAttacker(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.triple_attacker_player_amtOfBullets, tank.triple_attacker_player_radiusOfShooting, tank.damage, tank.bulletBounces, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_CHASE_SHOOTER) {
                    eList.add(new EntityEnemyTank.TankChaseShooter(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.bulletBounces, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_LASER_SHOOTER) {
                    eList.add(new EntityEnemyTank.TankLaserShooter(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.damage, tank.speed / (1 / 0.24f), tank.laser_shooter_chargeTime, tank.laser_shooter_laserTime));
                } else if (typeOfTank == TankType.TANK_MINE_SHOOTER) {
                    eList.add(new EntityEnemyTank.TankMineShooter(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.speed / (1 / 0.24f)));
                } else if (typeOfTank == TankType.TANK_HEAVY_SHOOTER) {
                    eList.add(new EntityEnemyTank.TankHeavyShooter(object.getX(), object.getY() + object.getHeight(), world, tank.health, tank.trackingRange, tank.shootingCooldown, tank.damage, tank.bulletBounces, tank.speed / (1 / 0.24f)));
                }
            }
        }
        Collisions c = new Collisions(cList);
        world.addCollisions(c);
        for (Entity e : eList) {
            world.addEntity(e);
        }
        world.worldChanged();
        return world;
    }

    public static void writeToTag(EditingWorld world, String name, Date date) {
        try {
            FileHandle saves = Gdx.files.local("saves/user");
            saves.file().mkdirs();

            String fileName = "saves/user/EditorSave_" + name + ".pssn";
            FileOutputStream fileOut = new FileOutputStream(Gdx.files.local(fileName).file());
            DataOutputStream out = new DataOutputStream(fileOut);
            PSTagCompound tag = new PSTagCompound();
            world.writeToTag(tag, date);

            tag.writeToStream(out);
            fileOut.close();
            System.out.println("This state was saved in: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToTag(PSTagCompound tag, Date date) {
        tag.setInteger("World-Save-Version", 1);
        tag.setString("name", name);
        tag.setInteger("width", width);
        tag.setInteger("height", height);
        tag.setInteger("bullet_type", bulletType.ordinal());
        tag.setInteger("background_type", background.ordinal());
        tag.setInteger("movable_objects_amt", movableObjects.size());

        for (int i = 0; i < movableObjects.size(); i++) {
            MovableObject object = movableObjects.get(i);
            PSTagCompound objectTag = new PSTagCompound();
            switch (object.getType()) {
                case MOVABLE_TANK:
                    ((MovableTank) object).writeToTag(objectTag);
                    break;
                case MOVABLE_COLLISION:
                    ((MovableCollision) object).writeToTag(objectTag);
                    break;
            }
            tag.setTag("object" + i, objectTag);
        }
        if (medalInfo == null) medalInfo = calcMedalInfo();
        PSTagCompound medalTag = new PSTagCompound();
        medalInfo.writeToTag(medalTag);
        tag.setTag("medal", medalTag);
        tag.setLong("time", date.getTime());
    }

    public static EditingWorld readFromTag(PSTagCompound tag) {
        EditingWorld edit = new EditingWorld(tag.getString("name"), tag.getInteger("width"), tag.getInteger("height"));
        edit.bulletType = BulletType.values()[tag.getInteger("bullet_type")];
        edit.background = BackGroundType.values()[tag.getInteger("background_type")];
        int times = tag.getInteger("movable_objects_amt");
        for (int i = 0; i < times; i++) {
            PSTagCompound objectTag = tag.getTag("object" + i);
            MovableObject object = null;
            switch (MovableObjects.values()[objectTag.getInteger("movable_object_type")]) {
                case MOVABLE_TANK:
                    object = new MovableTank();
                    ((MovableTank) object).readFromTag(objectTag);
                    break;
                case MOVABLE_COLLISION:
                    object = new MovableCollision();
                    ((MovableCollision) object).readFromFile(objectTag);
                    break;
            }

            edit.movableObjects.add(object);
        }
        edit.medalInfo = MedalInfo.readFromTag(tag.getTag("medal"));
        edit.timeSaved = new Date(tag.getLong("time"));
        return edit;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    public boolean isInsideWorld(float x, float y) {
        return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
    }

    public boolean isInsideAnObject(MovableObject object) {
        for (MovableObject ob : getMovableObjects()) {
            if (ob.collidingWith(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is commonly used as checking for collisions
     *
     * @param coll   the objects region, should be modified in the movements way
     * @param ignore a object to ignore
     * @return whether it allows the requested movement or not
     */
    public boolean allowMovement(Collision2D coll, List<MovableObject> ignore) {
        for (MovableObject ob : getMovableObjects()) {
            if (Collision2D.isRegionInRegion(ob.getCollision2D(), coll) && (ignore == null || !ignore.contains(ob))) {
                return false;
            }
        }
        return coll.getX() >= 0 && coll.getX2() <= width && coll.getY() >= 0 && coll.getY2() <= width;
    }

    public boolean overlaps(Collision2D collision, MovableObject... ignore) {
        List<MovableObject> l = Arrays.asList(ignore);
        for (MovableObject o : movableObjects) {
            if (!l.contains(o) && Collision2D.isRegionInRegion(o.getCollision2D(), collision)) {
                return true;
            }
        }
        return false;
    }

    public MedalInfo calcMedalInfo() {
        int potMaxPoints = 0;
        MovableTank player = null;
        List<MovableTank> enemyTanks = new ArrayList<MovableTank>();
        for (MovableObject mo : movableObjects) {
            if (mo instanceof MovableTank) {
                if (((MovableTank) mo).getTankType() != TankType.TANK_PLAYER) {
                    enemyTanks.add((MovableTank) mo);
                    potMaxPoints += ((EntityEnemyTank) ((MovableTank) mo).getTankType().getTank()).getScoreOnKilled();
                } else {
                    player = (MovableTank) mo;
                }
            }
        }
        if (player != null) {
            for (MovableTank enemyTank : enemyTanks) {
                potMaxPoints -= enemyTank.health / player.damage * 200 * 2;
            }
            int medalGold = MathHelper.getNextDivider(potMaxPoints, 100);
            int medalSilver = potMaxPoints - MathHelper.getNextDivider((int) (player.health / 3 * 5000), 500);
            int medalBronze = potMaxPoints - MathHelper.getNextDivider((int) (Math.ceil(player.health / 3 * 2) * 5000), 500);
            return new MedalInfo(medalBronze, medalSilver, medalGold);
        }
        return new MedalInfo(0, 0, 0);
    }

    public enum BackGroundType {
        WOODEN(ImageStorage.world_type_wooden, ImageStorage.background_wooden, ImageStorage.border_texture_wooden, ImageStorage.border_breakable_wooden),
        GRASS(ImageStorage.world_type_grass, ImageStorage.background_grass, ImageStorage.border_texture_grass, ImageStorage.border_breakable_grass);

        final Texture tinyTexture, backgroundTexture, unbreakableCollision, breakableCollision;

        BackGroundType(Texture tinyTexture, Texture backgroundTexture, Texture unbreakableCollision, Texture breakableCollision) {
            this.tinyTexture = tinyTexture;
            this.backgroundTexture = backgroundTexture;
            this.unbreakableCollision = unbreakableCollision;
            this.breakableCollision = breakableCollision;
        }

        public Texture getTinyTexture() {
            return tinyTexture;
        }

        public Texture getBackgroundTexture() {
            return backgroundTexture;
        }

        public Texture getCollisionTexture(WorldBorder.BorderType type) {
            switch (type) {
                case BREAKABLE:
                    return breakableCollision;
                case UNBREAKABLE:
                    return unbreakableCollision;
            }
            return null;
        }
    }
}