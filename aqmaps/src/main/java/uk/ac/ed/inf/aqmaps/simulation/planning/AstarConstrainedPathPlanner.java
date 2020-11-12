package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

public class AstarConstrainedPathPlanner extends BaseConstrainedPathPlanner {


    public AstarConstrainedPathPlanner(double moveLength, double readingRange, int maxMoves, int minAngle, int maxAngle,
            int angleIncrement) {
        super(moveLength, readingRange, maxMoves, minAngle, maxAngle, angleIncrement);
    }

    @Override
    public Queue<PathSegment> planPath(Coordinate startCoordinate, Queue<Sensor> route,
            Collection<Obstacle> obstacles) {
                

        
        int movesLeft = MAX_MOVES;
        var canRead = false;

        var routeCopy = new LinkedList<Sensor>(route);
        
        Sensor currentGoal = routeCopy.poll();
        GraphNode currentStartNode = new GraphNode(startCoordinate,null,-1,0,heuristic(startCoordinate, currentGoal.getCoordinates()));

        Queue<GraphNode> totalPath = new LinkedList<GraphNode>();

        while(!routeCopy.isEmpty() && movesLeft > 0){


            totalPath.add(currentStartNode);
            totalPath.addAll(aStar(currentStartNode,
                currentGoal,
                obstacles,
                movesLeft,
                READING_RANGE,
                canRead
                ));

            currentStartNode = totalPath.peek();
            // we can read straight away only if in the last
            // element of the path we did not just read a sensor
            // which is impossible, but we keep this in case A* behaviour is modified
            canRead = totalPath.peek().sensorRead == null;
            
            // the number of path segments is equal to the number of points - 1
            // but since the path returned does not include the starting point
            // the number of points is equal to the path segment number 
            movesLeft = totalPath.size() - 1;

            currentGoal = routeCopy.poll();
        }
        
        var output = createPath(totalPath);

        assert output.size() <= MAX_MOVES;

        return output;
    }

    private Queue<PathSegment> createPath(Queue<GraphNode> nodes){

        // we cannot make a path segment with only one graph node
        assert nodes.size() > 1;

        var output = new LinkedList<PathSegment>();

        // we use this variable to hold the "start point"
        // node, each time
        GraphNode lastGraphNode = nodes.poll();

        for (GraphNode graphNode : nodes) {

            Sensor sensorRead = graphNode.sensorRead;
            Coordinate start = lastGraphNode.coordinates;
            Coordinate end = graphNode.coordinates;

            output.add(
                new PathSegment(start,
                    graphNode.directionFromParent, 
                    end, 
                    sensorRead));

            lastGraphNode = graphNode;
                
        }

        return output;
    }

    private Queue<GraphNode> aStar(GraphNode start, Sensor goal, Collection<Obstacle> obstacles, int maxMoves, double threshold, boolean canReadSensor){
        
        Coordinate end = goal.getCoordinates();
        var movesLeft = maxMoves;
        var openSet = new PriorityQueue<GraphNode>();
        var closedSet = new HashSet<GraphNode>();

        var expandedNode = start;

        openSet.add(expandedNode);

        while(!openSet.isEmpty() && movesLeft > 0 ){

            // if reached the goal
            if(MathUtilities.thresholdEquals(expandedNode.coordinates,end,threshold)){

                // reconstruct the path to 
                var output = constructPath(expandedNode);

                // we make sure to only read after we have moved at least once
                if(!canReadSensor){
                    // if we haven't moved, we force a move into
                    // the node we came from and back
                    // if this is the only node then we pick any of its
                    // valid neighbours
                    
                    if(expandedNode.parentPath != null){

                        if(movesLeft-- > 0)
                            output.add(expandedNode.parentPath);
                        
                        if(movesLeft-- > 0){
                            output.add(expandedNode);

                            // we mark the sensor as read
                            expandedNode.sensorRead = goal;
                        }
    

                    } else {
                        var neighbours = expandedNode.getNeighbours(obstacles,goal.getCoordinates());

                        // if we have no neighbours and we are the first
                        // node, this means we started in a graph with a single node
                        // this is an invalid graph
                        assert neighbours.size() > 0; 

                        if(movesLeft-- > 0)
                            output.add(neighbours.get(0));
                        if(movesLeft-- > 0){
                            output.add(expandedNode);

                            // we mark the sensor as read
                            expandedNode.sensorRead = goal;
                        }
                    }

                    return output;
                } 

            }

            // go through each neighbour
            List<GraphNode> neighbours = expandedNode.getNeighbours(obstacles,goal.getCoordinates());
            for (var neighbour : neighbours) {
                
                // check neighbour is in the open set
                if(!openSet.contains(neighbour) && !closedSet.contains(neighbour)){
                    openSet.add(neighbour);
                } 

            }

            // we do not need to re-visit any values since we are assuming
            // that the heuristic is consistent, and therefore any expanded node
            // will already be part of the optimal path

            closedSet.add(expandedNode);
            expandedNode = openSet.poll();
            movesLeft -= 1;
            canReadSensor = true;
        }

        // if we have to halt early, we return the best result so far
        if(movesLeft == 0){
            return constructPath(expandedNode);
        } else {
            // if we did not find a path at all, then return the empty list
            return new LinkedList<GraphNode>();
        }
    }

    private Queue<GraphNode> constructPath(GraphNode goal){

        // we use a stack to reverse the path (FILO)
        Stack<GraphNode> output = new Stack<GraphNode>();

        GraphNode currNode = goal;

        while(currNode != null){
            output.add(currNode);
            currNode = currNode.parentPath;    
        }

        // and then convert to a queue (FIFO)
        return new LinkedList<GraphNode>(output);
    }

    private double heuristic(Coordinate start, Coordinate goal){
        return start.distance(goal);
    }

    private class GraphNode implements Comparable<GraphNode>{
        GraphNode parentPath;
        int directionFromParent;
        public Coordinate coordinates;
        public double cost;
        public double heuristic;
        public Sensor sensorRead;

        public GraphNode(Coordinate coordinate,GraphNode parent,int directionFromParent, double cost, double heuristic){
            coordinates = coordinate;
            this.cost = cost;
            this.heuristic = heuristic;
            parentPath = parent;
        }

        @Override
        public boolean equals(Object o) { 
        
            if (o == this) { 
                return true; 
            } 
        
            if (!(o instanceof GraphNode)) { 
                return false; 
            } 
                
            GraphNode c = (GraphNode) o; 
                
            return MathUtilities.thresholdEquals(coordinates, c.coordinates);
        }
        
        @Override
        public int hashCode(){
            return coordinates.hashCode();
        }

        @Override
        public int compareTo(GraphNode o) {

            if(equals(o)){
                return 0;
            }
            return (int)((o.cost + o.heuristic) - (cost + heuristic));
        }

        public List<GraphNode> getNeighbours(Collection<Obstacle> obstacles, Coordinate goal){

            List<Integer> possibleDirections = 
                getValidDirectionsBetween(MIN_ANGLE, MAX_ANGLE);

            var neighbours = new ArrayList<GraphNode>();
            
            for (Integer dir : possibleDirections) {

                Coordinate coordinatesNeighbour = stepInDirection(dir, coordinates);

                boolean intersectsObstacle = false;

                // we check for collisions
                for (Obstacle obstacle : obstacles) {
                    if(obstacle.intersectsPath(coordinates, 
                        coordinatesNeighbour)){

                            intersectsObstacle = true;
                            break;
                    }
                }

                if(!intersectsObstacle)
                    neighbours.add(
                        new GraphNode(coordinatesNeighbour,
                            this,
                            dir,
                            cost + MOVE_LENGTH,
                            heuristic(coordinatesNeighbour,goal))
                    );
            }

            return neighbours;
        }

        public double evaluationFunction(){
            return cost + heuristic;
        }
        
    }
}
