package uk.ac.ed.inf.aqmaps.unitTests.utilities;

import uk.ac.ed.inf.aqmaps.utilities.MathUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
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

        assertVectorEquals(new Vector2D(1, 0), MathUtilities.getHeadingVector(0), epsilon,null);
        assertVectorEquals(new Vector2D(0, 1), MathUtilities.getHeadingVector(90), epsilon,null);
        assertVectorEquals(new Vector2D(-1, 0), MathUtilities.getHeadingVector(180), epsilon,null);
        assertVectorEquals(new Vector2D(0, -1), MathUtilities.getHeadingVector(270), epsilon,null);

        assertVectorEquals(new Vector2D(Math.cos(Math.toRadians(45)), -Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(315), epsilon,null);
        assertVectorEquals(new Vector2D(Math.cos(Math.toRadians(45)), Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(45), epsilon,null);
        assertVectorEquals(new Vector2D(-Math.cos(Math.toRadians(45)), Math.cos(Math.toRadians(45))), MathUtilities.getHeadingVector(135), epsilon,null);
    }

    @Test 
    public void oppositeAngleTest(){
        assertEquals(190,MathUtilities.oppositeAngleFromEast(10));
        assertEquals(230,MathUtilities.oppositeAngleFromEast(50));
        assertEquals(260,MathUtilities.oppositeAngleFromEast(80));
        assertEquals(300,MathUtilities.oppositeAngleFromEast(120));
        assertEquals(70,MathUtilities.oppositeAngleFromEast(250));

        assertEquals(180,MathUtilities.oppositeAngleFromEast(360));
        assertEquals(270,MathUtilities.oppositeAngleFromEast(90));
        assertEquals(180,MathUtilities.oppositeAngleFromEast(0));
    }

    @Test
    public void thresholdEqualsTest(){
        assertTrue(MathUtilities.thresholdEquals(new Coordinate(), new Coordinate(0,1),1));
        assertTrue(MathUtilities.thresholdEquals(new Coordinate(), new Coordinate(0,0.000001)));

        assertTrue(MathUtilities.thresholdEquals(new Coordinate(), new Coordinate(0,-1),1));
        assertTrue(MathUtilities.thresholdEquals(new Coordinate(), new Coordinate(0,-0.000001)));

    }
}
