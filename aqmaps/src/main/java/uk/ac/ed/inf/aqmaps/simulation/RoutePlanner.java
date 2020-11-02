package uk.ac.ed.inf.aqmaps.simulation;

import java.util.List;
import java.util.Queue;

public interface RoutePlanner {
    public Queue<Sensor> planRoute(Sensor startSensor,
    List<Sensor> sensors);
}
