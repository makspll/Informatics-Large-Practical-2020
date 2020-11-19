package uk.ac.ed.inf.aqmaps.unitTests.output;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.visualisation.OutputFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutputFormatterTest {

    static ByteArrayOutputStream dummyStream;

    static Sensor sensorMock = mock(Sensor.class);
    static Sensor sensorMock2 = mock(Sensor.class);


    static Queue<PathSegment> testPath = new LinkedList<PathSegment>(Arrays.asList(
        new PathSegment(new Coordinate(0,0), 90, new Coordinate(0,1), null),
        new PathSegment(new Coordinate(0,1), 90,new Coordinate(0,2), sensorMock),
        new PathSegment(new Coordinate(0,2), 90,new Coordinate(0,3), sensorMock2)
    ));

    static String testPathOutput; 

    @BeforeEach
    public void setup(){
        dummyStream = new ByteArrayOutputStream();

    }

    public String readDummyOutput(){
        var inputStream = new ByteArrayInputStream(dummyStream.toByteArray());
        StringBuilder outputString = new StringBuilder();
        int c = 0;

        while((c = inputStream.read()) != -1)
            outputString.append((char)c);

        return outputString.toString();
    }

    @Test
    public void testWritePath(){

        // setup test data
        when(sensorMock.getW3WLocation()).thenReturn("address1");
        when(sensorMock2.getW3WLocation()).thenReturn("address2");
        
        testPathOutput = "0.0,0.0,90,0.0,1.0,null\n"
                           +"0.0,1.0,90,0.0,2.0,"+sensorMock.getW3WLocation()+"\n"
                           +"0.0,2.0,90,0.0,3.0,"+sensorMock2.getW3WLocation(); // notice no new line here !

        // check no exceptions are thrown
        assertDoesNotThrow(()->{
            OutputFormatter.writePath(testPath, dummyStream);
        });

        // check correct output was written
        assertEquals(testPathOutput,readDummyOutput());
        
    }

    @Test
    public void testWriteReadingsMap(){

        // setup test data
        var features = new ArrayList<Feature>(Arrays.asList(
            Feature.fromGeometry(Point.fromLngLat(0, 0)),
            Feature.fromGeometry(Point.fromLngLat(0, 0)),
            Feature.fromGeometry(Point.fromLngLat(0, 0)),
            Feature.fromGeometry(Point.fromLngLat(0, 0))
        ));

        FeatureCollection testCollection = FeatureCollection.fromFeatures(features);

        // check no exceptions are thrown
        assertDoesNotThrow(()->{
            OutputFormatter.writeReadingsMap(testCollection,dummyStream);
        });

        // check correct output was written
        assertEquals(testCollection.toJson(),readDummyOutput());
        
    }
}
