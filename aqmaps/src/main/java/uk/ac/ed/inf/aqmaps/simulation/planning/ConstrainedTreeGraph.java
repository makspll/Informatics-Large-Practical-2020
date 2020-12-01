package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.SearchGraph;
import uk.ac.ed.inf.aqmaps.simulation.DirectedSearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

/**
 * A graph which imposes angle, move length and boundary (+ obstacle) constraints for the nodes, and does not keep track of already produced nodes (tree search) i.e. a new node is returned each time
 */
public class ConstrainedTreeGraph implements SearchGraph<DirectedSearchNode> {

    /**
     * The angle system needs to allow for each possible angle to have a "complement angle" which takes
     * you back to where you started if you moved in its direction after steping in any possible angle.
     * the min and max angle need to cover a range of 360 degrees - the angle increment .
     * @param minAngle the highest angle (angles will loop around this value)
     * @param maxAngle the smallest angle
     * @param angleIncrement the size of the smallest angle increment in the graph (needs to divide into 180)
     * @param moveLength the exact distance between a node and its neighbour
     * @param obstacles the obstacles present on the map
     * @param boundary the polygon bounds of the world. Can be null
     */
    public ConstrainedTreeGraph(int minAngle, int maxAngle, int angleIncrement, double moveLength, Collection<Obstacle> obstacles, Polygon boundary) {
       this(minAngle, maxAngle, angleIncrement, moveLength, obstacles);
       this.boundary = boundary;
    }

    /**
     * The angle system needs to allow for each possible angle to have a "complement angle" which takes
     * you back to where you started if you moved in its direction after steping in any possible angle.
     * the min and max angle need to cover a range of 360 degrees - the angle increment .
     * @param minAngle the highest angle (angles will loop around this value)
     * @param maxAngle the smallest angle
     * @param angleIncrement the size of the smallest angle increment in the graph (needs to divide into 180)
     * @param moveLength the exact distance between a node and its neighbour
     * @param obstacles the obstacles present on the map
     */
    public ConstrainedTreeGraph(int minAngle, int maxAngle, int angleIncrement, double moveLength, Collection<Obstacle> obstacles) {
        assert 180 % angleIncrement == 0 && maxAngle - minAngle >= 360 - angleIncrement;

        this.MIN_ANGLE = minAngle;
        this.MAX_ANGLE = maxAngle;
        this.MOVE_LENGTH = moveLength;
        this.ANGLE_INCREMENT = angleIncrement;
        this.obstacles = obstacles;
        obstacleBVHTree = new BVHNode<Obstacle>(obstacles);

        // pre-calculate possible angles
        int currAngle = MIN_ANGLE;
        
        // find the number of possible angles
        int angles = countValidAnglesBetween(MIN_ANGLE, MAX_ANGLE);
        allDirections = new int[angles];

        int i = 0;
        // first angle
        while(i < angles){
            allDirections[i] = currAngle;
            currAngle = currAngle + ANGLE_INCREMENT;
            i+= 1;
        }
    }

    /**
     * Retrieve the obstacles present on the map
     */
    public Collection<Obstacle> getObstacles(){
        return obstacles;
    }

    /**
     * Retrieve the bounds of the map
     */
    public Polygon getBoundary(){
        return boundary;
    }

    /**
     * get the distance between a node and its neighbour
     */
    public double getMoveLength(){
        return MOVE_LENGTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  List<DirectedSearchNode> getNeighbouringNodes(DirectedSearchNode node) {

        // to get the neighbouring nodes, we HAVE to check collisions 
        // with obstacles around the map
        // we can save a lot of computation though by using 
        // bounding volume hierarchy's

        // we can completely avoid having to check any colisions by checking
        // that the the bounding box of move length width is not coliding with anything else
        var nodeLocation = node.getCoordinates();

        Geometry boundingBox = GeometryUtilities.geometryFactory.createPolygon(
            new Coordinate[]{
                new Coordinate(nodeLocation.getX() - MOVE_LENGTH, nodeLocation.getY() - MOVE_LENGTH),
                new Coordinate(nodeLocation.getX() - MOVE_LENGTH, nodeLocation.getY() + MOVE_LENGTH),
                new Coordinate(nodeLocation.getX() + MOVE_LENGTH, nodeLocation.getY() + MOVE_LENGTH),
                new Coordinate(nodeLocation.getX() + MOVE_LENGTH, nodeLocation.getY() - MOVE_LENGTH),
                new Coordinate(nodeLocation.getX() - MOVE_LENGTH, nodeLocation.getY() - MOVE_LENGTH)
            }
        );


        return findNeighboursUsingObstacles(node, obstacleBVHTree.getPossibleCollisions(boundingBox));
    }

    /**
     * finds the neighbours of the given node, only taking into account the given subset of obstacles
     * @param node
     * @param obstaclesIncluded
     */
    private List<DirectedSearchNode> findNeighboursUsingObstacles(DirectedSearchNode node,
        Collection<Obstacle> obstaclesIncluded ){

        var output = new ArrayList<DirectedSearchNode>();

        Coordinate currentPoint = node.getCoordinates();
        for (Integer dir : allDirections) {
            

            Coordinate neighbourCoordinate = stepInDirection(dir, currentPoint);

            Point neighbourPoint = GeometryUtilities.geometryFactory.createPoint(neighbourCoordinate);

            // bounds check
            if(boundary != null && !neighbourPoint.coveredBy(boundary))
                continue;

            // obstacles check
            boolean intersectsObstacle = false;
            for (Obstacle obstacle : obstaclesIncluded) {
                
                if (obstacle.intersectsPath(currentPoint, neighbourCoordinate)){
                    intersectsObstacle = true;
                    break;
                }
            }

            if(intersectsObstacle)
                continue;

            var newNode = new DirectedSearchNode(neighbourCoordinate,
                node,
                dir,
                node.getCost() + MOVE_LENGTH);

            output.add(newNode);
        }

        return output;
    }

    /**
     * Creates a new coordinate by stepping in the given direction from the original point
     * @param direction
     * @param point
     * @return
     */
    private Coordinate stepInDirection(int direction, Coordinate point){

        // the direction is a multiple of angleIncrement between minAngle and maxAngle inclusive
        assert direction <= MAX_ANGLE 
            && direction >= MIN_ANGLE 
            && direction % ANGLE_INCREMENT == 0;
        
        // we translate the current position coordinate by 
        // the vector heading in the given direciton
        Vector2D heading = MathUtilities.getHeadingVector(direction);

        return heading.multiply(MOVE_LENGTH).translate(point);
    }

    /**
     * returns the closest valid angle in this graph to the given double
     * @param direction
     */
    public int getClosestValidAngle(double direction){
        return ((int)Math.round(direction / ANGLE_INCREMENT) * ANGLE_INCREMENT) % (MAX_ANGLE+1);
    }


    /**
     * Retrieves all valid directions between the given low and high angle
     * @param lowAngle the low angle
     * @param highAngle the high angle
     */
    public int[] getValidDirectionsBetween(int lowAngle, int highAngle){
        
        assert lowAngle >= MIN_ANGLE && highAngle <= MAX_ANGLE;

        // get valid low and high bounds
        int lowBound = getClosestValidAngle(lowAngle);
        int highBound = getClosestValidAngle(highAngle);



        // find index of low bound
        int idxLow = lowBound / ANGLE_INCREMENT;
        int idxHigh = highBound / ANGLE_INCREMENT;
        int n = idxHigh - idxLow + 1;

        // find number of valid angles between low and high bound
        int[] output = new int[n];

        // fill in values 
        for (int i = 0; i < n; i++) {
            output[i] = allDirections[idxLow + i];
        }

        return output;
    }

    /**
     * Returns the count of the valid angles between the given low and high angle
     * @param low the low angle
     * @param high the high angle
     * @return
     */
    private int countValidAnglesBetween(int low, int high){
        return ((high - low) / ANGLE_INCREMENT) + 1;
    }



    private final int MIN_ANGLE;
    private final int MAX_ANGLE;
    private final double MOVE_LENGTH;
    private final int ANGLE_INCREMENT;
    private final int[] allDirections;
    private Polygon boundary;
    private final BVHNode<Obstacle> obstacleBVHTree;
    private final Collection<Obstacle> obstacles;

}
