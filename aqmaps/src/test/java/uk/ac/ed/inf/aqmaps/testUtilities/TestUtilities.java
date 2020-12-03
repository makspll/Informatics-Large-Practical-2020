package uk.ac.ed.inf.aqmaps.testUtilities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Deque;
import java.util.Queue;

import com.mapbox.geojson.Point;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.client.AQSensor;
import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.client.data.W3WSquareData;
import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;


public class TestUtilities {

    public static PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
    public static GeometryFactory gf = new GeometryFactory(precisionModel);

     /**
     * 7 digit precision epsilon
     */
    public static double epsilon = 0.00000001d;

    public static void assertPathGoesThroughInOrder(Queue<PathSegment> path,double epsilon,Coordinate ...coordinates){

        if(coordinates.length == 0)
            return;

        int currCoordinateIdx = 0;
        PathSegment lastSegment = null;
        boolean foundAll = false;
        
        for (PathSegment pathSegment : path) {

            lastSegment = pathSegment;
            
            Coordinate currCoordinate = coordinates[currCoordinateIdx];
            double distance = currCoordinate.distance(pathSegment.getStartPoint());
            
            if(distance <= epsilon)
                currCoordinateIdx += 1;

            if(currCoordinateIdx == coordinates.length) {
                foundAll = true;
                break;
            }  



        }
        if(!foundAll 
            && currCoordinateIdx == coordinates.length - 1 // if only one coordinate left   
            && coordinates[currCoordinateIdx].distance(lastSegment.getEndPoint()) <= epsilon){
            foundAll = true;
        }

        Coordinate unpassedCoordinate = foundAll ? null : coordinates[currCoordinateIdx];
        assertTrue(foundAll,"Path does not go through: " + unpassedCoordinate + ", at step:" + currCoordinateIdx);

    }

    public static void assertPointsConsecutive(Queue<PathSegment> path){

        PathSegment previousSegment = null;
        for (PathSegment pathSegment : path) {

            if(previousSegment != null){
                Coordinate previousEnd = previousSegment.getEndPoint();
                Coordinate currentStart = pathSegment.getStartPoint();
                assertCoordinateEquals(
                    previousEnd, 
                    currentStart, 
                    epsilon,null);
            }
            previousSegment = pathSegment;
        }
    }

    public static void assertVectorEquals(Vector2D expected, Vector2D actual, double epsilon, String msg){
        assertEquals(expected.getX(), actual.getX(),epsilon,msg == null ?"X component is not the same":msg);
        assertEquals(expected.getY(), actual.getY(),epsilon,msg == null ?"Y component is not the same":msg);
    }

    public static void assertCoordinateEquals(Coordinate expected, Coordinate actual, double epsilon, String msg){
        assertVectorEquals(new Vector2D(expected), new Vector2D(actual), epsilon,msg);
    }



    public static void assertDirectionValid(PathSegment p){
        Vector2D vector = new Vector2D(p.getStartPoint(), p.getEndPoint());
        double angle = Math.toDegrees(vector.angle()) ;
        
        if(angle < 0){
            angle += 360;
        }

        assertEquals(
            (int)((Math.round(angle / 10) * 10) % 360) ,
            p.getDirection(),"direction of the path segment is incorrect");
    }

    public static void assertDirectionValid(int minAngle,int maxAngle,int angleIncrement,PathSegment p){
        Vector2D vector = new Vector2D(p.getStartPoint(), p.getEndPoint());
        double angle = Math.toDegrees(vector.angle()) ;
        
        if(angle < 0){
            angle += 360;
        }

        int angleRounded = ((int)Math.round(angle) % (maxAngle+1));
        int distance = Math.abs(angleRounded - p.getDirection());

        assertTrue(
            distance <= angleIncrement,
            "direction of the path segment is incorrect");

    }


    public static void assertMoveLengthsEqual(double length,double epsilon,PathSegment... p){
        int idx = 0;
        for (PathSegment pathSegment : p) {
            Vector2D vector = new Vector2D(pathSegment.getStartPoint(),pathSegment.getEndPoint());
            assertEquals(length,vector.length(),epsilon,"segment at idx: "+ idx++ + ", has a different length");
        }
    }
    public static void assertDirectionsValid(PathSegment... p){
        int i = 0;
        for (PathSegment pathSegment : p) {
            assertDoesNotThrow(()->{
                assertDirectionValid(pathSegment);
            },"Direction at idx: " + i++ + " has invalid direction");
        }
    }

    public static void assertDirectionsValid(int minAngle,int maxAngle,int angleIncrement,PathSegment... p){
        int i = 0;
        for (PathSegment pathSegment : p) {
            assertDoesNotThrow(()->{
                assertDirectionValid(minAngle,maxAngle,angleIncrement,pathSegment);
            },"Direction at idx: " + i++ + " has invalid direction");
        }
    }

    public static void assertIntersectPath(Deque<PathSegment> path, Obstacle obstacle,boolean intersects){

        for (PathSegment pathSegment : path) {
            var ls = TestUtilities.gf.createLineString(
                new Coordinate[]{pathSegment.getStartPoint(),pathSegment.getEndPoint()});

            assertTrue(intersects==!obstacle.getShape().disjoint(ls), "obstacle intersects path:" + obstacle);

            assertTrue(intersects==obstacle.getShape().intersects(ls)
                || intersects==obstacle.getShape().touches(ls),"obstacle intersects path" + obstacle);

        }
    }

    public static Sensor constructSensor(Coordinate point, float reading, float battery){

        Point p = Point.fromLngLat(point.x, point.y);

        return new AQSensor(
            new SensorData("", battery, reading), 
            new W3WAddressData("country", new W3WSquareData(), "nearestPlace",  p, "w.o.rds", "language", "map"));
    }


}
