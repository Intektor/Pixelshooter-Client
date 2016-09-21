package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public abstract class Gui extends InputAdapter implements GestureDetector.GestureListener {

    public List<GuiComponent> componentList = new ArrayList<GuiComponent>();

    public Input input;
    protected boolean paused = false;

    protected int width;
    protected int height;
    protected long timeAtLastTap;

    public Gui() {
    }

    public void init() {
        input = Gdx.input;
        width = 1280;
        height = 720;
        InputMultiplexer multiplexer = new InputMultiplexer();
        GestureDetector detecto = new GestureDetector(this);
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(detecto);
        input.setInputProcessor(multiplexer);
        reInitButtons();
    }

    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        for (GuiComponent component : this.componentList) {
            if (component != null) {
                component.render(renderer);
            }
        }
    }

    public void update() {
        for (GuiComponent component : componentList) {
            component.updateComponent(calcScaledCoordX(input.getX()), calcScaledCoordY(input.getY()));
        }
    }

    public void reInitButtons() {
        this.componentList.clear();
        this.addGuiComponents();
    }

    public abstract void onButtonTouched(int id);

    public abstract int getID();

    public abstract void addGuiComponents();

    public GuiButton getButtonByID(int id) {
        for (GuiComponent c : componentList) {
            if (c instanceof GuiButton) {
                if (((GuiButton) c).id == id) {
                    return (GuiButton) c;
                }
            }
        }
        return null;
    }

    public GuiSlideArrow getArrowByID(int id) {
        for (GuiComponent c : componentList) {
            if (c instanceof GuiSlideArrow) {
                if (((GuiSlideArrow) c).getID() == id) {
                    return (GuiSlideArrow) c;
                }
            }
        }
        return null;
    }

    public GuiTextField getTextFieldByID(int id) {
        for (GuiComponent c : componentList) {
            if (c instanceof GuiTextField) {
                if (((GuiTextField) c).getId() == id) {
                    return (GuiTextField) c;
                }
            }
        }
        return null;
    }

    public boolean clickedOnComponent(int x, int y) {
        for (GuiComponent component : componentList) {
            if (component instanceof GuiButton) {
                if (component.isHoveredOver(x, y) && ((GuiButton) component).enabled) {
                    return true;
                }
            } else if (component instanceof GuiTextField) {
                if (component.isHoveredOver(x, y)) {
                    return true;
                }
            } else if (component instanceof GuiSlideArrow) {
                if (component.isHoveredOver(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pointerDown(calcScaledCoordX(screenX), calcScaledCoordY(screenY), pointer, button);
        return false;
    }

    /**
     * Uses already scaled mouse amounts
     */
    public void pointerDown(int x, int y, int pointer, int button) {
        for (GuiComponent component : componentList) {
            component.onClicked(x, y);
            if (component instanceof GuiButton && ((GuiButton) component).enabled) {
                if (component.isHoveredOver(x, y)) {
                    deactivateAllTextFields();
                    component.onClickedAt(x, y);
                    onButtonTouched(((GuiButton) component).id);
                    componentClicked(component, x, y);
                }
            } else if (component instanceof GuiTextField) {
                if (component.isHoveredOver(x, y)) {
                    component.onClickedAt(x, y);
                    componentClicked(component, x, y);
                }
            } else {
                if (component.isHoveredOver(x, y)) {
                    component.onClickedAt(x, y);
                    componentClicked(component, x, y);
                    deactivateAllTextFields();
                }
            }
        }
        prevX = x;
        prevY = y;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        pointerMoved(calcScaledCoordX(screenX), calcScaledCoordY(screenY));
        return false;
    }

    public void pointerMoved(int x, int y) {

    }

    @Override
    public final boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pointerUp(calcScaledCoordX(screenX), calcScaledCoordY(screenY), pointer, button);
        return false;
    }

    /**
     * Uses already scaled mouse amounts
     */
    public void pointerUp(int x, int y, int pointer, int button) {
    }

    public void deactivateAllTextFields() {
        for (GuiTextField field : getTextFields()) {
            if (field.isActive()) {
                field.setActive(false);
            }
        }
    }

    public void textFieldDeactivated(GuiTextField field) {

    }

    @Override
    public boolean keyTyped(char character) {
        for (GuiComponent guiComponent : componentList) {
            guiComponent.keyTyped(character);
        }
        return super.keyTyped(character);
    }

    @Override
    public boolean keyDown(int keycode) {
        for (GuiComponent guiComponent : componentList) {
            guiComponent.keyDown(keycode);
        }
        return super.keyDown(keycode);
    }

    public GuiTextField getActiveTextField() {
        for (GuiComponent c : componentList) {
            if (c instanceof GuiTextField) {
                if (((GuiTextField) c).isActive()) {
                    return (GuiTextField) c;
                }
            }
        }
        return null;
    }

    public List<GuiTextField> getTextFields() {
        List<GuiTextField> list = new ArrayList<GuiTextField>();
        for (GuiComponent component : componentList) {
            if (component instanceof GuiTextField) {
                list.add((GuiTextField) component);
            }
        }
        return list;
    }

    public int getNumberOfTouches() {
        int n = 0;
        for (int i = 0; i < 20; i++) {
            if (input.isTouched(i)) {
                n++;
            }
        }
        return n;
    }

    protected int prevX, prevY;

    @Override
    public final boolean touchDragged(int screenX, int screenY, int pointer) {
        pointerDragged(calcScaledCoordX(screenX), calcScaledCoordY(screenY), calcScaledCoordX(screenX - input.getDeltaX(pointer)), calcScaledCoordY(screenY - input.getDeltaY(pointer)), pointer);
        return false;
    }

    /**
     * Uses already scaled mouse amounts
     */
    public void pointerDragged(int x, int y, int prevX, int prevY, int pointer) {
        for (GuiComponent component : componentList) {
            if (component.isHoveredOver(x, y)) {
                component.onDragged(prevX, prevY, x, y);
            }
        }
        this.prevX = x;
        this.prevY = y;
    }

    public static int calcScaledCoordX(float mouseX) {
        float scaleX = 1280F / Gdx.graphics.getWidth();
        return (int) (mouseX * scaleX);
    }

    public static int calcScaledCoordY(float mouseY) {
        float scaleY = 720F / Gdx.graphics.getHeight();
        return (int) (mouseY * scaleY);
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        timeAtLastTap = System.currentTimeMillis();
        prevZoomDistance = -1;
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return true;
    }

    float prevZoomDistance;

    @Override
    public final boolean zoom(float initialDistance, float distance) {
        if (prevZoomDistance != -1) {
            zoomed(initialDistance, distance, prevZoomDistance);
        }
        prevZoomDistance = distance;
        return true;
    }

    public void zoomed(float initialDistance, float distance, float prevDistance) {

    }


    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return true;
    }

    @Override
    public void pinchStop() {
    }

    public void onArrowDragged(int arrowID, int prevX, int prevY, int cX, int cY) {
    }

    public boolean hoveredOverComponent(int x, int y) {
        for (GuiComponent component : componentList) {
            if (component.isHoveredOver(x, y)) return true;
        }
        return false;
    }

    /**
     * Returns the ID of the hovered button. Special case: -1 if no button hovered
     *
     * @param x the mouseX
     * @param y the mouseY
     * @return the id of the hovered button or -1
     */
    public int hoveredButtonID(int x, int y) {
        for (GuiComponent component : componentList) {
            if (component instanceof GuiButton && component.isHoveredOver(x, y))
                return ((GuiButton) component).id;
        }
        return -1;
    }

    public GuiComponent getHoveredComponent(int x, int y) {
        for (GuiComponent component : componentList) {
            if (component.isHoveredOver(x, y)) return component;
        }
        return null;
    }

    public void componentClicked(GuiComponent component, int mouseX, int mouseY) {
    }
}
