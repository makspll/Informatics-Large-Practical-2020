package uk.ac.ed.inf.aqmaps.simulation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.mapbox.geojson.Point;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.mockito.ArgumentMatchers;

// import uk.ac.ed.inf.aqmaps.client.Sensor;
// import uk.ac.ed.inf.aqmaps.client.W3WAddress;

public class DroneTest {
    
    @Test
    public void planCollectionTest(){

        //// prepare test data
        FlightPlanner fp = mock(FlightPlanner.class);
        RoutePlanner rp = mock(RoutePlanner.class);

        Coordinate startCoordinate = new Coordinate();
        Sensor startSensor = mock(Sensor.class);
        Obstacle obstacle = mock(Obstacle.class);

        List<Sensor> sensors = new ArrayList<Sensor>(
                                Arrays.asList(startSensor));

        List<Obstacle> obstacles = new ArrayList<Obstacle>(
                                    Arrays.asList(obstacle));

        when(startSensor.getCoordinates()).thenReturn(new Coordinate());
        
        when(rp.planRoute(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
            new LinkedList<Sensor>(
                Arrays.asList(
                    startSensor
            )));

        when(fp.planFlight(ArgumentMatchers.any(),
            ArgumentMatchers.any(), 
            ArgumentMatchers.any())).thenReturn(new LinkedList<PathSegment>(Arrays.asList(

            )));

        
        Drone drone = new Drone(fp,rp);
        
        //// perform basic checks

        // no exceptions thrown 
        assertDoesNotThrow(()->{
            drone.planCollection(startCoordinate, sensors, obstacles);
        });

        var output = drone.planCollection(startCoordinate, sensors, obstacles);
        
        // output path is of correct length
        assertEquals(0, output.size());

    }
}
