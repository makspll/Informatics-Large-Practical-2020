package uk.ac.ed.inf.aqmaps.simulation.planning.path;

import java.util.Deque;
import java.util.LinkedList;


import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PointGoal;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.DirectedSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;

/**
 * Base class for planners with a limited number of maximum moves and a
 * minimum reading range. All inheriting path planners must make sure that each sensor is read at the endpoint of some path segment
 * and that that sensor is within reading range of the endpoint. They also must make sure that the path is under the maximum move limit
 */
public abstract class BasePathPlanner implements PathPlanner {

    public BasePathPlanner(double readingRange, int maxMoves, PathfindingAlgorithm<DirectedSearchNode> algorithm) {
        this.READING_RANGE = readingRange;
        this.MAX_MOVES = maxMoves;
        this.ALGORITHM = algorithm;
    }

    /**
     * {@inheritDoc}
     * The ConstrainedPathPlanner class adds a maximum move,reading range and also the move sequence constraint to the path planning problem, a valid path will allow the collector
     * to come within READING_RANGE of each sensor (the distance between each sensor and the collector at some path segment's endpoint will be less than or equal to READING_RANGE).
     * The number of segments returned will always be <= MAX_MOVES. The move sequence in each path segment requires that a sensor be read at the end of each path segment only, i.e. the collector must move before collecting
     * any reading, and only one reading must be made per path segment. 
     */
    @Override
    public Deque<PathSegment> planPath(Coordinate startCoordinate, Deque<Sensor> route,
            ConstrainedTreeGraph graph,
            boolean formLoop) {
        

        // create start node
        // the parent and direction values can be anything as 
        // they won't ever be used in construction of the path segments
        var startNode = new DirectedSearchNode(startCoordinate,
            null,
            -1,
            0);

        // create goal nodes
        Deque<PathfindingGoal> goals = new LinkedList<PathfindingGoal>(route);

        PointGoal loopBackGoal = null;
        if(formLoop){
            // we include the start node goal at the end so that our path loops
            // keep the reference so we can remove it from the last node's reached list
            loopBackGoal = new PointGoal(startCoordinate);
            goals.add(loopBackGoal);
        }

        // invoke the algorithm and set out a path

        var path = ALGORITHM.findPath(graph, goals, startNode, READING_RANGE);

        if(formLoop){
            // remove the goal reached for the loopback segment as 
            // these should only contain sensors

            //TODO: this might need fixin
            if(path.size() != 0){
                path.getLast().removeGoalReached(loopBackGoal);
            }

            goals.remove(loopBackGoal);
        }


        
        // pass copy of route to make sure it doesn't get overwritten
        var pathOfSegments = pathPointsToSegmentsStrategy(
            path,
            goals,
            new LinkedList<Sensor>(route),
            graph);
        
        assert pathOfSegments.size() <= MAX_MOVES;

        return pathOfSegments;
    }

    
    /**
     * The main defining characteristic of a constrained path planner. Converts a path of points to a path of path segments
     * needs to make sure that each pathfinding goal is visited only in the end segment of some path segment in range.
     * the passed deque arguments will be consumed
     * @param pathPoints the whole path (will be consumed)
     * @param goalsRoute the pathfinding goals route
     * @param sensorRoute the sensor route which directly correspond to the goalsROute
     * @param graph the graph which defines the transitions between nodes
     * @return the path segments connecting the path points given
     */
    protected abstract Deque<PathSegment> pathPointsToSegmentsStrategy(Deque<DirectedSearchNode> pathPoints,
        Deque<PathfindingGoal> goalsRoute,
        Deque<Sensor> sensorRoute,
        ConstrainedTreeGraph graph
        );
    
  

    protected final double READING_RANGE;
    protected final int MAX_MOVES;
    protected final PathfindingAlgorithm<DirectedSearchNode> ALGORITHM;

}
