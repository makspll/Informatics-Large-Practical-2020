package uk.ac.ed.inf.aqmaps.simulation.collection;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;


public class Drone implements SensorDataCollector {

    public Drone(PathPlanner fp, BaseCollectionOrderPlanner rp){
        flightPlanner = fp;
        routePlanner = rp;
    }

    @Override
    public Queue<PathSegment> planCollection(Coordinate startCoordinate,
        Set<Sensor> sensors,
        ConstrainedTreeGraph graph,
        boolean formLoop) {
        
        //// first we identify which sensor we are the closest to
        //// at the start coordinate

        // iterate over all sensors and find the closest one
        Sensor startSensor = null;
        var smallestDistance = Double.MAX_VALUE;

        for (Sensor sensor : sensors) {

            double distance = sensor.getCoordinates().distance(startCoordinate);

            if(distance < smallestDistance){
                startSensor = sensor;
                smallestDistance = distance;
            }
                
        }

        //// then plan the high-level route
        // the other sensors set must not contain the start sensor
        // but we don't want to modify the data structure, so we put it back at the end
        sensors.remove(startSensor);

        Deque<Sensor> route = routePlanner.planRoute(startSensor,sensors,formLoop);

        if(formLoop){
            // remove the last sensor, since we form a loop but we do not need to visit it anymore
            route.removeLast();
        }


        //// plan the detailed flight path along the route
        Deque<PathSegment> flightPath = flightPlanner.planPath(startCoordinate, 
                                            route, 
                                            graph,
                                            formLoop);

        for (PathSegment pathSegment : flightPath) {
            Sensor read = pathSegment.getSensorRead();
            if(read != null)
                read.setHaveBeenRead(true);
        }

        sensors.add(startSensor);

        return flightPath;
    }


    private PathPlanner flightPlanner;
    private BaseCollectionOrderPlanner routePlanner;

}
