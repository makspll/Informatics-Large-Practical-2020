package uk.ac.ed.inf.aqmaps.simulation.planning;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public class EuclidianDistanceMatrix extends DistanceMatrix {

    @Override
    protected double distanceMetric(Sensor a, Sensor b) {
        return a.getPosition().distance(b.getPosition());
    }
    
}
