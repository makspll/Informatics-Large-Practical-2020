package uk.ac.ed.inf.aqmaps.unitTests.simulation.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;


// import uk.ac.ed.inf.aqmaps.client.Sensor;
// import uk.ac.ed.inf.aqmaps.client.W3WAddress;

@SuppressWarnings({"unchecked"})
public class DroneTest {

    PathPlanner mockPathPlanner = mock(PathPlanner.class);
    BaseCollectionOrderPlanner collectionOrderPlanner = mock(BaseCollectionOrderPlanner.class);
    Drone testUnit = new Drone(mockPathPlanner,collectionOrderPlanner); 
    ConstrainedTreeGraph mockGraph = mock(ConstrainedTreeGraph.class);

    Sensor mockSensor1 = mock(Sensor.class);
    Sensor mockSensor2 = mock(Sensor.class);
    Sensor mockSensor3 = mock(Sensor.class);
    Deque<Sensor> sensorList = new LinkedList<Sensor>(Arrays.asList(
        mockSensor1,mockSensor2,mockSensor3
    ));

    @BeforeEach
    public void reset(){


        when(mockSensor1.getCoordinates()).thenReturn(new Coordinate());
        when(mockSensor2.getCoordinates()).thenReturn(new Coordinate());
        when(mockSensor3.getCoordinates()).thenReturn(new Coordinate());

        when(collectionOrderPlanner.planRoute(any(Sensor.class), (Set<Sensor>)any(Set.class), anyBoolean())).thenReturn(sensorList);

        when(mockPathPlanner.planPath(any(Coordinate.class),(Deque<Sensor>)any(Deque.class), any(ConstrainedTreeGraph.class), anyBoolean()))
            .thenReturn(new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(), 0, new Coordinate(), mockSensor1),
            new PathSegment(new Coordinate(), 0, new Coordinate(), null),
            new PathSegment(new Coordinate(), 0, new Coordinate(), mockSensor3)
        )));
    }

    @Test
    public void planCollectionTest(){
      
        var collection = testUnit.planCollection(
            new Coordinate(0,0),
            new HashSet<Sensor>(Arrays.asList(mockSensor1,mockSensor2,mockSensor3)),
            mockGraph, 
            false, 
            0);

        verify(mockSensor1,times(1)).setHaveBeenRead(true);
        verify(mockSensor2,times(0)).setHaveBeenRead(true);
        verify(mockSensor3,times(1)).setHaveBeenRead(true);

        // should only affect the parameters of the flight and route planners
        // not the path itself
        assertEquals(3,collection.size());
        verify(collectionOrderPlanner,times(1)).planRoute(any(Sensor.class), anySet(), booleanThat((b)->b == false));
        verify(mockPathPlanner,times(1)).planPath(any(Coordinate.class),(Deque<Sensor>)any(Deque.class), any(ConstrainedTreeGraph.class), booleanThat((b)->b == false));
    }        


    @Test
    public void planCollectionLoopTest(){
      
        var collection = testUnit.planCollection(
            new Coordinate(0,0),
            new HashSet<Sensor>(Arrays.asList(mockSensor1,mockSensor2,mockSensor3)),
            mockGraph, 
            true, 
            0);

        verify(mockSensor1,times(1)).setHaveBeenRead(true);
        verify(mockSensor2,times(0)).setHaveBeenRead(true);
        verify(mockSensor3,times(1)).setHaveBeenRead(true);

        // should only affect the parameters of the flight and route planners
        // not the path itself
        assertEquals(3,collection.size());
        verify(collectionOrderPlanner,times(1)).planRoute(any(Sensor.class), anySet(), booleanThat((b)->b == true));
        verify(mockPathPlanner,times(1)).planPath(any(Coordinate.class),(Deque<Sensor>)any(Deque.class), any(ConstrainedTreeGraph.class), booleanThat((b)->b == true));

    }
}
