package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.lang.model.util.ElementScanner6;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.client.SensorData;
import uk.ac.ed.inf.aqmaps.pathfinding.Graph;
import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.PointGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.SpatialTreeSearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.TreePathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

/**
 * Base class for planners with a limited number of maximum moves and a
 * minimum reading range.
 */
public class ConstrainedPathPlanner extends LoopingPathPlanner {

    public ConstrainedPathPlanner(double readingRange, int maxMoves, TreePathfindingAlgorithm algorithm) {
        this.READING_RANGE = readingRange;
        this.MAX_MOVES = maxMoves;
        this.ALGORITHM = algorithm;
    }

    /**
     * {@inheritDoc}
     * The ConstrainedPathPlanner class adds a maximum move,reading range and also the move sequence constraint to the path planning problem, a valid path will allow the collector
     * to come within READING_RANGE of each sensor (the distance between each sensor and the collector will at some path segment be less than or equal to READING_RANGE).
     * The number of segments returned will always be <= MAX_MOVES. The move sequence in each path segment requires that a sensor be read at the end of each path segment only, i.e. the collector must move before collecting
     * any reading, and only one reading must be made per path segment. 
     */
    @Override
    public Deque<PathSegment> planPath(Coordinate startCoordinate, Deque<Sensor> route,
            DiscreteStepAndAngleGraph graph) {
        

        // create start node
        // the parent and direction values can be anything as 
        // they won't ever be used in construction of the path segments
        var startNode = new SpatialTreeSearchNode(startCoordinate,
            null,
            -1,
            0);

        // create goal nodes
        Deque<PathfindingGoal> goals = new LinkedList<PathfindingGoal>(route);

        // we include the start node goal at the end so that our path loops
        // keep the reference so we can remove it from the last node's reached list
        var loopBackGoal = new PointGoal(startCoordinate);
        goals.add(loopBackGoal);

        // invoke the algorithm and set out a path

        var path = ALGORITHM.findPath(graph, goals, startNode, READING_RANGE);

        // remove the loopBackGoal
        path.getLast().removeGoalReached(loopBackGoal);
        goals.remove(loopBackGoal);

        var pathOfSegments = pathPointsToSegmentsStrategy(path, goals,route, graph);
        
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
    protected Deque<PathSegment> pathPointsToSegmentsStrategy(Deque<SpatialTreeSearchNode> pathPoints,
        Deque<PathfindingGoal> goalsRoute,
        Deque<Sensor> sensorRoute,
        DiscreteStepAndAngleGraph graph
        ){

        assert pathPoints.size() > 1;
        assert goalsRoute.size() == sensorRoute.size();

        // we keep the start end end nodes as we iterate over the points
        var startNode = pathPoints.poll();
        var output = new LinkedList<PathSegment>();

        while(!pathPoints.isEmpty() && output.size() < MAX_MOVES){
            var endNode = pathPoints.poll();
            
            // we check for the sensors reached at each point

            // we have 4 possibilities
            // 1/2) no end reaches a goal node, or only the end point does
            // in that case we just create a single segment
            // 3/4) the start point has reaches at least one sensor, or both the start point and endpoint 
            // reach sensors
            // in those cases we need to insert proxy path segments for each goal reached by the start point
            // we can simply check for 3/4
            // and resolve the proxy path and then
            // treat the situation like the case 1/2
            // we also have to keep in mind that the goals reached might not be reached
            // in succession for any node,for example consider that the start node will have itself as the goal reached
            // if the path loops as well as any starting sensors

            var nodeGoalsAchievedAtStartNode = startNode.getGoalsReached();

            if(nodeGoalsAchievedAtStartNode.size() != 0) {
                // cases 3/4

                // add a proxy segment for each goal reached by the start node
                // this will go back and forth between the nearest neighbour and the start node 
                // untill all goals are reached
                resolveProxySegments(startNode,
                    graph,
                    goalsRoute,
                    sensorRoute,
                    output
                    );
               
            }

            // we can then safely proceed with the other 
            // cases since we are back to the start point
            // and we have not touched the endpoint or end sensor
            // cases 1 and 2

            var nodeGoalsAchievedAtEndNode = endNode.getGoalsReached();

            Sensor segmentSensorReached = null;

            // if we reached some node and it is the next goal on the goals route
            if(nodeGoalsAchievedAtEndNode.size() > 0
                && nodeGoalsAchievedAtEndNode.peek() == goalsRoute.peek()){

                // pop both the sensor and goal routes
                // simultaneously remove the goal reached from the node
                segmentSensorReached = sensorRoute.poll();
                endNode.removeGoalReached(goalsRoute.poll());
            }

            // for the end node, a single goal is allowed with no issue
            var pathSegment = new PathSegment(
                startNode.getLocation(),
                endNode.getDirectionFromParent(),
                endNode.getLocation(),
                segmentSensorReached);


            output.add(pathSegment);

            // if the end node has more than one endGoalReached
            // resolve the rest with proxy segments
            if(nodeGoalsAchievedAtEndNode.size() > 0){
                resolveProxySegments(endNode,
                    graph,
                    goalsRoute,
                    sensorRoute,
                    output);
            }
            startNode = endNode;

        }

        // the path can still be longer than MAX_MOVES so we trim it
        while(output.size() > MAX_MOVES){
            output.removeLast();
        }

        return output;
    }

    /**
     * Resolves multiple goals reached at a node into multiple segments which reach the goals at their endpoints 
     * @param node
     * @param sensors
     * @param output
     * @param goalStartIdx the index of the goal at which to start resolving
     */
    private void resolveProxySegments(
        SpatialTreeSearchNode node,
        DiscreteStepAndAngleGraph graph,
        Deque<PathfindingGoal> goalsRoute,
        Deque<Sensor> sensorRoute,
        Deque<PathSegment> output
        )
        {

        assert goalsRoute.size() == sensorRoute.size();

        // it does not matter how we pick the proxy segments
        // due to the fact we will reach the end point sensor anyway
        // and we have to go back to the optimal path

        var neighbours = graph.getNeighbouringNodes(node);

        // we simply pick the first neighbour
        // there has to be at least one,
        // otherwise the graph is a single point which is invalid
        assert neighbours.size() > 0;
        var pickedNeighbour = neighbours.get(0);

        var nextSensor = sensorRoute.poll();
        var nextGoal = goalsRoute.poll();
        var goalNodesReached = node.getGoalsReached();
        
        var currentGoalReachedByNode = goalNodesReached.peek();

        // we only proxy the segments in order of appearance
        // so if the current goal reached is not the next one to
        // appear on the route,
        // it means it appears later on in the route
        while(currentGoalReachedByNode == nextGoal){

            var proxyPathSegment = new PathSegment(
                node.getLocation(),
                pickedNeighbour.getDirectionFromParent(), 
                pickedNeighbour.getLocation(),
                null);

            var proxyBackPathSegment = new PathSegment(
                pickedNeighbour.getLocation(),
                graph.getClosestValidAngle(
                    MathUtilities.oppositeAngleFromEast(
                        pickedNeighbour.getDirectionFromParent())),
                node.getLocation(),
                nextSensor);

            output.add(proxyPathSegment);
            output.add(proxyBackPathSegment);

            node.removeGoalReached(currentGoalReachedByNode);

            nextSensor = sensorRoute.poll();
            nextGoal = goalsRoute.poll();

            if(node.getNumberOfGoalsReached() == 0){
                break;
            }

            currentGoalReachedByNode = goalNodesReached.peek();
        }

        //return unused sensor on top
        sensorRoute.addFirst(nextSensor);
        goalsRoute.addFirst(nextGoal);

        assert goalsRoute.size() == sensorRoute.size();

    }

    protected final double READING_RANGE;
    protected final int MAX_MOVES;
    protected final TreePathfindingAlgorithm ALGORITHM;

}
