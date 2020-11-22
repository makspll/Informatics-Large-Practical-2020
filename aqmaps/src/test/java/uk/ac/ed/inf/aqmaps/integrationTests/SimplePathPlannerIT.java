package uk.ac.ed.inf.aqmaps.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.client.AQSensor;
import uk.ac.ed.inf.aqmaps.client.SensorData;
import uk.ac.ed.inf.aqmaps.client.W3WAddressData;
import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.simulation.DirectedSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.SimplePathPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class SimplePathPlannerIT {


    PathfindingAlgorithm<DirectedSearchNode> algorithm = new AstarTreeSearch<DirectedSearchNode>(
        new StraightLineDistance(1),
        new GridSnappingSpatialHash(1d/75d,new Coordinate(0,0)));

    SimplePathPlanner testUnit = new SimplePathPlanner(1, 10, algorithm);
    ConstrainedTreeGraph graph = new ConstrainedTreeGraph(0, 360, 10, 1, new LinkedList<Obstacle>(),
        TestUtilities.gf.createPolygon(new Coordinate[]{
            new Coordinate(-2, -2),
            new Coordinate(-2,2),
            new Coordinate(2,2),
            new Coordinate(2,-2),
            new Coordinate(-2,-2)
        }));

    @Test
    public void goalIsStartTest(){

        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(0, 0), 1f, 1f);
        var route = new LinkedList<Sensor>(Arrays.asList(
            inputSensor
        ));

        var output = testUnit.planPath(new Coordinate(0,0), route, graph, false);

        assertTrue(output.size() == 2);
        assertEquals(null,output.poll().getSensorRead());
        assertEquals(inputSensor,output.poll().getSensorRead());

        output = testUnit.planPath(new Coordinate(0,0), route, graph, true);

        assertTrue(output.size() == 2);
        assertEquals(null,output.poll().getSensorRead());
        assertEquals(inputSensor,output.poll().getSensorRead());


    }   

    @Test
    public void emptyRoute(){

        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(0, 0), 1f, 1f);
        var route = new LinkedList<Sensor>(Arrays.asList(
        ));


        var output = testUnit.planPath(new Coordinate(0,0), route, graph, false);

        // we expect the pathfinding algorithm to return one single point (the start coordinate)
        // and so the below should include two path segments which read nothing
        assertTrue(output.size() == 2);
        assertEquals(null,output.poll().getSensorRead());
        assertEquals(null,output.poll().getSensorRead());


        output = testUnit.planPath(new Coordinate(0,0), route, graph, true);
        assertTrue(output.size() == 2);
        assertEquals(null,output.poll().getSensorRead());
        assertEquals(null,output.poll().getSensorRead());


    }   

    @Test
    @Timeout(value=10,unit=TimeUnit.SECONDS)
    public void unreachableGoal(){

        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(-20, -20), 1f, 1f);
        var route = new LinkedList<Sensor>(Arrays.asList(
            inputSensor
        ));


        var output = testUnit.planPath(new Coordinate(0,0), route, graph, false);

        // if there is no way to reach the one goal, we want the 
        // path to be empty
        assertTrue(output.size() == 0);


        output = testUnit.planPath(new Coordinate(0,0), route, graph, true);
        assertTrue(output.size() == 0);


    }   

    @Test
    @Timeout(value=10,unit=TimeUnit.SECONDS)
    public void unreachableGoalFurtherDown(){

        Sensor inputSensor2 = TestUtilities.constructSensor(new Coordinate(-20, -20), 1f, 1f);
        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(0, 0), 1f, 1f);

        var route = new LinkedList<Sensor>(Arrays.asList(
            inputSensor,inputSensor2
        ));


        var output = testUnit.planPath(new Coordinate(0,0), route, graph, false);

        // if there is no way to reach the one goal, we want the 
        // path to be empty
        assertTrue(output.size() == 0);


        output = testUnit.planPath(new Coordinate(0,0), route, graph, true);
        assertTrue(output.size() == 0);


    }   

    @Test
    public void inputCollectionsNotConsumedTest(){

        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(0, 0), 1f, 1f);
        var route = new LinkedList<Sensor>(Arrays.asList(
            inputSensor,inputSensor
        ));

        var output = testUnit.planPath(new Coordinate(0,0), route, graph, false);

        assertTrue(route.size() == 2);
    }   


    @Test
    public void loopingPathTest(){

        Sensor inputSensor = TestUtilities.constructSensor(new Coordinate(0, 0), 1f, 1f);
        Sensor inputSensor2 = TestUtilities.constructSensor(new Coordinate(0, 1.5), 1f, 1f);

        var route = new LinkedList<Sensor>(Arrays.asList(
            inputSensor,inputSensor2
        ));

        var output = testUnit.planPath(new Coordinate(0,0), route, graph, true);

        assertEquals(4,output.size());
    }   
}
