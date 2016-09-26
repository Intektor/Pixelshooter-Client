package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.AbstractHelper;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityEnemyTank;
import de.intektor.pixelshooter.enums.BulletType;
import de.intektor.pixelshooter.enums.LevelEditorTool;
import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter.enums.TankType;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.GuiComponent;
import de.intektor.pixelshooter.gui.GuiSlideArrow;
import de.intektor.pixelshooter.gui.text_field.GuiNumberField;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.levels.BasicPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static de.intektor.pixelshooter.enums.EnumSide.*;

/**
 * @author Intektor
 */
public class GuiLevelEditor extends Gui {

    public static final int COLLISION_SIZE = 4;
    public static volatile EditingWorld edit;

    protected LevelEditorTool tool = LevelEditorTool.TOOL_SELECT;
    protected LevelEditorTool prevTool;
    boolean selecting;

    List<MovableObject> removeNextLoop = new ArrayList<MovableObject>();

    public GuiLevelEditor() {

    }

    public void setEdit(EditingWorld edit) {
        GuiLevelEditor.edit = edit;
    }

    @Override
    public void enterGui() {
        super.enterGui();
        resetCamera();
        renderer2 = new ShapeRenderer();
        renderer2.setAutoShapeType(true);
        batch2 = new SpriteBatch();
        if (edit != null) {
            edit.unselectAll();
        }
    }

    ShapeRenderer renderer2;
    SpriteBatch batch2;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        renderWorld(rawWorldCamera);

        int mouseX = calcScaledCoordX(input.getX());
        int mouseY = calcScaledCoordX(input.getY());

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        //render white background behind tool buttons
        renderer.identity();
        renderer.setColor(Color.WHITE);
        renderer.rect(0, 0, 120 * 2, 30 * 2);
        renderer.end();

        super.render(renderer, batch);

        if ((tool == LevelEditorTool.TOOL_SELECT || tool == LevelEditorTool.TOOL_TRASH_CAN || tool == LevelEditorTool.TOOL_SQUARE_COLLISION || tool == LevelEditorTool.TOOL_COPY) && AbstractHelper.isTouchDevice()) {
            if (input.isTouched() && height - mouseY > 30 * 2) {
                TextureRegion texture = ScreenUtils.getFrameBufferTexture(input.getX() - 60 * 2, Gdx.graphics.getHeight() - input.getY() - 60 * 2, 120 * 2, 120 * 2);
                int zoomRenderX = mouseX + 60;
                int zoomRenderY = height - calcScaledCoordY(input.getY()) + 60;

                if (zoomRenderY + 120 > height) {
                    zoomRenderX += 60;
                    zoomRenderY = height - 120;
                }

                if (zoomRenderX + 120 > width) {
                    zoomRenderX = mouseX - 180;
                }

                AbstractHelper.print(zoomRenderX, zoomRenderY);

                batch.begin();
                batch.draw(texture, zoomRenderX, zoomRenderY, 60 * 2, 60 * 2);
                batch.end();
                renderer.begin();

                renderer.setColor(Color.BLACK);
                renderer.rect(zoomRenderX, zoomRenderY, 60 * 2, 60 * 2);
                renderer.end();

                texture.getTexture().dispose();
            }
        }

        renderer.begin();

        RenderHelper.renderTank2D(renderer, 90 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_PLAYER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 120 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_STANDARD_ATTACKER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 150 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_QUICK_SHOOTER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 180 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_ARTILLERY.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 210 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_TRIPLE_ATTACKER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 240 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_CHASE_SHOOTER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 270 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_LASER_SHOOTER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 300 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_MINE_SHOOTER.getTank(), 0);
        RenderHelper.renderTank2D(renderer, 330 * 2, 0, 30 * 2, 30 * 2, (float) Math.toRadians(90), TankType.TANK_HEAVY_SHOOTER.getTank(), 0);

        if (tool != null) {
            tool.render(renderer, 0, 720 - 32 * 2 - 25 * 2, 25 * 2, 25 * 2);
        }

        renderer.end();
    }

    public void renderWorld(Camera camera) {
        camera.update();

        renderer2.setProjectionMatrix(camera.combined);

        batch2.setProjectionMatrix(camera.combined);

        batch2.begin();
        batch2.draw(edit.background.getBackgroundTexture(), -50, -50, edit.getWidth() + 100, edit.getHeight() + 100);
        batch2.end();

        renderer2.begin();

        for (MovableObject mObject : edit.getMovableObjects()) {
            mObject.render(renderer2, camera, edit);
        }
        MouseInfo info = getMInfo(input.getX(), input.getY());
        if (!isGamePaused()) {
            if (tool == LevelEditorTool.TOOL_SELECT || tool == LevelEditorTool.TOOL_TRASH_CAN) {
                if (input.isTouched(0) && !input.isKeyPressed(Keys.SPACE) && edit.getSelectedObjects().size() == 0 && getNumberOfTouches() == 1) {
                    RenderHelper.renderSquare(renderer2, tool == LevelEditorTool.TOOL_SELECT ? Color.WHITE : Color.RED, CLICK_X, CLICK_Y, CURRENT_X - CLICK_X, CURRENT_Y - CLICK_Y);
                    for (MovableObject movableObject : edit.getObjectsInRange(CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y)) {
                        if (movableObject.canBeRemoved()) {
                            Collision2D col = movableObject.getCollision2D();
                            Color color = tool == LevelEditorTool.TOOL_TRASH_CAN ? new Color(0xee0000ff) : new Color(0x00ee00ff);
                            RenderHelper.renderSquare(renderer2, color, col.getX(), col.getY(), col.getWidth(), col.getHeight());
                        }
                    }
                }
            } else if (tool == LevelEditorTool.TOOL_SQUARE_COLLISION) {
                if (input.isTouched(0) && !input.isKeyPressed(Keys.SPACE) && getNumberOfTouches() == 1) {
                    Collision2D rect = MovableObject.calculateValidSize(CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y);
                    if (rect != null) {
                        RenderHelper.renderSquare(renderer2, edit.spaceClear(rect, null) ? Color.GREEN : Color.RED, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                    }
                }
            } else if (isTankPlaceTool(tool) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
                RenderHelper.renderTank2D(renderer2, info.getMouseX() - 5, info.getMouseY() - 5, 10, 10, 90 / 180 * Math.PI, tool.getTankType().getTank(), 0);
                Color color = edit.spaceClear(new Collision2D(info.getMouseX() - 5, info.getMouseY() - 5, 10, 10), null) ? Color.GREEN : Color.RED;
                RenderHelper.renderSquare(renderer2, color, info.getMouseX() - 5, info.getMouseY() - 5, 10, 10);
            } else if (tool == LevelEditorTool.TOOL_COPY) {
                Collision2D c = copiedObject.collision;
                Color color = edit.spaceClear(c, null) ? Color.GREEN : Color.RED;
                RenderHelper.renderSquare(renderer2, color, c.getX(), c.getY(), c.getWidth(), c.getHeight());
            }
        }

        renderer2.end();

        List<MovableObject> selectedObjects = edit.getSelectedObjects();
        for (MovableObject selected : selectedObjects) {
            renderGuiMovableObjectSelected(selected, selectedObjects.size() == 1, renderer2);
        }
    }

    static OrthographicCamera rawWorldCamera;
    static SpriteBatch rawWorldSpriteBatch;
    static ShapeRenderer rawWorldShapeRenderer;


    static {
        rawWorldCamera = new OrthographicCamera(1280, 720);
        rawWorldSpriteBatch = new SpriteBatch();
        rawWorldShapeRenderer = new ShapeRenderer();
        rawWorldShapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void update() {
        super.update();

        edit.getMovableObjects().removeAll(removeNextLoop);

        removeNextLoop.clear();

        if (!input.isTouched(0)) {
            for (MovableObject object : edit.getMovableObjects()) {
                object.update();
            }
        }
        getButtonByID(BUTTON_RESUME).setShown(paused);
        getButtonByID(BUTTON_EXIT).setShown(paused);
        getButtonByID(BUTTON_CONFIRM_NO_SAVE).setShown(confirm_exit_state);
        getButtonByID(BUTTON_CONFIRM_WITH_SAVE).setShown(confirm_exit_state);

        if (tool != LevelEditorTool.TOOL_SELECT) {
            for (MovableObject object : edit.getMovableObjects()) {
                object.setSelected(false);
            }
        }

        if (rawWorldCamera.zoom < 0.1f) {
            rawWorldCamera.zoom = 0.1f;
        }
        if (edit.getSelectedObjects().size() == 1) {
            MovableObject object = edit.getSelectedObjects().get(0);
            if (object instanceof MovableCollision) {
                for (int i = 0; i < 8; i++) {
                    GuiSlideArrow arrow = getArrowByID(i);
                    if (arrow != null && arrow.isShown()) {
                        Vector3 unprojected = null;
                        Vector3 p = rawWorldCamera.project(new Vector3(object.getX() + object.getWidth() / 2, object.getY() + object.getHeight() / 2, 0));
                        float d = 100;
                        float mX = calcScaledCoordX(p.x);
                        float mY = calcScaledCoordY(p.y);
                        switch (i) {
                            case ARROW_TOP:
                                unprojected = new Vector3(mX, mY + d, 0);
                                break;
                            case ARROW_RIGHT:
                                unprojected = new Vector3(mX + d, mY, 0);
                                break;
                            case ARROW_BOTTOM:
                                unprojected = new Vector3(mX, mY - d, 0);
                                break;
                            case ARROW_LEFT:
                                unprojected = new Vector3(mX - d, mY, 0);
                                break;
                        }
                        if (unprojected != null) {
                            arrow.setPosition((int) unprojected.x, (int) unprojected.y);
                        }
                    }
                }
            }
        }
    }

    float CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y;
    /**
     * This is used to add the difference of the pointer drag, it gets added to the collision size and when it reaches it is will get reset, that way we can provide an accurate movement
     */
    float D_MOVE_X, D_MOVE_Y;

    @Override
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        //Stop method if clicked a guiComponent
        if (hoveredOverComponent(x, y)) {
            super.pointerDragged(x, y, prevX, prevY, pointer);
            CLICK_X = CLICK_Y = CURRENT_X = CURRENT_Y = -1000;
            return;
        }

        if (pointer == 0) {
            MouseInfo fInfo = getMInfo(x, y);
            MouseInfo sInfo = getMInfo(prevX, prevY);
            int rPX = (int) (prevX * (Gdx.graphics.getWidth() / 1280f));
            int rPY = (int) (prevY * (Gdx.graphics.getHeight() / 720f));
            int rX = (int) (x * (Gdx.graphics.getWidth() / 1280f));
            int rY = (int) (y * (Gdx.graphics.getHeight() / 720f));
            MouseInfo rpInfo = getMInfo(rPX, rPY);
            MouseInfo rnInfo = getMInfo(rX, rY);
            MouseInfo rrInfo = getMInfo(input.getX(), input.getY());
            float dX = fInfo.getMouseX() - sInfo.getMouseX();
            float dY = fInfo.getMouseY() - sInfo.getMouseY();

            if (input.isTouched() && !isGamePaused() && getNumberOfTouches() == 1) {
                CURRENT_X = rrInfo.getMouseX();
                CURRENT_Y = rrInfo.getMouseY();
                if (input.isKeyPressed(Keys.SPACE) || (tool == LevelEditorTool.TOOL_GRAB)) {
                    rawWorldCamera.translate(-(rnInfo.getMouseX() - rpInfo.getMouseX()), -(rnInfo.getMouseY() - rpInfo.getMouseY()));
                    translationChanged();
                }
                if (input.isKeyPressed(Keys.SPACE)) return;
                switch (tool) {
                    case TOOL_SELECT:
                        List<MovableObject> l = edit.getSelectedObjects();
                        if (!input.isKeyPressed(Keys.SPACE) && getNumberOfTouches() != 3) {
                            Collision2D modCollision2D;
                            //Check for collisions
                            for (MovableObject o : l) {
                                modCollision2D = o.getCollision2D().copy();
                                modCollision2D.translate(dX, dY);
                                if (!edit.spaceClear(modCollision2D, l)) {
                                    super.pointerDragged(x, y, prevX, prevY, pointer);
                                    return;
                                }
                            }
                            prepareObjectMovement(dX, dY);
                            for (MovableObject s : l) {
                                moveMovableObject(s, dX, dY, s.hasToFitStandardSize());
                            }
                            finishObjectMovement();
                        }
                        break;
                    case TOOL_SQUARE_COLLISION:

                        break;
                    case TOOL_COPY:
                        moveCopyObject(rrInfo.getMouseX(), rrInfo.getMouseY());
                        break;
                }
            }
        }
        super.pointerDragged(x, y, prevX, prevY, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.D:
                prevTool = tool;
                changeTool(LevelEditorTool.TOOL_TRASH_CAN);
                break;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.D:
                changeTool(prevTool);
                break;
        }
        return super.keyUp(keycode);
    }

    @Override
    public void pointerMoved(int x, int y) {

    }

    public void prepareObjectMovement(float dX, float dY) {
        D_MOVE_X += dX;
        D_MOVE_Y += dY;
    }

    public void moveMovableObject(MovableObject object, float dX, float dY, boolean hasToFitCollisionSize) {
        if (hasToFitCollisionSize) {
            int rmdX = MathHelper.getNextDivider((int) Math.floor(D_MOVE_X), COLLISION_SIZE);
            int rmdY = MathHelper.getNextDivider((int) Math.floor(D_MOVE_Y), COLLISION_SIZE);

            Collision2D c = object.getCollision2D().copy();
            c.translate(rmdX, rmdY);
            if (edit.spaceClear(c, Collections.singletonList(object))) {
                int ccX = D_MOVE_X >= COLLISION_SIZE || D_MOVE_X <= -COLLISION_SIZE ? rmdX : 0;
                int ccY = D_MOVE_Y >= COLLISION_SIZE || D_MOVE_Y <= -COLLISION_SIZE ? rmdY : 0;
                object.translate(ccX, ccY);
            }
        } else {
            Collision2D c = object.getCollision2D().copy();
            c.translate(dX, dY);
            if (edit.spaceClear(c, Collections.singletonList(object))) {
                object.translate(dX, dY);
            }
        }
    }

    public void finishObjectMovement() {
        if (D_MOVE_X >= COLLISION_SIZE) {
            D_MOVE_X -= COLLISION_SIZE;
        } else if (D_MOVE_X <= -COLLISION_SIZE) {
            D_MOVE_X += COLLISION_SIZE;
        }
        if (D_MOVE_Y >= COLLISION_SIZE) {
            D_MOVE_Y -= COLLISION_SIZE;
        } else if (D_MOVE_Y <= -COLLISION_SIZE) {
            D_MOVE_Y += COLLISION_SIZE;
        }
    }

    @Override
    public boolean scrolled(int amount) {
        rawWorldCamera.zoom += (float) amount / (float) 10;
        translationChanged();
        return super.scrolled(amount);
    }

    @Override
    public void pointerDown(int x, int y, int pointer, int button) {
        super.pointerDown(x, y, pointer, button);
        //Stop method if clicked a guiComponent
        if (hoveredOverComponent(x, y)) {
            CLICK_X = CURRENT_X = CLICK_Y = CURRENT_Y = 0;
            return;
        }

        if (!isGamePaused()) {
            MouseInfo info = getMInfo(input.getX(), input.getY());
            CLICK_X = CURRENT_X = info.getMouseX();
            CLICK_Y = CURRENT_Y = info.getMouseY();
            if (getNumberOfTouches() == 1 && !input.isKeyPressed(Keys.SPACE)) {
                if (tool == LevelEditorTool.TOOL_SELECT) {
                    if (edit.getSelectedObjects().size() == 0 || ((edit.getSelectedObjects().size() == 1 ? edit.getObjectAtPosition(info.getMouseX(), info.getMouseY()) != edit.getSelectedObjects().get(0) : edit.getSelectedObjects() != null)) && !hoveredOverComponent(input.getX(), input.getY())) {
                        selecting = true;
                    }
                    if (edit.getSelectedObjects().size() != 1 || selecting) {

                    }
                } else if (tool == LevelEditorTool.TOOL_COPY) {
                    moveCopyObject(info.getMouseX(), info.getMouseY());
                } else if (tool == LevelEditorTool.TOOL_SQUARE_COLLISION) {

                } else if (tool == LevelEditorTool.TOOL_GRAB) {
                    if (getNumberOfTouches() == 1) {
                        if (System.currentTimeMillis() - timeAtLastTap > 100 && System.currentTimeMillis() - timeAtLastTap < 200) {
                            scrolled(-1);
                        }
                    } else if (getNumberOfTouches() == 2) {
                        if (System.currentTimeMillis() - timeAtLastTap > 100 && System.currentTimeMillis() - timeAtLastTap < 200) {
                            scrolled(1);
                        }
                    }
                } else if (tool == LevelEditorTool.TOOL_SET_PLAYER) {
                    if (isAllInsideWorld() && !edit.overlaps(Collision2D.createX2Y2(info.getMouseX() - 5, info.getMouseY() - 5, info.getMouseX() + 5, info.getMouseY() + 5))) {
                        for (MovableObject object : edit.getMovableObjects()) {
                            if (object instanceof MovableTank) {
                                if (((MovableTank) object).getTankType() == TankType.TANK_PLAYER) {
                                    removeNextLoop.add(object);
                                }
                            }
                        }
                        edit.addMoveableObject(new MovableTank(info.getMouseX() - 5, info.getMouseY() - 5, 10, 10, TankType.TANK_PLAYER));
                    }
                } else if (tool.getTankType() != null) {
                    if (isAllInsideWorld() && edit.spaceClear(Collision2D.createX2Y2(info.getMouseX() - 5, info.getMouseY() - 5, info.getMouseX() + 5, info.getMouseY() + 5), null)) {
                        edit.addMoveableObject(new MovableTank(info.getMouseX() - 5, info.getMouseY() - 5, 10, 10, tool.getTankType()));
                    }
                }
            }
        }
    }

    @Override
    public void pointerUp(int x, int y, int pointer, int button) {
        if (hoveredOverComponent(x, y)) return;
        if (!isGamePaused()) {
            if (tool == LevelEditorTool.TOOL_SELECT) {
                if (selecting) {
                    for (MovableObject selected : edit.getSelectedObjects()) {
                        selectToolObjectInteraction(false, selected, false);
                    }
                    edit.unselectAll();

                    List<MovableObject> objectsInRange = edit.getObjectsInRange(CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y);
                    for (MovableObject o : objectsInRange) {
                        o.setSelected(true);
                        selectToolObjectInteraction(true, o, objectsInRange.size() == 1);
                    }
                    selecting = false;
                    contextMenu(LevelEditorTool.TOOL_SELECT, edit.getSelectedObjects().size() == 1 && edit.getSelectedObjects().get(0) instanceof MovableCollision, edit.getSelectedObjects().size() == 1 ? edit.getSelectedObjects().get(0) : null);
                } else {
                    GuiComponent hoveredComponent = getHoveredComponent(x, y);
                    if (hoveredComponent == null || hoveredComponent instanceof GuiSlideArrow) {
                        showArrows(false);
                    }
                }

            } else if (tool == LevelEditorTool.TOOL_SQUARE_COLLISION) {
                Collision2D rect = MovableObject.calculateValidSize(CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y);
                if (rect != null) {
                    if (edit.spaceClear(rect, null)) {
                        edit.addMoveableObject(new MovableCollision(rect, true));
                    }
                }
            } else if (tool == LevelEditorTool.TOOL_TRASH_CAN) {
                for (MovableObject o : edit.getObjectsInRange(CLICK_X, CLICK_Y, CURRENT_X, CURRENT_Y)) {
                    if (o.canBeRemoved()) {
                        edit.getMovableObjects().remove(o);
                    }
                }
            }
        }
        super.pointerUp(x, y, pointer, button);
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_POINTER:
                if (tool != LevelEditorTool.TOOL_SELECT) {
                    changeTool(LevelEditorTool.TOOL_SELECT);
                    getButtonByID(BUTTON_POINTER).setImage(ImageStorage.pointer);
                } else {
                    changeTool(LevelEditorTool.TOOL_GRAB);
                    getButtonByID(BUTTON_POINTER).setImage(ImageStorage.grab_cursor);
                }
                break;
            case BUTTON_SQUARE_COLLISION:
                changeTool(LevelEditorTool.TOOL_SQUARE_COLLISION);
                break;
            case BUTTON_TRASH_CAN:
                if (edit.getSelectedObjects().size() == 0) {
                    changeTool(LevelEditorTool.TOOL_TRASH_CAN);
                } else {
                    List<MovableObject> selectedObjects = edit.getSelectedObjects();
                    for (MovableObject object : selectedObjects) {
                        edit.getMovableObjects().remove(object);
                        selectToolObjectInteraction(false, object, selectedObjects.size() == 1);
                    }
                    contextMenu(LevelEditorTool.TOOL_SELECT, false, null);
                }
                break;
            case BUTTON_SET_PLAYER:
                changeTool(LevelEditorTool.TOOL_SET_PLAYER);
                break;
            case BUTTON_SET_STANDARD_ATTACKER:
                changeTool(LevelEditorTool.TOOL_SET_STANDARD_SHOOTER);
                break;
            case BUTTON_TEST:
                if (edit.checkConvert()) {
                    PixelShooter.PIXEL_SHOOTER_STATE.setPlayInformation(new BasicPlayInformation(PlayStateStatus.LEVEL_EDITOR));
                    PixelShooter.PIXEL_SHOOTER_STATE.setTheWorld(edit);
                    PixelShooter.PIXEL_SHOOTER_STATE.setStart(true);
                    PixelShooter.enterGui(PixelShooter.PLAY_STATE);
                }
                break;
            case BUTTON_SAVE:
                saveGame();
                break;
            case BUTTON_RESUME:
                paused = false;
                break;
            case BUTTON_EXIT:
                confirm_exit_state = !confirm_exit_state;
                paused = !paused;
                break;
            case BUTTON_CONFIRM_NO_SAVE:
                confirm_exit_state = false;
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case BUTTON_CONFIRM_WITH_SAVE:
                confirm_exit_state = false;
                saveGame();
                break;
            case BUTTON_SET_QUICK_ATTACKER:
                changeTool(LevelEditorTool.TOOL_SET_QUICK_ATTACKER);
                break;
            case BUTTON_SET_ARTILLERY:
                changeTool(LevelEditorTool.TOOL_SET_ARTILLERY_TANK);
                break;
            case BUTTON_SELECT_AMMO:
                if (!editingObjectSettings) {
                    switch (edit.getBulletType()) {
                        case STANDARD_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.tankExplosion);
                            edit.bulletType = BulletType.ARTILLERY_BULLET;
                            break;
                        case ARTILLERY_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.triple_ammo);
                            edit.bulletType = BulletType.TRIPLE_BULLET;
                            break;
                        case TRIPLE_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.chasing_ammo);
                            edit.bulletType = BulletType.CHASING_BULLET;
                            break;
                        case CHASING_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.mine_bullet);
                            edit.bulletType = BulletType.MINE_BULLET;
                            break;
                        case MINE_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.heavy_ammo);
                            edit.bulletType = BulletType.HEAVY_BULLET;
                            break;
                        case HEAVY_BULLET:
                            getButtonByID(BUTTON_SELECT_AMMO).setImage(ImageStorage.standard_ammo);
                            edit.bulletType = BulletType.STANDARD_BULLET;
                            break;
                    }
                }
                break;
            case BUTTON_MAG_GLASS:
                resetCamera();
                break;
            case BUTTON_SET_TRIPLE_ATTACKER:
                changeTool(LevelEditorTool.TOOL_SET_TRIPLE_ATTACKER);
                break;
            case BUTTON_SET_CHASE_SHOOTER:
                changeTool(LevelEditorTool.TOOL_SET_TANK_CHASE_SHOOTER);
                break;
            case BUTTON_MENU:
                paused = !paused;
                break;
            case BUTTON_TOGGLE_TRANSLATION_DRAGGING:
                GuiButton.GuiButtonSwitchONOFF switchONOFF = (GuiButton.GuiButtonSwitchONOFF) getButtonByID(BUTTON_TOGGLE_TRANSLATION_DRAGGING);
                showArrows(switchONOFF.isOn());
                break;
            case BUTTON_SET_LASER_SHOOTER:
                changeTool(LevelEditorTool.TOOL_SET_TANK_LASER_SHOOTER);
                break;
            case BUTTON_TOGGLE_COLLISION_TYPE:
                ((MovableCollision) edit.getSelectedObjects().get(0)).switchType();
                break;
            case BUTTON_SET_MINE_SHOOTER:
                changeTool(LevelEditorTool.TOOL_SET_TANK_MINE_SHOOTER);
                break;
            case BUTTON_SET_HEAVY_SHOOTER:
                changeTool(LevelEditorTool.TOOL_SET_HEAVY_SHOOTER);
                break;
            case BUTTON_CALL_WORLD_ATTRIBUTES:
                PixelShooter.enterGui(PixelShooter.LE_SET_WORLD_ATTRIBUTES);
                break;
            case BUTTON_COPY:
                final List<MovableObject> selectedObjects = edit.getSelectedObjects();
                if (selectedObjects.size() == 1) {
                    changeTool(LevelEditorTool.TOOL_COPY);
                    copiedObject = selectedObjects.get(0).copy();
                }
                getButtonByID(BUTTON_ACCEPT_COPY).setShown(true);
                PixelShooter.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        copiedObject.collision = selectedObjects.get(0).collision.copy();
                    }
                });
                break;
            case BUTTON_ACCEPT_COPY:
                if (edit.spaceClear(copiedObject.collision, null)) {
                    edit.addMoveableObject(copiedObject.copy());
                }
                break;
        }
    }

    MovableObject copiedObject;

    @Override
    public void zoomed(float originalDistance, float currentDistance, float prevDistance) {
        if (tool == LevelEditorTool.TOOL_GRAB) {
            rawWorldCamera.zoom += (prevDistance - currentDistance) / 400;
            if (rawWorldCamera.zoom < 0.1f) {
                rawWorldCamera.zoom = 0.1f;
            }
        }
    }

    public void resetCamera() {
        rawWorldCamera = new OrthographicCamera(width, height);
        rawWorldCamera.position.set(edit.getWidth() / 2, edit.getHeight() / 2, 0);
        translationChanged();
    }

    public void saveGame() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        EditingWorld.writeToTag(edit, edit.getName() + "_" + dateFormat.format(date), date);
    }

    @Override
    public int getID() {
        return PixelShooter.LEVEL_EDITOR;
    }

    final int BUTTON_POINTER = 0, BUTTON_SQUARE_COLLISION = 1, BUTTON_TRASH_CAN = 2, BUTTON_SET_PLAYER = 3, BUTTON_SET_STANDARD_ATTACKER = 4, BUTTON_SAVE = 6, BUTTON_TEST = 5, BUTTON_EXIT = 7, BUTTON_RESUME = 8, BUTTON_CONFIRM_NO_SAVE = 9, BUTTON_CONFIRM_WITH_SAVE = 10;
    final int BUTTON_SET_QUICK_ATTACKER = 11, BUTTON_SET_ARTILLERY = 12, BUTTON_SELECT_AMMO = 13, BUTTON_MAG_GLASS = 14, BUTTON_SET_TRIPLE_ATTACKER = 15, BUTTON_SET_CHASE_SHOOTER = 16, BUTTON_MENU = 17, BUTTON_SET_LASER_SHOOTER = 19, BUTTON_SET_MINE_SHOOTER = 23, BUTTON_SET_HEAVY_SHOOTER = 24;
    final int BUTTON_CALL_WORLD_ATTRIBUTES = 25;
    //CONTEXT MENU SELECT - Collision selected
    final int BUTTON_TOGGLE_TRANSLATION_DRAGGING = 18, BUTTON_TOGGLE_COLLISION_TYPE = 22, BUTTON_COPY = 26, BUTTON_ACCEPT_COPY = 27;
    //CONTEXT MENU SELECT - ENEMY TANK SELECTED
    final int TEXT_FIELD_TANK_HEALTH = 0, TEXT_FIELD_TANK_TRACKING_RANGE = 1, TEXT_FIELD_SHOOTING_COOLDOWN = 2, TEXT_FIELD_DAMAGE = 7, TEXT_FIELD_BULLET_BOUNCES = 8, TEXT_FIELD_TANK_SPEED = 9;
    //PLAYER
    final int TEXT_FIELD_PLAYER_ALLOWED_BULLETS = 5, TEXT_FIELD_PLAYER_COOL_DOWN = 6;
    //TRIPLE_SHOOTER
    final int TEXT_FIELD_TANK_TRIPLE_SHOOTER_AMT_OF_BULLETS = 3, TEXT_FIELD_TANK_TRIPLE_SHOOTER_SHOOTING_FIELD = 4;
    //LASER SHOOTER
    final int TEXT_FIELD_TANK_LASER_SHOOTER_CHARGE_TIME = 10, TEXT_FIELD_TANK_LASER_SHOOTER_LASER_TIME = 11;

    final int
            ARROW_LEFT = 0,
            ARROW_TOP = 1,
            ARROW_RIGHT = 2,
            ARROW_BOTTOM = 3;

    boolean paused = false, confirm_exit_state = false;

    @Override
    public void addGuiComponents() {
        //Used to 2 up for bigger screens
        int i = 0;
        componentList.add(new GuiButton(i * 2, 0 * 2, 30 * 2, 30 * 2, BUTTON_POINTER, true, ImageStorage.pointer));
        componentList.add(new GuiButton(i += 30 * 2, 0 * 2, 30 * 2, 30 * 2, BUTTON_SQUARE_COLLISION, true, ImageStorage.collisionImage));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_TRASH_CAN, true, ImageStorage.trashCan));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_PLAYER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_STANDARD_ATTACKER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_QUICK_ATTACKER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_ARTILLERY, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_TRIPLE_ATTACKER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_CHASE_SHOOTER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_LASER_SHOOTER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_MINE_SHOOTER, true, null));
        componentList.add(new GuiButton(i += 30 * 2, 0, 30 * 2, 30 * 2, BUTTON_SET_HEAVY_SHOOTER, true, null));


        componentList.add(new GuiButton(width - 150 * 2, 0, 30 * 2, 30 * 2, "World", BUTTON_CALL_WORLD_ATTRIBUTES, true));
        componentList.add(new GuiButton(width - 120 * 2, 0, 30 * 2, 30 * 2, BUTTON_SELECT_AMMO, true, edit.getBulletType().getTexture()));
        componentList.add(new GuiButton(width - 90 * 2, 0, 30 * 2, 30 * 2, BUTTON_MAG_GLASS, true, ImageStorage.mag_glass));
        componentList.add(new GuiButton(width - 60 * 2, 0, 30 * 2, 30 * 2, "Save", BUTTON_SAVE, true));
        componentList.add(new GuiButton(width - 30 * 2, 0, 30 * 2, 30 * 2, "Test", BUTTON_TEST, true));

        componentList.add(new GuiButton(width / 2 - 100 * 2, height / 2 - 70 * 2, 200 * 2, 70 * 2, "Resume!", BUTTON_RESUME, false));
        componentList.add(new GuiButton(width / 2 - 100 * 2, height / 2, 200 * 2, 70 * 2, "Exit!", BUTTON_EXIT, false));
        componentList.add(new GuiButton(width / 2 - 100 * 2, height / 2 - 50 * 2, 100 * 2, 50 * 2, "No Save!", BUTTON_CONFIRM_NO_SAVE, false));
        componentList.add(new GuiButton(width / 2, height / 2 - 50 * 2, 100 * 2, 50 * 2, "Save!", BUTTON_CONFIRM_WITH_SAVE, false));

        componentList.add(new GuiButton(0 * 2, height - 30 * 2, 72 * 2, 30 * 2, "Menu", BUTTON_MENU, true));

        //CONTEXT MENU SELECT - COLLISION
        componentList.add(new GuiButton.GuiButtonSwitchONOFF(width - 100 * 2, height - 40 * 2, 100 * 2, 40 * 2, "Dragg-Trans:", BUTTON_TOGGLE_TRANSLATION_DRAGGING, false, false));
        componentList.add(new GuiButton.GuiButtonSwitch(width - 100 * 2, height - 40 * 2 * 2, 100 * 2, 40 * 2, BUTTON_TOGGLE_COLLISION_TYPE, false, Arrays.asList("Unbreakable", "Breakable"), 0));
        componentList.add(new GuiButton(width - 100 * 2, height - 40 * 3 * 2, 100 * 2, 40 * 2, "Copy", BUTTON_COPY, false));
        componentList.add(new GuiButton(width - 60, height - 60, 60, 60, BUTTON_ACCEPT_COPY, false, ImageStorage.green_check_mark));

        int arrowWidth = 10, arrowHeight = 30;

        componentList.add(new GuiSlideArrow(0, 0, arrowWidth, arrowHeight, false, (float) Math.toRadians(0), ARROW_TOP, this));
        componentList.add(new GuiSlideArrow(0, 0, arrowWidth, arrowHeight, false, (float) Math.toRadians(-90), ARROW_RIGHT, this));
        componentList.add(new GuiSlideArrow(0, 0, arrowWidth, arrowHeight, false, (float) Math.toRadians(180), ARROW_BOTTOM, this));
        componentList.add(new GuiSlideArrow(0, 0, arrowWidth, arrowHeight, false, (float) Math.toRadians(90), ARROW_LEFT, this));

        //CONTEXT MENU SELECT - TANK
        componentList.add(new GuiNumberField(width - 300, height - 60, 300, 60, TEXT_FIELD_TANK_HEALTH, false, 5, "Tank's Health", this, "", true));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 2, 300, 60, TEXT_FIELD_TANK_TRACKING_RANGE, false, 5, "Tracking Range", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 3, 300, 60, TEXT_FIELD_SHOOTING_COOLDOWN, false, 5, "Shooting Cooldown in ticks", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 4, 300, 60, TEXT_FIELD_TANK_TRIPLE_SHOOTER_AMT_OF_BULLETS, false, 5, "Amount of bullets", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 5, 300, 60, TEXT_FIELD_TANK_TRIPLE_SHOOTER_SHOOTING_FIELD, false, 3, "Radius", this, "", false));

        componentList.add(new GuiNumberField(width - 300, height - 60 * 6, 300, 60, TEXT_FIELD_PLAYER_ALLOWED_BULLETS, false, 3, "Shots before Cooldown", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 7, 300, 60, TEXT_FIELD_PLAYER_COOL_DOWN, false, 5, "Cooldown in ticks", this, "", false));

        componentList.add(new GuiNumberField(width - 300, height - 60 * 8, 300, 60, TEXT_FIELD_DAMAGE, false, 5, "Damage per hit", this, "", true));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 9, 300, 60, TEXT_FIELD_BULLET_BOUNCES, false, 5, "Bullet Bounces", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 10, 300, 60, TEXT_FIELD_TANK_SPEED, false, 5, "Tank Speed", this, "", true));

        componentList.add(new GuiNumberField(width - 300, height - 60 * 11, 300, 60, TEXT_FIELD_TANK_LASER_SHOOTER_CHARGE_TIME, false, 5, "Charge time in ticks", this, "", false));
        componentList.add(new GuiNumberField(width - 300, height - 60 * 12, 300, 60, TEXT_FIELD_TANK_LASER_SHOOTER_LASER_TIME, false, 5, "Laser time in ticks", this, "", true));
    }

    /**
     * A method to quick check bounds
     */
    public boolean isAllInsideWorld() {
        return CLICK_X >= 0 && CLICK_Y >= 0 && CLICK_X <= edit.getWidth() && CLICK_Y <= edit.getHeight() && CURRENT_X >= 0 && CURRENT_Y >= 0 && CURRENT_X <= edit.getWidth() && CURRENT_Y <= edit.getHeight();
    }

    public boolean isInsideWorld(MouseInfo info) {
        return edit.isInsideWorld(info.getMouseX(), info.getMouseY());
    }

    public void showArrows(boolean show) {
        for (int i = 0; i < 8; i++) {
            GuiSlideArrow arrow = getArrowByID(i);
            if (arrow != null) arrow.setShown(show);
        }
    }

    /**
     * Called when the position of the rawWorldCamera changes or the zoom
     */
    public void translationChanged() {
        rawWorldCamera.update();
    }

    public MouseInfo getMInfo() {
        return new MouseInfo(calcScaledCoordX(input.getX()), calcScaledCoordY(input.getY()), 0, rawWorldCamera);
    }

    public MouseInfo getMInfo(float x, float y) {
        return new MouseInfo(x, y, 0, rawWorldCamera);
    }


    public boolean isTankPlaceTool(LevelEditorTool tool) {
        return tool.getTankType() != null;
    }

    public boolean isGamePaused() {
        return paused || confirm_exit_state;
    }

    float arrowX, arrowY;

    @Override
    public void componentClicked(GuiComponent component, int mouseX, int mouseY) {
        if (component instanceof GuiSlideArrow) {
            arrowX = mouseX;
            arrowY = mouseY;
        }
    }

    @Override
    public void onArrowDragged(int arrowID, int prevX, int prevY, int cX, int cY) {
        super.onArrowDragged(arrowID, prevX, prevY, cX, cY);
        showArrows(false);
        getArrowByID(arrowID).setShown(true);
        MouseInfo cInfo = getMInfo();
        MouseInfo prevInfo = getMInfo(calcScaledCoordX(arrowX), calcScaledCoordY(arrowY));
        int dX = MathHelper.getNextDividerDown((int) (cInfo.getMouseX() - prevInfo.getMouseX()), COLLISION_SIZE);
        int dY = MathHelper.getNextDividerDown((int) (cInfo.getMouseY() - prevInfo.getMouseY()), COLLISION_SIZE);
        List<MovableObject> l = edit.getSelectedObjects();
        if (l.size() == 0) return;
        MovableObject selected = l.get(0);

        if (selected != null && selected.isResizeAble()) {
            Collision2D collision = selected.collision.copy();
            switch (arrowID) {
                case ARROW_TOP:
                    if (dY >= COLLISION_SIZE || dY <= -COLLISION_SIZE) {
                        arrowY = input.getY();
                        collision.stretchInDirection(UP, dY);
                    }
                    break;
                case ARROW_RIGHT:
                    if (dX >= COLLISION_SIZE || dX <= -COLLISION_SIZE) {
                        arrowX = input.getX();
                        collision.stretchInDirection(RIGHT, dX);
                    }
                    break;
                case ARROW_BOTTOM:
                    if (dY >= COLLISION_SIZE || dY <= -COLLISION_SIZE) {
                        arrowY = input.getY();
                        collision.stretchInDirection(DOWN, -dY);
                    }
                    break;
                case ARROW_LEFT:
                    if (dX >= COLLISION_SIZE || dX <= -COLLISION_SIZE) {
                        arrowX = input.getX();
                        collision.stretchInDirection(LEFT, -dX);
                    }
                    break;

            }
            if (edit.spaceClear(collision, Collections.singletonList(selected))) {
                selected.collision = collision;
            }
        }
    }

    public void changeTool(LevelEditorTool tool) {
        contextMenu(this.tool, false, null);
        switch (this.tool) {
            case TOOL_SELECT:
                showArrows(false);
                copiedObject = null;
                break;
        }
        this.tool = tool;
    }

    public void contextMenu(LevelEditorTool tool, boolean activate, MovableObject object) {
        switch (tool) {
            case TOOL_SELECT:
                GuiButton.GuiButtonSwitchONOFF transDragging = (GuiButton.GuiButtonSwitchONOFF) getButtonByID(BUTTON_TOGGLE_TRANSLATION_DRAGGING);
                GuiButton.GuiButtonSwitch collisionType = (GuiButton.GuiButtonSwitch) getButtonByID(BUTTON_TOGGLE_COLLISION_TYPE);
                GuiButton copy = getButtonByID(BUTTON_COPY);
                if (activate) {
                    if (object instanceof MovableCollision) {
                        MovableCollision collision = (MovableCollision) object;
                        transDragging.setOn(false).setShown(true);
                        collisionType.setCurrentState(collision.borderType == WorldBorder.BorderType.UNBREAKABLE ? 0 : 1).setShown(true);
                        copy.setShown(true);
                        showArrows(false);
                    }
                } else {
                    transDragging.setShown(false);
                    collisionType.setShown(false);
                    showArrows(false);
                    copy.setShown(false);
                    List<MovableObject> selectedObjects = edit.getSelectedObjects();
                    for (MovableObject selectedObject : selectedObjects) {
                        if (selectedObject != object) {
                            selectToolObjectInteraction(false, selectedObject, selectedObjects.size() == 1);
                        }
                    }
                }
                break;
            case TOOL_COPY:
                getButtonByID(BUTTON_ACCEPT_COPY).setShown(activate);
                break;
        }

    }

    boolean editingObjectSettings;

    public void selectToolObjectInteraction(boolean activated, MovableObject object, boolean onlyOneToBeActivated) {
        if (onlyOneToBeActivated || !activated) {
            editingObjectSettings = activated;
            if (object != null) {
                if (object instanceof MovableTank) {
                    MovableTank tank = (MovableTank) object;
                    GuiTextField healthField = getTextFieldByID(TEXT_FIELD_TANK_HEALTH);
                    GuiTextField trackingRangeField = getTextFieldByID(TEXT_FIELD_TANK_TRACKING_RANGE);
                    GuiTextField shootingCooldown = getTextFieldByID(TEXT_FIELD_SHOOTING_COOLDOWN);
                    GuiTextField triple_attacker_amtOfBullets = getTextFieldByID(TEXT_FIELD_TANK_TRIPLE_SHOOTER_AMT_OF_BULLETS);
                    GuiTextField triple_attacker_shootingField = getTextFieldByID(TEXT_FIELD_TANK_TRIPLE_SHOOTER_SHOOTING_FIELD);
                    GuiTextField player_allowedShotsBeforeCooldown = getTextFieldByID(TEXT_FIELD_PLAYER_ALLOWED_BULLETS);
                    GuiTextField player_cooldownTicks = getTextFieldByID(TEXT_FIELD_PLAYER_COOL_DOWN);
                    GuiTextField damageField = getTextFieldByID(TEXT_FIELD_DAMAGE);
                    GuiTextField bulletBounces = getTextFieldByID(TEXT_FIELD_BULLET_BOUNCES);
                    GuiTextField tankSpeed = getTextFieldByID(TEXT_FIELD_TANK_SPEED);
                    GuiTextField laser_chargeTime = getTextFieldByID(TEXT_FIELD_TANK_LASER_SHOOTER_CHARGE_TIME);
                    GuiTextField laser_laserTime = getTextFieldByID(TEXT_FIELD_TANK_LASER_SHOOTER_LASER_TIME);

                    int y = height;

                    if (tank.getTankType().getTank() instanceof Entity) {
                        healthField.setY(y -= 60);
                        healthField.setShown(activated);
                        healthField.setText(tank.health + "");

                        damageField.setShown(activated);
                        damageField.setY(y -= 60);
                        damageField.setText(tank.damage + "");

                        tankSpeed.setShown(activated);
                        tankSpeed.setY(y -= 60);
                        tankSpeed.setText(tank.speed + "");

                        if (tank.getTankType().getTank() instanceof EntityEnemyTank) {
                            trackingRangeField.setShown(activated);
                            trackingRangeField.setY(y -= 60);
                            trackingRangeField.setText(tank.trackingRange + "");

                            shootingCooldown.setShown(activated);
                            shootingCooldown.setY(y -= 60);
                            shootingCooldown.setText(tank.shootingCooldown + "");

                        }
                        if (tank.getTankType() == TankType.TANK_PLAYER) {
                            player_allowedShotsBeforeCooldown.setShown(activated);
                            player_allowedShotsBeforeCooldown.setY(y -= 60);
                            player_allowedShotsBeforeCooldown.setText(tank.player_shotsBeforeCooldown + "");

                            player_cooldownTicks.setShown(activated);
                            player_cooldownTicks.setY(y -= 60);
                            player_cooldownTicks.setText(tank.player_cooldownInTicks + "");
                        }
                        if (tank.getTankType() == TankType.TANK_TRIPLE_ATTACKER || (tank.getTankType() == TankType.TANK_PLAYER && edit.bulletType == BulletType.TRIPLE_BULLET)) {
                            triple_attacker_amtOfBullets.setShown(activated);
                            triple_attacker_amtOfBullets.setY(y -= 60);
                            triple_attacker_amtOfBullets.setText(tank.triple_attacker_player_amtOfBullets + "");

                            triple_attacker_shootingField.setShown(activated);
                            triple_attacker_shootingField.setY(y -= 60);
                            triple_attacker_shootingField.setText(tank.triple_attacker_player_field_of_shooting + "");
                        }
                        if (tank.getTankType().getTank().isStandardBulletShooter()) {
                            bulletBounces.setShown(activated);
                            bulletBounces.setY(y -= 60);
                            bulletBounces.setText(tank.bulletBounces + "");
                        }
                        if (tank.getTankType() == TankType.TANK_LASER_SHOOTER) {
                            laser_chargeTime.setShown(activated);
                            laser_chargeTime.setY(y -= 60);
                            laser_chargeTime.setText(tank.laser_shooter_chargeTime + "");

                            laser_laserTime.setShown(activated);
                            laser_laserTime.setY(y -= 60);
                            laser_laserTime.setText(tank.laser_shooter_laserTime + "");
                        }
                    }
                }
            }
        }

    }

    public void renderGuiMovableObjectSelected(MovableObject object, boolean onlyOneToBeSelected, ShapeRenderer renderer) {
        if (object instanceof MovableTank) {
            MovableTank tank = (MovableTank) object;
            if (onlyOneToBeSelected) {
                if (tank.getTankType().getTank() instanceof EntityEnemyTank) {
                    renderer.identity();
                    renderer.begin(ShapeRenderer.ShapeType.Line);
                    renderer.setColor(Color.CYAN);
                    renderer.circle(object.getX() + object.getWidth() / 2, object.getY() + object.getHeight() / 2, tank.trackingRange);
                    renderer.end();
                }
            }
        }
    }

    @Override
    public void textFieldDeactivated(GuiTextField field) {
        List<MovableObject> selectedObjects = edit.getSelectedObjects();
        if (selectedObjects.size() == 1) {
            MovableObject selected = selectedObjects.get(0);
            if (selected instanceof MovableTank) {
                MovableTank tank = (MovableTank) selected;
                try {
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_HEALTH)) {
                        tank.health = Float.parseFloat(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_TRACKING_RANGE)) {
                        tank.trackingRange = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_SHOOTING_COOLDOWN)) {
                        tank.shootingCooldown = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_TRIPLE_SHOOTER_AMT_OF_BULLETS)) {
                        tank.triple_attacker_player_amtOfBullets = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_TRIPLE_SHOOTER_SHOOTING_FIELD)) {
                        tank.triple_attacker_player_field_of_shooting = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_PLAYER_ALLOWED_BULLETS)) {
                        tank.player_shotsBeforeCooldown = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_PLAYER_COOL_DOWN)) {
                        tank.player_cooldownInTicks = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_DAMAGE)) {
                        tank.damage = Float.parseFloat(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_BULLET_BOUNCES)) {
                        tank.bulletBounces = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_SPEED)) {
                        tank.speed = Float.parseFloat(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_LASER_SHOOTER_CHARGE_TIME)) {
                        tank.laser_shooter_chargeTime = Integer.parseInt(field.convertText());
                    }
                    if (field == getTextFieldByID(TEXT_FIELD_TANK_LASER_SHOOTER_LASER_TIME)) {
                        tank.laser_shooter_laserTime = Integer.parseInt(field.convertText());
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void moveCopyObject(float x, float y) {
        Collision2D coll = copiedObject.collision.copy();
        int f = MathHelper.getNextDividerDown((int) (x - coll.getWidth() / 2), COLLISION_SIZE);
        int g = MathHelper.getNextDividerDown((int) (y - coll.getHeight() / 2), COLLISION_SIZE);
        copiedObject.setPosition(f, g);
    }
}
