package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;

/**
 * Collection order planners generate good traversal orders between the given set of sensors,
 * where "good" criteria are defined by each implementation of the collection planner.
 */
public abstract class BaseCollectionOrderPlanner {

    /**
     * Creates a collection order planner with the given opimisers and distance matrix method
     * @param optimisers the optimisers to apply in order to the draft route, can be null 
     * @param distMat the distance matrix to use in calculating distances between sensors
     */
    public BaseCollectionOrderPlanner(Collection<CollectionOrderOptimiser> optimisers, DistanceMatrix distMat){
        this.OPTIMISERS = optimisers == null ? new ArrayList<CollectionOrderOptimiser>() : optimisers;
        this.DISTANCE_MATRIX = distMat;
    }


    /**
     * Generates a collection order over the sensors.
     * @param startSensor the sensor which starts and ends the collection
     * @param otherSensors all the other sensors, this set must not include the start sensor
     * @param obstacles obstacles present on the map, these are taken into account when estimating path lengths
     */
    public Deque<Sensor> planRoute(Sensor startSensor,
        Set<Sensor> sensors,
        boolean formLoop){

        // by contract this set must not contain the starting sensor
        assert !sensors.contains(startSensor);

        // give each sensor an implicit index by placing
        // them in an array
        int sensorCount = sensors.size() + 1;
        
        // fill first sensorCount -1 spaces in the array with other sensors
        var sensorArray = sensors.toArray(new Sensor[sensorCount]);
        
        // put starting sensor at the end
        int startSensorIdx = sensorArray.length - 1;
        sensorArray[startSensorIdx] = startSensor;

        // setup the distance matrix 
        DISTANCE_MATRIX.setupDistanceMatrix(sensorArray);

        int[] routeArray = planInitialRoute(startSensorIdx, sensorArray,DISTANCE_MATRIX,formLoop);

        // apply all optimisers
        // that's if there are any to apply
        for (var optimiser : OPTIMISERS) {
            optimiser.optimise(DISTANCE_MATRIX,routeArray);
        }
        
        // produce route in correct format
        var route = new LinkedList<Sensor>();

        for (int i = 0; i < routeArray.length; i++) {
            route.addFirst(sensorArray[routeArray[i]]);
        }

        return route;
    }

    /**
     * Drafts a route between the given sensors, using the given matrix. If form loop is true then the route will also begin and end on the same sensor
     * @param startSensorIdx the index in the sensor array of the starting sensor
     * @param sensors the list of unique sensors, with the start sensor
     * @param distanceMatrix the distance matrix where d_[i][j] and d_[j][i] is the distance between sensor[i] and sensor[j]
     * @param formLoop whether or not to start and end on the same sensors
     * @return
     */
    protected abstract int[] planInitialRoute(int startSensorIdx,Sensor[] sensors,DistanceMatrix distanceMatrix, boolean formLoop);


    private final Collection<CollectionOrderOptimiser> OPTIMISERS;
    private final DistanceMatrix DISTANCE_MATRIX;
    
}
