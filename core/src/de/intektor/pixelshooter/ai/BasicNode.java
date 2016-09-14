package de.intektor.pixelshooter.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;

/**
 * @author Intektor
 */
public class BasicNode {

    private Array<Connection<BasicNode>> currentConnections;

    public int x, y, index;

    public boolean disabled;

    public BasicNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCurrentConnections(Array<Connection<BasicNode>> currentConnections) {
        this.currentConnections = currentConnections;
    }

    public Array<Connection<BasicNode>> getCurrentConnections() {
        return currentConnections;
    }

    public Point2i getPosI() {
        return new Point2i(x, y);
    }

    public Point2f getPosF() {
        return new Point2f(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }
}

