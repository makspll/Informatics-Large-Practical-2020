package uk.ac.ed.inf.aqmaps.unitTests.visualisation;

import java.util.Map;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.visualisation.AttributeMap;
import uk.ac.ed.inf.aqmaps.visualisation.UniformAttributeMap;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SensorReadingColourMapTest {

    String[] colours = new String[]{
        "#00ff00", // green
        "#40ff00", // medium green
        "#80ff00", // light green
        "#c0ff00", // lime green
        "#ffc000", // gold
        "#ff8000", // orange
        "#ff4000", // red / orange
        "#ff0000"  // red
    };

    AttributeMap<Float,String> colourMap = new UniformAttributeMap<String>(0f,256f,colours);


    @Test
    public void boundaryTest(){
        Map<Float,String> correctValues = Map.ofEntries(
            entry(0f,colours[0]),
            entry(32f,colours[1]),
            entry(64f,colours[2]),
            entry(96f,colours[3]),
            entry(128f,colours[4]),
            entry(160f,colours[5]),
            entry(192f,colours[6]),
            entry(224f,colours[7]),
            entry(255f,colours[7])
        );


        for (Float value : correctValues.keySet()) {
            assertEquals(colourMap.getFor(value),correctValues.get(value));
        }
    }

    @Test
    public void exceptionTest(){

        assertThrows(IndexOutOfBoundsException.class,()->{
            colourMap.getFor(-1f);
        });

        assertThrows(IndexOutOfBoundsException.class,()->{
            colourMap.getFor(256f);
        });
    }

}
