package uk.ac.ed.inf.aqmaps.pathfinding.heuristics;

import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;

/**
 * Pathfinding heuristics are used to isolate "good" nodes to expand next in the search.
 */
public interface PathfindingHeuristic {
    
    /**
     * Returns the value of the heuristic between the given node and the goal. A heuristic must be 
     * a) admissible - i.e. never overestimate the real cost of reaching the goal
     * b) consistent - i.e. follow the triangle inequality
     * Otherwise pathfinding algorithms might struggle to find a good solution or to even find one at all
     * @param a the node being evaluated
     * @param b the pathfinding goal
     * @return
     */
    public <T extends SearchNode<T>> double heuristic(T a, PathfindingGoal b);
}
