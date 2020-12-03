package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.GeometryFactorySingleton;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * Distance matrix using the greatest avoidance distance as the distance metric. This distance is calculated by forming a minimum bounding circle around all obstacles
 * between any two sensors and calculating the length of the path which "wraps" around the circle 
 */
public class GreatestAvoidanceDistanceMatrix extends DistanceMatrix {

    /**
     * initialize blank distance matrix with the given obstacles
     * @param obstacles
     */
    public GreatestAvoidanceDistanceMatrix(Collection<Obstacle> obstacles){
        this.obstacleBVHTree = new BVHNode<Obstacle>(obstacles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double distanceMetric(Sensor a, Sensor b) {
        return greatestAvoidanceDistance(a, b);
    }

    private double greatestAvoidanceDistance(Sensor a, Sensor b){

        Coordinate posA = a.getCoordinates();
        Coordinate posB = b.getCoordinates();
        
   
        var line = GeometryFactorySingleton.getGeometryFactory().createLineString(new Coordinate[]{
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
        var mergedGeometry = GeometryFactorySingleton.getGeometryFactory().buildGeometry(obstaclesOnTheWay);
        var minimumBoundingCircle = new MinimumBoundingCircle(mergedGeometry);
        
        // form the points of the maximum avoidance triangle
        
        Vector2D directionToB = Vector2D.create(posA, posB);
        Vector2D perpendicularDirection = directionToB.rotateByQuarterCircle(1).normalize();

        var circleCenter = minimumBoundingCircle.getCentre();
        var circleRadius = minimumBoundingCircle.getRadius();

        Coordinate turningPoint = perpendicularDirection.multiply(
                                    circleRadius)
                                    .translate(circleCenter);

        Vector2D segmentToTurningPoint = new Vector2D(posA, turningPoint);

        // sum the lengths to the turning point, around the circle and towards the goal from the end of the circle
        return segmentToTurningPoint.length() 
            + (circleRadius*Math.PI*1/2)
            + (circleCenter.distance(posB) - circleRadius);
    }

    private final BVHNode<Obstacle> obstacleBVHTree;
}
