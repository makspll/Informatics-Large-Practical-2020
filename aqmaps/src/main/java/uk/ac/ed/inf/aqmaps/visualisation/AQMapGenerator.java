package uk.ac.ed.inf.aqmaps.visualisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

public class AQMapGenerator implements SensorCollectionVisualiser {

    public AQMapGenerator(AttributeMap<Float, String> colourMap, AttributeMap<Float, MarkerSymbol> symbolMap) {
        this.colourMap = colourMap;
        this.symbolMap = symbolMap;
    }

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
    public FeatureCollection plotMap(Queue<PathSegment> flightPath, Collection<Sensor> sensorsToBeVisited) 
        throws IOException, InterruptedException  {
        
        var mapFeatures = new ArrayList<Feature>();
        var flightPathPoints = new ArrayList<Point>();
        int segmentCount = flightPath.size();


        //// first process the flight path

        // go through each segment, and add collect each start point
        // also collect the last segment's end point 
        for (PathSegment segment : flightPath) {

            // add each segment's start point
            flightPathPoints.add(
                GeometryUtilities.JTSCoordinateToMapboxPoint(
                    segment.getStartPoint()));

            // segment count counts how many segments are left
            segmentCount -= 1;


            // collect the last segment's end point
            if(segmentCount == 0)
                flightPathPoints.add(
                    GeometryUtilities.JTSCoordinateToMapboxPoint(
                        segment.getEndPoint()));
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

        Feature feature = Feature.fromGeometry(
                            GeometryUtilities.JTSCoordinateToMapboxPoint(
                                sensor.getCoordinates()));

        String colour = null;
        MarkerSymbol symbol = null;

        if (!sensor.hasBeenRead()) {
            colour = notVisitedMarkerColour;
            symbol = notVisitedMarkerSymbol;
        } else if (sensor.getBatteryLevel() < 10f) {
            colour = lowBatteryMarkerColour;
            symbol = lowBatteryMarkerSymbol;
        } else {

            colour = colourMap.getFor(sensor.getReading());
            symbol = symbolMap.getFor(sensor.getReading());
        }

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





}
