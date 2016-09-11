package de.intektor.pixelshooter.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.intektor.pixelshooter.ai.BasicNode;

import java.util.List;

/**
 * @author Intektor
 */
public class WorldIndexedGraph implements IndexedGraph<BasicNode> {

    public final Table<Integer, Integer, BasicNode> nodeTable;
    public final int distance;

    public WorldIndexedGraph(Table<Integer, Integer, BasicNode> nodeTable, int distance) {
        this.nodeTable = nodeTable;
        this.distance = distance;
    }

    @Override
    public int getIndex(BasicNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return nodeTable.values().size();
    }

    @Override
    public Array<Connection<BasicNode>> getConnections(BasicNode fromNode) {
        return fromNode.getCurrentConnections();
    }
}
