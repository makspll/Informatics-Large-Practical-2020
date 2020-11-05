package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public interface CollectionOrderPlanner {
    public Queue<Sensor> planRoute(Sensor startSensor,
        Set<Sensor> sensors,
        Collection<Obstacle> obstacles);
}
