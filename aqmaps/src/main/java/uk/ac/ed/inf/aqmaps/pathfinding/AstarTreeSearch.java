package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.HashSet;
import java.util.PriorityQueue;

import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.SpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.PathfindingHeuristic;


/**
 * Classic pathfinding algorithm, modified BFS which uses both the cost to reach a node and the predicted cost from that node to the goal
 * to chose the nodes to be expanded next. This version of Astar treats the search as a tree search and so uses a hashing function to determine if a node has been visited yet.
 */
public class AstarTreeSearch<T extends SearchNode<T>> extends PathfindingAlgorithm<T> {

    /**
     * Creates a new instance of Astar search with the given heuristic and spatial hashing function.
     * @param heuristic the huristic which determines order of expansion
     * @param hash the hashing function used to prune the search from states we've already visited. While this is a tree search implementation of the algorithm,
     * using a hashing function causes it to act slightly like a graph search algorithm.
     */
    public AstarTreeSearch(PathfindingHeuristic heuristic, SpatialHash hash){
        this.heuristic = heuristic;
        this.hash = hash;
    }




    @Override
    public  void findPath(SearchGraph<T> g, PathfindingGoal goal, T start, double goalThreshold,Deque<T> output) {
        
        // we keep an open set where the nodes are sorted by their heuristic + 
        // cost values. If the heuristic is admissible
        // Astar will return an optimal solution
        var openSet = new PriorityQueue<T>(1,(a,b)->compareNodes(a,b));
        
        // even though floating point issues may arise,
        // we benefit from at least avoiding some of the visited points
        var visitedSet = new HashSet<Integer>();
        visitedSet.add(hash.getHash(start.getLocation()));

        // start with the initial node in the open set
        openSet.add(start);

        // terminate when we have no more nodes in the open set
        // this means we have not found a solution
        while(!openSet.isEmpty()){

            // we expand the "best" node in the open set
            // then generate its neighbours if we haven't reached the goal
            var expandedNode = openSet.poll();

            // if the node is our goal
            // re-construct the path
            if(isAtGoal(goalThreshold, expandedNode , goal)){

                expandedNode.addGoalReached(goal);
                reconstructPathUpToIncluding(expandedNode, output, start);

                return;
            }
            
            var neighbours = g.getNeighbouringNodes(expandedNode);

            // we add each neighbour to the open set
            for (T n : neighbours) {

                int spatialHash = hash.getHash(n.getLocation());
                if(visitedSet.contains(spatialHash))
                    continue;

                n.setHeuristic(heuristic.heuristic(n, goal));

                visitedSet.add(spatialHash);
                        
                openSet.add(n);
            }
        }

        // no solution
        return;
    }

    /**
     * The comparison function used when ordering the nodes in the open set. 
     * @param A node 1
     * @param B node 2 
     * @return a positive integer, zero or a negative integer if A has greater cost + heuristic, A and B have the same values or if A has lower cost + heuristic
     */
    private int compareNodes(T A, T B){

        // this wil be either 1,0 or -1 depending on the values of the nodes
        int signOfDistance = (int)Math.signum((A.getCost() + A.getHeuristic()) - (B.getCost() + B.getHeuristic()));
       
        // this is the tie breaking mechanism
        // this is particularly important with floating point errors
        // we want to limit the number of nodes searched as much as possible
        if(signOfDistance == 0){
            // tie breaker,
            // pick based on cost only
            return (int)Math.signum((A.getHeuristic() - B.getHeuristic()));
        } else {
            return signOfDistance;
        }
    }

    /**
     * The huristic used to compare nodes
     */
    private final PathfindingHeuristic heuristic;

    /**
     * The hashing function used to prune the search
     */
    private final SpatialHash hash;

    
}
