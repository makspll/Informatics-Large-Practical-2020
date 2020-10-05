package uk.ac.ed.inf.heatmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * Generates a geo-json file which visualises the sensor data given,
 * according to emission values of each sensor. 
 */
public class App{

    
    /**
     * Main method. Will always gracefully terminate the program on encountering known errors.
     * Expects the input file to contain SENSOR_COUNT_Y lines of SENSOR_COUNT_X comma separated integers
     * between 0 and 255 inclusive followed by a newline.
     * @param args the program's arguments, expects only the input file name as the first argument
     */
    public static void main(String[] args) {

        // if program is launched incorrectly, we have to terminate the program
        if(args.length != 1){
            terminateProgramWithError(ErrorCode.BAD_PROGRAM_USAGE,"");
        } else {

            // the first argument is the filename of the input file
            var inputFile = new File(args[0]);

            // parse sensor readings into 2d array, handle errors gracefully
            int[][] sensorReadings = null;
            try {
                sensorReadings = parseSensorReadings(inputFile);
            } catch (IOException e) {
                terminateProgramWithError(ErrorCode.CANNOT_READ_INPUT_FILE,e.getMessage());
            } catch (IllegalArgumentException e){
                terminateProgramWithError(ErrorCode.INPUT_FILE_MALFORMED,e.getMessage());
            }


            // generate polygon features and put them into a feature collection
            // we don't use var on function calls for readability
            FeatureCollection featureCollection = generateFeaturesGrid(sensorReadings);

            // write the geo-json to a file
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(OUTPUT_FILE);
                fileWriter.write(featureCollection.toJson());
                fileWriter.close();

            } catch (Exception e) {
                terminateProgramWithError(ErrorCode.CANNOT_CREATE_OUTPUT_FILE,e.getMessage());
            }
        }
    }


    /**
     *  the output file to which we write heatmap geo-json data
     */  
    private static final File OUTPUT_FILE = new File("heatmap.geojson");

    /**
     * the amount of rows of sensors/sensor readings to expect in each file
     */
    private static final int SENSORS_COUNT_Y = 10;

    /**
     * the amount of sensors/sensor readings to expect in each row of the file
     */
    private static final int SENSORS_COUNT_X = 10;


    /**
     * Drone confinement area upper left corner coordinate when treated as a square facing north 
     */
    private static final Point CONFINEMENT_TOP_LFT = Point.fromLngLat(-3.192473d, 
                                                                    55.946233d);

    /**
     * Drone confinement area upper right corner coordinate when treated as a square facing north 
     */
    private static final Point CONFINEMENT_TOP_RGHT = Point.fromLngLat(-3.184319d, 
                                                                    55.946233d);

    /**
     * Drone confinement area lower left corner coordinate when treated as a square facing north 
     */
    private static final Point CONFINEMENT_BOT_LFT = Point.fromLngLat(-3.192473d, 
                                                                    55.942617d);


    /**
     * Drone confinement area's height when treated as a square facing north 
     */
    private static final double CONFINEMENT_HEIGHT = findDistanceBetween(CONFINEMENT_TOP_LFT, 
                                                                    CONFINEMENT_BOT_LFT); 

    /**
     * Drone confinement area's width when treated as a square facing north 
     */
    private static final double CONFINEMENT_WIDTH = findDistanceBetween(CONFINEMENT_TOP_LFT, 
                                                                    CONFINEMENT_TOP_RGHT);

    /**
     * The colour mapping we use to colour each rectangle on the map 
     */
    private static final UniformIntToHexStringColourMap colourMap = new UniformIntToHexStringColourMap(0,
                                                                                    256,
                                                                                    "#00ff00",
                                                                                    "#40ff00",
                                                                                    "#80ff00",
                                                                                    "#c0ff00",
                                                                                    "#ffc000",
                                                                                    "#ff8000",
                                                                                    "#ff4000",
                                                                                    "#ff0000");

    /**
     * Fatal error codes, used to distinguish the ways in which the program 
     * could not complete the task
     */
    private enum ErrorCode{
        INPUT_FILE_MISSING,
        INPUT_FILE_MALFORMED,
        BAD_PROGRAM_USAGE,
        CANNOT_READ_INPUT_FILE,
        CANNOT_CREATE_OUTPUT_FILE,
    }


    /**
     * Parses the given input file and populates a corresponding 2D array.
     * the array has dimensions: SENSOR_COUNT_Y by SENSOR_COUNT_X.
     * @param inputFile the file containing SENSOR_COUNT_Y lines of SENSOR_COUNT_X comma separated values
     * @return 2D array of integer sensor readings between 0 and 255
     * @throws IOException if the input file could not be read
     * @throws IllegalArgumentException if the input file is incorrectly formatted
     */
    private static int[][] parseSensorReadings(File inputFile) throws IOException,IllegalArgumentException{
                
        // set up file reader
        var fileReader = new BufferedReader(new FileReader(inputFile));

        // read the input file line by line into a 2d array [y,x]
        var sensorReadings  = new int[SENSORS_COUNT_Y][SENSORS_COUNT_X];
        var y = 0;
        String currLine = fileReader.readLine();

        // go through each line
        do{
            // only an empty file will fire this condition
            if(currLine == null)
                throw new IllegalArgumentException("File is empty");
                
            
            // split each line on the comma char and expect SENSOR_COUNT_X strings to be present
            String[] stringReadings = currLine.split(",");

            // make sure there aren't too many readings in the line and that
            // there aren't too many lines in the file
            if(stringReadings.length != SENSORS_COUNT_X 
                || y + 1 > SENSORS_COUNT_Y){
                throw new IllegalArgumentException("The shape of the input file is invalid, should be: " 
                                                    + SENSORS_COUNT_Y 
                                                    + " lines of " 
                                                    + SENSORS_COUNT_X + " comma separated numbers between 0-255 inclusive. \n" 
                                                    + " each line should end with a newline.");
            }

            // extract integer values from the line we just read
            for (var x = 0; x < SENSORS_COUNT_X; x++) {

                // remove whitespace at start and end
                String cleanedReadingString = stringReadings[x].trim();

                // parse the integer
                var integer = 0;
                try{
                    integer = Integer.parseInt(cleanedReadingString);
                } catch(NumberFormatException e){
                    throw new IllegalArgumentException("The value: \""+ cleanedReadingString + "\" is not a valid number.");
                }

                if(integer > 255 || integer < 0)
                    throw new IllegalArgumentException("Value is out of range (0,255)");
                    
                sensorReadings[y][x] = integer;
            }
            
            // proceed to next line
            y++;
            currLine = fileReader.readLine();
        } while(currLine != null);
        
        // check we don't have too few lines of readings
        // y will be equal to the number of lines read in the end
        if(y < SENSORS_COUNT_Y){
            throw new IllegalArgumentException("There are too few lines, expected: "+ SENSORS_COUNT_Y);
        }

        // release resources
        fileReader.close();

        return sensorReadings;
    }


    /**
     * constructs a geo-json rectangle as a polygon, given the top left corner and its width and height.
     * The easternOffset is the rectangle's width and southernOffset is its height. 
     * The rectangle is "extended" in the eastern and southern directions starting from the top left corner.
     * @param topLeft
     * @param width
     * @param height
     * @return Polygon geo-json geometry representing the rectangle
     */
    private static Polygon constructRectangle(Point topLeft, double width, double height){

        // we build each point of the rectangle, keeping in mind that longitude grows east-ward and 
        // latitude grows north-ward
        Point topRight = Point.fromLngLat(topLeft.longitude() + width,
                topLeft.latitude());

        Point botRight = Point.fromLngLat(topLeft.longitude() + width,
                topLeft.latitude() - height);                

        Point botLeft = Point.fromLngLat(topLeft.longitude() ,
                topLeft.latitude() - height);
                   
        // polygons are defined as lists of lists, to support complex shapes
        // the first and last points are the same
        var polygonPoints = new LinkedList<List<Point>>(Arrays.asList(
            new LinkedList<Point>(Arrays.asList(
                topLeft,topRight,botRight,botLeft,topLeft
            ))
        ));

        // finally create the polygon geometry 
        return Polygon.fromLngLats(polygonPoints);
    }


    /**
     * Converts the given sensor readings as a 2D geo-json grid of rectangles coloured according to sensor emissions.
     * @param sensorReadings a 2D integer grid of sensor readings between 0 and 255 of dimensions: 
     * SENSOR_COUNT_Y by SENSOR_COUNT_X 
     * @return A list of geo-json features representing a rectangular grid with each rectnagle 
     * coloured according to its emission value.
     */
    private static FeatureCollection generateFeaturesGrid(int[][] sensorReadings){
        assert sensorReadings.length == SENSORS_COUNT_Y 
                && sensorReadings[0].length == SENSORS_COUNT_X;

         // generate the geo-json data for each rectangle

         // find the dimensions in degrees of each rectangle
         Point rectangleDimensions = Point.fromLngLat(CONFINEMENT_WIDTH / SENSORS_COUNT_X, CONFINEMENT_HEIGHT / SENSORS_COUNT_Y);
        
         // we store the polygons in an arraylist to improve readability
         // should more performance be required, a flattened 1d Array would be appropriate here
         var polygons = new ArrayList<Feature>(SENSORS_COUNT_X * SENSORS_COUNT_Y);

         for (var y = 0; y < SENSORS_COUNT_Y; y++) {

             for (var x = 0; x < SENSORS_COUNT_X; x++) {

                // Calculate the rectangle's top left corner coordinate
                var xOffset = rectangleDimensions.longitude() * x;
                var yOffset = rectangleDimensions.latitude() * -y; // more negative latitude = more southern point

                Point rectTopLeft = Point.fromLngLat(CONFINEMENT_TOP_LFT.longitude() + xOffset,
                                                    CONFINEMENT_TOP_LFT.latitude() + yOffset);

                // Construct the polygon geometry and its corresponding feature from the 
                // x and y offsets
                Polygon polygonGeometry = constructRectangle(rectTopLeft, 
                                rectangleDimensions.longitude(),
                                rectangleDimensions.latitude());

                Feature polygonFeature = Feature.fromGeometry(polygonGeometry);

                // set the properties of the rectangle
                String rgbString = colourMap.getColour(sensorReadings[y][x]);
                polygonFeature.addStringProperty("rgb-string",rgbString);
                polygonFeature.addStringProperty("fill", rgbString);
                polygonFeature.addNumberProperty("fill-opacity", 0.75f);
        

                // Store the polygon
                polygons.add(polygonFeature);
             }
         }

         // we then create a feature collection of the polygons and return it
         return FeatureCollection.fromFeatures(polygons);
    }


    /**
     * Calculates the distance between point a and b in degrees
     * @param a point a
     * @param b point b
     * @return the distance between the points
     */
    private static double findDistanceBetween(Point a, Point b){
        // euclidian distance calculation assuming the points lie on a plane
        var diffLongitude = a.longitude() - b.longitude();
        double diffSqLongitude = Math.pow(diffLongitude,2d);

        var diffLatitude = a.latitude() - b.latitude();
        double diffSqLattitude = Math.pow(diffLatitude,2d);
        
        return Math.sqrt( diffSqLongitude + diffSqLattitude);
    }


    /**
     * Terminates the program gracefully with the appropriate err output and a detailed error message if provided
     * @param errorCode the error code
     * @param detailMsg detail message, if empty string is passed it will not be displayed 
     */
    private static void terminateProgramWithError(ErrorCode errorCode, String detailMsg){
        switch (errorCode){
            case BAD_PROGRAM_USAGE:
                System.out.println("Usage: java "+App.class.getSimpleName()+" predictions.txt");
                System.out.println("where  predictions.txt is a file containing " 
                                    + SENSORS_COUNT_Y 
                                    + " lines of text with "
                                    + SENSORS_COUNT_X
                                    + " comma separated values");
                break;
            case INPUT_FILE_MISSING:
                System.err.println("Error: Input file cannot be found.");
                break;
            case INPUT_FILE_MALFORMED:
                System.err.println("Error: Input file is malformed.");
                break;
            case CANNOT_READ_INPUT_FILE:
                System.err.println("Error: Could not read input file.");
                break;
            case CANNOT_CREATE_OUTPUT_FILE:
                System.err.println("Error: Could not create output file: " + OUTPUT_FILE.getName());
                break;
        }

        if(detailMsg != ""){
            System.err.println("Error Message: " + detailMsg);
        }

        System.exit(-1);
    }

}
