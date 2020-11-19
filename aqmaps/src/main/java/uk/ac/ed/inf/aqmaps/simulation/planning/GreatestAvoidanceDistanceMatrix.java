package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * Distance matrix using the greatest avoidance distance as the distance metric. This distance is calculated by forming a minimum bounding circle around all obstacles
 * between any two sensors and calculating the length of the path which "wraps" around the circle (approximated as a triangle)
 */
public class GreatestAvoidanceDistanceMatrix extends DistanceMatrix {

    /**
     * initialize blank distance matrix with the given obstacles
     * @param obstacles
     */
    public GreatestAvoidanceDistanceMatrix(Collection<Obstacle> obstacles){
        this.obstacles = obstacles;
        this.obstacleBVHTree = new BVHNode<Obstacle>(obstacles);
    }

    @Override
    protected double distanceMetric(Sensor a, Sensor b) {
        return greatestAvoidanceDistance(a, b);
    }

    private double greatestAvoidanceDistance(Sensor a, Sensor b){

        Coordinate posA = a.getCoordinates();
        Coordinate posB = b.getCoordinates();
        
        var line = GeometryUtilities.geometryFactory.createLineString(new Coordinate[]{
            posA,posB
        });

        // find all obstacles which are on the way
        List<Polygon> obstaclesOnTheWay = new ArrayList<Polygon>();
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

    private final Collection<Obstacle> obstacles;
    private final BVHNode<Obstacle> obstacleBVHTree;
}
