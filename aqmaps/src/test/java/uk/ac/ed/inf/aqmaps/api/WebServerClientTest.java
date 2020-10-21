package uk.ac.ed.inf.aqmaps.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.client.Sensor;
import uk.ac.ed.inf.aqmaps.client.W3WAddress;
import uk.ac.ed.inf.aqmaps.client.W3WSquare;
import uk.ac.ed.inf.aqmaps.client.WebServerClient;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebServerClientTest {

    @Test
    public void testFetchSensorsForDateCorrectData() throws IOException, InterruptedException {

        // check a couple of the dates have the right data
        Map<String,Sensor> correctOutputs = Map.ofEntries(
            entry("hooked.shine.third",new Sensor("hooked.shine.third",5.542588f,Float.NaN)),
            entry("sports.topic.clocks",new Sensor("sports.topic.clocks",42.300785f,194.9f)),
            entry("noted.friday.jams",new Sensor("noted.friday.jams",9.161681f,Float.NaN))

        );

        List<Sensor> sensors = WebServerClient.fetchSensorsForDate( LocalDate.of(2020,1,1));

        for (Sensor sensor : sensors) {
            if(correctOutputs.containsKey(sensor.getLocation())){
                Sensor correctOutput = correctOutputs.get(sensor.getLocation());
                assertEquals(correctOutput,sensor);

            }
        }

    }

    @Test
    public void testFetchW3WAddress() throws IOException, InterruptedException {

        // check a couple of the addresses have correct data
        Map<String,W3WAddress> correctOutputs = Map.ofEntries(
            entry("rated.fired.crowds", new W3WAddress(  "GB", new W3WSquare(
                Point.fromLngLat(-3.186416, 55.944642), 
                Point.fromLngLat(-3.186368, 55.944669)), 
                "Edinburgh", 
                Point.fromLngLat(-3.186392, 55.944656),
                "rated.fired.crowds", 
                "en",
                "https://w3w.co/rated.fired.crowds"))
        );

        for (String address  : correctOutputs.keySet()) {
            String[] words = address.split("\\.");
            W3WAddress output = WebServerClient.fetchW3WAddress(words[0], words[1], words[2]);

            assertEquals(correctOutputs.get(address), output);
        }
    }


    @Test
    public void testFetchBuildings() throws IOException, InterruptedException {
        String correctJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"name\":\"Appleton Tower\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1871804594993587,55.94448750356385],[-3.187042325735092,55.944193856370475],[-3.1869766116142273,55.94420361961219],[-3.186897486448288,55.94404815847112],[-3.186330199241638,55.94413227278919],[-3.186405301094055,55.944296745793935],[-3.1863033771514893,55.944311015108454],[-3.1863395869731903,55.94437109637507],[-3.186409324407577,55.94436208419099],[-3.186425417661667,55.944397381899876],[-3.186226934194565,55.94442366740687],[-3.1863945722579956,55.94457011490493],[-3.1864307820796967,55.94458588614092],[-3.186488449573517,55.94459189422912],[-3.1871804594993587,55.94448750356385]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"David Hume Tower\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.186697661876678,55.943360216655655],[-3.186556175351143,55.94306656091478],[-3.186345621943474,55.943098480124895],[-3.1864864379167557,55.94339251114087],[-3.186697661876678,55.943360216655655]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"Main Library\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.189708441495895,55.94280820210462],[-3.1894750893115997,55.94233128523903],[-3.1882721185684204,55.942514542178216],[-3.1885054707527156,55.94299295887116],[-3.189708441495895,55.94280820210462]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"Informatics Forum\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.188078999519348,55.94541799748307],[-3.187633752822876,55.94458288209647],[-3.1869176030158997,55.94470154167527],[-3.187046349048614,55.94494186375901],[-3.186909556388855,55.944964393877896],[-3.18678081035614,55.94481569485101],[-3.1865662336349487,55.94487727734554],[-3.187113404273987,55.94555317633777],[-3.1872957944869995,55.945505112799054],[-3.187832236289978,55.945460053177314],[-3.188078999519348,55.94541799748307]]]}}]}";
        FeatureCollection correctOutput = FeatureCollection.fromJson(correctJson);
        
        FeatureCollection output = WebServerClient.fetchBuildings();

        assertEquals(correctOutput, output);
    }

}
