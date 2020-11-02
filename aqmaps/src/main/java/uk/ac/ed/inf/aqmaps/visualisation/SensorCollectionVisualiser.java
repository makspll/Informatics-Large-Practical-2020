package uk.ac.ed.inf.aqmaps.visualisation;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public interface SensorCollectionVisualiser {
    
    public FeatureCollection plotMap(Queue<PathSegment> flightPath, Collection<Sensor> sensorsToBeVisited) throws IOException, InterruptedException ;

}
