package de.intektor.pixelshooter.path;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector3;
import de.intektor.pixelshooter.ai.BasicNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Intektor
 */
public class WorldIndexedSmoothableGraphPath implements SmoothableGraphPath<BasicNode, Vector3> {

    List<BasicNode> nodes = new ArrayList<BasicNode>();

    public WorldIndexedSmoothableGraphPath(GraphPath<BasicNode> graph) {
        for (BasicNode basicNode : graph) {
            nodes.add(basicNode);
        }
    }

    @Override
    public Vector3 getNodePosition(int index) {
        BasicNode node = nodes.get(index);
        return new Vector3(node.x, 0, node.y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        BasicNode node1 = nodes.get(index1);
        BasicNode node2 = nodes.get(index2);
        nodes.set(index1, node2);
        nodes.set(index2, node1);
    }

    @Override
    public void truncatePath(int newLength) {
        if (getCount() > newLength) {
            nodes = nodes.subList(0, newLength);
        }
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public int getCount() {
        return nodes.size();
    }

    @Override
    public void add(BasicNode node) {
        nodes.add(node);
    }

    @Override
    public BasicNode get(int index) {
        return nodes.get(index);
    }

    @Override
    public void reverse() {
        ArrayList<BasicNode> list = new ArrayList<BasicNode>();
        list.addAll(nodes);
        nodes.clear();
        for (int i = 0; i < list.size(); i--) {
            BasicNode basicNode = list.get(i);
            nodes.add(basicNode);
        }
    }

    @Override
    public Iterator<BasicNode> iterator() {
        return nodes.iterator();
    }
}
