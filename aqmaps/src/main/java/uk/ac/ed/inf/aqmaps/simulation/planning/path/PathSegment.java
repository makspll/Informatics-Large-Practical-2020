package uk.ac.ed.inf.aqmaps.simulation.planning.path;


import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.GeometryFactorySingleton;


/**
 * represents a singular move made by the sensor data collector
 * each move follows the pattern of: move and then collect reading, 
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

        // snap the values to the precision model
        GeometryFactorySingleton.getGeometryFactory().getPrecisionModel().makePrecise(this.startPoint);
        GeometryFactorySingleton.getGeometryFactory().getPrecisionModel().makePrecise(this.endPoint);
    }

    /**
     * returns the start point of the segment
     */
    public Coordinate getStartPoint() {
        return this.startPoint;
    }

    /**
     * returns the direction between the start and end point
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * returns the end point of the segment
     * @return
     */
    public Coordinate getEndPoint() {
        return this.endPoint;
    }


    /**
     * returns the sensor read at the end point of this segment
     * @return
     */
    public Sensor getSensorRead() {
        return this.sensorRead;
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
