package uk.ac.ed.inf.aqmaps.integrationTests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.client.data.W3WSquareData;
import uk.ac.ed.inf.aqmaps.client.AQWebServerClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes","unchecked"})
public class WebServerClientIT {

    private static HttpClient mockClient = mock(HttpClient.class);
    private static URI apiURI = URI.create("http://localhost:9898");
    private static AQWebServerClient testClient = new AQWebServerClient(mockClient,apiURI);

    private static SensorData testSensorData1 = new SensorData("a.b.c", 1f, Float.NaN);
    private static SensorData testSensorData2 = new SensorData("a.b.c", 1f, Float.NaN);
    private static SensorData testSensorData3 = new SensorData("a.b.c", 1f, 255f);

    private static String testSensorDataJson;
    private static List<SensorData> testSensorDataList = new ArrayList<SensorData>(Arrays.asList(
        testSensorData1,testSensorData2,testSensorData3
    ));

    private static W3WAddressData  testW3WAddressData = new W3WAddressData(
            "GB",
            new W3WSquareData(Point.fromLngLat(1,1), Point.fromLngLat(1, 1)), 
            "",
            Point.fromLngLat(1, 1), 
            "a.b.c", 
            "", 
            "");
    private static String testW3WAddressDataJson = "{"+
        "\"country\": \""+testW3WAddressData.getCountry()+"\"," +
        "\"square\": {"
          + "\"southwest\": {" +
            "\"lng\":"+ testW3WAddressData.getSquare().getSouthwest().longitude()+", "+
            "\"lat\":"+ testW3WAddressData.getSquare().getSouthwest().latitude()+
          "}," +
          "\"northeast\": {" +
            "\"lng\":"+ testW3WAddressData.getSquare().getNorthwest().longitude()+", "+
            "\"lat\":"+ testW3WAddressData.getSquare().getNorthwest().latitude()+
          "}" +
        "}," +
        "\"nearestPlace\": \""+ testW3WAddressData.getNearestPlace()+"\" ,"+
        "\"coordinates\": {" +
          "\"lng\":"+ testW3WAddressData.getCoordinates().longitude()+", "+
          "\"lat\":"+ testW3WAddressData.getCoordinates().latitude()+
        "}," +
        "\"words\": \""+testW3WAddressData.getWords()+"\","+
        "\"language\": \""+testW3WAddressData.getLanguage()+"\","+
        "\"map\": \""+testW3WAddressData.getMap()+"\""+
    "}";

    private void setMockResponseString(String output,int statusCode) throws IOException, InterruptedException {
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(output);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockClient.send(any(HttpRequest.class),any(BodyHandler.class))).thenReturn(mockResponse);
    }

    @BeforeAll
    public static void setup(){
        testSensorDataJson = "[" +
        String.format("{\"location\":%s,\"battery\":%s,\"reading\":%s}",
            testSensorData1.getW3WLocation(),
            testSensorData1.getBattery(),
            "\"null\"") + "," +
        String.format("{\"location\":%s,\"battery\":%s,\"reading\":%s}",
            testSensorData2.getW3WLocation(),
            testSensorData2.getBattery(),
            "\"NaN\"") + "," +
        String.format("{\"location\":%s,\"battery\":%s,\"reading\":%s}",
            testSensorData3.getW3WLocation(),
            testSensorData3.getBattery(),
            "\"255.0\"") + "]";
    }
    @BeforeEach
    public void reset(){
        mockClient = mock(HttpClient.class);
        testClient = new AQWebServerClient(mockClient,apiURI);
    }

    @Test
    public void testFetchSensorsForDateCorrectData() throws IOException, InterruptedException {

        // setup test data
        setMockResponseString(testSensorDataJson,200);
        // test output
        List<SensorData> sensors = testClient.fetchSensorsForDate( LocalDate.of(1,1,1));

        assertEquals(testSensorDataList, sensors);
        

    }

    @Test
    public void testFetchW3WAddress() throws IOException, InterruptedException {
      
        // setup test data
        setMockResponseString(testW3WAddressDataJson,200);
        // test output
        W3WAddressData output = testClient.fetchW3WAddress("w.w.w");
        assertEquals(testW3WAddressData, output);

    }


    @Test
    public void testFetchBuildings() throws IOException, InterruptedException {

        // setup test data
        String buildingsJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"name\":\"Appleton Tower\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1871804594993587,55.94448750356385],[-3.187042325735092,55.944193856370475],[-3.1869766116142273,55.94420361961219],[-3.186897486448288,55.94404815847112],[-3.186330199241638,55.94413227278919],[-3.186405301094055,55.944296745793935],[-3.1863033771514893,55.944311015108454],[-3.1863395869731903,55.94437109637507],[-3.186409324407577,55.94436208419099],[-3.186425417661667,55.944397381899876],[-3.186226934194565,55.94442366740687],[-3.1863945722579956,55.94457011490493],[-3.1864307820796967,55.94458588614092],[-3.186488449573517,55.94459189422912],[-3.1871804594993587,55.94448750356385]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"David Hume Tower\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.186697661876678,55.943360216655655],[-3.186556175351143,55.94306656091478],[-3.186345621943474,55.943098480124895],[-3.1864864379167557,55.94339251114087],[-3.186697661876678,55.943360216655655]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"Main Library\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.189708441495895,55.94280820210462],[-3.1894750893115997,55.94233128523903],[-3.1882721185684204,55.942514542178216],[-3.1885054707527156,55.94299295887116],[-3.189708441495895,55.94280820210462]]]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"Informatics Forum\",\"fill\":\"#ff0000\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.188078999519348,55.94541799748307],[-3.187633752822876,55.94458288209647],[-3.1869176030158997,55.94470154167527],[-3.187046349048614,55.94494186375901],[-3.186909556388855,55.944964393877896],[-3.18678081035614,55.94481569485101],[-3.1865662336349487,55.94487727734554],[-3.187113404273987,55.94555317633777],[-3.1872957944869995,55.945505112799054],[-3.187832236289978,55.945460053177314],[-3.188078999519348,55.94541799748307]]]}}]}";

        setMockResponseString(buildingsJson,200);
        
        FeatureCollection correctOutput = FeatureCollection.fromJson(buildingsJson);
        
        FeatureCollection output = testClient.fetchBuildings();

        assertEquals(correctOutput, output);
    }

    @Test
    public void testBadStatusCodesThrowExceptions() throws IOException, InterruptedException {

        for(int i = 300; i < 600; i++){
            setMockResponseString("", i);
            assertThrows(IOException.class,()->{
                testClient.fetchBuildings();
            });
    
            assertThrows(IOException.class,()->{
                testClient.fetchSensorsForDate(LocalDate.of(1, 1, 1));
            });
    
            assertThrows(IOException.class,()->{
                testClient.fetchW3WAddress("w.w.w");
            });
        }
       
    }

}
