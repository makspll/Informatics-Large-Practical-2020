package uk.ac.ed.inf.aqmaps.visualisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * A visualiser which produces a geojson representation of the collection
 */
public class AQMapGenerator implements SensorCollectionVisualiser {

    /**
     * Create an AQMapGenerator with the default low battery and non-visited symbol/colour values
     * @param colourMap the map from sensor readings to colours used in the visualisation
     * @param symbolMap the map from sensor readings to symbols usedi in the visualisation
     */
    public AQMapGenerator(AttributeMap<Float, String> colourMap, AttributeMap<Float, MarkerSymbol> symbolMap) {
        this.colourMap = colourMap;
        this.symbolMap = symbolMap;
    }

    /**
     * Create an AQMapGenerator specyfing all its parameters
     * @param colourMap the map from sensor readings to colours used in the visualisation
     * @param symbolMap the map from sensor readings to symbols usedi in the visualisation
     * @param lowBatteryColour the color used for sensors with a low battery value
     * @param lowBatterySymbol the symbol used for sensors with a low battery value
     * @param notVisitedColour the color used for sensors which were not visited
     * @param notVisitedSymbol the symbol used for sensors which were not visited
     */
    public AQMapGenerator(AttributeMap<Float, String> colourMap, AttributeMap<Float, MarkerSymbol> symbolMap,
            String lowBatteryColour, MarkerSymbol lowBatterySymbol, String notVisitedColour,
            MarkerSymbol notVisitedSymbol) {
        this.colourMap = colourMap;
        this.symbolMap = symbolMap;
        this.lowBatteryMarkerColour = lowBatteryColour;
        this.lowBatteryMarkerSymbol = lowBatterySymbol;
        this.notVisitedMarkerColour = notVisitedColour;
        this.notVisitedMarkerSymbol = notVisitedSymbol;
    }

    @Override
    public FeatureCollection plotMap(Deque<PathSegment> flightPath, Collection<Sensor> sensorsToBeVisited) 
        throws IOException, InterruptedException  {
        
        var mapFeatures = new ArrayList<Feature>();
        var flightPathPoints = new ArrayList<Point>();
        int segmentCount = flightPath.size();


        //// first process the flight path

        // go through each segment, and add collect each start point
        // also collect the last segment's end point 
        for (PathSegment segment : flightPath) {

            // add each segment's start point
            var startPoint = segment.getStartPoint();

            flightPathPoints.add(
                GeometryUtilities.JTSCoordinateToMapboxPoint(
                    startPoint));

            // segment count counts how many segments are left
            segmentCount -= 1;


            // collect the last segment's end point
            var endPoint = segment.getEndPoint();

            if(segmentCount == 0)
                flightPathPoints.add(
                    GeometryUtilities.JTSCoordinateToMapboxPoint(
                        endPoint));
        }

        // compile collected flight path points into 
        // a line string feature
        mapFeatures.add(
            Feature.fromGeometry(
                LineString.fromLngLats(flightPathPoints)));


        //// next process the sensors
        // create and collect a marker feature for each sensor (visited or not)
        for (Sensor sensor : sensorsToBeVisited) {
            mapFeatures.add(createMarker(sensor));
        }

        // collect all features under a feature collection
        return FeatureCollection.fromFeatures(mapFeatures);
    }


    private Feature createMarker(Sensor sensor) throws IOException, InterruptedException {

        // make coordinate precise to avoid floating point error
        var position = sensor.getCoordinates();

        // convert to feature
        Feature feature = Feature.fromGeometry(
                            GeometryUtilities.JTSCoordinateToMapboxPoint(
                                position));


        // work out the attributes depending on the sensor's values
        String colour = null;
        MarkerSymbol symbol = null;

        if (!sensor.hasBeenRead()) {    
            colour = notVisitedMarkerColour;
            symbol = notVisitedMarkerSymbol;
        } else if (sensor.getBatteryLevel() < LOW_BATTERY_THRESHOLD) {
            colour = lowBatteryMarkerColour;
            symbol = lowBatteryMarkerSymbol;
        } else {
            var sensorReading = sensor.getReading();

            // we should get a concrete value if the sensor has both a normal battery level and has been read
            assert sensorReading != Float.NaN;

            // retrieve the mapping
            colour = colourMap.getFor(sensorReading);
            symbol = symbolMap.getFor(sensorReading);
        }

        // add the attributes 
        feature.addStringProperty("location", sensor.getW3WLocation());
        feature.addStringProperty("rgb-string", colour);
        feature.addStringProperty("marker-color", colour);

        // no symbol means we do not add a symbol property
        if(symbol != MarkerSymbol.NO_SYMBOL)
            feature.addStringProperty("marker-symbol", symbol.toString());

        return feature;
    }

    private AttributeMap<Float, String> colourMap;

    private AttributeMap<Float, MarkerSymbol> symbolMap;

    private String lowBatteryMarkerColour = "#000000";
    private MarkerSymbol lowBatteryMarkerSymbol = MarkerSymbol.CROSS;
    private String notVisitedMarkerColour = "aaaaaa";
    private MarkerSymbol notVisitedMarkerSymbol = MarkerSymbol.NO_SYMBOL;

    private static final double LOW_BATTERY_THRESHOLD = 10f;



}
