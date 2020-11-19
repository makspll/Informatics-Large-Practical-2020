package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;


public abstract class PathfindingAlgorithm<T extends SearchNode<T>>{
    
    /**
     * Finds a path from the start to the goal node and outputs the result into the provided deque. Modifies the goal node's location to accomodate for the goal threshold
     * @param g the graph which will build the traversal tree
     * @param goal the goal node *location will be modified if the goal threshold is not zero*
     * @param start the node from which to begin pathfinding
     * @param goalThreshold the distance d such that for a node is thought to reach the goal only if it's distance from it is less than or equal to d
     * @param output the Deque into which the final path will be deposited. The last node will always be the goal node provided. If the threshold is non-zero, 
     * the goal node's location will be changed in order to accomodate for the real path traversed.
     */
    public abstract  void findPath(SearchTree<T> g,
        PathfindingGoal goal,
        T start,
        double goalThreshold,
        Deque<T> output);


    /**
     * Finds a path from the start node through the provided route. Modifies the goal nodes' locations to accomodate for the goal threshold.
     * @param g the graph which will build the traversal tree
     * @param route the goal nodes *location will be modified if the goal threshold is not zero*
     * @param start the node from which to begin pathfinding
     * @param goalThreshold the distance away from the goal allowed for a node to be considered as reaching one of the goals.
     * @return Deque with the final path. The route nodes will be part of the route and the last route node will be at the end. If the threshold is non-zero, 
     * the goal node's locations will be changed in order to accomodate for the real path traversed.
     */
    public  Deque<T> findPath(SearchTree<T> g, Deque<PathfindingGoal> route, T start, double goalThreshold) {

        // we perform smaller searches between the sequential start and end goals
        var startNode = start;
        var finalPath = new LinkedList<T>();


        for (var goalNode : route) {
            
            // find the path for the segment, remove the last point
            // start the next search on that point
            findPath(g,goalNode,startNode,goalThreshold, finalPath);
            startNode = finalPath.pollLast();
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
        return node.getLocation().distance(goal.getPosition()) <= threshold;
    }
    
    /**
     * reconstructs the path to node and deposits it in the given queue
     * @param node the node to which to reconstruct the path
     * @param out the queue to deposit the output into
     */
    protected  void reconstructPathUpToIncluding(T node, Queue<T> out,T limitNode){
        
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
