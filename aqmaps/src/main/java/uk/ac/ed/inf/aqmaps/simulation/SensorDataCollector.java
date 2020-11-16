package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.planning.DiscreteStepAndAngleGraph;

public interface SensorDataCollector {
    public Queue<PathSegment> planCollection(Coordinate startPosition,
        Set<Sensor> sensors,
        DiscreteStepAndAngleGraph graph);
}
