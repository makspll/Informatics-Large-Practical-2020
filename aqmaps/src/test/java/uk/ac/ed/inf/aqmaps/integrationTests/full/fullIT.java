package uk.ac.ed.inf.aqmaps.integrationTests.full;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.ac.ed.inf.aqmaps.App;
import uk.ac.ed.inf.aqmaps.client.AQSensor;
import uk.ac.ed.inf.aqmaps.client.AQWebServerClient;
import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.CollectionOrderPlannerType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.DistanceMatrixType;
import uk.ac.ed.inf.aqmaps.simulation.SensorDataCollectorFactory.PathfindingHeuristicType;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;
import uk.ac.ed.inf.aqmaps.utilities.GeometryFactorySingleton;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;
import uk.ac.ed.inf.aqmaps.visualisation.AttributeMap;
import uk.ac.ed.inf.aqmaps.visualisation.MarkerSymbol;
import uk.ac.ed.inf.aqmaps.visualisation.UniformAttributeMap;

import com.google.gson.reflect.TypeToken;

@SuppressWarnings({"unchecked"})
/**
 * This is a massive integration test designed to completely and thoroughly test the output of the program.
 * Should not necessarily be run on build and hence it's an IT as opposed to a unit test.
 * This test checks all the paths are valid, do not cross buildings, the distances are within tolerances and so on.
 */
public class fullIT {
    App app = new App();

    HttpClient mockClient = mock(HttpClient.class);


        
    String[] colours = new String[]{
        "#00ff00", // green
        "#40ff00", // medium green
        "#80ff00", // light green
        "#c0ff00", // lime green
        "#ffc000", // gold
        "#ff8000", // orange
        "#ff4000", // red / orange
        "#ff0000"  // red
    };

    MarkerSymbol[] symbols = new MarkerSymbol[]{
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.LIGHTHOUSE,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER,
        MarkerSymbol.DANGER
    };
    
    
    AttributeMap<Float, String> colourMap = new UniformAttributeMap<String>(0f, 256f, colours);
    AttributeMap<Float, MarkerSymbol> symbolMap = new UniformAttributeMap<MarkerSymbol>(0f, 256f, symbols);
    
    /**
     * The main program testing routine, more can be employed to check for more parameters.
     * @throws Exception
     */
    @Test
    public void testNormalDataMethods() throws Exception {

        Coordinate[] startingCoordinates = new Coordinate[]{
            // new Coordinate( // bottom right
            //     -3.1846618652343746,
            //     55.942888563895934),
            // new Coordinate( // in crack of informatics forum
            //     -3.186888098716736,
            //     55.944838225043306),
            // new Coordinate( // top left
            //     -3.19087,
            //     55.945778
            // ),
            new Coordinate( // default starting point as per cw
                -3.1878,
                55.9444)};

        String resultsFilePath = "results/normal-data/data.csv";
        String testServerDir = "/normalTestData";

        performTests(resultsFilePath,
         testServerDir, 
         startingCoordinates,
         new PathfindingHeuristicType[]{PathfindingHeuristicType.STRAIGHT_LINE},
         new CollectionOrderPlannerType[]{CollectionOrderPlannerType.NEAREST_INSERTION},
         new DistanceMatrixType[]{DistanceMatrixType.EUCLIDIAN},
         new float[]{1.5f},
         new double[]{MOVE_LENGTH/75d},
         new double[]{0.1d*MOVE_LENGTH});
    
    }

    /**
     * performs tests on all combinations of the given parameters and outputs them to a csv file, requires that test data is in src/test/resources
     * @param resultFilePath
     * @param testServerDataDir
     * @param startingCoordinates
     * @param heuristics
     * @param plannerTypes
     * @param matrixTypes
     * @param relaxationFactors
     * @param hashingGridWidths
     * @param opt2Epsilons
     * @throws IOException
     */
    private void performTests(String resultFilePath,String testServerDataDir,
        Coordinate[] startingCoordinates,
        PathfindingHeuristicType[] heuristics,
        CollectionOrderPlannerType[] plannerTypes,
        DistanceMatrixType[] matrixTypes,
        float[] relaxationFactors,
        double[] hashingGridWidths,
        double[] opt2Epsilons
        ) throws IOException {

        // prepare all files
        String resourcesPath = "src/test/resources";

        File resources = new File(resourcesPath);
        String absoluteServerDataPath = resources.getAbsolutePath() + testServerDataDir;

        File resultFile = new File(resultFilePath);
        File resultDir = new File(resultFile.getParent());

        if (!resultDir.exists()){
            resultDir.mkdirs();
        }

        // are we running the first test ? used to append headers on first run
        boolean firstTest = true;

        // as horendous as this looks this won't ever be fully utilized
        // we do this only to cover all possible bases with a simple call to this function
        // idealy only one of the arguments will vary at a time
        for (Coordinate coordinate : startingCoordinates) {
            for (LocalDate date : getDatesAvailableAt(absoluteServerDataPath)) {
                for (var heuristicType : heuristics) {
                    for (var plannerType : plannerTypes) {
                        for (var matrixType: matrixTypes){
                            for (var relaxationFactor: relaxationFactors){
                                for(var gridWidth: hashingGridWidths){
                                    for(var epsilon: opt2Epsilons){
                                        
                                        // prepare arguments to the program

                                        String[] args = new String[]{
                                            date.getDayOfMonth()+"" ,
                                            date.getMonthValue()+"" ,
                                            date.getYear()+"",
                                            coordinate.y+"",
                                            coordinate.x+"",
                                            "5679",
                                            "1111",
                                            "-tspp",plannerType.toString(),
                                            "-dm",matrixType.toString(),
                                            "-r",relaxationFactor+"",
                                            "-eps",epsilon+"",
                                            "-sw",gridWidth+""};
                                            
                                        
                                        // prepare output variables
                                        boolean failed = false;
                                        String failureReason = null;
                                        int pathLength = -1;
                                        int sensorsReached = -1;
                                        long executionTime = -1;
                                        boolean returnsCloseToStart = false;
                                        try {
                                            // measure time
                                            var startTime = System.nanoTime();
                                            testFullOn(absoluteServerDataPath, args);
                                            var endTime = System.nanoTime();
                                            
                                            // read the generated flight path and geojson
                                            var flightpath = readFlightpathOutput(date, absoluteServerDataPath + "/words");
                                            var geojson = readGeojsonOutput(date);
                                            
                                            // calculate how long it took,
                                            // check the path is correct and measure path length

                                            executionTime = endTime - startTime;
                                            pathLength = flightpath.size();
                                            sensorsReached = countSensorsReached(flightpath);

                                            var lastPoint = flightpath.peekLast().getEndPoint();
                                            var firstPoint = flightpath.peek().getStartPoint();

                                            double distanceFromEndToStart = firstPoint.distance(lastPoint);
                                            
                                            // make sure path returns to start
                                            returnsCloseToStart = distanceFromEndToStart < RETURN_POS_DISTANCE_MAX;
                                            
                                            // assert the output is correct 
                                            assertTrue(returnsCloseToStart);
                                            assertOutput(date, absoluteServerDataPath,geojson, flightpath);
                                        } catch (Exception e) {
                                            failed = true;
                                            failureReason = e.toString();
                                        }
                                        
                                        // append all this data to a file on the fly
                                        appendResultToCsv(resultFilePath,
                                            date, 
                                            pathLength,
                                            coordinate.getX(),
                                            coordinate.getY(),
                                            sensorsReached,
                                            returnsCloseToStart, 
                                            executionTime, 
                                            failed, 
                                            failureReason, 
                                            heuristicType, 
                                            plannerType, 
                                            matrixType,
                                            relaxationFactor,
                                            gridWidth,
                                            epsilon,
                                            firstTest);
                                        System.out.println("Finished test: " + date.toString());

                                        firstTest = false;
                                        
                                        // clean up files, remove generated ones apart from data
                                        try {
                                            var flightPathGen = new File(String.format("flightpath-%02d-%02d-%04d.txt",
                                            date.getDayOfMonth(),
                                            date.getMonthValue(),
                                            date.getYear())); 

                                            var readingsGen = new File(String.format("readings-%02d-%02d-%04d.geojson",
                                                date.getDayOfMonth(),
                                                date.getMonthValue(),
                                                date.getYear()));
                                                
                                            flightPathGen.delete();
                                            readingsGen.delete();
                                        } catch (Exception e) {
                                            System.err.println("couldnt delete file");
                                        }
                                        

                                    }
                                }
                            }
                        }
                    }
                }
               
                
            }
        }

    }

    private void appendResultToCsv(
        String path, 
        LocalDate date, 
        int pathLength, 
        double startX,
        double startY,
        int sensorsReached, 
        boolean returnsCloseToStart,
        long executionTime, 
        boolean failed, 
        String failMsg,
        PathfindingHeuristicType heuristicType,
        CollectionOrderPlannerType plannerType,
        DistanceMatrixType matrixType,
        float relaxationFactor,
        double hashingGridWidth,
        double opt2Epsilon,
        boolean appendHeaders
        ) throws IOException {

        if(appendHeaders){
            appendLineTo(
                path,
                String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    "day",
                    "month",
                    "year",
                    "moves",
                    "start x",
                    "start y",
                    "sensorsReached",
                    "returnsClose",
                    "time(ms)",
                    "failed",
                    "failedCause",
                    "pathfindingHeuristic",
                    "plannerType",
                    "distanceMatrixType",
                    "AstarRelaxationFactor",
                    "hashingGridWidth",
                    "2optEpsilon"));
        }
        appendLineTo(
            path,
            String.format("%02d,%02d,%04d,%d,%f,%f,%d,%b,%f,%b,%s,%s,%s,%s,%f,%f,%f",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear(),
                pathLength,
                startX,
                startY,
                sensorsReached,
                returnsCloseToStart,
                executionTime*0.000001,
                failed,
                failMsg,
                heuristicType.toString(),
                plannerType.toString(),
                matrixType.toString(),
                relaxationFactor,
                hashingGridWidth,
                opt2Epsilon));
    }

    private void appendLineTo(String path, String line) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(line);
        bw.newLine();
        bw.close();
    }
    
    private int countSensorsReached(Deque<PathSegment> path){
        int count = 0;
        for (PathSegment pathSegment : path) {
            if(pathSegment.getSensorRead() != null){
                count++;
            }
        }
        return count;
    }
    /**
     * runs a full test on the given root path test dat alocation and arguments
     * @param dataRootPath
     * @param args
     * @throws Exception
     */
    private void testFullOn(String dataRootPath, String... args)
            throws Exception {
        // inject fake http client
        FieldSetter.setField(app, app.getClass().getDeclaredField("httpClient"), mockClient);
        when(mockClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenAnswer(new Answer<HttpResponse<String>>() {
            @Override
            public HttpResponse<String> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();

                var httpRequest = (HttpRequest)args[0];
                var response = (HttpResponse<String>)mock(HttpResponse.class);
                var relativePath = httpRequest.uri().getPath();
                when(response.body()).thenReturn(getFileContentsAt(dataRootPath, relativePath));
                when(response.statusCode()).thenReturn(200);

                return response;
            }
          });

        App.main(args);
    }

    /**
     * Gets the entire file content at the given fiele path
     * @param rootPath
     * @param relativePath
     */
    private String getFileContentsAt(String rootPath,String relativePath) throws IOException {
        File file = new File(rootPath + relativePath);

        var reader = new FileReader(file);
                
        var currChar = 0;
        var stringBuilder = new StringBuilder();
        while((currChar = reader.read()) != -1){
            stringBuilder.append((char)currChar);
        }
        reader.close();
        return stringBuilder.toString();
    }

    /**
     * Returns the available test data in the folder (dates)
     */
    private List<LocalDate> getDatesAvailableAt(String rootPath){
        
        var output = new ArrayList<LocalDate>();
        
        File file = new File(rootPath + "/maps");

        for (String year : file.list()) {

            File yearFile = new File(file.getPath() + "/"+ year);

            for (String month : yearFile.list()) {

                File monthFile = new File(yearFile.getPath() + "/" + month);

                for (String day : monthFile.list()) {
                    
                    File dayFile = new File(monthFile +"/" +day);

                    output.add(LocalDate.of(
                        Integer.parseInt(yearFile.getName()),
                        Integer.parseInt(monthFile.getName()),
                        Integer.parseInt(dayFile.getName())));
                }
            }
        } 

        return output;
    }

    /**
     * Asserts that a path segmenth holds teh correct information
     * @param p
     * @param d
     */
    private void assertPathSegment(PathSegment p, LocalDate d){
        // assert sensor read within range
        if(p.getSensorRead() != null){

            assertTrue(
                p.getEndPoint().distance(
                    p.getSensorRead().getCoordinates()) <=
                READING_RANGE, 
                "sensor out of range at:" +d.toString());
        }

        // assert move length correct

        var moveLength = p.getStartPoint().distance(p.getEndPoint());
        GeometryFactorySingleton.getGeometryFactory().getPrecisionModel().makePrecise(moveLength);

        assertEquals(MOVE_LENGTH,
            moveLength,
            TOLERANCE,"Move length is incorrect at :" + d.toString());

        // assert is not on the boundary
        var lineString = GeometryFactorySingleton.getGeometryFactory().createLineString(new Coordinate[]{p.getStartPoint(),p.getEndPoint()});
        assertTrue(boundary.getBoundary().disjoint(lineString),"path segment touches the boundary at:" + d.toString());
    }

    /**
     * asserts that the written output is correct
     * @param date
     * @param rootPath
     * @param featureCollection
     * @param flightPath
     * @throws IOException
     */
    private void assertOutput(LocalDate date,
        String rootPath,
        FeatureCollection featureCollection, 
        Deque<PathSegment> flightPath) throws IOException {
    
        // check that the path is valid
        // 1) each sensor read is read within reading range off of the end segment
        // 2) the path segments are consecutive
        // 3) the path segments number is less than or equal to 150
        // 4) the path length is equal to move length
        // 5) assert the path is always within the confinement area
        assertTrue(flightPath.size() <= 150,"flight path is above 150 moves");

        PathSegment previous = flightPath.poll();
        
        var visitedSensors = new HashSet<String>();
        var sensorCoordinates = new HashMap<String,Coordinate>();

        assertPathSegment(previous, date);

        if(previous.getSensorRead() != null){
            var sensor= previous.getSensorRead();
            visitedSensors.add(sensor.getW3WLocation());
            sensorCoordinates.put(sensor.getW3WLocation(),sensor.getCoordinates());
        }

        for (PathSegment current : flightPath) {

            assertPathSegment(current,date);

            TestUtilities.assertCoordinateEquals(
                    previous.getEndPoint(), 
                    current.getStartPoint(), 
                    TOLERANCE,
                    "path not consecutive at:" + date.toString());
    
            if(current.getSensorRead() != null){
                var sensor = current.getSensorRead();
                visitedSensors.add(sensor.getW3WLocation());
                sensorCoordinates.put(sensor.getW3WLocation(),sensor.getCoordinates());
            }


            previous = current;
        }


        // check that the sensors have the right colours, and reading values
        
        //// parse the sensor data for the day

        String json = getFileContentsAt(rootPath, String.format("/maps/%04d/%02d/%02d/air-quality-data.json",date.getYear(),date.getMonthValue(),date.getDayOfMonth()));
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SensorData.class, AQWebServerClient.sensorDataDeserializer);
        Gson gson = gsonBuilder.create();

        // capture type of list of sensors ussing annonymous inner class
        Type sensorListType = 
            new TypeToken<ArrayList<SensorData>>() {}.getType(); 

        ArrayList<SensorData> sensorList =
            gson.fromJson(json, sensorListType);

        // extract the sensor data into dictionaries

        var addressToColour = new HashMap<String,String>();
        var addressToSymbol = new HashMap<String,String>();
        var addressToReading = new HashMap<String,Float>();

        for (SensorData sensorData : sensorList) {
            String address = sensorData.getW3WLocation();
            boolean visited =visitedSensors.contains(sensorData.getW3WLocation());
            boolean lowBattery = sensorData.getBattery() < 10f;

            if(!visited){
                addressToColour.put(address,"#aaaaaa");
                addressToSymbol.put(address,"");
            } else if(lowBattery){
                addressToColour.put(address,"#000000");
                addressToSymbol.put(address,MarkerSymbol.CROSS.toString());            
            } else {
                addressToColour.put(address,colourMap.getFor(sensorData.getReading()));
                addressToSymbol.put(address,symbolMap.getFor(sensorData.getReading()).toString());
            }
            addressToReading.put(address,sensorData.getReading());
        }

        // check that
        // 1) sensors are placed in the correct positions
        // 2) sensors have correct reading colours and symbols

        for (Feature feature : featureCollection.features()) {
            if(feature.geometry() instanceof LineString)
                continue;
            
            var marker = (Point)feature.geometry();
            String location = feature.getStringProperty("location");
            var expectedColour = addressToColour.get(location);
            var expectedSymbol = addressToSymbol.get(location);

            var position = new Coordinate(marker.longitude(),marker.latitude());

            TestUtilities.assertCoordinateEquals(
                position, 
                sensorCoordinates.get(location), 
                TOLERANCE,
                "marker position not correct:" + location + "," + date.toString());
            
            assertEquals(expectedColour,feature.getStringProperty("marker-color"),"marker colour not correct:" + date.toString() + "," +location);
            assertEquals(expectedColour,feature.getStringProperty("rgb-string"),"marker colour not correct:" + date.toString() + ","+location);
            
            var actualSymbol =feature.getStringProperty("marker-symbol"); 
            assertEquals(expectedSymbol == null ? null : expectedSymbol.toString()
            ,actualSymbol == null ? null : actualSymbol.toString(),"marker symbol not correct:" + date.toString() + "," + location);

        }
    }

    /**
     * Parses the output geojson
     * @param date
     * @return
     * @throws IOException
     */
    private FeatureCollection readGeojsonOutput(LocalDate date) throws IOException {

        var relativePath = String.format("readings-%02d-%02d-%04d.geojson",
            date.getDayOfMonth(),
            date.getMonthValue(),
            date.getYear());

        return FeatureCollection.fromJson(getFileContentsAt("", relativePath));
    }

    /**
     * Reads the fight path output (only sensor location data is included in the path segment sensor data)
     * @param date
     * @param rootWordsLocation
     * @return
     * @throws IOException
     */
    private Deque<PathSegment> readFlightpathOutput(LocalDate date, String rootWordsLocation) throws IOException {


        Deque<PathSegment> pathSegments = new LinkedList<PathSegment>();

        var relativePath = String.format("flightpath-%02d-%02d-%04d.txt",
            date.getDayOfMonth(),
            date.getMonthValue(),
            date.getYear());

        var content = getFileContentsAt("", relativePath);

        var lines = content.split("\\r?\\n");

        for (String line : lines) {

            var values = line.split(",");

            double startX = Double.parseDouble(values[1]);
            double startY = Double.parseDouble(values[2]);

            int dir = Integer.parseInt(values[3]);

            double endX = Double.parseDouble(values[4]);
            double endY = Double.parseDouble(values[5]);
    
            // find out location of sensor
            // if it exists
            Sensor sensor = null;
            if(!values[6].equals("null")){

                String[] words = values[6].split("\\.");

                String locationDetails = getFileContentsAt(rootWordsLocation, "/" + words[0] + "/" + words[1] + "/" + words[2] + "/details.json");

                var gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(W3WAddressData.class, AQWebServerClient.w3wAddressDeserializer);
                Gson gson = gsonBuilder.create();
        
                W3WAddressData address =
                    gson.fromJson(locationDetails, W3WAddressData.class);
                sensor = new AQSensor(new SensorData(), address);
            }

            pathSegments.add(
                new PathSegment(
                    new Coordinate(startX,startY),
                    dir, 
                    new Coordinate(endX,endY),
                    sensor));
            
        }

        return pathSegments;
    }

    private static double READING_RANGE = 0.0002;
    private static double MOVE_LENGTH = 0.0003;

    /**
     * exclusive
     */
    private static double RETURN_POS_DISTANCE_MAX = 0.0003;

    private static double TOLERANCE = 0.00000000001d;
    
    private static final Polygon boundary = GeometryFactorySingleton.getGeometryFactory().createPolygon(
        new Coordinate[]{
            new Coordinate(-3.192473d, 55.946233d),
            new Coordinate(-3.184319d, 55.946233d),
            new Coordinate(-3.184319d, 55.942617d),
            new Coordinate(-3.192473d, 55.942617d),
            new Coordinate(-3.192473d, 55.946233d)
        }
    );
}
