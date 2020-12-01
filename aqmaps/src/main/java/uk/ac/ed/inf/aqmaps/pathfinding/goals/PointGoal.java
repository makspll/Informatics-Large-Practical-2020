package uk.ac.ed.inf.aqmaps.pathfinding.goals;

import org.locationtech.jts.geom.Coordinate;


/**
 * A point goal is the simplest kind of goal, a single location in space.
 */
public class PointGoal implements PathfindingGoal{

    /**
     * Creates a new point goal
     * @param goal
     */
    public PointGoal(Coordinate goal){
        this.goal = goal;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Coordinate getCoordinates() {
        return goal;
    }


    /**
     * The location of the goal
     */
    private Coordinate goal;
}
