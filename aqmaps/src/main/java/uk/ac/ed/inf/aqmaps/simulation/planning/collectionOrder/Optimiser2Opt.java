package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;

public class Optimiser2Opt implements CollectionOrderOptimiser {

    public Optimiser2Opt(double epsilon){
        this.EPSILON = epsilon;
    }

    @Override
    public void optimise(DistanceMatrix distanceMatrix, int[] path) {
         // perform 2-opt optimisation
         double bestDistance = distanceMatrix.totalDistance(path);
         double improvement = Double.MAX_VALUE;
 
         int noSwappedNodes = path.length-1;
 
         while(improvement >= EPSILON){
             improvement = 0;
             outerloop:
             for(int i = 1; i <= noSwappedNodes -1;i++ ){
                 for (int k = i+1; k < noSwappedNodes; k++) {
                     var newRoute = opt2Swap(path, i, k);
                     double newDistance = distanceMatrix.totalDistance(newRoute);
 
                     if(newDistance < bestDistance){
                         path = newRoute;
                         improvement = bestDistance - newDistance;
                         bestDistance = newDistance;
                         break outerloop;
                     }
                 }
             }
         }
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

    private final double EPSILON;
}
