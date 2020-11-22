package uk.ac.ed.inf.aqmaps.simulation.collection;

import java.util.Deque;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;

/**
 * Data collectors provide a method to generate an ordered collection of path segments which when followed
 * will lead to a successfull retrieval of sensor data.
 */
public interface SensorDataCollector {
    
    /**
     * Plans the best order of visiting sensors (best being defined by the collector itself)
     * and creates a detailed ordered path segment collection which when followed will allow a successful collection
     * @param startPosition the starting coordinate
     * @param sensors the sensors needing to be visited
     * @param graph the graph which defines the possible positions of the collector
     * @param formLoop if true the path will lead back to the start position
     * @return
     */
    public Deque<PathSegment> planCollection(Coordinate startPosition,
        Set<Sensor> sensors,
        ConstrainedTreeGraph graph,
        boolean formLoop,
        int randomSeed);
}
