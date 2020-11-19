package uk.ac.ed.inf.aqmaps.pathfinding.heuristics;

import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * A more complex heuristic which attempts to model the cost of avoiding obstacles into the distance.
 * This is done by forming the greatest containing circle around all obstacles in direct line of sight between the point and a goal and instead
 * of using straight line distance, using the distance between the farthest visible point in the circle and the point on top  of the cost of reaching the goal from there
 */
public class GreatestAvoidanceDistance implements PathfindingHeuristic {

    /**
     * Initialises a greatest avoidance distance instance with the given obstacles
     * @param obstacles the obstacles to avoid
     */
    public GreatestAvoidanceDistance(Collection<Obstacle> obstacles){
        this.obstacleBVHTree = new BVHNode<Obstacle>(obstacles);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SearchNode<T>> double heuristic(T a, PathfindingGoal b) {
        var posA = a.getLocation();
        var posB = b.getPosition();
        
        var line = GeometryUtilities.geometryFactory.createLineString(new Coordinate[]{
            posA,posB
        });

        // find all obstacles which are on the way
        var obstaclesOnTheWay = new ArrayList<Polygon>();
        for (Obstacle obstacle : obstacleBVHTree.getPossibleCollisions(line)) {
            if(obstacle.intersectsPath(posA, posB)){
                obstaclesOnTheWay.add(obstacle.getShape());
            }
        }

        // if there are no obstacles return the normal distance
        if(obstaclesOnTheWay.size() == 0)
            return posA.distance(posB);

        //form a minimum bounding circle around all found obstacles
        var minimumBoundingCircle = new MinimumBoundingCircle(
            GeometryUtilities.geometryFactory.buildGeometry(obstaclesOnTheWay));
        
        // form the points of the maximum avoidance triangle
        
        Vector2D directionToB = Vector2D.create(posA, posB);
        Vector2D perpendicularDirection = directionToB.rotateByQuarterCircle(1).normalize();

        Coordinate turningPoint = perpendicularDirection.multiply(
                                    minimumBoundingCircle.getRadius())
                                    .translate(minimumBoundingCircle.getCentre());

        Vector2D segmentToTurningPoint = new Vector2D(posA, turningPoint);
        Vector2D segmentFromTurningPoint = new Vector2D(turningPoint,posB);

        // sum the lengths of the arms of the circle
        return segmentToTurningPoint.length() + segmentFromTurningPoint.length();
    }
    
    private final BVHNode<Obstacle> obstacleBVHTree;
}
