package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.SpatialTreeSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.planning.DiscreteStepAndAngleGraph;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class DiscreteStepAndAngleGraphTest {

    private static DiscreteStepAndAngleGraph testGraph;
    private static Collection<Obstacle> obstacles = new ArrayList<Obstacle>();
    private static Obstacle obstacle;


    private static final int MIN_ANGLE = 0;
    private static final int MAX_ANGLE = 350;

    @BeforeAll
    public static void setup(){

        obstacle = mock(Obstacle.class);

        obstacles.add(obstacle);

        testGraph = new DiscreteStepAndAngleGraph(MIN_ANGLE, MAX_ANGLE, 10, 1, obstacles);
    }

    @Test
    public void allPossibleAnglesTest(){

        var angles = testGraph.getValidDirectionsBetween(MIN_ANGLE, MAX_ANGLE);

        assertEquals(36, angles.size());
    }


    @Test
    public void neighboursNearObstacleTest(){

        when(obstacle.intersectsPath(any(Coordinate.class),any(Coordinate.class)))
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false);

        var output = testGraph.getNeighbouringNodes(new SpatialTreeSearchNode(new Coordinate(0,0), null, 0, 0));
        assertEquals(36 - 4, output.size());
    }


    @Test
    public void noNeighboursTest(){

        when(obstacle.intersectsPath(any(Coordinate.class),any(Coordinate.class)))
            .thenReturn(true);

        var output = testGraph.getNeighbouringNodes(new SpatialTreeSearchNode(new Coordinate(0,0), null, 0, 0));
        assertEquals(0, output.size());
    }
    
}
