package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * Collection order planners generate good traversal orders between the given set of sensors,
 * where "good" criteria are defined by each implementation of the collection planner.
 */
public abstract class CollectionOrderPlanner {

    /**
     * Generates a collection order over the sensors.
     * @param startSensor the sensor which starts and ends the collection
     * @param otherSensors all the other sensors, this set must not include the start sensor
     * @param obstacles obstacles present on the map, these are taken into account when estimating path lengths
     */
    public abstract Deque<Sensor> planRoute(Sensor startSensor,
        Set<Sensor> sensors,
        Collection<Obstacle> obstacles);


    /**
     * The Biggest possible distance needed to avoid an obstacle, calculated by forming a triangle which has one side between the two endpoints
     * and one corner touching the furthest point on the minimum bounding circle of the obstacles between a and b
     * @param a
     * @param b
     * @param obstacles
     * @return
     */
    public double biggestAvoidanceDistance(Coordinate a, Coordinate b, Collection<Obstacle> obstacles){

        // find all obstacles which are on the way
        List<Polygon> obstaclesOnTheWay = new ArrayList<Polygon>();
        for (Obstacle obstacle : obstacles) {
            if(obstacle.intersectsPath(a, b)){
                obstaclesOnTheWay.add(obstacle.getShape());
            }
        }

        // if there are no obstacles return the normal distance
        if(obstaclesOnTheWay.size() == 0)
            return a.distance(b);

        //form a minimum bounding circle around all found obstacles
        var minimumBoundingCircle = new MinimumBoundingCircle(
            GeometryUtilities.geometryFactory.buildGeometry(obstaclesOnTheWay));
        
        // form the points of the maximum avoidance triangle
        
        Vector2D directionToB = Vector2D.create(a, b);
        Vector2D perpendicularDirection = directionToB.rotateByQuarterCircle(1).normalize();

        Coordinate turningPoint = perpendicularDirection.multiply(
                                    minimumBoundingCircle.getRadius())
                                    .translate(minimumBoundingCircle.getCentre());

        Vector2D segmentToTurningPoint = new Vector2D(a, turningPoint);
        Vector2D segmentFromTurningPoint = new Vector2D(turningPoint,b);

        // sum the lengths of the arms of the circle
        return segmentToTurningPoint.length() + segmentFromTurningPoint.length();
    }

    public double[][] computeBiggestAvoidanceDistanceMatrix(Sensor[] sensors, Collection<Obstacle> obstacles){

        // compute avoidance distance between each pair of sensors
        int n = sensors.length;
        double[][] distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {

                if(i == j)
                    distanceMatrix[i][j] = 0;
                else{
                    double avoidanceDisance = biggestAvoidanceDistance(sensors[i].getCoordinates(), sensors[j].getCoordinates(), obstacles);
                    distanceMatrix[i][j] = avoidanceDisance;
                    distanceMatrix[j][i] = avoidanceDisance;
                }

            }
        }

        return distanceMatrix;
    }

    public double[][] computeEuclidianDistanceMatrix(Sensor[] sensors, Collection<Obstacle> obstacles){

        // compute avoidance distance between each pair of sensors
        int n = sensors.length;
        double[][] distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {

                if(i == j)
                    distanceMatrix[i][j] = 0;
                else{
                    double distance = sensors[i].getCoordinates().distance(sensors[j].getCoordinates());
                    distanceMatrix[i][j] = distance;
                    distanceMatrix[j][i] = distance;
                }

            }
        }

        return distanceMatrix;
    }
    
}
