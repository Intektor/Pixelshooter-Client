package de.intektor.pixelshooter.render;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.ai.BasicNode;
import de.intektor.pixelshooter.collision.Collision2D;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.Tank;
import de.intektor.pixelshooter.util.FontHelper;
import de.intektor.pixelshooter.world.EditingWorld;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;

/**
 * @author Intektor
 */
public class RenderHelper {

    public static void renderTank2D(ShapeRenderer renderer, float x, float y, float width, float height, double barrelRotation, Tank tank, float bodyRotation) {
        renderer.identity();
        renderer.set(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(tank.getTankColor());
        renderer.translate(x + width / 2, y + height / 2, 0);
        renderer.rotate(0, 0, 0.1f, (float) (bodyRotation * 180 / Math.PI));
        renderer.rect(-width / 2, -height / 2, width, height);

        renderer.identity();

        renderer.setColor(tank.getBarrelColor());
        float rw = 10 * (width / 10);
        float rh = 2 * (height / 10);
        float rx = x + width / 2;
        float ry = y + height / 2;

        renderer.translate(rx, ry, 0);
        renderer.rotate(0, 0, 1f, (float) (barrelRotation * 180 / Math.PI));
        renderer.rect(-rw / 10, -rh / 2, rw, rh);
    }

    public static void renderTank3D(ModelBatch batch, Camera camera, Tank tank, Environment environment, Entity entity) {
        batch.begin(camera);

        ModelInstance i = tank.getModelInstance()[0];
        i.materials.get(0).set(ColorAttribute.createDiffuse(tank.getTankColor()), new BlendingAttribute(entity.opacity));
        i.transform.rotateRad(Vector3.Y, -entity.getBaseRotation());
        batch.render(i, environment);

        i = tank.getModelInstance()[1];
        i.materials.get(0).set(ColorAttribute.createDiffuse(tank.getTankColor()), new BlendingAttribute(entity.opacity));
        i.transform.rotateRad(Vector3.Y, -entity.getLookRotation());
        batch.render(i, environment);

        i = tank.getModelInstance()[2];
        i.materials.get(0).set(ColorAttribute.createDiffuse(tank.getBarrelColor()), new BlendingAttribute(entity.opacity));
        float lookX = tank.getBarrelLength() / 2 * entity.lookCos;
        float lookZ = tank.getBarrelLength() / 2 * entity.lookSin;
        float tX = entity.posX + entity.getWidth() / 2 + lookX;
        float tY = entity.posY + entity.getHeight() - 2.7f;
        float tZ = entity.posZ + entity.getDepth() / 2 + lookZ;
        i.transform.translate(tX, tY, tZ).rotateRad(Vector3.Y, (float) (-entity.getLookRotation() - Math.PI / 2));
        batch.render(i, environment);

        batch.end();

    }

    private static SpriteBatch spriteBatch = new SpriteBatch();

    public static void renderSquare(ShapeRenderer renderer, Color renderColor, float x, float y, float width, float height) {
        renderer.identity();
        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(renderColor);
        renderer.rect(x, y, width, height);
    }

    public static void renderRectangle(ShapeRenderer renderer, Color color, Rectangle rectangle) {
        renderSquare(renderer, color, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    static PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch();

    public static void renderCollision(Matrix4 projection, EditingWorld.BackGroundType backgroundType, WorldBorder.BorderType borderType, Collision2D collision) {
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(projection);
        spriteBatch.draw(backgroundType.getCollisionTexture(borderType), collision.getX(), collision.getY(), collision.getWidth(), collision.getHeight());
        spriteBatch.end();
    }

    public static void drawString(float x, float y, String string, BitmapFont font, Batch batch) {
        drawString(x, y, string, font, batch, true);
    }

    public static void drawString(float x, float y, String string, BitmapFont font, Batch batch, boolean centerString) {
        drawString(x, y, string, font, batch, centerString, centerString);
    }

    public static void drawString(float x, float y, String string, BitmapFont font, Batch batch, boolean centerX, boolean centerY) {
        PixelShooter.layout.setText(font, string);
        float rx = centerX ? x - PixelShooter.layout.width / 2 : x;
        float ry = centerY ? y + PixelShooter.layout.height / 2 : y;
        font.draw(batch, string, rx, ry);
    }

    public static void drawSplitString(float x, float y, float width, String string, BitmapFont font, Batch batch,
                                       int fontHeight) {
        int i = 0;
        for (String s : FontHelper.splitString(string, width, font)) {
            drawString(x, y - (i * fontHeight), s, font, batch, false);
            i++;
        }
    }

    public static void renderPolygon3D(Camera camera, Polygon polygon, Color color) {
        lineRenderer.begin(camera.combined, GL30.GL_LINES);
        float[] f = polygon.getTransformedVertices();
        for (int i = 0; i < f.length / 2; i++) {
            lineRenderer.color(color);
            lineRenderer.vertex(f[i / 2], 0, f[i / 2 + 1]);
        }
        lineRenderer.end();
    }

    public static void renderVector3D(Camera camera, Point3f origin, Vector3 vector) {
        renderLine3D(camera, origin, new Point3f(origin.x + vector.x * 100, origin.y + vector.y * 100, origin.z + vector.z * 100), Color.RED);
    }

    public static void renderVector2D(ShapeRenderer renderer, Vector2 vector, Point2f startPoint, float length) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.identity();
        renderer.line(startPoint.x - vector.x * (length / 2), startPoint.y - vector.y * (length / 2), startPoint.x + vector.x * (length / 2), startPoint.y + vector.y * (length / 2));
        renderer.end();
    }

    public static void renderVector2D(ShapeRenderer renderer, Vector2f vector, Point2f startPoint, float length, Color color) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.identity();
        renderer.setColor(color);
        renderer.line(startPoint.x - vector.x * length / 2, startPoint.y - vector.y * length / 2, startPoint.x + vector.x * length / 2, startPoint.y + vector.y * length / 2);
        renderer.end();
    }

    public static void renderLine2D(ShapeRenderer renderer, Point2f point1, Point2f point2, Color renderColor) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.identity();
        renderer.setColor(renderColor);
        renderer.line(point1.x, point1.y, point2.x, point2.y);
        renderer.end();
    }

    private static ImmediateModeRenderer20 lineRenderer = new ImmediateModeRenderer20(false, true, 0);

    public static void renderRay3D(Camera camera, Ray ray, float length, Color color) {
        Point3f point1 = new Point3f(ray.origin.x, ray.origin.y, ray.origin.z);
        Point3f direction = new Point3f(ray.direction.x * length, ray.direction.y * length, ray.direction.z * length);
        direction.add(point1);
        renderLine3D(camera, point1, direction, color);
    }

    public static void renderLine3D(Camera camera, Vector3 point1, Vector3 point2, Color color) {
        renderLine3D(camera, new Point3f(point1.x, point1.y, point2.z), new Point3f(point2.x, point2.y, point2.z), color);
    }

    public static void renderLine3D(Camera camera, Point3f point1, Point3f point2, Color color) {
        renderLine3D(camera, point1, point2, color, color);
    }

    public static void renderLine3D(Camera camera, Point3f point1, Point3f point2, Color color1, Color color2) {
        lineRenderer.begin(camera.combined, GL30.GL_LINES);
        lineRenderer.color(color1);
        lineRenderer.vertex(point1.x, point1.y, point1.z);
        lineRenderer.color(color2);
        lineRenderer.vertex(point2.x, point2.y, point2.z);
        lineRenderer.end();
    }

    public static void renderPath(Camera camera, GraphPath<BasicNode> path, Color color) {
        if (path == null) return;
        for (int i = 0; i < path.getCount() - 1; i++) {
            BasicNode step = path.get(i);
            BasicNode step2 = path.get(i + 1);
            Point3f point1 = new Point3f(step.x, 0, step.y);
            Point3f point2 = new Point3f(step2.x, 0, step2.y);
            renderLine3D(camera, point1, point2, color);
        }
    }


}
