package uk.ac.ed.inf.aqmaps.unitTests.visualisation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;
import uk.ac.ed.inf.aqmaps.visualisation.AQMapGenerator;
import uk.ac.ed.inf.aqmaps.visualisation.AttributeMap;
import uk.ac.ed.inf.aqmaps.visualisation.MarkerSymbol;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AQMapGeneratorTest {

    AQMapGenerator testGenerator = null;

    AttributeMap<Float,String> mockColourMap = (AttributeMap<Float, String>) mock(AttributeMap.class);
    AttributeMap<Float,MarkerSymbol> mockSymbolMap = (AttributeMap<Float,MarkerSymbol>)mock(AttributeMap.class);;

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

    Sensor mockSensor = mock(Sensor.class);

    @Test
    public void plotMapTestBasic() throws IOException, InterruptedException {

        when(mockSensor.getCoordinates()).thenReturn(new Coordinate());
        when(mockColourMap.getFor(any())).thenReturn("");
        when(mockSymbolMap.getFor(any())).thenReturn(MarkerSymbol.NO_SYMBOL);
        
        var moves = new LinkedList<PathSegment>(Arrays.asList(
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
