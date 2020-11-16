package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Graph;
import uk.ac.ed.inf.aqmaps.pathfinding.SpatialTreeSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

public class DiscreteStepAndAngleGraph implements Graph {

    private Polygon boundary;

    /**
     * The angle system needs to allow for each possible angle to have a "complement angle" which takes
     * you back to where you started if you moved in its direction after steping in any possible angle.
     * the min and max angle need to cover a range of 360 degrees - the angle increment .
     * @param minAngle the highest angle (angles will loop around this value)
     * @param maxAngle the smallest angle
     * @param angleIncrement the size of the smallest angle increment in the graph (needs to divide into 180)
     * @param moveLength
     * @param obstacles
     * @param boundary the polygon bounds of the world
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
    }


    public Collection<Obstacle> getObstacles(){
        return obstacles;
    }

    @Override
    public  List<SpatialTreeSearchNode> getNeighbouringNodes(SpatialTreeSearchNode node) {

        var output = new ArrayList<SpatialTreeSearchNode>();
        var directions = getValidDirectionsBetween(MIN_ANGLE, MAX_ANGLE);

        Coordinate currentPoint = node.getLocation();
        for (Integer dir : directions) {
            

            Coordinate neighbourCoordinate = stepInDirection(dir, currentPoint);

            Point neighbourPoint = GeometryUtilities.geometryFactory.createPoint(neighbourCoordinate);

            // bounds check
            if(boundary != null && !neighbourPoint.within(boundary))
                continue;

            // obstacles check
            boolean intersectsObstacle = false;
            for (Obstacle obstacle : obstacles) {
                
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


    public List<Integer> getValidDirectionsBetween(int lowAngle, int highAngle){
        
        var output = new ArrayList<Integer>();
        int currAngle = getClosestValidAngle(lowAngle);
        
        if(currAngle >= lowAngle){
            output.add(currAngle);
        }

        int angles = ((highAngle - lowAngle) / ANGLE_INCREMENT) + 1;
        while(output.size() < angles){
            currAngle = getClosestValidAngle(currAngle + ANGLE_INCREMENT);
            output.add(currAngle);
        }

        return output;
    }



    private final int MIN_ANGLE;
    private final int MAX_ANGLE;
    private final double MOVE_LENGTH;
    private final int ANGLE_INCREMENT;
    private final Collection<Obstacle> obstacles;
}
