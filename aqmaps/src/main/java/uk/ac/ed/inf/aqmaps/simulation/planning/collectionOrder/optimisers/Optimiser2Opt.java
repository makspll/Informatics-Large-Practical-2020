package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers;

import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;

/**
 * An optimiser which performs the 2-opt algorithm to remove crossings in the path
 */
public class Optimiser2Opt implements CollectionOrderOptimiser {

    /**
     * Construct a 2 opt optimiser with the given epsilon threshold. The threshold determines the minimum decrease in path cost required for the optimiser to keep optimising each loop.
     * @param epsilon
     */
    public Optimiser2Opt(double epsilon){
        this.EPSILON = epsilon;
    }

    @Override
    public void optimise(DistanceMatrix distanceMatrix, int[] path) {

         // keep track of the best path so far
         var bestDistance = distanceMatrix.totalDistance(path);
         var improvement = Double.MAX_VALUE;
        
         // the number of nodes allowed to be swapped
         var noSwappedNodes = path.length-1;
         // copy the path so as to not touch the original until we have an improvement
         var pathCopy = path.clone();

         // keep going until improvement is below epsilon
         while(improvement >= EPSILON){
             improvement = 0;
             outerloop:
             // do not swap first or last node (noSwappedNodes is less than path length by 1)
             for(int i = 1; i <= noSwappedNodes -1;i++ ){
                 for (int k = i+1; k < noSwappedNodes; k++) {
                     // construct new route each time by performing a reversal between two points in the tour
                     var newRoute = opt2Swap(path, i, k);
                     var newDistance = distanceMatrix.totalDistance(newRoute);
                    
                     // if we got an improvement keep track of it 
                     if(newDistance < bestDistance){
                         pathCopy = newRoute;
                         improvement = bestDistance - newDistance;
                         bestDistance = newDistance;
                         break outerloop;
                     }
                 }
             }
         }

         // change the original path to the improved path
         // if we have found one (if not path copy will be the original)
         for (int i = 0; i < pathCopy.length; i++) {
             path[i] = pathCopy[i];
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
