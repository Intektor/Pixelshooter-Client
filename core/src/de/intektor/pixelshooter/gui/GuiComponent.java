package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * @author Intektor
 */
public abstract class GuiComponent {

    protected int x, y, width, height, prevX, prevY;
    protected boolean isShown;
    protected boolean isHovered = false, isDragged = false;

    public GuiComponent(int x, int y, int width, int height, boolean isShown) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isShown = isShown;
    }

    public void updateComponent(int mouseX, int mouseY) {
        isHovered = isHoveredOver(mouseX, mouseY);
        if (isDragged) {
            isDragged = isHovered;
        }
    }

    public boolean isHoveredOver(int mouseX, int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && 720 - mouseY > this.y && 720 - mouseY < this.y + this.height && this.isShown;
    }

    public void render(ShapeRenderer renderer) {
        if (isShown) {
            renderComponent(renderer);
        }
    }

    protected abstract void renderComponent(ShapeRenderer renderer);

    public GuiComponent setX(int x) {
        this.prevX = this.x;
        this.x = x;
        return this;
    }

    public GuiComponent setY(int y) {
        this.prevY = this.y;
        this.y = y;
        return this;
    }

    public GuiComponent setPosition(int x, int y) {
        this.prevX = this.x;
        this.prevY = this.y;
        this.x = x;
        this.y = y;
        return this;
    }

    public GuiComponent setWidth(int width) {
        this.width = width;
        return this;
    }

    public GuiComponent setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isShown() {
        return isShown;
    }

    public GuiComponent setShown(boolean shown) {
        isShown = shown;
        return this;
    }

    public void onClickedAt(int x, int y) {

    }

    public void onClicked(int x, int y) {

    }

    public void onDragged(int prevX, int prevY, int cX, int cY) {
        isDragged = true;
    }

    public void keyTyped(char character) {

    }

    public void keyDown(int keyCode) {

    }
}
