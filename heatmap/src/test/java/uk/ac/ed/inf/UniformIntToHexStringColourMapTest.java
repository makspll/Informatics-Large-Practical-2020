package uk.ac.ed.inf;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniformIntToHexStringColourMapTest {
    
    UniformIntToHexStringColourMap testMap;
    String[] testColours;

    /**
     * asserts that the input integer values map to the given colours.
     * the input at i is tested against the output at i.
     * @param inputs
     * @param outputs
     */
    private void AssertOutput(Integer[] inputs, String[] outputs){
        assert inputs.length == outputs.length;

        for(int i = 0; i< inputs.length;i++) {
            assertEquals(outputs[i], testMap.getColour(inputs[i]),"Colour did not match");
        }
    }
    
    @BeforeEach
    private void setUp(){
        // reset the map each time
        testColours = new String[]{"#00ff00",
                                    "#40ff00",
                                    "#80ff00",
                                    "#c0ff00",
                                    "#ffc000",
                                    "#ff8000",
                                    "#ff4000",
                                    "#ff0000"};

        testMap = new UniformIntToHexStringColourMap(0,
                                                256, 
                                                testColours
                                                );

    }

    @Test
    public void testBoundaryColours(){
        AssertOutput(new Integer[]{
                        0,
                        32,
                        64,
                        96,
                        128,
                        160,
                        192,
                        224,
                    },
                    new String[]{
                        testColours[0],
                        testColours[1],
                        testColours[2],
                        testColours[3],
                        testColours[4],
                        testColours[5],
                        testColours[6],
                        testColours[7],
                    });
    }

    @Test
    public void testExtremeColoursThrows(){
        assertThrows(IndexOutOfBoundsException.class, ()->{
            testMap.getColour(256);
        });
    }
}
