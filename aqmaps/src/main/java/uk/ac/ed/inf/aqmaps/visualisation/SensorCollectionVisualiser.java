package uk.ac.ed.inf.aqmaps.visualisation;

import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;


/**
 * Classes implementing this interface generate geojson visualisations of the flight path and sensor readings
 */
public interface SensorCollectionVisualiser {
    
    /**
     * Create a geojson visualisation of the given flight path and sensors with their readings
     * @param flightPath the flight path
     * @param sensorsToBeVisited the sensors
     * @param precisionModel the precision model to use for final values
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public FeatureCollection plotMap(
        Deque<PathSegment> flightPath,
        Collection<Sensor> sensorsToBeVisited) throws IOException, InterruptedException ;

}
