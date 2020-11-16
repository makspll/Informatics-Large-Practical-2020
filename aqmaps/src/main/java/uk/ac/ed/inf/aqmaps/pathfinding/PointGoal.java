package uk.ac.ed.inf.aqmaps.pathfinding;

import org.locationtech.jts.geom.Coordinate;

public class PointGoal implements PathfindingGoal{

    public PointGoal(Coordinate goal){
        this.goal = goal;
    }


    @Override
    public Coordinate getPosition() {
        return goal;
    }


    private Coordinate goal;
}
