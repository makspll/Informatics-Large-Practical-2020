package uk.ac.ed.inf.aqmaps.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DiscreteStepAndAngleGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.ConstrainedPathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;


public abstract class PathPlannerTestBase {
    private ConstrainedPathPlanner planner;

    private Coordinate startCoordinate;
    private Deque<Sensor> route;
    private DiscreteStepAndAngleGraph graph;
    
    @BeforeEach
    public void setup(){
        startCoordinate = setupStartCoordinate();
        route = setupRoute();
        graph = setupGraph();

        planner = setupTestInstance();
    }

    protected abstract Coordinate setupStartCoordinate();
    protected abstract Deque<Sensor> setupRoute();
    protected abstract DiscreteStepAndAngleGraph setupGraph();

    protected abstract ConstrainedPathPlanner setupTestInstance();
    protected abstract double getTestInstanceMoveLength();
    protected abstract double getTestInstanceReadingRange();
    protected abstract int getTestInstanceMaxAngle();
    protected abstract int getTestInstanceMinAngle();
    protected abstract int getTestInstanceAngleIncrement();
    protected abstract int getTestInstanceMaxMoves();

    @Test 
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void directionsAreValidTest(){
        var output = planner.planPath(startCoordinate, route, graph,true );

        TestUtilities.assertDirectionsValid(getTestInstanceMinAngle(),
            getTestInstanceMaxAngle(),
            getTestInstanceAngleIncrement(),
            output.toArray(new PathSegment[output.size()])
            );
    }

    @Test 
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void moveLenghtsAreValidTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        TestUtilities.assertMoveLengthsEqual(
            getTestInstanceMoveLength(),
            TestUtilities.epsilon, 
            output.toArray(new PathSegment[output.size()]));

    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void pathConsecutiveTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        TestUtilities.assertPointsConsecutive(output);
    }

    public void routeIsNotConsumedTest(){
        int oldSize = route.size();
        var output = planner.planPath(startCoordinate, route, graph,true);

        assertEquals(oldSize, route.size());
    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void pathFollowsRouteTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        ArrayList<Sensor> visitedSensors = new ArrayList<Sensor>();
        for (PathSegment pathSegment : output) {
            if(pathSegment.getSensorRead() != null){
                visitedSensors.add(pathSegment.getSensorRead());
            }
        }

        Coordinate[] coordinates = new Coordinate[visitedSensors.size()];
        int i= 0;

        for (Sensor s : visitedSensors) {
            coordinates[i] = s.getPosition();
            i+= 1;
        }

        TestUtilities.assertPathGoesThroughInOrder(output, getTestInstanceReadingRange(), coordinates);
    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void pathReachesAtLeastOneSensor(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        for (PathSegment pathSegment : output) {
            if(pathSegment.getSensorRead() != null){
                return;
            }
        }

        assertFalse(true);
    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void pathIsUnderMaxMovesTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        assertTrue(output.size() <= getTestInstanceMaxMoves());
    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void pathDoesntCrossObstaclesTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        int idx =0;
        for (Obstacle o : graph.getObstacles()) {
            assertFalse(o.intersectsPath(output),"path crosses obstacle at index: " + idx);
            idx += 1;
        }
    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void sensorsInRangeTest(){
        var output = planner.planPath(startCoordinate, route, graph,true);

        for (PathSegment pathSegment : output) {
            
            if(pathSegment.getSensorRead() == null)
                break;

            double distance = pathSegment.getSensorRead().getCoordinates().distance(
                pathSegment.getEndPoint()
            );

            assertTrue(distance < getTestInstanceReadingRange());
        }
    }
}
