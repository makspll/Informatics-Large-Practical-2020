package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

public class GreedyConstrainedPathPlanner extends BaseConstrainedPathPlanner {

    public GreedyConstrainedPathPlanner(double moveLength, double readingRange, int maxMoves, int minAngle,
            int maxAngle, int angleIncrement) {
        super(moveLength, readingRange, maxMoves, minAngle, maxAngle, angleIncrement);
    }

    @Override
    public Queue<PathSegment> planPath(Coordinate startCoordinate, Queue<Sensor> route, Collection<Obstacle> obstacles) {

        Queue<Sensor> routeCopy = new LinkedList<Sensor>(route);

        // keep track of where we last moved, pick 
        // shortest way to target while avoiding buildings (by going around their corners)
        // incrementally add to the path queue
        Queue<PathSegment> path = new LinkedList<PathSegment>();
        var currentPosition = startCoordinate;
        Sensor currSensorTarget = routeCopy.poll();
        
        // consume the route one sensor at a time
        while(currSensorTarget != null 
            && path.size() < MAX_MOVES){

            //// we build a path segment corresponding to our move each iteration
            //// a move consists of a start position, direction, 
            //// end position and the sensor read at the end position
            PathSegment currMove = null;

            // remember the start position
            var moveStartCoordinate = currentPosition;

            // work out the direction vector towards the sensor target
            Coordinate sensorCoordinate = currSensorTarget.getCoordinates();
            Vector2D vectorToTarget = Vector2D.create(currentPosition, 
                                        sensorCoordinate); 

            // work out the heading angle which will direct us closest to the sensor goal
            int heading = getClosestValidAngle(
                            MathUtilities.angleFromEast(
                                vectorToTarget));

            // get the vector which will take us in the direction of the heading
            // with length of MOVE_LENGTH
            Vector2D moveVector = MathUtilities.getHeadingVector(heading).multiply(MOVE_LENGTH);

            // translate the start coordinate of the move
            currentPosition = moveVector.translate(moveStartCoordinate);
            Coordinate moveEndCoordinate = currentPosition;
            
            // check if we reached the sensor target
            boolean haveReachedSensor = canReadSensorFrom(currSensorTarget, moveEndCoordinate);
            
            // if we have we add that to the path segment
            var reachedSensor = haveReachedSensor ? currSensorTarget : null;

            //// create the path segment and add it to the output queue
            currMove = new PathSegment(moveStartCoordinate, heading, moveEndCoordinate, reachedSensor);
            path.add(currMove);

            //// if we reached the sensor change the target
            if(haveReachedSensor)
                currSensorTarget = routeCopy.poll();
        }
        
        // sanity check
        assert path.size() <= MAX_MOVES;

        return path;
    }
    
}
