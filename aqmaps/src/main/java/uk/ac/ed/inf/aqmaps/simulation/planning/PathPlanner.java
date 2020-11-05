package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public interface PathPlanner {

    public Queue<PathSegment> planPath(Coordinate startCoordinate,
        Queue<Sensor> route,
        Collection<Obstacle> obstacles);
}
