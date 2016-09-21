package de.intektor.pixelshooter.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.PositionHelper;
import de.intektor.pixelshooter.ai.BasicNode;
import de.intektor.pixelshooter.collision.Collision3D;
import de.intektor.pixelshooter.collision.Collisions;
import de.intektor.pixelshooter.collision.WorldBorder;
import de.intektor.pixelshooter.entity.Entity;
import de.intektor.pixelshooter.entity.EntityBullet;
import de.intektor.pixelshooter.entity.EntityPlayer;
import de.intektor.pixelshooter.helper.MathHelper;
import de.intektor.pixelshooter.level.editor.MouseInfo;
import de.intektor.pixelshooter.path.PathTraveller;
import de.intektor.pixelshooter.path.WorldIndexedGraph;
import de.intektor.pixelshooter.score.object.IScoreObject;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Intektor
 */
public class World {

    public Collisions borders;

    public List<Entity> entityList = new ArrayList<Entity>();
    public List<Entity> nextUpdate = new ArrayList<Entity>();

    public List<IScoreObject> scoreObjects = new ArrayList<IScoreObject>();

    public List<Collision3D> collisionList;
    public List<Entity> pathFinderCollidableEntities = new ArrayList<Entity>();

    public EntityPlayer thePlayer;

    public EditingWorld.BackGroundType backGroundType = EditingWorld.BackGroundType.WOODEN;

    private int width, height, worldTime;

    public PerspectiveCamera camera;

    public DecalBatch decalBatch;
    /**
     * The lightning
     */
    private Environment environment;

    //1u = 4
    public WorldIndexedGraph worldPathFinderGraphDistance_2u;
    public WorldIndexedGraph worldPathFinderGraphDistance_4u;

    public World(Collisions borders, final int width, final int height, EditingWorld.BackGroundType backGroundType) {
        this.borders = borders;
        this.width = width;
        this.height = height;
        this.backGroundType = backGroundType;
        environment = new Environment();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.far = 500;
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

        groundCube = PixelShooter.modelBuilder.createBox(width + 482, 1, height + 302, new Material(TextureAttribute.createDiffuse(backGroundType.getBackgroundTexture())), VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);
    }

    public void updateWorld() {
        worldTime++;
        entityList.addAll(nextUpdate);
        nextUpdate.clear();

        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i).isDead) {
                entityList.remove(i);
            }
        }

        for (Entity entity : entityList) {
            if (!entity.isDead) {
                entity.updateEntity(this);
            }
        }

        updateCollisionList();
    }

    public Model groundCube;

    public void renderWorld(ModelBatch batch, boolean isUpdating) {
        //Clears everything out, so we don't have any glitching things
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.set(thePlayer.getMid().x, 150, thePlayer.getMid().z + 75);
        camera.lookAt(thePlayer.getMid().x, thePlayer.posY, thePlayer.getMid().z);
        camera.update();

        batch.begin(camera);
        batch.render(new ModelInstance(groundCube, width / 2, 0, height / 2), environment);
        batch.end();


        borders.renderCollisions(batch, camera, environment);

        for (Entity entity : entityList) {
            if (!entity.isDead) {
                entity.renderEntity(batch, camera, environment);
            }
        }
        if (isUpdating) {
            decalBatch.flush();
        }

//        for (BasicNode basicNode : worldPathFinderGraphDistance_2u.nodeTable.values()) {
//            int x = basicNode.x;
//            int z = basicNode.y;
//            RenderHelper.renderLine3D(camera, new Point3f(x - 0.5f, 1, z - 0.5f), new Point3f(x + 0.5f, 1, z + 0.5f), Color.RED);
//        }

    }

    public void addEntity(Entity entity) {
        nextUpdate.add(entity);
        if (entity.collidesWithPathFinder()) {
            pathFinderCollidableEntities.add(entity);
        }
    }

    public void addCollision(WorldBorder collision) {
        borders.getBorders().add(collision);
    }

    public void addCollisions(Collisions collisions) {
        for (WorldBorder c : collisions) {
            addCollision(c);
        }
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public List<Entity> getEntityList(Class<? extends Entity> c) {
        List<Entity> list = new ArrayList<Entity>();
        for (Entity entity : entityList) {
            if (entity.getClass() == c) {
                list.add(entity);
            }
        }
        return list;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Entity getEntityAt(float x, float y, float z) {
        for (Entity entity : entityList) {
            if (entity.getCollisionBox().isPointInCollision(x, y, z) != null) {
                return entity;
            }
        }
        return null;
    }

    public List<Entity> getEntitiesAt(float x, float y, float z) {
        List<Entity> list = new ArrayList<Entity>();
        for (Entity entity : entityList) {
            if (entity.getCollisionBox().isPointInCollision(x, y, z) != null) {
                list.add(entity);
            }
        }
        return list;
    }

    public MouseInfo getMouseInfo() {
        return new MouseInfo(Gdx.input.getX(), 0, Gdx.input.getY(), camera);
    }

    public List<Entity> getEntitiesInRegion(Collision3D collision3D) {
        List<Entity> l = new ArrayList<Entity>();
        for (Entity entity : entityList) {
            if (collision3D.collidingWith(entity.getHitBox().getCollisionBox())) {
                l.add(entity);
            }
        }
        return l;
    }

    public List<WorldBorder> getCollisionsWithMaxDistance(final Point3f srcPos, float maxDistance) {
        List<WorldBorder> collision3Ds = new ArrayList<WorldBorder>();
        for (WorldBorder c : borders) {
            float distance = c.getCollisionBox().getDistance(srcPos.x, srcPos.z);
            if (distance <= maxDistance) {
                collision3Ds.add(c);
            }
        }
        Collections.sort(collision3Ds, new Comparator<WorldBorder>() {
            @Override
            public int compare(WorldBorder o1, WorldBorder o2) {
                double d1 = o1.getCollisionBox().getDistance(srcPos.x, srcPos.z);
                double d2 = o2.getCollisionBox().getDistance(srcPos.x, srcPos.z);
                return Double.compare(d1, d2);
            }
        });
        return collision3Ds;
    }

    public List<WorldBorder.Collision3DEntity> getEntityCollisions() {
        List<WorldBorder.Collision3DEntity> list = new ArrayList<WorldBorder.Collision3DEntity>(entityList.size());
        for (Entity entity : entityList) {
            if (entity instanceof EntityBullet) {
                Collision3D c = entity.getHitBox().getCollisionBox();
                c.grow(0.5f);
                list.add(entity.getHitBox());
            } else {
                list.add(entity.getHitBox());
            }
        }
        return list;
    }

    /**
     * Saves a huge amount of time in path finding
     *
     * @return a list of bounding-boxes representing the collisions
     */
    public List<Collision3D> createCollisionList() {
        List<Collision3D> list = new ArrayList<Collision3D>(borders.getBorders().size());
        for (WorldBorder collision : borders.getBorders().subList(4, borders.getBorders().size())) {
            list.add(collision.getCollisionBox());
        }
        return list;
    }

    public boolean isValidPosition(Point3f point) {
        return isValidPosition(point.x, point.y, point.z);
    }

    public boolean isValidPosition(float x, float y, float z) {
        return !isPointInCollision(x, y, z) && isPointInsideWorld(x, y, z);
    }

    public boolean isPointInsideWorld(Point3f point) {
        return isPointInsideWorld(point.x, point.y, point.z);
    }

    public boolean isPointInsideWorld(float x, float y, float z) {
        return x >= 0 && x <= width && z >= 0 && z <= height;
    }

    public boolean isPointInCollision(Point3f point) {
        return isPointInCollision(point.x, point.y, point.z);
    }

    public boolean isPointInCollision(float x, float y, float z) {
        for (Collision3D border : collisionList) {
            if (border.getBoundingBox().contains(new Vector3(x, y, z))) {
                return true;
            }
        }
        return false;
    }

    public void updateCollisionList() {
        collisionList = createCollisionList();
    }

    public int getWorldTime() {
        return worldTime;
    }

    public void finishWorld() {
        decalBatch.dispose();
    }

    public void worldChanged() {
        updateCollisionList();
        worldPathFinderGraphDistance_2u = calculatePossibleNodes(4, 1, 2, 2);
        worldPathFinderGraphDistance_4u = calculatePossibleNodes(4, 4, 0, 0);
    }

    public PathFinderRequest<BasicNode> calculatePathFindingRequestForPathTraveller(PathTraveller traveller, BasicNode endNode) {
        BasicNode startNode = getNextNodeForEntityMid((Entity) traveller, traveller.getGraphPath());
        GraphPath<BasicNode> path = new DefaultGraphPath<BasicNode>();
        return new PathFinderRequest<BasicNode>(startNode, endNode, new Heuristic<BasicNode>() {
            @Override
            public float estimate(BasicNode node, BasicNode endNode1) {
                return 1;
            }
        }, path);
    }

    public BasicNode getNextNodeForEntityMid(Entity entity, WorldIndexedGraph graphPath) {
        return getNextNodeForPosition((int) entity.getMid().x, (int) entity.getMid().z, graphPath);
    }

    public BasicNode getNextNodeForPosition(int targetX, int targetY, WorldIndexedGraph graphPath) {
        int x = MathHelper.getNextDivider(targetX, graphPath.distance) + graphPath.offsetX;
        int z = MathHelper.getNextDivider(targetY, graphPath.distance) + graphPath.offsetY;
        List<Point2f> allPointsInRadius = PositionHelper.getAllPointsInRadius(new Point2f(x, z), graphPath.distance, graphPath.distance);
        for (Point2f point : allPointsInRadius) {
            if (graphPath.nodeTable.get((int) point.x, (int) point.y) != null) {
                return graphPath.nodeTable.get((int) point.x, (int) point.y);
            }
        }
        return null;
    }

    /**
     * Calculates all the possible nodes for this world, watching borders.
     *
     * @return a future graph with all nodes while those which are in borders are already removed.
     */
    public WorldIndexedGraph calculatePossibleNodes(final int minDistance, final int collsionDistance, int offsetX, int offsetY) {
        long startTime = System.nanoTime();
        Table<Integer, Integer, BasicNode> nodes = HashBasedTable.create();
        WorldIndexedGraph graph = new WorldIndexedGraph(nodes, minDistance, offsetX, offsetY);
        List<Collision3D> collisionList = createCollisionList();
        List<Point2i> pointsInAllCollisions = new ArrayList<Point2i>();
        for (Collision3D c : collisionList) {
            pointsInAllCollisions.addAll(getNodePointsInCollision(c, minDistance, collsionDistance, offsetX, offsetY));
        }
        for (int x = offsetX; x < World.this.width; x += minDistance) {
            for (int y = offsetY; y < World.this.height; y += minDistance) {
                BasicNode e = new BasicNode(x, y);
                nodes.put(x, y, e);
            }
        }

        for (Point2i ps : pointsInAllCollisions) {
            nodes.remove(ps.x, ps.y);
        }

        int index = 0;
        //Give the remaining nodes their connections and their index

        for (BasicNode node : nodes.values()) {
            node.index = index++;
            Array<Connection<BasicNode>> connections = new Array<Connection<BasicNode>>();
            for (EnumDirection direction : EnumDirection.values()) {
                BasicNode nodeForSide = getNodeForSide(node, direction, nodes, minDistance);
                if (nodeForSide != null) {
                    connections.add(new DefaultConnection<BasicNode>(node, nodeForSide));
                }
            }
            node.setCurrentConnections(connections);
        }

        long timeTook = System.nanoTime() - startTime;
        Gdx.app.log("DEBUG", "Calculating the world took " + TimeUtils.nanosToMillis(timeTook) + "milli sec");
        return graph;
    }

    public List<Point2i> getNodePointsInCollision(Collision3D c, int distance, int cGrowAmount, int offsetX, int offsetY) {
        List<Point2i> list = new ArrayList<Point2i>();
        for (int x = MathHelper.getNextDivider((int) c.x - cGrowAmount, distance) + offsetX; x < c.x + c.width + cGrowAmount * 2; x += distance) {
            for (int z = MathHelper.getNextDivider((int) c.z - cGrowAmount, distance) + offsetY; z < c.z + c.depth + cGrowAmount * 2; z += distance) {
                list.add(new Point2i(x, z));
            }
        }
        return list;
    }


    public enum EnumDirection {
        UP,
        RIGHT,
        DOWN,
        LEFT;

        public boolean isCounterPart(EnumDirection direction) {
            switch (this) {
                case UP:
                    return direction == DOWN;
                case RIGHT:
                    return direction == LEFT;
                case DOWN:
                    return direction == UP;
                case LEFT:
                    return direction == RIGHT;
            }
            return false;
        }
    }

    public boolean nodeAvailable(BasicNode node, EnumDirection direction) {
        switch (direction) {
            case UP:
                return node.y > 0;
            case RIGHT:
                return node.x < width;
            case DOWN:
                return node.y < height;
            case LEFT:
                return node.x > 0;
        }
        throw new IllegalArgumentException("There is no such enum or we have a null pointer!");
    }

    public BasicNode getNodeForSide(BasicNode node, EnumDirection direction, Table<Integer, Integer, BasicNode> table, int distance) {
        if (!nodeAvailable(node, direction)) return null;
        switch (direction) {
            case UP:
                return table.get(node.x, node.y - distance);
            case RIGHT:
                return table.get(node.x + distance, node.y);
            case DOWN:
                return table.get(node.x, node.y + distance);
            case LEFT:
                return table.get(node.x - distance, node.y);
        }
        throw new IllegalArgumentException("There is no such enum or we have a null pointer!");
    }
}
