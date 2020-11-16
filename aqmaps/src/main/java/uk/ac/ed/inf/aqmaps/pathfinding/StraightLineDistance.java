package uk.ac.ed.inf.aqmaps.pathfinding;

public class StraightLineDistance implements Heuristic{

    public StraightLineDistance(){
    }

    public StraightLineDistance(double relaxationFactor){
        assert relaxationFactor >= 1;
        this.relaxationFactor = relaxationFactor;
    }

    @Override
    public double heuristic(SpatialTreeSearchNode a, PathfindingGoal b) {
        return a.getLocation().distance(b.getPosition()) * relaxationFactor;
    }


    private double relaxationFactor = 1;
    
}
