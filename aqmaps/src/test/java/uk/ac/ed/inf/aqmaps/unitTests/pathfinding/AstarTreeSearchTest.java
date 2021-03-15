package uk.ac.ed.inf.aqmaps.unitTests.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.SearchGraph;
import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.SpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.PathfindingHeuristic;

@SuppressWarnings({"rawtypes","unchecked"})
public class AstarTreeSearchTest {

    AstarTreeSearch testUnit;

    SearchNode mockNode = mock(SearchNode.class);
    PathfindingGoal mockGoal = mock(PathfindingGoal.class);
    SearchGraph mockGraph = mock(SearchGraph.class);
    PathfindingHeuristic pathHeuristic = mock(PathfindingHeuristic.class);
    SpatialHash hash = mock(SpatialHash.class);

    @Test
    public void routeIsNotConsumedTest(){


        testUnit = new AstarTreeSearch(pathHeuristic, hash);

        var path = new LinkedList<PathfindingGoal>(Arrays.asList(
            mockGoal,mockGoal,mockGoal
        ));
    
        when(mockGoal.getCoordinates()).thenReturn(new Coordinate());
        when(mockNode.getCoordinates()).thenReturn(new Coordinate());
        when(hash.getHash(any())).thenReturn(1);

        testUnit.findPath(mockGraph, path, mockNode, 0.1);

        assertEquals(3, path.size(),"path was consumed");
    }


    @Test
    public void outputHasCorrectNumberOfValuesTest(){


        testUnit = new AstarTreeSearch(pathHeuristic, hash);

        var path = new LinkedList<PathfindingGoal>(Arrays.asList(
            mockGoal,mockGoal,mockGoal
        ));
    
        when(mockGoal.getCoordinates()).thenReturn(new Coordinate());
        when(mockNode.getCoordinates()).thenReturn(new Coordinate());
        when(hash.getHash(any())).thenReturn(1);

        var output = testUnit.findPath(mockGraph, path, mockNode, 0.1);

        assertEquals(1,output.size(),"output has too many values");
    }
    
}
