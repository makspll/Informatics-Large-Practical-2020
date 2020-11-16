package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;


public class AstarTreeSearch extends TreePathfindingAlgorithm {

    public AstarTreeSearch(Heuristic h){
        this.h = h;
    }

    private static int compareNodes(SpatialTreeSearchNode A, SpatialTreeSearchNode B, PathfindingGoal Goal){
        int output = (int)Math.signum((A.getCost() + A.getHeuristic()) - (B.getCost() + B.getHeuristic()));
       
        if(output == 0){
            // tie breaker,
            // pick based on cost only
            return (int)Math.signum((A.getCost() - B.getCost()));
        }

        return output;
    }


    @Override
    public  void findPath(Graph g, PathfindingGoal goal, SpatialTreeSearchNode start, double goalThreshold,Deque<SpatialTreeSearchNode> output) {
        
        // we keep an open set where the nodes are sorted by their heuristic + 
        // cost values. If the heuristic is admissible
        // Astar will return an optimal solution
        PriorityQueue<SpatialTreeSearchNode> openSet = new PriorityQueue<SpatialTreeSearchNode>(1,(a,b)->compareNodes(a,b,goal));

        // start with the initial node in the open set
        openSet.add(start);

        // terminate when we have no more nodes in the open set
        // this means we have not found a solution
        while(!openSet.isEmpty()){

            // we expand the "best" node in the open set
            // then generate its neighbours if we haven't reached the goal
            SpatialTreeSearchNode expandedNode = openSet.poll();

            // if the node is our goal
            // re-construct the path
            if(isAtGoal(goalThreshold, expandedNode , goal)){

                expandedNode.addGoalReached(goal);
                reconstructPathUpToIncluding(expandedNode, output, start);

                return;
            }
            
            List<SpatialTreeSearchNode> neighbours = g.getNeighbouringNodes(expandedNode);

            // we add each neighbour to the open set
            for (SpatialTreeSearchNode n : neighbours) {

                n.setHeuristic(h.heuristic(n, goal));

                openSet.add(n);
            }
        }

        // no solution
        return;
    }

    private Heuristic h;


    
}
