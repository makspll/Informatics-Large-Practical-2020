package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.BaseConstrainedPathPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public abstract class ConstrainedPathPlannerTest {
    private BaseConstrainedPathPlanner planner;

    private Coordinate startCoordinate;
    private Queue<Sensor> route;
    private Collection<Obstacle> obstacles; 
    
    @BeforeEach
    public void setup(){
        startCoordinate = setupStartCoordinate();
        route = setupRoute();
        obstacles = setupObstacles();

        planner = setupTestInstance();
    }

    protected abstract Coordinate setupStartCoordinate();
    protected abstract Queue<Sensor> setupRoute();
    protected abstract Collection<Obstacle> setupObstacles();

    protected abstract BaseConstrainedPathPlanner setupTestInstance();
    protected abstract double getTestInstanceMoveLength();
    protected abstract double getTestInstanceReadingRange();
    protected abstract int getTestInstanceMaxAngle();
    protected abstract int getTestInstanceMinAngle();
    protected abstract int getTestInstanceAngleIncrement();
    protected abstract int getTestInstanceMaxMoves();

    @Test 
    public void directionsAreValidTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        TestUtilities.assertDirectionsValid(getTestInstanceMinAngle(),
            getTestInstanceMaxAngle(),
            getTestInstanceAngleIncrement(),
            output.toArray(new PathSegment[output.size()])
            );
    }

    @Test 
    public void moveLenghtsAreValidTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        TestUtilities.assertMoveLengthsEqual(
            getTestInstanceMoveLength(),
            0.000000001d, 
            output.toArray(new PathSegment[output.size()]));

    }

    @Test
    public void pathConsecutiveTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        TestUtilities.assertPointsConsecutive(output);
    }

    public void routeIsNotConsumedTest(){
        int oldSize = route.size();
        var output = planner.planPath(startCoordinate, route, obstacles);

        assertEquals(oldSize, route.size());
    }

    @Test
    public void pathFollowsRouteTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        ArrayList<Sensor> visitedSensors = new ArrayList<Sensor>();
        for (PathSegment pathSegment : output) {
            if(pathSegment.getSensorRead() != null){
                visitedSensors.add(pathSegment.getSensorRead());
            }
        }

        Coordinate[] coordinates = new Coordinate[visitedSensors.size()];
        int i= 0;

        for (Sensor s : visitedSensors) {
            coordinates[i] = s.getCoordinates();
            i+= 1;
        }

        TestUtilities.assertPathGoesThroughInOrder(output, getTestInstanceReadingRange(), coordinates);
    }

    @Test
    public void pathReachesAtLeastOneSensor(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        for (PathSegment pathSegment : output) {
            if(pathSegment.getSensorRead() != null){
                return;
            }
        }

        assertFalse(true);
    }

    @Test
    public void pathIsUnderMaxMovesTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        assertTrue(output.size() <= getTestInstanceMaxMoves());
    }

    @Test
    public void pathDoesntCrossObstaclesTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        int idx =0;
        for (Obstacle o : obstacles) {
            assertFalse(o.intersectsPath(output),"path crosses obstacle at index: " + idx);
            idx += 1;
        }
    }

    @Test
    public void sensorsInRangeTest(){
        var output = planner.planPath(startCoordinate, route, obstacles);

        for (PathSegment pathSegment : output) {
            double distance = pathSegment.getSensorRead().getCoordinates().distance(
                pathSegment.getEndPoint()
            );

            assertTrue(distance < getTestInstanceReadingRange());
        }
    }
}
