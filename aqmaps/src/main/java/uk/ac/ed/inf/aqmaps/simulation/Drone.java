package uk.ac.ed.inf.aqmaps.simulation;

import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

public class Drone implements SensorDataCollector {

    public Drone(FlightPlanner fp, RoutePlanner rp){
        flightPlanner = fp;
        routePlanner = rp;
    }

    @Override
    public Queue<PathSegment> planCollection(Coordinate startCoordinate,
        List<Sensor> sensors,
        List<Obstacle> obstacles) {
        
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
        Queue<Sensor> route = routePlanner.planRoute(startSensor, sensors);

        //// plan the detailed flight path along the route
        Queue<PathSegment> flightPath = flightPlanner.planFlight(startCoordinate, 
                                            route, 
                                            obstacles);


        return flightPath;
    }


    private FlightPlanner flightPlanner;
    private RoutePlanner routePlanner;
    
    // private List<Coordinate> currentPath = new ArrayList<Coordinate>();
    // private Coordinate currentPosition = null;
    // private Sensor currentGoal = null;

    // private double MOVE_LENGTH = 0.0003d;
    // private int MAX_MOVES = 150;
    // private double READING_RANGE = 0.0002d;

    // private void fly(int direction){
    //     // the direction is a multiple of 10 between 0 and 350 inclusive
    //     assert direction <= 350 && direction >= 0 && direction % 10 == 0;
        
    //     // we translate the current position coordinate by 
    //     // the vector heading in the given direciton
    //     Vector2D heading = MathUtilities.getHeadingVector(direction);
    //     currentPosition = heading.multiply(MOVE_LENGTH).translate(currentPosition);
    // }

    // private int clampDirection(double direction){
    //     return ((int)Math.round(direction / 10) * 10) % 350;
    // }


}
