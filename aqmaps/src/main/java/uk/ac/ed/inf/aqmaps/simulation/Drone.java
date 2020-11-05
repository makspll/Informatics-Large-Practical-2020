package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.planning.CollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.PathPlanner;

public class Drone implements SensorDataCollector {

    public Drone(PathPlanner fp, CollectionOrderPlanner rp){
        flightPlanner = fp;
        routePlanner = rp;
    }

    @Override
    public Queue<PathSegment> planCollection(Coordinate startCoordinate,
        Set<Sensor> sensors,
        Collection<Obstacle> obstacles) {
        
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
        Queue<Sensor> route = routePlanner.planRoute(startSensor,sensors,obstacles);

        //// plan the detailed flight path along the route
        Queue<PathSegment> flightPath = flightPlanner.planPath(startCoordinate, 
                                            route, 
                                            obstacles);

        for (PathSegment pathSegment : flightPath) {
            Sensor read = pathSegment.getSensorRead();
            if(read != null)
                read.setHaveBeenRead(true);
        }

        return flightPath;
    }


    private PathPlanner flightPlanner;
    private CollectionOrderPlanner routePlanner;

}
