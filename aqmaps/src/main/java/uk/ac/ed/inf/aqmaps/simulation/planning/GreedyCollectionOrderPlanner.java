package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.Deque;
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
public class GreedyCollectionOrderPlanner extends LoopingCollectionOrderPlanner {

    @Override
    public Deque<Sensor> planRoute(Sensor startSensor, Set<Sensor> otherSensors, Collection<Obstacle> obstacles) {

        assert !otherSensors.contains(startSensor);

        // we produce an ordering on the sensors and compute a distance matrix using 
        // biggest avoidance distance
        // last sensor in the list is the starting sensor
        Sensor[] sensorList = otherSensors.toArray(new Sensor[otherSensors.size() + 1]);
        int currSensorIdx = sensorList.length - 1;
        sensorList[currSensorIdx] = startSensor;

        double[][] distanceMatrix = computeEuclidianDistanceMatrix(sensorList, obstacles);
        int[] routeIdxs = new int[sensorList.length + 1];

        var visitedSensorIdxs = new HashSet<Integer>();

        visitedSensorIdxs.add(currSensorIdx);

        int currentRouteIdx = 1;

        // we always start and end on the starting sensor
        routeIdxs[0] = currSensorIdx;
        routeIdxs[routeIdxs.length - 1] = currSensorIdx;

        while(currentRouteIdx < sensorList.length-1){

            // we find the sensor which is closest to the current sensor at each step
            double smallestDistance = Double.MAX_VALUE;
            int closestSensorIdx = -1;

            for(int i = 0; i < sensorList.length; i++){
                if(visitedSensorIdxs.contains(i) || i == currSensorIdx)
                    continue;
                else{
                    double distance = distanceMatrix[currSensorIdx][i];

                    if(distance < smallestDistance){
                        smallestDistance = distance;
                        closestSensorIdx = i;
                    }
                }
            }

            visitedSensorIdxs.add(closestSensorIdx);
            routeIdxs[currentRouteIdx] = closestSensorIdx;

            currentRouteIdx += 1;
            currSensorIdx = closestSensorIdx;
        }

        // perform 2-opt optimisation
        double bestDistance = totalDistance(routeIdxs, distanceMatrix);
        double epsilon = 0.0000000000000001d;
        double improvement = epsilon;

        int noSwappedNodes = routeIdxs.length-1;

        int iterations = 0;
        while(improvement >= epsilon){
            improvement = 0;
            outerloop:
            for(int i = 1; i <= noSwappedNodes -1;i++ ){
                for (int k = i+1; k < noSwappedNodes; k++) {
                    var newRoute = opt2Swap(routeIdxs, i, k);
                    double newDistance = totalDistance(newRoute, distanceMatrix);
                    iterations += 1;

                    if(newDistance < bestDistance){
                        routeIdxs = newRoute;
                        improvement = bestDistance - newDistance;
                        bestDistance = newDistance;
                        break outerloop;
                    }
                }
            }
        }

        // produce proper route
        var route = new LinkedList<Sensor>();

        for (int i = 0; i < routeIdxs.length; i++) {
            route.addFirst(sensorList[routeIdxs[i]]);
        }

        return route;
    }


    private int[] opt2Swap(int[] route,int i,int k){

        int[] swapped = new int[route.length];

        for(int j = 0; j <= i-1; j++){
            swapped[j] = route[j];
        }

        for(int j = i; j <= k; j++){
            swapped[j] = route[k-j +i]; 
        }

        for(int j = k+1; j < route.length;j++){
            swapped[j] = route[j];
        }

        return swapped;
    }

    private double totalDistance(int[] route, double[][] distanceMatrix){
        
        double distance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            int curr = route[i];
            int next = route[i+1];
            distance += distanceMatrix[curr][next];
        }

        return distance;
    }


    
}
