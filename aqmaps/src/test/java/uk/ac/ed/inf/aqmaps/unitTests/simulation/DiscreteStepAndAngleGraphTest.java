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

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.DirectedSearchNode;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class DiscreteStepAndAngleGraphTest {

    private static ConstrainedTreeGraph testGraph;
    private static Collection<Obstacle> obstacles = new ArrayList<Obstacle>();
    private static Obstacle obstacle;


    private static final int MIN_ANGLE = 0;
    private static final int MAX_ANGLE = 350;

    @BeforeAll
    public static void setup(){

        obstacle = mock(Obstacle.class);
        when(obstacle.getShape()).thenReturn(TestUtilities.gf.createPolygon());

        obstacles.add(obstacle);


        testGraph = new ConstrainedTreeGraph(MIN_ANGLE, MAX_ANGLE, 10, 1, obstacles);
    }

    // @Test
    // public void allPossibleAnglesTest(){

    //     var angles = testGraph.getValidDirectionsBetween(MIN_ANGLE, MAX_ANGLE);

    //     assertEquals(36, angles.length);

    //     var correctOut = new int[]{
    //         0,10,20,30,40,50,60,70,80,90,
    //         100,110,120,130,140,150,160,170,180,190,
    //         200,210,220,230,240,250,260,270,280,290,
    //         300,310,320,330,340,350};

    //     for (int i = 0; i < correctOut.length; i++) {
    //         assertEquals(correctOut[i],angles[i]);
    //     }
    // }

    // @Test
    // public void someAnglesTest(){

    //     var angles = testGraph.getValidDirectionsBetween(30,60);

    //     assertEquals(4, angles.length);

    //     var correctOut = new int[]{
    //         30,40,50,60};

    //     for (int i = 0; i < correctOut.length; i++) {
    //         assertEquals(correctOut[i],angles[i]);
    //     }
    // }
    @Test
    public void allNeighboursTest(){

        when(obstacle.intersectsPath(any(Coordinate.class),any(Coordinate.class)))
            .thenReturn(false);

        var output = testGraph.getNeighbouringNodes(new DirectedSearchNode(new Coordinate(0,0), null, 0, 0));
        assertEquals(36, output.size());
    }


    @Test
    public void noNeighboursTest(){

        when(obstacle.intersectsPath(any(Coordinate.class),any(Coordinate.class)))
            .thenReturn(true);

        var output = testGraph.getNeighbouringNodes(new DirectedSearchNode(new Coordinate(0,0), null, 0, 0));
        assertEquals(0, output.size());
    }
    
}
