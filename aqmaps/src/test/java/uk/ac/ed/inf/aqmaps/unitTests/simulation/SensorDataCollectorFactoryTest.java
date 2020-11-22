package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.pathfinding.SearchGraph;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.CollectionOrderPlannerType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.CollectorType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.DistanceMatrixType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.PathfindingHeuristicType;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public class SensorDataCollectorFactoryTest {
    
    SensorDataCollectorFactory testUnit;
    ConstrainedTreeGraph mockGraph = mock(ConstrainedTreeGraph.class);

    @Test
    public void testAll(){
        testUnit = new SensorDataCollectorFactory();
        when(mockGraph.getBoundary()).thenReturn(TestUtilities.gf.createPolygon());
        when(mockGraph.getMoveLength()).thenReturn(0d);
        when(mockGraph.getObstacles()).thenReturn(new LinkedList<Obstacle>());

        assertDoesNotThrow(
            ()->{
                var collector = testUnit.createCollector(mockGraph, 0, 0, CollectorType.DRONE, PathfindingHeuristicType.STRAIGHT_LINE, CollectionOrderPlannerType.GREEDY, DistanceMatrixType.EUCLIDIAN);
            }
        );

        assertDoesNotThrow(
           ()->{
                var collector = testUnit.createCollector(mockGraph, 0, 0, CollectorType.DRONE, PathfindingHeuristicType.STRAIGHT_LINE, CollectionOrderPlannerType.NEAREST_INSERTION, DistanceMatrixType.EUCLIDIAN);
            }
        );

        assertDoesNotThrow(
            ()->{
                var collector = testUnit.createCollector(mockGraph, 0, 0, CollectorType.DRONE, PathfindingHeuristicType.STRAIGHT_LINE, CollectionOrderPlannerType.GREEDY, DistanceMatrixType.GREATEST_AVOIDANCE);
            }
        );

        assertDoesNotThrow(
            ()->{
                var collector = testUnit.createCollector(mockGraph, 0, 0, CollectorType.DRONE, PathfindingHeuristicType.STRAIGHT_LINE, CollectionOrderPlannerType.NEAREST_INSERTION, DistanceMatrixType.GREATEST_AVOIDANCE);
            }
        );
    }
}
