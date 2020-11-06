package uk.ac.ed.inf.aqmaps.unitTests.utilities;


import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.math.Vector2D;
import static uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities.assertVectorEquals;

public class MathUtilitiesTest {
    
    @Test
    public void angleFromEastTest(){
        
        assertEquals(0,(int)MathUtilities.angleFromEast(new Vector2D(1, 0)));
        assertEquals(90,(int)MathUtilities.angleFromEast(new Vector2D(0, 1)));
        assertEquals(180,(int)MathUtilities.angleFromEast(new Vector2D(-1, 0)));
        assertEquals(270,(int)MathUtilities.angleFromEast(new Vector2D(0, -1)));
    }

    @Test
    public void getHeadingVectorTest(){
        double epsilon = 0.000001d;

        assertVectorEquals(new Vector2D(1, 0), MathUtilities.getHeadingVector(0), epsilon);
        assertVectorEquals(new Vector2D(0, 1), MathUtilities.getHeadingVector(90), epsilon);
        assertVectorEquals(new Vector2D(-1, 0), MathUtilities.getHeadingVector(180), epsilon);
        assertVectorEquals(new Vector2D(0, -1), MathUtilities.getHeadingVector(270), epsilon);

        assertVectorEquals(new Vector2D(Math.cos(Math.toRadians(45)), -Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(315), epsilon);
        assertVectorEquals(new Vector2D(Math.cos(Math.toRadians(45)), Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(45), epsilon);
        assertVectorEquals(new Vector2D(-Math.cos(Math.toRadians(45)), Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(135), epsilon);
    }
}
