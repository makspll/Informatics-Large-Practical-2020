package uk.ac.ed.inf.aqmaps.unitTests.simulation.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreatestAvoidanceDistanceMatrix;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

@SuppressWarnings("all")
public class GreatestAvoidanceDistanceTest {
    GreatestAvoidanceDistanceMatrix testUnit;
    Obstacle mockObstacle = mock(Obstacle.class);
    
    Sensor mockSensor1 = mock(Sensor.class);
    Sensor mockSensor2 = mock(Sensor.class);

    @BeforeEach
    public void reset(){
        testUnit = null;
    }

    @Test
    public void simpleTest(){

        when(mockObstacle.getShape()).thenReturn(TestUtilities.gf.createPolygon(new Coordinate[]{
            new Coordinate(-1,0),
            new Coordinate(0,0.5),
            new Coordinate(1,0),
            new Coordinate(0,-0.5),
            new Coordinate(-1,0)
        }));

        var a = new Coordinate(0,-2);
        var b = new Coordinate(0,2);

        when(mockSensor1.getCoordinates()).thenReturn(a);
        when(mockSensor2.getCoordinates()).thenReturn(b);
        when(mockObstacle.intersectsPath(any(), any())).thenReturn(true);

        var obstacles = new ArrayList<Obstacle>(Arrays.asList(
            mockObstacle    
        ));

        testUnit = new GreatestAvoidanceDistanceMatrix(obstacles);
        testUnit.setupDistanceMatrix(new Sensor[]{mockSensor1,mockSensor2});

        var distanceFromCircle = 1;
        var distanceToCircle = new Vector2D(-1, 2).length();
        var circumferenceQuarter = Math.PI*1*0.5;

        assertEquals(distanceFromCircle + distanceToCircle + circumferenceQuarter,testUnit.distanceBetween(0, 1));
        assertEquals(distanceFromCircle + distanceToCircle + circumferenceQuarter,testUnit.distanceBetween(1, 0));
    }

    @Test
    public void rotatedObstacleTest(){

        when(mockObstacle.getShape()).thenReturn(TestUtilities.gf.createPolygon(new Coordinate[]{
            new Coordinate(-0.5,0.5),
            new Coordinate(0,0.9),
            new Coordinate(0.5,0.5),
            new Coordinate(0,0.1),
            new Coordinate(-0.5,0.5)
        }));

        var a = new Coordinate(0,0);
        var b = new Coordinate(0,1);

        when(mockSensor1.getCoordinates()).thenReturn(a);
        when(mockSensor2.getCoordinates()).thenReturn(b);
        when(mockObstacle.intersectsPath(any(), any())).thenReturn(true);

        var obstacles = new ArrayList<Obstacle>(Arrays.asList(
            mockObstacle    
        ));

        testUnit = new GreatestAvoidanceDistanceMatrix(obstacles);
        testUnit.setupDistanceMatrix(new Sensor[]{mockSensor1,mockSensor2});
        
        var circumferenceQuarter = (Math.PI*0.5*0.5);
        var distanceToCircle = Math.sqrt(0.5);
        var distanceFromCircle = ((a.distance(b)/2) - 0.5);

        assertEquals( circumferenceQuarter + distanceToCircle + distanceFromCircle , testUnit.distanceBetween(0, 1));
        assertEquals( circumferenceQuarter + distanceToCircle + distanceFromCircle , testUnit.distanceBetween(1, 0));

    }

    @Test
    public void obstaclNotInWayTest(){

        when(mockObstacle.getShape()).thenReturn(TestUtilities.gf.createPolygon(new Coordinate[]{
            new Coordinate(1,0),
            new Coordinate(1,0.5),
            new Coordinate(1,0.6),
            new Coordinate(1,0),
        }));
        var a = new Coordinate(0,0);
        var b = new Coordinate(0,1);

        when(mockSensor1.getCoordinates()).thenReturn(a);
        when(mockSensor2.getCoordinates()).thenReturn(b);

        var obstacles = new ArrayList<Obstacle>(Arrays.asList(
            mockObstacle    
        ));

        testUnit = new GreatestAvoidanceDistanceMatrix(obstacles);
        testUnit.setupDistanceMatrix(new Sensor[]{mockSensor1,mockSensor2});
        
        assertEquals(1, testUnit.distanceBetween(0, 1));
        assertEquals(1, testUnit.distanceBetween(1, 0));

    }
}
