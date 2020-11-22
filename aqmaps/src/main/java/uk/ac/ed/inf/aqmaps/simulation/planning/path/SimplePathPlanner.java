package uk.ac.ed.inf.aqmaps.simulation.planning.path;

import java.util.Deque;
import java.util.LinkedList;

import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.simulation.DirectedSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

/**
 * This path planner will apply some simple optimisations in order to produce a smaller number of path segments than the naive implementation.
 */
public class SimplePathPlanner extends BasePathPlanner {

    public SimplePathPlanner(double readingRange, int maxMoves, PathfindingAlgorithm<DirectedSearchNode> algorithm) {
        super(readingRange, maxMoves, algorithm);
    }
    
    /**
     * {@inheritDoc}. This planner will try to perform some simple optimisations in order to shorten the route.
     * In order to produce a valid route this planner will introduce proxy segments which go back and forth between the nearest neighbour
     * whenever a sensor is read at the start point of a segment or if more than one sensor is read at the endpoint. The optimisations currently include:
     * <br> 
     * 1) if the current segment does not read anything at the end point and the next reads a sensor at its start point, we "absorb" that sensor into
     * the current segment.
     * <br>
     * 
     */
    @Override
    protected Deque<PathSegment> pathPointsToSegmentsStrategy(Deque<DirectedSearchNode> pathPoints,
        Deque<PathfindingGoal> goalsRoute,
        Deque<Sensor> sensorRoute,
        ConstrainedTreeGraph graph
        ){



        assert goalsRoute.size() == sensorRoute.size();

        // we keep the start end end nodes as we iterate over the points
        var startNode = pathPoints.poll();
        var output = new LinkedList<PathSegment>();


        // if we only have one singular point we have to resolve it with a proxy segment
        // size then would be zero as we already polled
        if(pathPoints.size() == 0 && startNode != null){
            resolveProxySegments(startNode, graph, goalsRoute, sensorRoute, output);
        }


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

            if(nodeGoalsAchievedAtEndNode.size() == 0){
                // if we haven't reached a node
                // we can use this opportunity to see if the next start node has a goal reached
                // if so, we can "steal" it to make it a legal move
                // that's only if we can reach the sensor that that move reaches
                var nextNode = pathPoints.peek();
                
                if(nextNode != null 
                    && nextNode.getNumberOfGoalsReached() > 0
                    && nextNode.getGoalsReached().peek().getPosition().distance(endNode.getLocation()) < READING_RANGE){
                    endNode.addGoalReached(
                        nextNode.getGoalsReached().poll());
                }

                // we then process the reached goal as normal
            }

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
        DirectedSearchNode node,
        ConstrainedTreeGraph graph,
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
        // it means it appears later on in the route and we don't touch it
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

            // add proxy segment back and forth
            output.add(proxyPathSegment);
            output.add(proxyBackPathSegment);

            // remove the goal reached
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
}
