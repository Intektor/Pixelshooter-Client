package de.intektor.pixelshooter.game_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.AbstractHelper;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.collision.*;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityBullet;
import de.intektor.pixelshooter.entity.EntityBullet.*;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.entity.EntityPlayer;
import de.intektor.pixelshooter.enums.Medals;
import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter.game_state.community_levels.GuiPublishLevelToMainServer;
import de.intektor.pixelshooter.game_state.user_level.GuiFinishLevelToPublishLevel;
import de.intektor.pixelshooter.game_state.worlds.GuiViewCampaignWorld;
import de.intektor.pixelshooter.gui.*;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.levels.CampaignInformation.WorldInformation;
import de.intektor.pixelshooter.levels.CommunityPlayInformation;
import de.intektor.pixelshooter.levels.PlayInformation;
import de.intektor.pixelshooter.levels.WorldPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.score.ScoreCounter;
import de.intektor.pixelshooter.score.object.BulletShotScore;
import de.intektor.pixelshooter.sound.SoundStorage;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter.world.World;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.net.packet.RatingPacketToServer;
import de.intektor.pixelshooter_common.packet.PacketHelper;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.intektor.pixelshooter.render.RenderHelper.drawString;

/**
 * @author Intektor
 */
public class GuiPlayState extends Gui implements DPadHandler {

    private World theWorld;
    private EditingWorld backup;
    protected boolean start;
    protected PlayInformation info;
    protected final ThumbGameController thumbGameController = new ThumbGameController(20 + 100, 20 + 100, 100);
    private final String TICK_TIMER_SPAWN_BUTTONS = "TICK_TIMER_SPAWN_BUTTONS_PLAY_STATE";
    private int starsRated;
    private boolean rated;

    @Override
    public int getID() {
        return PixelShooter.PLAY_STATE;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(PixelShooter.getWidth() / 2 - 50, PixelShooter.getHeight() / 2 - 50, 100, 50, "Resume!", 0, false));
        componentList.add(new GuiButton(PixelShooter.getWidth() / 2 - 50, PixelShooter.getHeight() / 2, 100, 50, "Exit!", 1, false));
        componentList.add(new GuiButton(PixelShooter.getWidth() / 2 - 50, PixelShooter.getHeight() / 2 + 50, 100, 50, "Restart!", 2, false));
        int scoreBoardWidth = MathHelper.getNextDivider(width / 2, 31);
        int scoreBoardHeight = MathHelper.getNextDivider(height / 3, 31);
        int x = width / 2 - scoreBoardWidth / 2;
        int y = height / 3 * 2 - scoreBoardHeight / 2;
        switch (info.getStatus()) {
            case LEVEL_EDITOR:
                componentList.add(new GuiButton(0, height - 100, 100, 100, "Exit", 3, true));
                componentList.add(new GuiButton(x, y - 80, scoreBoardWidth / 2, 80, "Back to Level Editor", 4, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 2, y - 80, scoreBoardWidth / 2, 80, "Restart Level", 5, false));
                break;
            case USER_LEVEL:
                componentList.add(new GuiButton(0, height - 100, 100, 100, "Exit", 3, true));
                componentList.add(new GuiButton(x, y - 80, scoreBoardWidth / 2, 80, "Back to Main Menu", 4, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 2, y - 80, scoreBoardWidth / 2, 80, "Restart Level", 5, false));
                break;
            case WORLD_LEVEL:
                componentList.add(new GuiButton(0, height - 100, 100, 100, "Exit", 3, true));
                componentList.add(new GuiButton(x, y - 80, scoreBoardWidth / 3, 80, "Back to World 1", 4, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 3, y - 80, scoreBoardWidth / 3, 80, "Restart Level", 5, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 3 * 2, y - 80, scoreBoardWidth / 3, 80, "Next Level", 6, false));
                break;
            case PUBLISH_LEVEL:
                componentList.add(new GuiButton(0, height - 100, 100, 100, "Exit", 3, true));
                componentList.add(new GuiButton(x, y - 80, scoreBoardWidth / 2, 80, "", 4, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 2, y - 80, scoreBoardWidth / 2, 80, "Retry!", 5, false));
                break;
            case COMMUNITY_LEVEL:
                componentList.add(new GuiButton(0, height - 100, 100, 100, "Exit", 3, true));
                componentList.add(new GuiButton(x, y - 80 - 75, scoreBoardWidth / 2, 80, "Back to Level Overview", 4, false));
                componentList.add(new GuiButton(x + scoreBoardWidth / 2, y - 80 - 75, scoreBoardWidth / 2, 80, "Restart Level", 5, false));
                break;
        }
    }

    @Override
    public void enterGui() {
        super.enterGui();
        TickTimerHandler.registerTickTimer(90, TICK_TIMER_SPAWN_BUTTONS);
        restartGame();
    }


    boolean prevPlayerDeathState, prevAllEnemiesDeadState;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        if (start) {
            theWorld.renderWorld(PixelShooter.modelBatch, isGameRunning());

            if (!theWorld.thePlayer.isDead) {
                float f = 0;
                float heartSize = 30;
                float ceil = (float) Math.ceil(theWorld.thePlayer.getHealth());
                //Draw the health of the player
                batch.begin();
                while (f < ceil) {
                    if (theWorld.thePlayer.getHealth() - f >= 1) {
                        batch.draw(ImageStorage.heart, width - heartSize * (f + 1), 0, heartSize, heartSize);
                    } else {
                        float remHealth = theWorld.thePlayer.getHealth() - f;
                        int x = (int) ((1 - remHealth) * ImageStorage.heart.getWidth());
                        batch.draw(new TextureRegion(ImageStorage.heart, x, 0, ImageStorage.heart.getWidth() - x, ImageStorage.heart.getHeight()), width - heartSize * (f) - (heartSize * remHealth), 0, heartSize * remHealth, heartSize);
                    }
                    f++;
                }
                batch.end();
                if (getEnemiesAlive() > 0) {
                    if (AbstractHelper.isTouchDevice()) {
                        thumbGameController.renderDPad(renderer);
                    }
                } else {
                    drawScoreBoard(renderer, batch);
                }
            } else {
                drawScoreBoard(renderer, batch);
            }
        }
        super.render(renderer, batch);
    }

    public void drawScoreBoard(ShapeRenderer renderer, SpriteBatch batch) {
        if (counter != null) {
            drawMissionSuccess();
            counter.renderScoreCounter(renderer, 0, info.getStatus() == PlayStateStatus.COMMUNITY_LEVEL ? -75 : 0);
            if (info.getStatus() == PlayStateStatus.COMMUNITY_LEVEL) {
                CommunityPlayInformation community = (CommunityPlayInformation) info;
                batch.begin();
                batch.draw(ImageStorage.empty_stars, width / 2 - 200, height - 160, 400, 80);
                float ratio = starsRated / 5F;
                batch.draw(new TextureRegion(ImageStorage.full_stars, 0, 0, ratio, 1), width / 2 - 200, height - 160, 400 * ratio, 80);
                String message = !community.alreadyRated ? rated ? "Stars rated: " + starsRated : "Not rated yet!" : "You have already rated this level!";
                RenderHelper.drawString(width / 2, height - 175, message, PixelShooter.unScaledPerfectPixel22, batch);
                batch.end();
            }
        }
    }

    public void drawMissionSuccess() {
        BitmapFont font = PixelShooter.unScaledPerfectPixel64;
        GlyphLayout layout = PixelShooter.layout;
        String message = missionSuccess ? "Mission successful!" : "Mission failed!";
        layout.setText(font, message);

        PixelShooter.spriteBatch.begin();
        drawString(1280 / 2, 720 - layout.height, message, font, PixelShooter.spriteBatch, true);
        PixelShooter.spriteBatch.end();
    }

    public int getEnemiesAlive() {
        int enemiesAlive = 0;
        for (Entity entity : theWorld.getEntityList()) {
            if (entity instanceof EntityEnemyTank) {
                enemiesAlive++;
            }
        }
        return enemiesAlive;
    }

    ScoreCounter counter;

    boolean missionSuccess;

    public void playerDeath() {
        createScoreCounter(false);
    }

    public void createScoreCounter(boolean success) {
        missionSuccess = success;
        counter = new ScoreCounter(theWorld.scoreObjects, success);
        TickTimerHandler.resetTickTimer(TICK_TIMER_SPAWN_BUTTONS);
    }

    public boolean showScoreButtons() {
        return !isGameRunning() && TickTimerHandler.hasTickTimerFinished(TICK_TIMER_SPAWN_BUTTONS);
    }

    @Override
    public void update() {
        super.update();
        if (start && !paused) {
            if (isGameRunning()) {
                theWorld.updateWorld();
                if (!AbstractHelper.isTouchDevice()) {
                    RayTrace ray = getMousePosInWorld(0);
                    if (ray != null) {
                        theWorld.thePlayer.lookAt(ray.hitX, ray.hitZ);
                    }
                }
            }
        }
        GuiButton button4 = getButtonByID(4);
        GuiButton button5 = getButtonByID(5);
        GuiButton button6 = getButtonByID(6);
        if (button4 != null) {
            button4.setShown(showScoreButtons());
            if (info.getStatus() == PlayStateStatus.PUBLISH_LEVEL) {
                button4.setDescription(missionSuccess ? "Publish now!" : "Back to user levels!");
            }
        }
        if (button5 != null) {
            button5.setShown(showScoreButtons());
        }
        if (button6 != null) {
            button6.setShown(showScoreButtons());
        }
        if (info.getStatus() == PlayStateStatus.LEVEL_EDITOR) {
            if (input.isKeyJustPressed(Input.Keys.BACK)) {
                start = false;
                PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
            }
        }
        ticksSinceLastShoot++;
        if (prevPlayerDeathState != theWorld.thePlayer.isDead) {
            prevPlayerDeathState = theWorld.thePlayer.isDead;
            if (prevPlayerDeathState) {
                playerDeath();
            }
        }
        if (prevAllEnemiesDeadState != (getEnemiesAlive() == 0)) {
            prevAllEnemiesDeadState = getEnemiesAlive() == 0;
            if (prevAllEnemiesDeadState) {
                if (info.getStatus() == PlayStateStatus.WORLD_LEVEL) {
                    WorldPlayInformation worldInfo = (WorldPlayInformation) info;
                    PixelShooter.campaign.levelFinished(worldInfo.worldID, worldInfo.levelID);
                }
                createScoreCounter(true);
            }
        }
        if (theWorld.thePlayer.isDead || prevAllEnemiesDeadState) {
            if (counter != null) {
                counter.update();
            }
        }
    }

    public void restartGame() {
        if (theWorld != null) theWorld.finishWorld();
        theWorld = backup.convertToWorld();
        prevPlayerDeathState = false;
        prevAllEnemiesDeadState = false;
        paused = false;
        starsRated = 0;
    }

    @Override
    public void onButtonTouched(int id) {
        System.out.println(id);
        switch (id) {
            case 0:
                paused = !paused;
                getButtonByID(0).setShown(paused);
                getButtonByID(1).setShown(paused);
                getButtonByID(2).setShown(paused);
                break;
            case 1:
                start = false;
                paused = false;
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case 2:
                theWorld = backup.convertToWorld();
                paused = false;
                break;
            case 3:
                switch (info.getStatus()) {
                    case LEVEL_EDITOR:
                        PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
                        break;
                    case USER_LEVEL:
                        PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                        break;
                    case WORLD_LEVEL:
                        PixelShooter.enterGui(PixelShooter.VIEW_CAMPAIGN_WORLD);
                        break;
                    case PUBLISH_LEVEL:
                        PixelShooter.enterGui(PixelShooter.FINISH_LEVEL_TO_PUBLISH_LEVEL);
                        break;
                    case COMMUNITY_LEVEL:
                        PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL);
                        break;
                }
                start = false;
                break;
            case 4:
                switch (info.getStatus()) {
                    case LEVEL_EDITOR:
                        PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
                        break;
                    case USER_LEVEL:
                        PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                        break;
                    case WORLD_LEVEL:
                        saveCampaignProgress();
                        PixelShooter.enterGui(PixelShooter.VIEW_CAMPAIGN_WORLD);
                        break;
                    case PUBLISH_LEVEL:
                        if (missionSuccess) {
                            GuiFinishLevelToPublishLevel finishGui = (GuiFinishLevelToPublishLevel) PixelShooter.getGuiByID(PixelShooter.FINISH_LEVEL_TO_PUBLISH_LEVEL);
                            ((GuiPublishLevelToMainServer) PixelShooter.getGuiByID(PixelShooter.PUBLISH_LEVEL_TO_MAIN_SERVER)).setFile(finishGui.getFolder(), finishGui.getIdToPublish());
                            PixelShooter.enterGui(PixelShooter.PUBLISH_LEVEL_TO_MAIN_SERVER);
                        } else {
                            PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                        }
                        break;
                    case COMMUNITY_LEVEL:
                        CommunityPlayInformation communityPlayInformation = (CommunityPlayInformation) info;
                        PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL);
                        if (rated && !communityPlayInformation.alreadyRated && PixelShooter.googleAccount != null) {
                            RatingPacketToServer packet = new RatingPacketToServer(starsRated, communityPlayInformation.info.officialID, PixelShooter.googleAccount.idToken);
                            PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
                        }
                        break;
                }
                break;
            case 5:
                restartGame();
                break;
            case 6:
                WorldPlayInformation worldInfo = (WorldPlayInformation) info;
                if (worldInfo.levelID < 30) {
                    saveCampaignProgress();
                    ((GuiViewCampaignWorld) PixelShooter.getGuiByID(PixelShooter.VIEW_CAMPAIGN_WORLD)).startLevel(worldInfo.levelID + 1);
                }
                break;
        }
    }

    @Override
    public boolean keyDown(int keyID) {
        if (!paused && !thumbGameController.isActive()) {
            if (keyID == Input.Keys.W) {
                theWorld.thePlayer.isForward = true;
            } else if (keyID == Input.Keys.S) {
                theWorld.thePlayer.isBackward = true;
            }
            if (keyID == Input.Keys.A) {
                theWorld.thePlayer.isLeft = true;
            } else if (keyID == Input.Keys.D) {
                theWorld.thePlayer.isRight = true;
            }
        }
        if (keyID == Input.Keys.R) {
            restartGame();
        }
        if (keyID == Input.Keys.ESCAPE) {
            if (info.getStatus() != PlayStateStatus.LEVEL_EDITOR) {
                paused = !paused;
            } else {
                start = false;
                PixelShooter.enterGui(PixelShooter.LEVEL_EDITOR);
            }
        }
        return super.keyDown(keyID);
    }

    @Override
    public boolean keyUp(int key) {
        if (!paused && !thumbGameController.isActive()) {
            if (key == Input.Keys.W) {
                theWorld.thePlayer.isForward = false;
            } else if (key == Input.Keys.S) {
                theWorld.thePlayer.isBackward = false;
            }
            if (key == Input.Keys.A) {
                theWorld.thePlayer.isLeft = false;
            } else if (key == Input.Keys.D) {
                theWorld.thePlayer.isRight = false;
            }
        }
        return super.keyUp(key);
    }

    int ticksSinceLastShoot;
    int timesOverTimed;

    public boolean isGameRunning() {
        return !prevPlayerDeathState && !prevAllEnemiesDeadState;
    }

    @Override
    public void pointerDown(int x, int y, int pointer, int button) {
        if (isGameRunning()) {
            GuiComponent hovered = getHoveredComponent(x, y);
            if (hovered != null && hovered instanceof GuiButton) {
                super.pointerDown(x, y, pointer, button);
                return;
            }
            EntityPlayer player = theWorld.thePlayer;
            if (!player.isDead && !paused && start) {
                if (MathHelper.isPointInsideCircle(x, height - y, (int) thumbGameController.getMid().x, (int) thumbGameController.getMid().y, thumbGameController.getRadius()) && AbstractHelper.isTouchDevice()) {
                    thumbGameController.setActive(true);
                    thumbGameController.setPosition(x, height - y);
                    activateGameController(thumbGameController);
                }

                if (!thumbGameController.isActive() ? pointer != 0 : pointer != 1) {
                    super.pointerDown(x, y, pointer, button);
                    return;
                }
                if (ticksSinceLastShoot > player.cooldownInTicks) timesOverTimed = 0;
                if (timesOverTimed == player.shotsBeforeCooldown - 1) {
                    super.pointerDown(x, y, pointer, button);
                    return;
                }
                if (ticksSinceLastShoot < 30) {
                    timesOverTimed++;
                }
                EntityBullet bullet = null;

                RayTrace ray = getMousePosInWorld(pointer);

                if (ray == null) return;

                player.lookAt(ray.hitX, ray.hitZ);

                double sin = player.lookSin;
                double cos = player.lookCos;

                boolean shot = false;

                switch (player.bulletType) {
                    case STANDARD_BULLET:
                        bullet = new StandardBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getDepth() / 2, theWorld, player, Color.GREEN, player.damage, player.bulletBounces);
                        bullet.motionX += cos * 1;
                        bullet.motionZ += sin * 1;
                        shot = true;
                        break;
                    case ARTILLERY_BULLET:
                        bullet = new ArtilleryBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getHeight() / 2, theWorld, player, Color.GREEN, ray.hitX, ray.hitY, ray.hitZ, player.damage);
                        bullet.motionX += cos * 1;
                        bullet.motionZ += sin * 1;
                        shot = true;
                        break;
                    case TRIPLE_BULLET:
                        double radius = player.fieldOfShooting;
                        for (double r = 0; r <= radius; r += radius / (player.amtOfBullets - 1)) {
                            bullet = new StandardBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getDepth() / 2, player.worldObj, player, Color.RED, player.damage, player.bulletBounces);

                            sin = Math.sin(player.getLookRotation() + Math.toRadians(r - radius / 2));
                            cos = Math.cos(player.getLookRotation() + Math.toRadians(r - radius / 2));

                            bullet.motionZ = (float) (sin * 1);
                            bullet.motionX = (float) (cos * 1);

                            player.worldObj.addEntity(bullet);
                        }
                        shot = true;
                        bullet = null;
                        ticksSinceLastShoot = 0;
                        break;
                    case CHASING_BULLET:
                        bullet = new ChasingBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getDepth() / 2, player.worldObj, player, Color.ORANGE, player.damage, player.bulletBounces);
                        bullet.motionX = (float) cos;
                        bullet.motionZ = (float) sin;
                        shot = true;
                        break;
                    case MINE_BULLET:
                        bullet = new MineBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getHeight() / 2, theWorld, player, Color.GREEN, ray.hitX, ray.hitY, ray.hitZ, player, player.damage);
                        bullet.motionX += cos * 1;
                        bullet.motionZ += sin * 1;
                        shot = true;
                        break;
                    case HEAVY_BULLET:
                        bullet = new HeavyBullet(player.posX + player.getWidth() / 2, 0, player.posZ + player.getDepth() / 2, theWorld, player, Color.GREEN, player.damage, player.bulletBounces);
                        bullet.motionX += cos * 1;
                        bullet.motionZ += sin * 1;
                        shot = true;
                        break;
                }
                ticksSinceLastShoot = 0;
                if (shot) {
                    Random r = new Random();
                    SoundStorage.shootPlop.play(1, 1 + r.nextFloat() % 0.1f - 0.05f, 0);
                    theWorld.scoreObjects.add(new BulletShotScore(-200));
                }
                if (bullet != null) {
                    theWorld.addEntity(bullet);
                }
            }
        } else {
            rate(x, y);
        }
        super.pointerDown(x, y, pointer, button);
    }

    public RayTrace getMousePosInWorld(int pointer) {
        //Gets the position where the mouse is pointing at
        Vector3 mousePos = theWorld.camera.unproject(new Vector3(input.getX(pointer), input.getY(pointer), 0));

        Ray ray2 = new Ray(new Vector3(mousePos.x, mousePos.y, mousePos.z), mousePos.cpy().sub(theWorld.camera.position));

        List<WorldBorder> borders = new ArrayList<WorldBorder>();
        borders.addAll(theWorld.borders.getBorders());
        borders.add(new WorldBorder(new Collision3D(Float.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MAX_VALUE, 0, Float.MAX_VALUE), WorldBorder.BorderType.UNBREAKABLE, theWorld));

        List<RayTrace> rays = RayTraceHelper.rayTrace(ray2, false, borders);

        return RayTraceHelper.getFirstRayCollided(rays, new Point3f(theWorld.camera.position.x, theWorld.camera.position.y, theWorld.camera.position.z));
    }

    public void setPlayInformation(PlayInformation info) {
        this.info = info;
    }

    public void setTheWorld(EditingWorld theWorld) {
        backup = theWorld;
    }

    public void setStart(boolean start) {
        this.start = start;
    }


    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public void pointerUp(int x, int y, int pointer, int button) {
        if (pointer == 0) {
            deactivateGameController(thumbGameController);
            thumbGameController.setActive(false);
        }
    }

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        if (pointer == 0) {
            if (thumbGameController.isActive()) {
                thumbGameController.setThumb(x, 720 - y);
                moveGameController(thumbGameController);
            }
        }
        if (counter != null) {
            counter.mouseDragged(x, y, width, height);
            rate(x, y);
        }
    }

    @Override
    public void activateGameController(ThumbGameController pad) {
        theWorld.thePlayer.keepMotion = true;
    }

    @Override
    public void deactivateGameController(ThumbGameController pad) {
        theWorld.thePlayer.keepMotion = false;
    }

    @Override
    public void moveGameController(ThumbGameController pad) {
        EntityPlayer player = theWorld.thePlayer;
        player.motionX = thumbGameController.thumbCos * thumbGameController.getStrength();
        player.motionZ = -thumbGameController.thumbSin * thumbGameController.getStrength();
    }

    public World getWorld() {
        return theWorld;
    }

    public void setTheWorld(World theWorld) {
        this.theWorld = theWorld;
    }

    public EditingWorld getWorldBackup() {
        return backup;
    }

    public void rate(int x, int y) {
        if (info.getStatus() == PlayStateStatus.COMMUNITY_LEVEL && !isGameRunning()) {
            CommunityPlayInformation communityPlayInformation = (CommunityPlayInformation) info;
            if (Collision2D.isPointInRegion(new Point2f(x, height - y), new Collision2D(width / 2 - 280, height - 160, 480, 80)) && !communityPlayInformation.alreadyRated) {
                rated = true;
                int localX = x - (width / 2 - 280);
                starsRated = Math.max(0, localX / 80);
            }
        }
    }

    public void saveCampaignProgress() {
        WorldPlayInformation info = (WorldPlayInformation) this.info;
        WorldInformation worldInfo = PixelShooter.campaign.getInformation(info.worldID);
        worldInfo.levelState = info.levelID + 1;
        Medals futureMedal = counter.getMedal();
        if (futureMedal.ordinal() > worldInfo.getLevel(info.levelID).medal.ordinal()) {
            worldInfo.getLevel(info.levelID).medal = futureMedal;
        }
        try {
            PSTagCompound campaignTag = new PSTagCompound();
            PixelShooter.campaign.writeToTag(campaignTag);
            campaignTag.writeToStream(new DataOutputStream(new FileOutputStream(Gdx.files.local("campaign.info").file())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
