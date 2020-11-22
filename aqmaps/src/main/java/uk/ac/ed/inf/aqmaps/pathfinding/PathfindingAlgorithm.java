package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;


public abstract class PathfindingAlgorithm<T extends SearchNode<T>>{
    
    /**
     * Finds a path from the start to the goal node and outputs the result into the provided deque.
     * If a path doesn't exist no nodes will be added to the output, if it does at least one node will be added. The nodes will have their goalsReached deque's set
     * to the corresponding goals that can be reached from their locations within the goal threshold. One node can reach multiple nodes
     * @param g the graph which will build the traversal tree
     * @param goal the goal node *location will be modified if the goal threshold is not zero*
     * @param start the node from which to begin pathfinding
     * @param goalThreshold the distance d such that for a node is thought to reach the goal only if it's distance from it is less than or equal to d
     * @param output the Deque into which the final path will be deposited. The last node will always be the goal node provided. If the threshold is non-zero, 
     * the goal node's location will be changed in order to accomodate for the real path traversed.
     */
    public abstract  void findPath(SearchGraph<T> g,
        PathfindingGoal goal,
        T start,
        double goalThreshold,
        Deque<T> output);


    /**
     * Finds a path from the start node through the provided route. Modifies the goal nodes' locations to accomodate for the goal threshold.
     * if at any point there exists no path between the given goals, the route will halt before that segment (so might be empty).
     * 
     * @param g the graph which will build the traversal tree
     * @param route the goal nodes *location will be modified if the goal threshold is not zero*
     * @param start the node from which to begin pathfinding
     * @param goalThreshold the distance away from the goal allowed for a node to be considered as reaching one of the goals.
     * @return Deque with the final path. The route nodes will be part of the route and the last route node will be at the end. If the threshold is non-zero, 
     * the goal node's locations will be changed in order to accomodate for the real path traversed.
     */
    public  Deque<T> findPath(SearchGraph<T> g, Deque<PathfindingGoal> route, T start, double goalThreshold) {

        // we perform smaller searches between the sequential start and end goals
        var startNode = start;
        var finalPath = new LinkedList<T>();

        int lastPathSize = 0;
        for (var goalNode : route) {
            
            // find the path for the segment, remove the last point
            // start the next search on that point
            findPath(g,goalNode,startNode,goalThreshold, finalPath);

            // if we couldn't find a path at all
            if(lastPathSize == finalPath.size()){
                // we halt and return the path we have so far
                // which might be nothing
                return finalPath;
            } else {
                startNode = finalPath.pollLast();
                lastPathSize = finalPath.size();

            }

        }

        // we will be missing the last goal node at the end so we
        // add it back
        finalPath.add(startNode);

        // return the merged paths
        return finalPath;
    }



    /**
     * Checks whether the given node is within a threshold away from the goal
     * @param threshold
     * @param node
     * @param goal
     * @return
     */
    protected  boolean isAtGoal(double threshold, T node, PathfindingGoal goal){
        return node.getLocation().distance(goal.getPosition()) < threshold;
    }
    
    /**
     * reconstructs the path to node and deposits it in the given queue
     * @param node the node to which to reconstruct the path
     * @param out the queue to deposit the output into
     */
    protected  void reconstructPathUpToIncluding(T node, Deque<T> out,T limitNode){
        
        // follow the chain of child-parent relationships
        // deposit them in the stack in effect reversing the order
        Stack<T> nodeStack = new Stack<T>();
        T currNode = node;

        while(currNode != null){
            nodeStack.push(currNode);

            if(currNode == limitNode)
                break;

            currNode = (T)currNode.getParentNode();
        }

        while(!nodeStack.isEmpty()){
            out.add(nodeStack.pop());
        }
    }

}
