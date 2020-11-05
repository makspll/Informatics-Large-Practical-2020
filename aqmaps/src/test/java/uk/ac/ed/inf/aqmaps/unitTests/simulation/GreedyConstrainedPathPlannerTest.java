package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities.assertPointsConsecutive;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreedyConstrainedPathPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class GreedyConstrainedPathPlannerTest {

    GreedyConstrainedPathPlanner testPlanner;

    private static Sensor mockSensor1 = mock(Sensor.class);
    private static Sensor mockSensor2 = mock(Sensor.class);
    private static Sensor mockSensor3 = mock(Sensor.class);
    private static Sensor mockSensor4 = mock(Sensor.class);

    Queue<Sensor> testRoute1 = new LinkedList<Sensor>(
            Arrays.asList(mockSensor1, mockSensor2, mockSensor4, mockSensor3, mockSensor1, mockSensor4));

    @BeforeAll
    public static void setup() {
        when(mockSensor1.getCoordinates()).thenReturn(new Coordinate(0, 0));
        when(mockSensor2.getCoordinates()).thenReturn(new Coordinate(0, 1));
        when(mockSensor3.getCoordinates()).thenReturn(new Coordinate(1, 1));
        when(mockSensor4.getCoordinates()).thenReturn(new Coordinate(1, 0));
    }

    @BeforeEach
    public void reset() {
        testPlanner = new GreedyConstrainedPathPlanner(0.1d, 0.1, 150, 0, 350, 10);

    }

    @Test
    public void planPathTest() {
        Queue<PathSegment> path = testPlanner.planPath(new Coordinate(0, -0.2), testRoute1, new LinkedList<Obstacle>());
        // TODO: left herer
        TestUtilities.assertPathGoesThroughInOrder(path, 0.1, 
                mockSensor1.getCoordinates(),
                mockSensor2.getCoordinates(), mockSensor4.getCoordinates(), mockSensor3.getCoordinates(),
                mockSensor1.getCoordinates(), mockSensor4.getCoordinates());
    }

    @Test
    public void planPathPointsAreConsecutiveTest() {
        Queue<PathSegment> path = testPlanner.planPath(new Coordinate(0, -1), testRoute1, new LinkedList<Obstacle>());
        assertPointsConsecutive(path);
    }

    @Test
    public void planPathDirectionsValidTest() {
        Queue<PathSegment> path = testPlanner.planPath(new Coordinate(0, -1), testRoute1, new LinkedList<Obstacle>());
        TestUtilities.assertDirectionsValid(path.toArray(new PathSegment[path.size()]));
    }

    @Test
    public void planPathMoveLengthsAreCorrectTest() {
        Queue<PathSegment> path = testPlanner.planPath(new Coordinate(0, -1), testRoute1, new LinkedList<Obstacle>());
        
        TestUtilities.assertMoveLengthsEqual(0.1, 0.0000001d, path.toArray(new PathSegment[path.size()]));
    }
}
