package uk.ac.ed.inf.aqmaps.unitTests.simulation;


import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import uk.ac.ed.inf.aqmaps.simulation.Building;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

@SuppressWarnings("all")
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

        TestUtilities.assertIntersectPath(path, testBuilding,false);
    }

    @Test
    public void IntersectsPathTestPositive(){
        var path = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1.1,-1.1), 0, new Coordinate(1.1,1.1), mock(Sensor.class))
        )); 

        TestUtilities.assertIntersectPath(path, testBuilding,true);

        var path2 = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1.01,0.95), 0, new Coordinate(-0.99,1.05), mock(Sensor.class))
        )); 

        TestUtilities.assertIntersectPath(path, testBuilding,true);

    }

    @Test
    public void IntersectsPathTestEdge(){
        var path = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(new Coordinate(-1,-1), 0, new Coordinate(-1,1), mock(Sensor.class)),
            new PathSegment(new Coordinate(-1,1), 0, new Coordinate(1,1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1,1), 0, new Coordinate(1,-1), mock(Sensor.class)),
            new PathSegment(new Coordinate(1,-1), 0, new Coordinate(-1, -1), mock(Sensor.class))
        )); 


        TestUtilities.assertIntersectPath(path, testBuilding,true);
    }
}
