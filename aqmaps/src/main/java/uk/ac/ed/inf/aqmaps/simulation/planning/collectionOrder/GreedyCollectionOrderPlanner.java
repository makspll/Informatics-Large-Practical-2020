package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder;

import java.util.Collection;
import java.util.HashSet;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;

/**
 * Plans a collection of sensor data in a greedy order and in a way that forms a loop, 
 * i.e. by picking the closest sensor at each step.
 */
public class GreedyCollectionOrderPlanner extends BaseCollectionOrderPlanner {


    public GreedyCollectionOrderPlanner(Collection<CollectionOrderOptimiser> optimiser, DistanceMatrix distMat) {
        super(optimiser, distMat);
    }

    @Override
    protected int[] planInitialRoute(int startSensorIdx, Sensor[] sensors, DistanceMatrix distanceMatrix,
            boolean formLoop) {

    
        // decide on the length of the route
        int routeLength = sensors.length + (formLoop ? 1 : 0);

        int[] route = new int[routeLength];
        int currentSensorIndex = startSensorIdx;

        // keep track of what we've visited
        var visitedSensorIdxs = new HashSet<Integer>();
        visitedSensorIdxs.add(currentSensorIndex);

        // route starts at the start sensor given
        route[0] = startSensorIdx;

        // this variable points to the current route index being
        // filled
        int routeIndexCounter = 1;

        // now keep visiting the nearest sensor untill we visit all of them
        while(visitedSensorIdxs.size() != sensors.length ){
            int lastVisitedSensor = route[routeIndexCounter - 1];

            // we find the sensor which is closest to the current sensor at each step
            double smallestDistance = Double.MAX_VALUE;
            int closestSensorIdx = -1;

            for(int i = 0; i < sensors.length; i++){
                if(visitedSensorIdxs.contains(i) || i == lastVisitedSensor)
                    continue;
                else{
                    double distance = distanceMatrix.distanceBetween(lastVisitedSensor,i);

                    if(distance < smallestDistance){
                        smallestDistance = distance;
                        closestSensorIdx = i;
                    }
                }
            }

            visitedSensorIdxs.add(closestSensorIdx);
            route[routeIndexCounter] = closestSensorIdx;

            routeIndexCounter += 1;
        }


        // if we are looping, we will have one slot left to fill
        // routeIndexCounter will be one more than the last added index
        // and so if we are looping it should be equal to the last index of hte route
        assert !formLoop || (formLoop && routeIndexCounter == route.length - 1);
        if(formLoop){
            // fill that slot with the start sensor
            route[routeIndexCounter] = startSensorIdx;
        }

        return route;
    }


}
