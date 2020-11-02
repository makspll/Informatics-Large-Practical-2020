package uk.ac.ed.inf.aqmaps.simulation;

import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

public interface SensorDataCollector {
    public Queue<PathSegment> planCollection(Coordinate startPosition,
        List<Sensor> sensors,
        List<Obstacle> obstacles);
}
