package uk.ac.ed.inf.aqmaps.unitTests.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

public class ConversionUtilitiesTest {
    @Test
    public void CoordinateToPointConversionTest(){
        
        var original = new Coordinate(1,1);

        assertEquals(original,
            GeometryUtilities.MapboxPointToJTSCoordinate(
                GeometryUtilities.JTSCoordinateToMapboxPoint(
                    original)));
    }

    @Test
    public void PolygonConversionTest(){
        List<List<Point>> coordinates = new LinkedList<List<Point>>(Arrays.asList(
            Arrays.asList(
                Point.fromLngLat(0, 0),
                Point.fromLngLat(0,1),
                Point.fromLngLat(1, 1),
                Point.fromLngLat(1, 0),
                Point.fromLngLat(0, 0)

            ),
            Arrays.asList(
                Point.fromLngLat(0.2, 0.2),
                Point.fromLngLat(0.2, 0.1),
                Point.fromLngLat(0.1, 0.1),
                Point.fromLngLat(0.1, 0.2),
                Point.fromLngLat(0.2, 0.2)
            )

        ));
        Polygon p = Polygon.fromLngLats(coordinates);

        var otherP = GeometryUtilities.MapboxPolygonToJTSPolygon(p);

        assertEquals(1 - 0.01, otherP.getArea(),"the area of converted polygon is not correct");

        assertEquals(10, otherP.getNumPoints(),"the number of points in converted polygon did not match up");
    }
}
