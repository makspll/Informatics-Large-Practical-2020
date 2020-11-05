package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;

/**
 * represents a singular move made by the sensor data collector
 * each move follows the pattern of: move->collect reading, 
 * we cannot collect a reading in a move unless we have moved
 */
public class PathSegment {


    /**
     * Creates a path segment from the start and end points, the direction of movement and the sensor read if any
     * @param startPoint start coordinate 
     * @param direction direction in degrees, from the eastern direction anti-clockwise
     * @param endPoint end point 
     * @param sensorRead the sensor read at the end point, or null
     */
    public PathSegment(Coordinate startPoint, int direction, Coordinate endPoint, Sensor sensorRead) {
        this.startPoint = startPoint;
        this.direction = direction;
        this.endPoint = endPoint;
        this.sensorRead = sensorRead;
    }

    public Coordinate getStartPoint() {
        return this.startPoint;
    }

    public int getDirection() {
        return this.direction;
    }

    public Coordinate getEndPoint() {
        return this.endPoint;
    }

    public Sensor getSensorRead() {
        return this.sensorRead;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PathSegment)) {
            return false;
        }
        PathSegment move = (PathSegment) o;
        return Objects.equals(startPoint, move.startPoint) && direction == move.direction && Objects.equals(endPoint, move.endPoint) && Objects.equals(sensorRead, move.sensorRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, direction, endPoint, sensorRead);
    }

    @Override
    public String toString() {
        return "{" +
            " startPoint='" 
                + "("+(float)getStartPoint().getX()
                +","+(float)getStartPoint().getY()+")" 
            + "'" +
            ", direction='" + getDirection() + "'" +
            ", endPoint='" 
                + "("+(float)getEndPoint().getX()
                +","+(float)getEndPoint().getY()+")" 
            + "'" +
            ", sensorRead='" + getSensorRead() + "'" +
            "}";
    }
    
    private Coordinate startPoint;
    private int direction;
    private Coordinate endPoint;
    private Sensor sensorRead;


}
