package uk.ac.ed.inf.aqmaps.unitTests.pathfinding.hashing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;

public class GridSnappingSpatialHashTest {
    GridSnappingSpatialHash testUnit;


    @BeforeEach
    public void reset(){
        testUnit = null;
    }

    @Test
    public void boundarySimpleGridTest(){
        testUnit = new GridSnappingSpatialHash(0.5, new Coordinate(0,0));

        assertEquals(testUnit.getHash(new Coordinate(-0.49,-0.49)),testUnit.getHash(new Coordinate(0.49,0.49)));
        assertEquals(testUnit.getHash(new Coordinate(0.49,-0.49)),testUnit.getHash(new Coordinate(-0.49,0.49)));

        assertNotEquals(testUnit.getHash(new Coordinate(-0.51,-0.51)),testUnit.getHash(new Coordinate(-0.49,-0.49)));
    }


    @Test
    public void normalSimpleGridTest(){
        testUnit = new GridSnappingSpatialHash(0.5, new Coordinate(0,0));

        assertEquals(testUnit.getHash(new Coordinate(0,0)),testUnit.getHash(new Coordinate(0.1,-0.1)));
        assertEquals(testUnit.getHash(new Coordinate(1,1)),testUnit.getHash(new Coordinate(1,1.1)));

        assertNotEquals(testUnit.getHash(new Coordinate(0,0)),testUnit.getHash(new Coordinate(1,1)));
        assertNotEquals(testUnit.getHash(new Coordinate(1,1)),testUnit.getHash(new Coordinate(2,2)));

    }


}
