package uk.ac.ed.inf.aqmaps.pathfinding.heuristics;

import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;

/**
 * The simplest heuristic, uses the euclidian distance between the node and its goal as the value of its heuristic.
 */
public class StraightLineDistance implements PathfindingHeuristic{

    /** 
     * Initializes a new instance of the straight line heuristic with a relaxation factor of 1 (i.e. no relaxation)
    */
    public StraightLineDistance(){
    }

    /**
     * Initializes a new instance of the straight line heuristic with the given relaxation factor. Some pathfinding algorithms such as Astar will run much faster 
     * with a relaxed heuristic, but the relaxed solution might not be optimal (path cost <= relaxationFactor * optimal path cost)
     * @param relaxationFactor
     */
    public StraightLineDistance(double relaxationFactor){
        assert relaxationFactor >= 1;
        this.relaxationFactor = relaxationFactor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SearchNode<T>> double heuristic(T a, PathfindingGoal b) {
        return a.getCoordinates().distance(b.getCoordinates()) * relaxationFactor;
    }


    /**
     * The value by which euclidian distance is multiplied to give the final heuristic value
     */
    private double relaxationFactor = 1;
    
}
