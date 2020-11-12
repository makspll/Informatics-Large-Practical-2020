package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.CollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreedyCollectionOrderPlanner;

public class GreedyCollectionOrderPlannerTest extends CollectionOrderPlannerTest {
    private static GreedyCollectionOrderPlanner testPlanner = new GreedyCollectionOrderPlanner();

    private static Sensor mockSensor1 = mock(Sensor.class);
    private static Sensor mockSensor2 = mock(Sensor.class);
    private static Sensor mockSensor3 = mock(Sensor.class);
    private static Sensor mockSensor4 = mock(Sensor.class);


    @Override
    protected Sensor setupStartSensor() {
        when(mockSensor1.getCoordinates()).thenReturn(new Coordinate(0, 0));
        when(mockSensor1.toString()).thenReturn("Sensor 1");

        return mockSensor1;
    }

    @Override
    protected Set<Sensor> setupOtherSensors() {
        when(mockSensor2.getCoordinates()).thenReturn(new Coordinate(1, 1));
        when(mockSensor3.getCoordinates()).thenReturn(new Coordinate(1, -2));
        when(mockSensor4.getCoordinates()).thenReturn(new Coordinate(5, 1));

        when(mockSensor2.toString()).thenReturn("Sensor 2");
        when(mockSensor3.toString()).thenReturn("Sensor 3");
        when(mockSensor4.toString()).thenReturn("Sensor 4");        
        
        return new HashSet<Sensor>(Arrays.asList(mockSensor2, mockSensor3, mockSensor4));
    }

    @Override
    protected CollectionOrderPlanner setupTestInstance() {
        return testPlanner;
    }

    
}
