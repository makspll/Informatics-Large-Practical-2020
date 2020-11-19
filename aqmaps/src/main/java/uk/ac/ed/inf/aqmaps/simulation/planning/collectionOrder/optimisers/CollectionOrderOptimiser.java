package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers;


import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;

/**
 * Collection order optimisers provide re-usable methods for optimising a route after one is already selected
 */
public interface CollectionOrderOptimiser {
    public void optimise(DistanceMatrix distanceMatrix, int[] path);
}
