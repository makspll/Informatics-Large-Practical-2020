package uk.ac.ed.inf.aqmaps.simulation.planning;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;

/**
 * Distance matrix using euclidian distance as the value for distances
 */
public class EuclidianDistanceMatrix extends DistanceMatrix {

    @Override
    protected double distanceMetric(Sensor a, Sensor b) {
        return a.getCoordinates().distance(b.getCoordinates());
    }
    
}
