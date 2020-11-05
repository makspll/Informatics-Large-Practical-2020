package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Queue;

import org.locationtech.jts.geom.Polygon;

public interface Obstacle {
    public Polygon getShape();
    public boolean intersectsPath(Queue<PathSegment> path);
}
