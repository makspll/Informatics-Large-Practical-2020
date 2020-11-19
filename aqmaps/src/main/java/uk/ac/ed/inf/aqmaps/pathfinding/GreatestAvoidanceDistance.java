package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

public class GreatestAvoidanceDistance implements Heuristic {

    public GreatestAvoidanceDistance(Collection<Obstacle> obstacles){
        this.obstacleBVHTree = new BVHNode<Obstacle>(obstacles);
    }
    
    @Override
    public double heuristic(SpatialTreeSearchNode a, PathfindingGoal b) {
        Coordinate posA = a.getLocation();
        Coordinate posB = b.getPosition();
        
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
