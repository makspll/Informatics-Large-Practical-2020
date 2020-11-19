package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.utilities.Shape;

public interface Obstacle extends Shape{
    public Polygon getShape();
    public boolean intersectsPath(Queue<PathSegment> path);
    public boolean intersectsPath(Coordinate a, Coordinate b);

}
