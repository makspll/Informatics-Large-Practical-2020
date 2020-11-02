package uk.ac.ed.inf.aqmaps.visualisation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.mockito.Mock;

import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AQMapGeneratorTest {

    AQMapGenerator testGenerator = null;

    AttributeMap<Float,String> mockColourMap = new SensorReadingColourMap(0f, 1f, "#000000","#ffffff");
    AttributeMap<Float,MarkerSymbol> mockSymbolMap = new SensorReadingMarkerSymbolMap(0f, 1f, MarkerSymbol.LIGHTHOUSE,MarkerSymbol.DANGER);

    String testLowBatteryColour = "#111111";
    MarkerSymbol testLowBatterySymbol = MarkerSymbol.CROSS;

    String testNotVisitedColour = "#222222";
    MarkerSymbol testNotVisitedSymbol = MarkerSymbol.CROSS;

    @BeforeEach
    public void reset(){
        testGenerator = new AQMapGenerator(mockColourMap,
                                        mockSymbolMap,
                                        testLowBatteryColour,
                                        testLowBatterySymbol,
                                        testNotVisitedColour,
                                        testNotVisitedSymbol);
    }

    Coordinate zeroPoint = new Coordinate(0, 0);

    @Mock
    Sensor mockSensor;

    @Test
    public void plotMapTestBasic() throws IOException, InterruptedException {

        when(mockSensor.getCoordinates()).thenReturn(new Coordinate());
        
        Queue<PathSegment> moves = new LinkedList<PathSegment>(Arrays.asList(
            new PathSegment(zeroPoint, 0, zeroPoint, null),
            new PathSegment(zeroPoint, 0, zeroPoint, mockSensor)

        ));

        var sensors = new ArrayList<Sensor>(Arrays.asList(mockSensor));

        assertDoesNotThrow(()->{
            testGenerator.plotMap(moves,sensors);

        });

        var output = testGenerator.plotMap(moves,sensors);

        // we should have a sensor marker + a line string
        assertEquals(2, output.features().size(), "Invalid number of outermost features");
    }


}
