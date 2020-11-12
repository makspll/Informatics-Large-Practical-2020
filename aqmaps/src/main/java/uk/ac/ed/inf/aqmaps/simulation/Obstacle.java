package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

public interface Obstacle {
    public Polygon getShape();
    public boolean intersectsPath(Queue<PathSegment> path);
    public boolean intersectsPath(Coordinate a, Coordinate b);

}
