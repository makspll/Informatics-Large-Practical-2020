package uk.ac.ed.inf.aqmaps.unitTests.pathfinding.heuristics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;

@SuppressWarnings({"rawtypes","unchecked"})
public class StraightLineDistanceTest {
    StraightLineDistance testUnit;

    PathfindingGoal mockGoal=  mock(PathfindingGoal.class);
    SearchNode mockNode = mock(SearchNode.class);

    @BeforeEach
    public void reset(){
        testUnit = null;
    }


    @Test
    public void simpleTest(){

        testUnit = new StraightLineDistance(1);

        var a = new Coordinate(0,0);
        var b = new Coordinate(20,20);

        when(mockGoal.getCoordinates()).thenReturn(a);
        when(mockNode.getCoordinates()).thenReturn(b);
        
        assertEquals(a.distance(b),testUnit.heuristic(mockNode,mockGoal));
    }

    @Test
    public void relaxationFactorTest(){

        testUnit = new StraightLineDistance(2);

        var a = new Coordinate(0,0);
        var b = new Coordinate(20,20);

        when(mockGoal.getCoordinates()).thenReturn(a);
        when(mockNode.getCoordinates()).thenReturn(b);
        
        assertEquals(a.distance(b)*2,testUnit.heuristic(mockNode,mockGoal));
    }
}
