package uk.ac.ed.inf.aqmaps.unitTests.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;

public abstract class CollectionOrderPlannerTest {
    
    private BaseCollectionOrderPlanner planner;

    private static Set<Sensor> mockSensors;
    private static Sensor startSensor;
    private static Collection<Obstacle> obstacles;



    @BeforeEach
    private void setup(){
        obstacles = new LinkedList<Obstacle>();
        mockSensors = setupOtherSensors();
        startSensor = setupStartSensor();
        planner = setupTestInstance();

        assert startSensor != null;
        assert mockSensors.size() != 0;
    }

    protected abstract Sensor setupStartSensor();
    protected abstract Set<Sensor> setupOtherSensors();
    protected abstract BaseCollectionOrderPlanner setupTestInstance();


    @Test
    public void testRouteIsARing(){

        Queue<Sensor> output = planner.planRoute(startSensor
                            ,mockSensors,
                            true);
        
        Sensor firstSensor = output.peek();
        Sensor lastSensor = null;
        for (Sensor sensor : output) {
            lastSensor = sensor;
        }

        assertEquals(firstSensor,lastSensor,"first and last sensor are not the same");
    }

    @Test
    public void testRouteStartsAtStartSensor(){

        Queue<Sensor> output = planner.planRoute(startSensor
                            ,mockSensors,
                            true);
        
        Sensor firstSensor = output.peek();
        
        assertEquals(startSensor,firstSensor);
    }

    @Test
    public void testRouteVisitsAllSensorsOnce(){
        Queue<Sensor> output = planner.planRoute(startSensor
        ,mockSensors,
        true);

        assertEquals(1 + mockSensors.size(),new HashSet<Sensor>(output).size());
    }

}
