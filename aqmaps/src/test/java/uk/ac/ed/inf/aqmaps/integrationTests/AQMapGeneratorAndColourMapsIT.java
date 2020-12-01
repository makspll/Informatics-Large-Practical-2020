package uk.ac.ed.inf.aqmaps.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.client.AQSensor;
import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.client.data.W3WSquareData;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;
import uk.ac.ed.inf.aqmaps.testUtilities.TestUtilities;
import uk.ac.ed.inf.aqmaps.visualisation.AQMapGenerator;
import uk.ac.ed.inf.aqmaps.visualisation.AttributeMap;
import uk.ac.ed.inf.aqmaps.visualisation.MarkerSymbol;
import uk.ac.ed.inf.aqmaps.visualisation.UniformAttributeMap;

import static java.util.Map.entry;    

public class AQMapGeneratorAndColourMapsIT {

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
    AttributeMap<Float, MarkerSymbol> symbolMap = new UniformAttributeMap<MarkerSymbol>(0f, 265f, symbols);

    String testLowBatteryColour = "#000000";
    MarkerSymbol testLowBatterySymbol = MarkerSymbol.CROSS;

    String testNotVisitedColour = "#aaaaaa";
    MarkerSymbol testNotVisitedSymbol = MarkerSymbol.NO_SYMBOL;

    Sensor sensor1 = new AQSensor(new SensorData("1.1.1", 1f, 1f), new W3WAddressData("country", new W3WSquareData(),
            "nearestPlace", Point.fromLngLat(1, 1), "1.1.1", "language", "map"));
    Sensor sensor2 = new AQSensor(new SensorData("2.2.2", 1f, 50f), new W3WAddressData("country", new W3WSquareData(),
            "nearestPlace", Point.fromLngLat(2, 2), "2.2.2", "language", "map"));
    Sensor sensor3 = new AQSensor(new SensorData("3.3.3", 100f, 100f), new W3WAddressData("country",
            new W3WSquareData(), "nearestPlace", Point.fromLngLat(3, 3), "3.3.3", "language", "map"));
    Sensor sensor4 = new AQSensor(new SensorData("4.4.4", 200f, 200f), new W3WAddressData("country",
            new W3WSquareData(), "nearestPlace", Point.fromLngLat(4, 4), "4.4.4", "language", "map"));
    Sensor sensor5 = new AQSensor(new SensorData("5.5.5", 255f, 255f), new W3WAddressData("country",
            new W3WSquareData(), "nearestPlace", Point.fromLngLat(5, 5), "5.5.5", "language", "map"));

    @Test
    public void test() throws IOException, InterruptedException {
        var sensors = new ArrayList<Sensor>(Arrays.asList(sensor1,sensor2,sensor3,sensor4,sensor5));

        sensor1.setHaveBeenRead(true);
        sensor3.setHaveBeenRead(true);
        sensor5.setHaveBeenRead(true);

        var visitedSet = new HashSet<Sensor>(Arrays.asList(
            sensor1,
            sensor3,
            sensor5
        ));
        
        var lowBatterySet = new HashSet<Sensor>(Arrays.asList(
            sensor1
        ));

        AQMapGenerator testUnit = new AQMapGenerator(colourMap,
            symbolMap,
            testLowBatteryColour,
            testLowBatterySymbol,
            testNotVisitedColour,
            testNotVisitedSymbol);


        var output = testUnit.plotMap(new LinkedList<PathSegment>(), sensors);

        HashMap<String,Sensor> sensorLocationToSensorMap = new HashMap<String,Sensor>();
        sensorLocationToSensorMap.put("1.1.1", sensor1);
        sensorLocationToSensorMap.put("2.2.2",sensor2);
        sensorLocationToSensorMap.put("3.3.3",sensor3);
        sensorLocationToSensorMap.put("4.4.4",sensor4);
        sensorLocationToSensorMap.put("5.5.5",sensor5);

        for (var point : output.features()) {
            if(point.geometry() instanceof LineString){
                continue;
            }
            
            Sensor correspondingSensor = sensorLocationToSensorMap.get(point.getStringProperty("location"));
            
            boolean visited = visitedSet.contains(correspondingSensor);
            boolean lowBattery = lowBatterySet.contains(correspondingSensor);

            String correctColor = "";

            if(!visited){
                correctColor = testNotVisitedColour;
            } else if (lowBattery){
                correctColor = testLowBatteryColour;
            } else {
                correctColor = colourMap.getFor(correspondingSensor.getReading());
            }

            MarkerSymbol correctSymbol;
            if(!visited){
                correctSymbol = testNotVisitedSymbol == MarkerSymbol.NO_SYMBOL ? 
                    null : testNotVisitedSymbol;
            } else if (lowBattery){
                correctSymbol = testLowBatterySymbol;
            } else {
                correctSymbol = symbolMap.getFor(correspondingSensor.getReading());
            }

            assertEquals(correctColor,point.getStringProperty("rgb-string"));
            assertEquals(correctColor,point.getStringProperty("marker-color"));
            var generatedSymbol = point.getStringProperty("marker-symbol");
            assertEquals(correctSymbol == null? null:correctSymbol.toString(),generatedSymbol == null?null : generatedSymbol.toString());


        }


    }

}
