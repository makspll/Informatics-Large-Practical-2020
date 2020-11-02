package uk.ac.ed.inf.aqmaps.simulation;

import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

public interface FlightPlanner {

    public Queue<PathSegment> planFlight(Coordinate startSensor,
        Queue<Sensor> route,
        List<Obstacle> obstacles);
}
