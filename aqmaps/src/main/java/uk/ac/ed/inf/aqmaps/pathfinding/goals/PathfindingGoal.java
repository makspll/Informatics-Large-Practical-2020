package uk.ac.ed.inf.aqmaps.pathfinding.goals;

import org.locationtech.jts.geom.Coordinate;

/**
 * Classes inheriting from this interface can be used as pathfinding goals
 */
public interface PathfindingGoal {
    
    /**
     * @return Returns the spatial position of the pathfinding goal
     */
    public Coordinate getCoordinates();
}
