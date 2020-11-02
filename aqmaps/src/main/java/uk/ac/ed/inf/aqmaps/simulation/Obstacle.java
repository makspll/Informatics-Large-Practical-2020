package uk.ac.ed.inf.aqmaps.simulation;

import org.locationtech.jts.geom.Polygon;

public interface Obstacle {
    public Polygon getShape();
}
