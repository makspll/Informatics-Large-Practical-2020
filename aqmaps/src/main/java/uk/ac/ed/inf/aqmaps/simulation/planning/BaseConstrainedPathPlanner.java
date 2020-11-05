package uk.ac.ed.inf.aqmaps.simulation.planning;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

public abstract class BaseConstrainedPathPlanner implements PathPlanner {

    public BaseConstrainedPathPlanner(double moveLength, double readingRange, int maxMoves, int minAngle, int maxAngle, int angleIncrement) {
        this.MOVE_LENGTH = moveLength;
        this.READING_RANGE = readingRange;
        this.MAX_MOVES = maxMoves;
        this.MIN_ANGLE = minAngle;
        this.MAX_ANGLE = maxAngle;
        this.ANGLE_INCREMENT = angleIncrement;
    }
    
    protected Coordinate stepInDirection(int direction, Coordinate point){

        // the direction is a multiple of angleIncrement between minAngle and maxAngle inclusive
        assert direction <= MAX_ANGLE 
            && direction >= MIN_ANGLE 
            && direction % ANGLE_INCREMENT == 0;
        
        // we translate the current position coordinate by 
        // the vector heading in the given direciton
        Vector2D heading = MathUtilities.getHeadingVector(direction);

        return heading.multiply(MOVE_LENGTH).translate(point);
    }

    protected int getClosestValidAngle(double direction){
        return ((int)Math.round(direction / ANGLE_INCREMENT) * ANGLE_INCREMENT) % MAX_ANGLE;
    }

    protected boolean canReadSensorFrom(Sensor sensor, Coordinate coordinates){
        return coordinates.distance(sensor.getCoordinates()) < READING_RANGE;
    }


    protected Coordinate currentPosition;

    protected final double MOVE_LENGTH;
    protected final double READING_RANGE;
    protected final int MAX_MOVES;
    protected final int MIN_ANGLE;
    protected final int MAX_ANGLE;
    protected final int ANGLE_INCREMENT;

}
