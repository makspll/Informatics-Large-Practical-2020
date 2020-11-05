package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreedyCollectionOrderPlanner;

public class GreedyCollectionOrderPlannerTest {
    private static GreedyCollectionOrderPlanner planner = new GreedyCollectionOrderPlanner();

    private static Sensor mockSensor1 = mock(Sensor.class);
    private static Sensor mockSensor2 = mock(Sensor.class);
    private static Sensor mockSensor3 = mock(Sensor.class);
    private static Sensor mockSensor4 = mock(Sensor.class);


    @BeforeAll
    public static void setup(){
        when(mockSensor1.getCoordinates()).thenReturn(new Coordinate(0, 0));
        when(mockSensor2.getCoordinates()).thenReturn(new Coordinate(1, 1));
        when(mockSensor3.getCoordinates()).thenReturn(new Coordinate(1, -2));
        when(mockSensor4.getCoordinates()).thenReturn(new Coordinate(5, 1));

        when(mockSensor1.toString()).thenReturn("1");
        when(mockSensor2.toString()).thenReturn("2");
        when(mockSensor3.toString()).thenReturn("3");
        when(mockSensor4.toString()).thenReturn("4");
    }


    @Test
    public void planRouteTest(){
        Queue<Sensor> route = planner.planRoute(mockSensor1, 
            new HashSet<Sensor>(Arrays.asList(mockSensor2,mockSensor3,mockSensor4)), 
            new LinkedList<Obstacle>());

        assertEquals(new LinkedList<Sensor>(Arrays.asList(
            mockSensor1,mockSensor2,mockSensor3,mockSensor4
        )), route);
    }
    
}
