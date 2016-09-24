package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * @author Intektor
 */
public class GuiScrollBar extends GuiComponent {

    Direction direction;
    private int allWindowSize;
    private int canBeShownInWindow;
    public boolean justScrolled;

    int currentScrollAmt;

    public boolean currentlyClicked;
    int clickOffsetX, clickOffsetY;

    public GuiScrollBar(int x, int y, int width, int height, boolean isShown, Direction direction, int allWindowSize, int canBeShownInWindow) {
        super(x, y, width, height, isShown);
        this.direction = direction;
        this.allWindowSize = allWindowSize;
        this.canBeShownInWindow = canBeShownInWindow;
        if (direction == Direction.VERTICAL) {
            this.currentScrollAmt = height - getScrollToolSize();
        }
    }

    @Override
    protected void renderComponent(ShapeRenderer renderer) {
        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.rect(x, y, width, height);

        renderer.identity();

        int scrollToolSize = getScrollToolSize();
        renderer.setColor(Color.GRAY);
        switch (direction) {
            case HORIZONTAL:
                renderer.rect(x + currentScrollAmt, y, scrollToolSize, height);
                break;
            case VERTICAL:
                renderer.rect(x, y + currentScrollAmt, width, scrollToolSize);
                break;
        }
        renderer.end();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        super.updateComponent(mouseX, mouseY);
        justScrolled = false;
    }

    @Override
    public void onClickedAt(int x, int y) {
        int scrollToolSize = getScrollToolSize();
        int mY = 720 - y;
        switch (direction) {
            case HORIZONTAL:
                currentlyClicked = x >= this.x + currentScrollAmt && x <= this.x + currentScrollAmt + scrollToolSize && mY >= this.y && mY <= this.y + height;
                clickOffsetX = x - this.x - currentScrollAmt;
                break;
            case VERTICAL:
                currentlyClicked = x >= this.x && x <= this.x + width && mY >= this.y + currentScrollAmt && mY <= this.y + currentScrollAmt + scrollToolSize;
                clickOffsetY = mY - this.y - currentScrollAmt;
                break;
        }
    }

    @Override
    public void onDraggedOn(int prevX, int prevY, int cX, int cY) {
        super.onDraggedOn(prevX, prevY, cX, cY);
    }

    @Override
    public void pointerUp(int x, int y, boolean onComponent) {
        super.pointerUp(x, y, onComponent);
        currentlyClicked = false;
    }

    @Override
    public void onDragged(int prevX, int prevY, int cX, int cY) {
        super.onDragged(prevX, prevY, cX, cY);
        if (currentlyClicked) {
            justScrolled = true;
            float nX = cX - x;
            float nY = 720 - cY - y;
            float percent;
            switch (direction) {
                case HORIZONTAL:
                    percent = (nX - clickOffsetX) / (width);
                    currentScrollAmt = (int) (width * percent);
                    if (currentScrollAmt < 0) {
                        currentScrollAmt = 0;
                        clickOffsetX += cX - prevX;
                    }
                    if (currentScrollAmt > width - getScrollToolSize()) {
                        currentScrollAmt = width - getScrollToolSize();
                        clickOffsetX -= prevX - cX;
                    }
                    break;
                case VERTICAL:
                    percent = (nY - clickOffsetY) / (height);
                    currentScrollAmt = (int) (height * percent);
                    if (currentScrollAmt < 0) {
                        currentScrollAmt = 0;
                        clickOffsetY += cY - prevY;
                    }
                    if (currentScrollAmt > height - getScrollToolSize()) {
                        currentScrollAmt = height - getScrollToolSize();
                        clickOffsetY -= prevY - cY;
                    }
                    break;
            }
        }
    }

    public void checkScroll() {
        switch (direction) {
            case HORIZONTAL:
                if (currentScrollAmt < 0) {
                    currentScrollAmt = 0;
                }
                if (currentScrollAmt > width - getScrollToolSize()) {
                    currentScrollAmt = width - getScrollToolSize();
                }
                break;
            case VERTICAL:
                if (currentScrollAmt < 0) {
                    currentScrollAmt = 0;
                }
                if (currentScrollAmt > height - getScrollToolSize()) {
                    currentScrollAmt = height - getScrollToolSize();
                }
                break;
        }
    }

    public float getScrollPercent() {
        switch (direction) {
            case HORIZONTAL:
                return 1 - ((float) currentScrollAmt) / (float) (width - getScrollToolSize());
            case VERTICAL:
                return 1 - ((float) currentScrollAmt) / (float) (height - getScrollToolSize());
        }
        return Float.MIN_VALUE;
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    public int getScrollToolSize() {
        switch (direction) {
            case HORIZONTAL:
                return (int) (width * ((float) canBeShownInWindow / allWindowSize));
            case VERTICAL:
                return (int) (height * ((float) canBeShownInWindow / allWindowSize));
        }
        return Integer.MIN_VALUE;
    }

    public void setAllWindowSize(int allWindowSize) {
        this.allWindowSize = allWindowSize;
        if (direction == Direction.VERTICAL) {
            this.currentScrollAmt = height - getScrollToolSize();
        } else {
            this.currentScrollAmt = width - getScrollToolSize();
        }
    }

    public void addAllWindowSize(int addition) {
        float relation = (this.allWindowSize + (float) addition) / this.allWindowSize;
        currentScrollAmt = height - getScrollToolSize();
        currentScrollAmt /= relation;
        this.allWindowSize += addition;
    }

    public int getAllWindowSize() {
        return allWindowSize;
    }

    public void setScrollPercent(float amount) {
        switch (direction) {
            case HORIZONTAL:
                currentScrollAmt = -(int) (1 - amount * (width - getScrollToolSize()));
                break;
            case VERTICAL:

                break;
        }
    }
}
