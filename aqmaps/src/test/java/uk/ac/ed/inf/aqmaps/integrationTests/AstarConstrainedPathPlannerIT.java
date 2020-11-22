package uk.ac.ed.inf.aqmaps.integrationTests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.simulation.Building;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.BasePathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.SimplePathPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class AstarConstrainedPathPlannerIT extends PathPlannerTestBase {

    private double scale = 0.1d;

    private double moveLength = 0.5d*scale;
    private double readingRange = 1d*scale;

    private int maxMoves = 150;
    private int minAngle = 0;
    private int maxAngle = 350;
    private int angleIncrement = 10;

    BasePathPlanner testPlanner;

    private static Sensor mockSensor1;
    private static Sensor mockSensor2;
    private static Sensor mockSensor3;
    private static Sensor mockSensor4;

    private static Deque<Sensor> testRoute;

    @Override
    protected Coordinate setupStartCoordinate() {
        return new Coordinate(0,0);
    }

    @Override
    protected Deque<Sensor> setupRoute() {
        mockSensor1 = mock(Sensor.class);
        mockSensor2 = mock(Sensor.class);
        mockSensor3 = mock(Sensor.class);
        mockSensor4 = mock(Sensor.class);
        
        when(mockSensor1.toString()).thenReturn("Sensor 1");
        when(mockSensor2.toString()).thenReturn("Sensor 2");
        when(mockSensor3.toString()).thenReturn("Sensor 3");
        when(mockSensor4.toString()).thenReturn("Sensor 4");

        when(mockSensor1.getPosition()).thenReturn(new Coordinate(0*scale, 0*scale) );
        when(mockSensor2.getPosition()).thenReturn(new Coordinate(0*scale, 10*scale));
        when(mockSensor3.getPosition()).thenReturn(new Coordinate(10*scale, 10*scale));
        when(mockSensor4.getPosition()).thenReturn(new Coordinate(10*scale, 0*scale));

        testRoute = new LinkedList<Sensor>(
                        Arrays.asList(mockSensor1,mockSensor2,mockSensor3,mockSensor4));
        return testRoute;
    }

    @Override
    protected ConstrainedTreeGraph setupGraph() {
        var obstacles = new LinkedList<Obstacle>(
            Arrays.asList(
                new Building(
                    TestUtilities.gf.createPolygon(new Coordinate[]{
                        new Coordinate(0*scale,4*scale),
                        new Coordinate(0*scale,5*scale),
                        new Coordinate(1*scale,5*scale),
                        new Coordinate(1*scale,4*scale),
                        new Coordinate(0*scale,4*scale),
                    })),
                new Building(
                    TestUtilities.gf.createPolygon(new Coordinate[]{
                        new Coordinate(9*scale,4*scale),
                        new Coordinate(9*scale,5*scale),
                        new Coordinate(10*scale,5*scale),
                        new Coordinate(10*scale,4*scale),
                        new Coordinate(9*scale,4*scale),
                    }))
            )
        );

        return new ConstrainedTreeGraph(minAngle, maxAngle, angleIncrement, moveLength, obstacles,
            TestUtilities.gf.createPolygon(
                new Coordinate[]{
                    new Coordinate(-1,-1),
                    new Coordinate(-1,11),
                    new Coordinate(11,11),
                    new Coordinate(11,-1),
                    new Coordinate(-1,-1)
                }
            ));
    }

    @Override
    protected BasePathPlanner setupTestInstance() {
        return new SimplePathPlanner(readingRange, maxMoves, new AstarTreeSearch(new StraightLineDistance(1.1), new GridSnappingSpatialHash(moveLength/75d, new Coordinate())));
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
