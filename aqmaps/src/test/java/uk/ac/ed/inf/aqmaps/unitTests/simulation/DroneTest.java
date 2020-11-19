package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.mapbox.geojson.Point;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.mockito.ArgumentMatchers;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;


// import uk.ac.ed.inf.aqmaps.client.Sensor;
// import uk.ac.ed.inf.aqmaps.client.W3WAddress;

public class DroneTest {
    
    // @Test
    // public void planCollectionTest(){

    //     //// prepare test data
    //     PathPlanner fp = mock(PathPlanner.class);
    //     CollectionOrderPlanner rp = mock(CollectionOrderPlanner.class);

    //     Coordinate startCoordinate = new Coordinate();
    //     Sensor startSensor = mock(Sensor.class);
    //     Obstacle obstacle = mock(Obstacle.class);

    //     Set<Sensor> sensors = new HashSet<Sensor>(
    //                             Arrays.asList(startSensor));

    //     List<Obstacle> obstacles = new ArrayList<Obstacle>(
    //                                 Arrays.asList(obstacle));

    //     when(startSensor.getCoordinates()).thenReturn(new Coordinate());
        
    //     when(rp.planRoute(ArgumentMatchers.any(), ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(
    //         new LinkedList<Sensor>(
    //             Arrays.asList(
    //                 startSensor
    //         )));

    //     PathSegment outputPath = new PathSegment(new Coordinate(), 0, new Coordinate(), startSensor);

    //     when(fp.planPath(ArgumentMatchers.any(),
    //         ArgumentMatchers.any(), 
    //         ArgumentMatchers.any())).thenReturn(new LinkedList<PathSegment>(Arrays.asList(
    //             outputPath
    //         )));

        
    //     Drone drone = new Drone(fp,rp);
        
    //     //// perform basic checks

    //     // no exceptions thrown 
    //     assertDoesNotThrow(()->{
    //         drone.planCollection(startCoordinate, sensors, obstacles);
    //     });

    //     var output = drone.planCollection(startCoordinate, sensors, obstacles);
        
    //     // output path is of correct length
    //     assertEquals(1, output.size());

    //     // check the output path is correct
    //     assertEquals(outputPath, output.peek());

    // }
}
