package uk.ac.ed.inf.aqmaps.unitTests.utilities;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;
import uk.ac.ed.inf.aqmaps.utilities.BVHNode;
import uk.ac.ed.inf.aqmaps.utilities.Shape;

public class BVHNodeTest {
    
    private static Shape mockShape1 = mock(Shape.class);
    private static Shape mockShape2 = mock(Shape.class);
    private static Shape mockShape3 = mock(Shape.class);
    private static Shape mockShape4 = mock(Shape.class);


    @BeforeAll 
    public static void setup(){
        when(mockShape1.getShape()).thenReturn(
            TestUtilities.gf.createPolygon( new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(-0.5,1),
                new Coordinate(0.5,1),
                new Coordinate(0,0)
            }));

        when(mockShape2.getShape()).thenReturn(
            TestUtilities.gf.createPolygon( new Coordinate[]{
                new Coordinate(1, 1),
                new Coordinate(0.5,0),
                new Coordinate(1.5,0),
                new Coordinate(1,1)
            }));

        when(mockShape3.getShape()).thenReturn(
            TestUtilities.gf.createPolygon( new Coordinate[]{
                new Coordinate(2, 0),
                new Coordinate(1.5,1),
                new Coordinate(2.5,1),
                new Coordinate(2,0)
            }));

        when(mockShape4.getShape()).thenReturn(
            TestUtilities.gf.createPolygon( new Coordinate[]{
                new Coordinate(3, 1),
                new Coordinate(2.5,0),
                new Coordinate(3.5,0),
                new Coordinate(3,1)
            }));
    }

    @Test
    public void testCollisionPositive(){

        var shapes = new ArrayList<Shape>(
            Arrays.asList(
                mockShape1,mockShape2,
                mockShape3,mockShape4
            )
        );

        var testTree = new BVHNode<Shape>(shapes);

        var possibleCollisions1 = testTree.getPossibleCollisions(mockShape1.getShape());
        var possibleCollisions2 = testTree.getPossibleCollisions(mockShape2.getShape());
        var possibleCollisions3 = testTree.getPossibleCollisions(mockShape3.getShape());
        var possibleCollisions4 = testTree.getPossibleCollisions(mockShape4.getShape());

        assertAll(()->{
            assertTrue(possibleCollisions1.contains(mockShape1));
            assertTrue(possibleCollisions1.contains(mockShape2));
        });

        assertAll(()->{
            assertTrue(possibleCollisions2.contains(mockShape1));
            assertTrue(possibleCollisions2.contains(mockShape2));
            assertTrue(possibleCollisions2.contains(mockShape3));
        });

        assertAll(()->{
            assertTrue(possibleCollisions3.contains(mockShape2));
            assertTrue(possibleCollisions3.contains(mockShape3));
            assertTrue(possibleCollisions3.contains(mockShape4));
        });

        assertAll(()->{
            assertTrue(possibleCollisions4.contains(mockShape4));
            assertTrue(possibleCollisions4.contains(mockShape3));
        });
    }

    @Test
    public void testCollisionNegative(){

        var shapes = new ArrayList<Shape>(
            Arrays.asList(
                mockShape1,mockShape2,
                mockShape3,mockShape4
            )
        );

        var testTree = new BVHNode<Shape>(shapes);

        var possibleCollisions1 = testTree.getPossibleCollisions(mockShape1.getShape());
        var possibleCollisions2 = testTree.getPossibleCollisions(mockShape2.getShape());
        var possibleCollisions3 = testTree.getPossibleCollisions(mockShape3.getShape());
        var possibleCollisions4 = testTree.getPossibleCollisions(mockShape4.getShape());

        assertAll(()->{
            assertFalse(possibleCollisions1.contains(mockShape3));
            assertFalse(possibleCollisions1.contains(mockShape4));
        });

        assertAll(()->{
            assertFalse(possibleCollisions2.contains(mockShape4));
        });

        assertAll(()->{
            assertFalse(possibleCollisions3.contains(mockShape1));
        });

        assertAll(()->{
            assertFalse(possibleCollisions4.contains(mockShape1));
            assertFalse(possibleCollisions4.contains(mockShape2));
        });
    }


    @Test
    public void testEmpty(){

        var shapes = new ArrayList<Shape>(
        );

        var testTree = new BVHNode<Shape>(shapes);

        var possibleCollisions1 = testTree.getPossibleCollisions(mockShape1.getShape());
        var possibleCollisions2 = testTree.getPossibleCollisions(mockShape2.getShape());
        var possibleCollisions3 = testTree.getPossibleCollisions(mockShape3.getShape());
        var possibleCollisions4 = testTree.getPossibleCollisions(mockShape4.getShape());

        assertAll(()->{
            assertTrue(possibleCollisions1.size() == 0);
            assertTrue(possibleCollisions2.size() == 0);
            assertTrue(possibleCollisions3.size() == 0);
            assertTrue(possibleCollisions4.size() == 0);
        });
    }
}
