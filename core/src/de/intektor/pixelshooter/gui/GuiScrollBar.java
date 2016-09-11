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
                renderer.rect(x + currentScrollAmt, y, width, scrollToolSize);
                break;
            case VERTICAL:
                renderer.rect(x, y + currentScrollAmt, width, scrollToolSize);
                break;
        }
        renderer.end();
    }

    @Override
    public void onClickedAt(int x, int y) {
        int scrollToolSize = getScrollToolSize();
        switch (direction) {
            case HORIZONTAL:

                clickOffsetX = x - this.x - clickOffsetX;
                if (!currentlyClicked) {
                    checkScroll();
                }
                break;
            case VERTICAL:
                int mY = 720 - y;
                currentlyClicked = x >= this.x && x <= this.x + width && mY >= this.y + currentScrollAmt && mY <= this.y + currentScrollAmt + scrollToolSize;
                clickOffsetY = mY - this.y - currentScrollAmt;
                break;
        }
    }

    @Override
    public void onDragged(int prevX, int prevY, int cX, int cY) {
        if (currentlyClicked) {
            float nX = cX - x;
            float nY = 720 - cY - y;
            float percent;
            switch (direction) {
                case HORIZONTAL:
                    percent = nX / width;
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
        return 1 - ((float) currentScrollAmt) / (float) (height - getScrollToolSize());
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    public int getScrollToolSize() {
        return (int) (height * ((float) canBeShownInWindow / allWindowSize));
    }

    public void setAllWindowSize(int allWindowSize) {
        this.allWindowSize = allWindowSize;
        if (direction == Direction.VERTICAL) {
            this.currentScrollAmt = height - getScrollToolSize();
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
}
