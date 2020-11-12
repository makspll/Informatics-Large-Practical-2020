package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

/**
 * Plans a collection of sensor data in a greedy order and in a way that form a loop, 
 * i.e. by picking the closest sensor at each step.
 */
public class GreedyCollectionOrderPlanner extends BaseRingCollectionOrderPlanner {

    /**
     * Generates a looping collection order over the sensors in a greedy manner.
     * @param startSensor the sensor which starts and ends the collection
     * @param otherSensors all the other sensors, this set must not include the start sensor
     * @param obstacles obstacles present on the map, these are taken into account when estimating path lengths
     */
    @Override
    public Queue<Sensor> planRoute(Sensor startSensor, Set<Sensor> otherSensors, Collection<Obstacle> obstacles) {

        var unvisitedSensors = new HashSet<Sensor>(otherSensors);

        Queue<Sensor> route = new LinkedList<Sensor>();
        
        Sensor currentSensor = startSensor;
        route.add(currentSensor);

        // we simply pick the closest sensor to the one we are currently at
        // untill we run out of sensors
        for(int i = 0 ; i < otherSensors.size();i++){

            // find closest sensor to the current sensor
            double smallestDistance = Double.MAX_VALUE;
            Sensor closestSensor = null;

            for (Sensor otherSensor : unvisitedSensors) {
                double distance = otherSensor.getCoordinates()
                                    .distance(currentSensor.getCoordinates());

                if( distance < smallestDistance ){
                    smallestDistance = distance;
                    closestSensor = otherSensor;
                }
            }

            // pick the closest sensor as the next move
            route.add(closestSensor);
            currentSensor = closestSensor;
            unvisitedSensors.remove(closestSensor);
        }

        // we then loop back
        route.add(startSensor);

        // sanity check
        assert route.size() == otherSensors.size() + 2;

        return route;
    }
    
}
