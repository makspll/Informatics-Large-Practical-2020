package uk.ac.ed.inf.aqmaps.unitTests.visualisation;

import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.visualisation.MarkerSymbol;
import uk.ac.ed.inf.aqmaps.visualisation.SensorReadingColourMap;
import uk.ac.ed.inf.aqmaps.visualisation.SensorReadingMarkerSymbolMap;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SensorReadingMarkerSymbolMapTest {

    MarkerSymbol[] symbols = new MarkerSymbol[]{
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER
    };

    SensorReadingMarkerSymbolMap symbolMap = new SensorReadingMarkerSymbolMap(0f,256f,symbols);


    @Test
    public void boundaryTest(){
        Map<Float,MarkerSymbol> correctValues = Map.ofEntries(
            entry(0f,symbols[0]),
            entry(32f,symbols[1]),
            entry(64f,symbols[2]),
            entry(96f,symbols[3]),
            entry(128f,symbols[4]),
            entry(160f,symbols[5]),
            entry(192f,symbols[6]),
            entry(224f,symbols[7]),
            entry(255f,symbols[7])
        );

        for (Float value : correctValues.keySet()) {
            assertEquals(symbolMap.getFor(value),correctValues.get(value));
        }
    }

    @Test
    public void exceptionTest(){

        assertThrows(IndexOutOfBoundsException.class,()->{
            symbolMap.getFor(-1f);
        });

        assertThrows(IndexOutOfBoundsException.class,()->{
            symbolMap.getFor(256f);
        });
    }

}
