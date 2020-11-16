package uk.ac.ed.inf.aqmaps.pathfinding;

public interface Heuristic {
    
    public  double heuristic(SpatialTreeSearchNode a, PathfindingGoal b);
}
