package de.intektor.pixelshooter.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;

import javax.vecmath.Vector2f;

/**
 * @author Intektor
 */
public class GuiSlideArrow extends GuiComponent {

    private float directionAngle;
    private int ID;
    private Polygon polygon;
    private Gui gui;

    public GuiSlideArrow(int x, int y, int width, int height, boolean isShown, float directionAngle, int ID, Gui gui) {
        super(x, y, width, height, isShown);
        this.directionAngle = directionAngle;
        this.ID = ID;
        this.gui = gui;
        polygon = new Polygon(new float[]{
                0 - width / 2, 0 - height / 2,
                0 - width / 2, 0,
                -width / 2 - width / 2, 0,
                0, height - height / 2,
                width * 1.5f - width / 2, 0,
                width - width / 2, 0,
                width - width / 2, 0 - height / 2
        });
        polygon.scale(2);
        polygon.rotate((float) (directionAngle * 180 / Math.PI));
    }

    @Override
    protected void renderComponent(ShapeRenderer renderer) {
        if (!isDragged) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.identity();
            renderer.setColor(Color.BLUE);
            renderer.polygon(polygon.getTransformedVertices());
            renderer.end();
        } else {
            float f[] = polygon.getTransformedVertices();
            float length = 100000;
            Vector2f vector = new Vector2f((float) Math.cos(directionAngle + Math.PI / 2), (float) Math.sin(directionAngle + Math.PI / 2));
            Polygon rectangle = new Polygon(new float[]{
                    f[0] - vector.x * length / 2, f[1] - vector.y * length / 2,
                    f[2] + vector.x * length / 2, f[3] + vector.y * length / 2,
                    f[10] + vector.x * length / 2, f[11] + vector.y * length / 2,
                    f[12] - vector.x * length / 2, f[13] - vector.y * length / 2,
            });
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.identity();
            renderer.setColor(Color.BLUE);
            renderer.polygon(rectangle.getTransformedVertices());
            renderer.end();
        }
    }

    @Override
    public boolean isHoveredOver(int mouseX, int mouseY) {
        return !isDragged ? polygon.contains(mouseX, 720 - mouseY) && isShown() : isShown();
    }

    @Override
    public GuiComponent setPosition(int x, int y) {
        polygon.setPosition(x, y);
        return super.setPosition(x, y);
    }

    @Override
    public void onDraggedOn(int prevX, int prevY, int cX, int cY) {
        super.onDraggedOn(prevX, prevY, cX, cY);
        int dX = cX - prevX;
        int dY = cY - prevY;
        int pX = x;
        int pY = y;
        x += dX;
        y += dY;
        polygon.setPosition(x, y);
        gui.onArrowDragged(getID(), pX, pY, x, y);
    }

    public int getID() {
        return ID;
    }
}
