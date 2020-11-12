package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Building;
import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.AstarConstrainedPathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.BaseConstrainedPathPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class AstarConstrainedPathPlannerTest extends ConstrainedPathPlannerTest {

    private double moveLength = 0.1d;
    private double readingRange = 0.1d;

    private int maxMoves = 150;
    private int minAngle = 0;
    private int maxAngle = 360;
    private int angleIncrement = 10;

    AstarConstrainedPathPlanner testPlanner;

    private static Sensor mockSensor1;
    private static Sensor mockSensor2;
    private static Sensor mockSensor3;
    private static Sensor mockSensor4;

    private static Queue<Sensor> testRoute;

    @Override
    protected Coordinate setupStartCoordinate() {
        return new Coordinate(0,0);
    }

    @Override
    protected Queue<Sensor> setupRoute() {
        mockSensor1 = mock(Sensor.class);
        mockSensor2 = mock(Sensor.class);
        mockSensor3 = mock(Sensor.class);
        mockSensor4 = mock(Sensor.class);
        
        when(mockSensor1.toString()).thenReturn("Sensor 1");
        when(mockSensor2.toString()).thenReturn("Sensor 2");
        when(mockSensor3.toString()).thenReturn("Sensor 3");
        when(mockSensor4.toString()).thenReturn("Sensor 4");

        when(mockSensor1.getCoordinates()).thenReturn(new Coordinate(0, 0));
        when(mockSensor2.getCoordinates()).thenReturn(new Coordinate(0, 1));
        when(mockSensor3.getCoordinates()).thenReturn(new Coordinate(1, 1));
        when(mockSensor4.getCoordinates()).thenReturn(new Coordinate(1, 0));

        testRoute = new LinkedList<Sensor>(
                        Arrays.asList(mockSensor1, mockSensor2, mockSensor4, mockSensor3, mockSensor1, mockSensor4));
        return testRoute;
    }

    @Override
    protected Collection<Obstacle> setupObstacles() {
        return new LinkedList<Obstacle>(Arrays.asList(new Building(
            TestUtilities.gf.createPolygon(new Coordinate[]{
                new Coordinate(-0.5,0.6),
                new Coordinate(0.4,0.6),
                new Coordinate(0.4,1.6),
                new Coordinate(0.6,1.6),
                new Coordinate(0.6,0.4),
                new Coordinate(-0.5,0.4),
                new Coordinate(-0.5,0.6)
            })
        )));
    }

    @Override
    protected BaseConstrainedPathPlanner setupTestInstance() {
        return new AstarConstrainedPathPlanner(moveLength, readingRange, maxMoves, minAngle, maxAngle, angleIncrement);
    }

    @Override
    protected double getTestInstanceMoveLength() {
        return moveLength;
    }

    @Override
    protected double getTestInstanceReadingRange() {
        return readingRange;
    }

    @Override
    protected int getTestInstanceMaxAngle() {
        return maxAngle;
    }

    @Override
    protected int getTestInstanceMinAngle() {
        return minAngle;
    }

    @Override
    protected int getTestInstanceAngleIncrement() {
        return angleIncrement;
    }

    @Override
    protected int getTestInstanceMaxMoves() {
        return maxMoves;
    }
    
}
