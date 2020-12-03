package uk.ac.ed.inf.aqmaps.unitTests.simulation.planning.collectionOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;

public abstract class CollectionOrderPlannerTest {
    
    private BaseCollectionOrderPlanner planner;

    private static Set<Sensor> mockSensors;
    private static Sensor startSensor;



    @BeforeEach
    private void setup(){
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
