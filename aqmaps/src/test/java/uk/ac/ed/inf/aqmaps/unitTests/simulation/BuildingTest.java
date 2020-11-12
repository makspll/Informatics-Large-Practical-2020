package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import uk.ac.ed.inf.aqmaps.simulation.Building;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public class BuildingTest {

    GeometryFactory gf = new GeometryFactory();
    Building testBuilding = new Building(gf.createPolygon(new Coordinate[]{
        new Coordinate(-1, -1),
        new Coordinate(-1,1),
        new Coordinate(1,1),
        new Coordinate(1,-1),
        new Coordinate(-1, -1)
    }));

    @Test
    public void IntersectsPathTestNegative(){
        var path = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1.1,-1.1), 0, new Coordinate(-1.1,1.1), mock(Sensor.class)),
            new PathSegment(new Coordinate(-1.1,1.1), 0, new Coordinate(1.1,1.1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1.1,1.1), 0, new Coordinate(1.1,-1.1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1.1,-1.1), 0, new Coordinate(-1.1, -1.1), mock(Sensor.class))
        )); 

        assertFalse(testBuilding.intersectsPath(path));
    }

    @Test
    public void IntersectsPathTestPositive(){
        var path = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1.1,-1.1), 0, new Coordinate(1.1,1.1), mock(Sensor.class))
        )); 

        assertTrue(testBuilding.intersectsPath(path));

        var path2 = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1.01,0.95), 0, new Coordinate(-0.99,1.05), mock(Sensor.class))
        )); 

        assertTrue(testBuilding.intersectsPath(path2));

    }

    @Test
    public void IntersectsPathTestEdge(){
        var path = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1,-1), 0, new Coordinate(-1,1), mock(Sensor.class)),
            new PathSegment(new Coordinate(-1,1), 0, new Coordinate(1,1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1,1), 0, new Coordinate(1,-1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1,-1), 0, new Coordinate(-1, -1), mock(Sensor.class))
        )); 


        assertTrue(testBuilding.intersectsPath(path));
    }
}