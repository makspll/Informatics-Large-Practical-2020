package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.awt.PointShapeFactory.Square;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Graph;
import uk.ac.ed.inf.aqmaps.pathfinding.SpatialTreeSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

public class DiscreteStepAndAngleGraph implements Graph {

    /**
     * The angle system needs to allow for each possible angle to have a "complement angle" which takes
     * you back to where you started if you moved in its direction after steping in any possible angle.
     * the min and max angle need to cover a range of 360 degrees - the angle increment .
     * @param minAngle the highest angle (angles will loop around this value)
     * @param maxAngle the smallest angle
     * @param angleIncrement the size of the smallest angle increment in the graph (needs to divide into 180)
     * @param moveLength
     * @param obstacles
     * @param boundary the polygon bounds of the world. Can be null
     */
    public DiscreteStepAndAngleGraph(int minAngle, int maxAngle, int angleIncrement, double moveLength, Collection<Obstacle> obstacles, Polygon boundary) {
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
     * @param moveLength
     * @param obstacles
     */
    public DiscreteStepAndAngleGraph(int minAngle, int maxAngle, int angleIncrement, double moveLength, Collection<Obstacle> obstacles) {
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


    public Collection<Obstacle> getObstacles(){
        return obstacles;
    }

    public Polygon getBoundary(){
        return boundary;
    }

    public double getMoveLength(){
        return MOVE_LENGTH;
    }

    @Override
    public  List<SpatialTreeSearchNode> getNeighbouringNodes(SpatialTreeSearchNode node) {

        // to get the neighbouring nodes, we HAVE to check collisions 
        // with obstacles around the map
        // we can save a lot of computation though by using 
        // bounding volume hierarchy's

        // we can completely avoid having to check any colisions by checking
        // that the the bounding box of move length width is not coliding with anything else
        var nodeLocation = node.getLocation();

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
     * finds the neighbours of the given node, only taking into account the given obstacles
     * @param node
     * @param obstaclesIncluded
     * @return
     */
    private List<SpatialTreeSearchNode> findNeighboursUsingObstacles(SpatialTreeSearchNode node,
        Collection<Obstacle> obstaclesIncluded ){

        var output = new ArrayList<SpatialTreeSearchNode>();

        Coordinate currentPoint = node.getLocation();
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

            var newNode = new SpatialTreeSearchNode(neighbourCoordinate,
                node,
                dir,
                node.getCost() + MOVE_LENGTH);

            output.add(newNode);
        }

        return output;
    }

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

    public int getClosestValidAngle(double direction){
        return ((int)Math.round(direction / ANGLE_INCREMENT) * ANGLE_INCREMENT) % (MAX_ANGLE+1);
    }


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
