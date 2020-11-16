package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public class NearestInsertionOrderPlanner extends LoopingCollectionOrderPlanner {

    @Override
    public Deque<Sensor> planRoute(Sensor startSensor, Set<Sensor> sensors, Collection<Obstacle> obstacles) {

        var unvisitedSensors = new HashSet<Sensor>(sensors);
        return null;
        
    }
    
}
