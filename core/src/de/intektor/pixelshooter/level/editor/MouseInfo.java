package de.intektor.pixelshooter.level.editor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Intektor
 */
public class MouseInfo {

    private float mouseX, mouseY, mouseZ;
    private Camera camera;


    public MouseInfo(float mouseX, float mouseY, float mouseZ, Camera camera) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseZ = mouseZ;
        this.camera = camera;
    }

    public float getMouseX() {
        return camera.unproject(new Vector3(mouseX, mouseY, mouseZ)).x;
    }

    public float getMouseY() {
        return camera.unproject(new Vector3(mouseX, mouseY, mouseZ)).y;
    }

    /**
     * This is used for calculating in 3D
     */
    public float getMouseZ() {
        return camera.unproject(new Vector3(mouseX, mouseY, mouseZ)).z;
    }

    @Override
    public String toString() {
        return "( " + getMouseX() + " | " + getMouseY() +  " | " + getMouseZ() + " )";
    }
}
