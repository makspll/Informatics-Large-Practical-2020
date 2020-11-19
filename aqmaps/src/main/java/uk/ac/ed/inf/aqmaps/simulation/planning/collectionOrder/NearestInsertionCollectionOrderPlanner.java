package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder;

import java.util.Collection;
import java.util.HashSet;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;

/**
 * Collection order planner which employs the nearest insertion method to try and pick the best route.
 */
public class NearestInsertionCollectionOrderPlanner extends BaseCollectionOrderPlanner {

    public NearestInsertionCollectionOrderPlanner(Collection<CollectionOrderOptimiser> optimisers,
            DistanceMatrix distMat) {
        super(optimisers, distMat);
    }



    /**
     * {@inheritDoc}. The route is planned using the nearest insertion heuristic.
     */
    @Override
    protected int[] planInitialRoute(int startSensorIdx, Sensor[] sensors, DistanceMatrix distanceMatrix,
            boolean formLoop) {
        
        // we calculate how long the route should be
        var routeLength = sensors.length + (formLoop ? 1 : 0);
        var route = new int[routeLength]; 

        // always start with the initial sensor
        route[0] = startSensorIdx;

        // keep track of what we've visited so far
        var visitedIndices = new HashSet<Integer>();
        visitedIndices.add(startSensorIdx);

        // keep track of which route position is to be filled next
        var routeIndexCounter = 1;

        // each time visite the sensor according to our heuristic which was not yet visited 
        // and fill the route.
        // we pick the sensors first which are the closest to one of the sensors already in the route
        // and insert them in a way that minimizes the total cost

        while(visitedIndices.size() != sensors.length){

            // find nearest sensor to any sensor in the tour
            int nearestSensor = findSensorNearestToTour(route, 
                routeIndexCounter, 
                sensors.length, 
                visitedIndices, 
                distanceMatrix);
           
            // mark it as visited
            visitedIndices.add(nearestSensor);

            // then we find the tour with the smallest insertion cost
            double smallestTotalDistance = Double.MAX_VALUE;
            int[] leastInsertionCostTour = null;

            // note we do not allow insertion at the beginning
            // so as to not overwrite the start sensor
            for (int i = 1; i <= routeIndexCounter; i++) {

                // create new tour by insertion
                int[] tour = insertIntoRoute(nearestSensor, route, i);
                // evaluate it
                double distance = distanceMatrix.totalDistance(tour,0,routeIndexCounter + 1) 
                    + distanceMatrix.distanceBetween(tour[routeIndexCounter], startSensorIdx);

                // if it's better, remember it
                if(distance < smallestTotalDistance){
                    smallestTotalDistance = distance;
                    leastInsertionCostTour = tour;
                }
            }

            // remember the best tour 
            route = leastInsertionCostTour;
            // increment the counter
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

    /**
     * finds the closest unvisited sensor to any sensors on the tour up to (not including) the last sensor index. 
     * @param route the route
     * @param lastSensorIdx the highest index to search up to within the route (exlusive). has to be smaller than the length of route
     * @param numberOfSensors the route
     * @param visited the sensors which were already visited
     * @param distanceMatrix the distance matrix to use
     * @return
     */
    private int findSensorNearestToTour(int[] route, int lastSensorIdx, int numberOfSensors, HashSet<Integer> visited, DistanceMatrix distanceMatrix){
        assert lastSensorIdx < route.length;

        int closestSensor = -1;
        double smallestDistance = Double.MAX_VALUE;

        // for all sensors in the current path
        for (int i = 0; i < lastSensorIdx; i++) {

            // loop over all unvisited sensors
            for (int j = 0; j < numberOfSensors - 1; j++) {
                
                if(visited.contains(i))
                    continue;
                
                // if we found a smaller distance we update the state
                // of the search
                double distance = distanceMatrix.distanceBetween(i, j);
                if(distance < smallestDistance){
                    smallestDistance = distance;
                    closestSensor = j;
                }

            }
        }

        return closestSensor;
    }

    /**
     * generates new route with the given sensor index inserted into the route at the given position 
     * @param sensorIndex
     * @param route
     * @param insertionIndex
     * @return
     */
    private int[] insertIntoRoute(int sensorIndex,int[] route, int positionInRoute){
        assert positionInRoute < route.length;

        var newRoute = new int[route.length];

        for (int i = 0; i < positionInRoute; i++) {
            newRoute[i] = route[i];
        }

        newRoute[positionInRoute] = sensorIndex;

        for (int i = positionInRoute + 1; i < newRoute.length; i++) {
            newRoute[i] = route[i-1];
        }

        return newRoute;
    }
  


}
