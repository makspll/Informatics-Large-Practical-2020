package uk.ac.ed.inf.aqmaps;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import uk.ac.ed.inf.aqmaps.client.AQSensor;
import uk.ac.ed.inf.aqmaps.client.ClientService;
import uk.ac.ed.inf.aqmaps.client.DroneWebServerClient;
import uk.ac.ed.inf.aqmaps.client.SensorData;
import uk.ac.ed.inf.aqmaps.client.W3WAddressData;
import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.GreatestAvoidanceDistance;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.PathfindingHeuristic;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.simulation.Building;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.CollectionOrderPlannerType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.DistanceMatrixType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.PathfindingHeuristicType;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreatestAvoidanceDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.GreedyCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.NearestInsertionCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.Optimiser2Opt;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.BasePathPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;
import uk.ac.ed.inf.aqmaps.visualisation.AQMapGenerator;
import uk.ac.ed.inf.aqmaps.visualisation.AttributeMap;
import uk.ac.ed.inf.aqmaps.visualisation.MarkerSymbol;
import uk.ac.ed.inf.aqmaps.visualisation.OutputFormatter;
import uk.ac.ed.inf.aqmaps.visualisation.SensorReadingColourMap;
import uk.ac.ed.inf.aqmaps.visualisation.SensorReadingMarkerSymbolMap;

/**
 * Hello world!
 *
 */
public class App {

    private static LocalDate parseCollectionDateArg(String dayString, String monthString, String yearString){
        LocalDate collectionDate = null;

        try {
            int day = Integer.parseInt(dayString);
            int month = Integer.parseInt(monthString);
            int year = Integer.parseInt(yearString);
            collectionDate =  LocalDate.of(year, month, day);

        } catch (NumberFormatException e) {
            terminateProgramWithError(ErrorCode.INVALID_ARGUMENT,
                "one of day,month or year arguments is not a valid integer");
        } catch (DateTimeException e){
            terminateProgramWithError(ErrorCode.INVALID_ARGUMENT, 
                "one of day,month or year arguments are out of range for a valid date");
        }

        return collectionDate;
    }

    private static Coordinate parseStartCoordinateArg(String x, String y){
        // start position of collection
        double startLatitude = 0;
        double startLongitude = 0;

        try {
            startLatitude = Double.parseDouble(x);
            startLongitude = Double.parseDouble(y);

        } catch (NumberFormatException e){
            terminateProgramWithError(ErrorCode.INVALID_ARGUMENT,
                "one of longitude or latitude arguments is not a valid floating point number");
        }

        return new Coordinate(startLongitude,startLatitude);
    }

    private static int parseRandomSeedArg(String seed){
        int randomSeed = 0;

        try {
            randomSeed = Integer.parseInt(seed);
        } catch (NumberFormatException e) {
            terminateProgramWithError(ErrorCode.INVALID_ARGUMENT,
                "the random seed argument is not a valid integer");        
        }

        return randomSeed;
    }

    private static int parsePortNoArg(String port){
        int portNumber = 80;

        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            terminateProgramWithError(ErrorCode.INVALID_ARGUMENT,
                "the host number argument is not a valid integer");         
        }

        return portNumber;
    }

    private static Set<Sensor> convertSensorData(ClientService clientService,List<SensorData> sensorDataList)
            throws IOException, InterruptedException {
        Set<Sensor> sensors = new HashSet<>();

        for (SensorData sensorData : sensorDataList) {
            
            // fetch sensor address data for this sensor
            W3WAddressData addressData = clientService.fetchW3WAddress(
                                            sensorData.getLocation());

            // create the sensor and add it to the set
            AQSensor sensor = new AQSensor(sensorData, addressData);
            sensors.add(sensor);
        }

        return sensors;
    }

    private static Collection<Obstacle> convertBuildingData(FeatureCollection buildingData){

        // convert geojson buildings to obstacles
        Collection<Obstacle> obstacles = new ArrayList<Obstacle>();

        try {

            for (Feature featurePolygon : buildingData.features()) {
                com.mapbox.geojson.Polygon polygon = (com.mapbox.geojson.Polygon)featurePolygon.geometry();

                Polygon jtsPolygon = GeometryUtilities.MapboxPolygonToJTSPolygon(polygon);

                Obstacle building = new Building(jtsPolygon);
                obstacles.add(building);
            }

        } catch (ClassCastException e) {

            terminateProgramWithError(ErrorCode.INVALID_CLIENT_DATA,
            "The building feature collection received from the client"
            +" contains something other than a list of polygon features.");

        }

        return obstacles;
    }

    public static void main(String[] args) throws Exception
    {
        //// first check the program is run correctly
        // if not terminate
        if(args.length != NO_OF_ARGS)
            terminateProgramWithError(ErrorCode.BAD_USAGE, "");

        //// parse the arguments

        LocalDate collectionDate = parseCollectionDateArg(args[0],args[1],args[2]);
        Coordinate startCoordinate = parseStartCoordinateArg(args[3], args[4]);
        int randomSeed = parseRandomSeedArg(args[5]);
        int portNumber = parsePortNoArg(args[6]);

        //// initialize the client service
        
        URI hostResolvedAPIURI = URI.create(
            API_BASE_URI_STRING + String.format(":%s",portNumber));

        var clientService = new DroneWebServerClient(
            HttpClient.newHttpClient(),
            hostResolvedAPIURI);

        //// load the necessary data

        List<SensorData> sensorDataList = clientService.fetchSensorsForDate(collectionDate);
        FeatureCollection buildingCollection = clientService.fetchBuildings();

        //// convert data to classes accepted by the simulation module

        // convert each sensor data element to a sensor class
        // this means we have to retrieve the W3W address information for each

        Set<Sensor> sensors = convertSensorData(clientService, sensorDataList);

        // convert geojson buildings to obstacles
        Collection<Obstacle> obstacles = convertBuildingData(buildingCollection);

        //// initialize collector

        ConstrainedTreeGraph graph = new ConstrainedTreeGraph(EASTERN_ANGLE,
            MAXIMUM_ANGLE,
            DISCRETE_ANGLE_STEP_SIZE,
            DISCRETE_POSITION_STEP_SIZE,
            obstacles,
            boundary);

        var collector = SensorDataCollectorFactory.createCollector(graph,
            READING_RANGE, MAXIMUM_MOVES_NO, 
            PathfindingHeuristicType.STRAIGHT_LINE,
            CollectionOrderPlannerType.NEAREST_INSERTION,
            DistanceMatrixType.EUCLIDIAN);

        //// run simulation 
        
        
        Queue<PathSegment> path = collector.planCollection(
            startCoordinate,
            sensors, 
            graph,
            true);

        //// produce visualisation

        var visualiser = new AQMapGenerator(markerColourMap, markerSymbols);

        FeatureCollection map = visualiser.plotMap(path, sensors);
        
        //// write everything 

        OutputFormatter.writeReadingsMap(map, new FileOutputStream(
            String.format("readings-%01d-%01d-%04d.geojson",
                collectionDate.getDayOfMonth(),
                collectionDate.getMonth().getValue(),
                collectionDate.getYear())));

        OutputFormatter.writePath(path, new FileOutputStream(
            String.format("flightpath-%01d-%01d-%04d.txt",
                collectionDate.getDayOfMonth(),
                collectionDate.getMonth().getValue(),
                collectionDate.getYear())));
    }


    private static void terminateProgramWithError(ErrorCode error,String message){
        switch(error){
            case BAD_USAGE:
                System.err.println("Invalid usage, example usage:" + "\n"
                    + "<jarname>.jar dd mm yyyy longitude latitude random_seed server_port"
                );
                break;
            case INVALID_ARGUMENT:
                System.err.println("The program received an invalid argument value: " + message);
                break;
            case INVALID_CLIENT_DATA:
                System.err.println("The data loaded from the client was invalid: " + message);
                break;
            default:
                System.err.println("An unknown error has occured, please report this to the maintainers.");
                break;
        }

        System.exit(1);
    }

    private static final String API_BASE_URI_STRING = "http://localhost";
    private static final int NO_OF_ARGS = 7;
    private static final double READING_RANGE = 0.0002d;
    private static final int EASTERN_ANGLE = 0;
    private static final int MAXIMUM_ANGLE = 350;
    private static final int MAXIMUM_MOVES_NO = 150;
    private static final int DISCRETE_ANGLE_STEP_SIZE = 10;
    private static final double DISCRETE_POSITION_STEP_SIZE = 0.0003d;

    /**
     * the confinement area of the sensor collector
     */
    private static final Polygon boundary = GeometryUtilities.geometryFactory.createPolygon(
        new Coordinate[]{
            new Coordinate(-3.192473d, 55.946233d),
            new Coordinate(-3.184319d, 55.946233d),
            new Coordinate(-3.184319d, 55.942617d),
            new Coordinate(-3.192473d, 55.942617d),
            new Coordinate(-3.192473d, 55.946233d)
        }
    );

    private static final AttributeMap<Float,String> markerColourMap = new SensorReadingColourMap(
        0f,
        256f,
        "#00ff00", // green
        "#40ff00", // medium green
        "#80ff00", // light green
        "#c0ff00", // lime green
        "#ffc000", // gold
        "#ff8000", // orange
        "#ff4000", // red / orange
        "#ff0000"  // red
    );

    private static final AttributeMap<Float,MarkerSymbol> markerSymbols = new SensorReadingMarkerSymbolMap(
        0f,
        256f,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER
    );

    private enum ErrorCode {
        BAD_USAGE,INVALID_ARGUMENT,INVALID_CLIENT_DATA
    }
}
