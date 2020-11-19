package uk.ac.ed.inf.aqmaps.pathfinding;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import uk.ac.ed.inf.aqmaps.utilities.Shape;

/**
 * Obstacles represent impassable regions in pathfinding
 */
public interface Obstacle extends Shape{

    /**
     * Retrieve the shape of the obstacle
     */
    public Polygon getShape();

    /**
     * Returns true if the line formed from a to b intersects this obstacle
     * @param a start point
     * @param b end point
     * @return true if line intersects this obstacle
     */
    public boolean intersectsPath(Coordinate a, Coordinate b);

}
