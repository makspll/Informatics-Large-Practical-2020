package uk.ac.ed.inf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;    

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Main{


    public static void main(String[] args) {

        // if program is launched incorrectly, we have to terminate the program
        if(args.length != 1){
            terminateProgramWithError(ErrorCode.BAD_USAGE);
        } else {

            // the first argument is the filename of the input file
            File inputFile = new File(args[0]);
            BufferedReader fileReader = null;
                
            // set up file reader
            try {
                fileReader = new BufferedReader(new FileReader(inputFile));
            } catch (FileNotFoundException e) {

                // print error message
                terminateProgramWithError(ErrorCode.NO_INPUT_FILE);
            }

            // finally read the input file line by line and extract values into a 
            // a 2d array of integers of size corresponding to the number of sensors expected
            int[][] sensorReadings  = new int[SENSORS_COUNT_Y][SENSORS_COUNT_X];

            try{
                int y = 0;

                String currLine = fileReader.readLine();
                do{
                    // need at least one line
                    if(currLine == null)
                        terminateProgramWithError(ErrorCode.BAD_INPUT_FILE);

                    // split the line on the comma and expect 10 strings to be present
                    String[] stringReadings = currLine.split(",");

                    // make sure the file being read is of the right size, 
                    // and we don't cause an array out of bounds exception
                    if(stringReadings.length != SENSORS_COUNT_X 
                        || y + 1 > SENSORS_COUNT_Y){
                        terminateProgramWithError(ErrorCode.BAD_INPUT_FILE);
                    }

                    // extract integer values and populate the array of sensor readings
                    for (int x = 0; x < SENSORS_COUNT_X; x++) {

                        // remove whitespace at start and end
                        String cleanedReadingString = stringReadings[x].trim();

                        // parse the integer
                        sensorReadings[y][x] = Integer.parseInt(cleanedReadingString);
                    }
                    
                    // proceed to next line
                    y++;
                    currLine = fileReader.readLine();
                } while(currLine != null);
                

                fileReader.close();

            } catch(IOException e){
                terminateProgramWithError(ErrorCode.COULD_NOT_READ_INPUT_FILE);
            }



            // find the dimensions of each scanner's rectangle
            Point rectDimensions = Point.fromLngLat(CONFINEMENT_WIDTH / SENSORS_COUNT_X, CONFINEMENT_HEIGHT / SENSORS_COUNT_Y);
            
            // generate the geo-json data for each rectangle
            // each rectangle is represented with a geo-json polygon
            // at the same time we also flatten the 2d data into a uni-dimensional array of features
            Feature[] polygons = new Feature[SENSORS_COUNT_Y * SENSORS_COUNT_X];

            for (int y = 0; y < SENSORS_COUNT_Y; y++) {

                for (int x = 0; x < SENSORS_COUNT_X; x++) {

                    // x and y represent the polygon number, 
                    // we calculate the coordinates of each of its 4 corners
                    // the y-axis is the latitudinal axis (east to west)
                    // for both the asis we can calculate the top left corner of the polygon by first finding 
                    // an offset from the top left side of the containment area as follows:
                    double xOffset = rectDimensions.longitude() * x;
                    double yOffset = rectDimensions.latitude() * -y; // south is in the negative direction

                    Point rectTopLeft = Point.fromLngLat(CONFINEMENT_TOP_LFT.longitude() + xOffset,
                                                        CONFINEMENT_TOP_LFT.latitude() + yOffset);

                    // we then simply use the top left confinement coordinate and add the offsets in different linear combinations
                    // to produce each of the 4 corners
                    Point rectTopRight = Point.fromLngLat(rectTopLeft.longitude() + rectDimensions.longitude(),
                                                        rectTopLeft.latitude());
                
                    Point rectBotRight = Point.fromLngLat(rectTopLeft.longitude() + rectDimensions.longitude(),
                                                        rectTopLeft.latitude() - rectDimensions.latitude());                

                    Point rectBotLeft = Point.fromLngLat(rectTopLeft.longitude() ,
                                                        rectTopLeft.latitude() - rectDimensions.latitude());
                                                        

                    
                    
                    // polygons are defined as lists of lists, to support complex shapes, we only need a rectangle
                    List<List<Point>> polygonPoints = new LinkedList<List<Point>>(Arrays.asList(
                        new LinkedList<Point>(Arrays.asList(
                            rectTopLeft,rectTopRight,rectBotRight,rectBotLeft,rectTopLeft
                        ))
                    ));

                    // Create polygon from points as a feature from the calculated points
                    Feature polygon = Feature.fromGeometry(Polygon.fromLngLats(polygonPoints));

                    // find and set the colour of the rectangle
                    String rgbString = findColourRGBString(sensorReadings[y][x]);

                    polygon.addStringProperty("rgb-string",rgbString);
                    polygon.addStringProperty("fill", rgbString);
                    
                    // set fill opacity
                    polygon.addNumberProperty("fill-opacity", 0.75f);

                    // we store the polygons as Feature's at appropriate place in flattened array.
                    polygons[x + SENSORS_COUNT_X  * y] = polygon;
                }
            }

            // we then create a feature collection and dump its json value to
            // the output file
            String output = FeatureCollection.fromFeatures(polygons).toJson();

            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(OUTPUT_FILE);

                fileWriter.write(output);

                fileWriter.close();

            } catch (Exception e) {
                terminateProgramWithError(ErrorCode.COULD_NOT_CREATE_OUTPUT_FILE);
            }
                

        }




    }



    /** the output file to which we write heatmap geo-json data */
    private static final File OUTPUT_FILE = new File("heatmap.geojson");

    /** the amount of rows of sensors/sensor readin
     * gs to expect in each file */
    private static final int SENSORS_COUNT_Y = 10;
    /** the amount of sensors/sensor readings to expect in each row of the file */
    private static final int SENSORS_COUNT_X = 10;


    /** Drone confinement area upper left corner coordinate when treated as a square facing north */
    private static final Point CONFINEMENT_TOP_LFT = Point.fromLngLat(-3.192473d, 55.946233d);
    /** Drone confinement area upper right corner coordinate when treated as a square facing north */
    private static final Point CONFINEMENT_TOP_RGHT = Point.fromLngLat(-3.184319d, 55.946233d);
    /** Drone confinement area lower left corner coordinate when treated as a square facing north */
    private static final Point CONFINEMENT_BOT_LFT = Point.fromLngLat(-3.192473d, 55.942617d);


    /** Drone confinement area's height when treated as a square facing north */
    private static final double CONFINEMENT_HEIGHT = findDistanceBetween(CONFINEMENT_TOP_LFT, CONFINEMENT_BOT_LFT); //Math.abs(-3.192473d + 3.184319d);////Math.abs(CONFINEMENT_BOT_LFT.latitude() - CONFINEMENT_TOP_LFT.latitude());
    /** Drone confinement area's width when treated as a square facing north */
    private static final double CONFINEMENT_WIDTH = findDistanceBetween(CONFINEMENT_TOP_LFT, CONFINEMENT_TOP_RGHT);//Math.abs(55.946233d - 55.942617d);//findDistanceBetween(CONFINEMENT_TOP_LFT, CONFINEMENT_TOP_RIGHT);//Math.abs(CONFINEMENT_TOP_LFT.longitude() - CONFINEMENT_TOP_RIGHT.longitude());


    /** Used to communicate appropriate user error messages  */
    private enum ErrorCode{
        NO_INPUT_FILE,
        BAD_INPUT_FILE,
        BAD_USAGE,
        COULD_NOT_READ_INPUT_FILE,
        COULD_NOT_CREATE_OUTPUT_FILE,
    }


    private static final List<String> rGBStringValues = new LinkedList(Arrays.asList(
        "#00ff00","#40ff00","#80ff00","#c0ff00","#ffc000","#ff8000","#ff4000","#ff0000"
    ));

    /** find the rgb string corresponding to the given integer (between 0-255) */
    private static String findColourRGBString(int value){

        // we simply have to find which colour bucket the value falls into,
        // each bucket contains 32 values starting from 0 and ending on 255
        int bucketNumber = (int)Math.floor(value / 32);

        return rGBStringValues.get(bucketNumber);
    
    }   

    /** calculate pythagorean distance between two points on earth, assumes planar geometry */
    private static double findDistanceBetween(Point a, Point b){
        // euclidian distance calculation assuming the points lie on a plane
        double diffLongitude = a.longitude() - b.longitude();
        double diffSqLongitude = Math.pow(diffLongitude,2d);

        double diffLatitude = a.latitude() - b.latitude();
        double diffSqLattitude = Math.pow(diffLatitude,2d);
        
        return Math.sqrt( diffSqLongitude + diffSqLattitude);
    }

    /** displays appropriate error message and terminates the program */
    private static void terminateProgramWithError(ErrorCode e){
        switch (e){
            case BAD_USAGE:
                System.out.println("Usage: java "+Main.class.getSimpleName()+" predictions.txt");
                System.out.println("where  predictions.txt is a file containing 10 lines of text with 10 comma separated values");
                break;
            case NO_INPUT_FILE:
                System.err.println("Error: input file cannot be found.");
                break;
            case BAD_INPUT_FILE:
                System.err.println("Error: input file is malformed.");
                break;
            case COULD_NOT_READ_INPUT_FILE:
                System.err.println("Error: Could not read input file.");
                break;
            case COULD_NOT_CREATE_OUTPUT_FILE:
                System.err.println("Error: Could not create output file: " + OUTPUT_FILE.getName());
                break;
        }
        System.exit(-1);
    }



        
}
