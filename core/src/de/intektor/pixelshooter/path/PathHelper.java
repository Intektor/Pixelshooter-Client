package de.intektor.pixelshooter.path;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import de.intektor.pixelshooter.abstrct.PositionHelper;
import de.intektor.pixelshooter.ai.BasicNode;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.world.World;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Point3f;
import java.util.List;

/**
 * @author Intektor
 */
public class PathHelper {

    public static GraphPath<BasicNode> findAStarPathEntity(PathTraveller traveller, BasicNode endNode) {
        return findAStarPath(traveller, endNode);
    }

    public static GraphPath<BasicNode> findAStarPathEntityToOtherEntityOrAround(PathTraveller traveller, Entity toFind, float radAround) {
        World world = traveller.getTravellerWorld();
        Point3f prevSearchPos = toFind.getMid();
        Point2i realSearchPos = new Point2i(((int) prevSearchPos.x), ((int) prevSearchPos.z));
        if (world.getNextNodeForPosition(realSearchPos.x, realSearchPos.y, traveller.getGraphPath()) == null) {
            List<Point2f> allPointsInRadius = PositionHelper.getAllPointsInRadius(new Point2f(prevSearchPos.x, prevSearchPos.z), radAround, 1);
            for (Point2f point : allPointsInRadius) {
                GraphPath<BasicNode> path = findAStarPathEntity(traveller, world.getNextNodeForPosition((int) point.x, (int) point.y, traveller.getGraphPath()));
                if (path != null) {
                    return path;
                }
            }
        } else {
            return findAStarPathEntityToOtherEntity(traveller, toFind);
        }
        return null;
    }

    public static GraphPath<BasicNode> findAStarPathEntityToOtherEntity(PathTraveller traveller, Entity toFind) {
        return findAStarPathEntity(traveller, traveller.getTravellerWorld().getNextNodeForEntityMid(toFind, traveller.getGraphPath()));
    }

    public static GraphPath<BasicNode> findAStarPath(PathTraveller traveller, BasicNode endNode) {
        IndexedAStarPathFinder<BasicNode> pathFinder = new IndexedAStarPathFinder<BasicNode>(traveller.getGraphPath());
        PathFinderRequest<BasicNode> request = traveller.getTravellerWorld().calculatePathFindingRequestForPathTraveller(traveller, endNode);
        if (request.startNode == null) return null;
        pathFinder.searchNodePath(request.startNode, request.endNode, request.heuristic, request.resultPath);
        return request.resultPath;
    }

    public static void setMotionToStep(BasicNode node, Entity entity, float speed) {
        float atan = (float) Math.atan2(node.y - entity.getMid().z, node.x - entity.getMid().x);
        entity.motionX = (float) Math.cos(atan) * speed;
        entity.motionZ = (float) Math.sin(atan) * speed;
    }
}
