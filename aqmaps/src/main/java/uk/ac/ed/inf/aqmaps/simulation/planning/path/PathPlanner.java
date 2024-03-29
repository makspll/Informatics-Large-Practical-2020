package uk.ac.ed.inf.aqmaps.simulation.planning.path;

import java.util.Deque;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;

/**
 * Path planners create a detailed path between given route points which takes a sensor data collector 
 * between the route points according to the path planner's constraints
 */
public interface PathPlanner {

    /**
     * Plans the exact path required to reach all the given sensors, the specific constraints on placed on the route are decided
     * by the specific implementation of the planner itself.
     * @param startCoordinate the starting coordinate
     * @param route the sensors in order they need to be accessed
     * @param graph the graph which defines the traversal graph
     * @param formLoop if true the path will lead back to the start coordinate
     * @return
     */
    public Deque<PathSegment> planPath(
        Coordinate startCoordinate,
        Deque<Sensor> route,
        ConstrainedTreeGraph graph,
        boolean formLoop);

}
