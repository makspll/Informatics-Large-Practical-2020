package uk.ac.ed.inf.aqmaps.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;

import java.lang.reflect.Type;


public class WebServerClient {


    public static List<Sensor> fetchSensorsForDate(LocalDate date) throws IOException, InterruptedException {

        //// work out the relative api uri in the /maps/yyyy/mm/dd/air-quality-data.json format
        var requestURI = getFullAPIURI(
            URI.create(
                String.format("maps/%04d/%02d/%02d/air-quality-data.json",
                    date.getYear(),
                    date.getMonthValue(),
                    date.getDayOfMonth())));


        var request = HttpRequest.newBuilder()
            .uri(requestURI)
            .build();
        
        //// perform request
        var response = client.send(request, BodyHandlers.ofString());

        //// make sure request was sucessfull before continuing
        var responseCodeRange = HttpStatusCodeRange.getRange(response.statusCode());

        if(responseCodeRange != HttpStatusCodeRange.SUCCESS_RANGE){
            throw new HTTPException(response.statusCode(),"Could not fetch sensor data from web server" + "," + requestURI.toString());
        }

        //// parse the json

        // capture type of list of sensors ussing annonymous inner class
        Type sensorListType = 
            new TypeToken<ArrayList<Sensor>>() {}.getType(); 

        ArrayList<Sensor> studentList =
            new Gson().fromJson(response.body(), sensorListType);

        return studentList;

    }
    

    public static W3WAddress fetchW3WAddress(String w1, String w2, String w3) throws IOException, InterruptedException {
        //// work out the relative api uri in the /words/word1/word2/word3/details.json format
        var requestURI = getFullAPIURI(
            URI.create(
                String.format("/words/%s/%s/%s/details.json",
                    w1,
                    w2,
                    w3)));
        var request = HttpRequest.newBuilder()
                        .uri(requestURI)
                        .build();
                
        //// perform request
        var response = client.send(request, BodyHandlers.ofString());


        //// make sure request was sucessfull before continuing
        var responseCodeRange = HttpStatusCodeRange.getRange(response.statusCode());

        if(responseCodeRange != HttpStatusCodeRange.SUCCESS_RANGE){
            throw new HTTPException(response.statusCode(),"Could not fetch word data from web server" + "," + requestURI.toString());
        }

        //// parse the json
        W3WAddress address =
            new Gson().fromJson(response.body(), W3WAddress.class);

        return address;
    }

    public static FeatureCollection fetchBuildings() throws IOException, InterruptedException {
        //// work out the relative api uri in the /buildings/no-fly-zones.geojson format
        var requestURI = getFullAPIURI(
            URI.create("/buildings/no-fly-zones.geojson"));

        var request = HttpRequest.newBuilder()
                        .uri(requestURI)
                        .build();
                
        //// perform request
        var response = client.send(request, BodyHandlers.ofString());


        //// make sure request was sucessfull before continuing
        var responseCodeRange = HttpStatusCodeRange.getRange(response.statusCode());

        if(responseCodeRange != HttpStatusCodeRange.SUCCESS_RANGE){
            throw new HTTPException(response.statusCode(),"Could not fetch building data from web server" + "," + requestURI.toString());
        }

        //// parse the json
        FeatureCollection buildings = FeatureCollection.fromJson(response.body());
        return buildings;
    }

    private static final URI APIBaseURI = URI.create("http://localhost:9898");
    private static final HttpClient client = HttpClient.newHttpClient();

    private enum HttpStatusCodeRange {
        SUCCESS_RANGE, CLIENT_ERROR_RANGE, SERVER_ERROR_RANGE, UNKNOWN; 

        private static HttpStatusCodeRange getRange(int code) {
            if (code >= 200 && code < 300) {
                return HttpStatusCodeRange.SUCCESS_RANGE;
            } 
            if (code >= 400 && code < 500) {
                return HttpStatusCodeRange.CLIENT_ERROR_RANGE;
            } 
            if (code >= 500 && code < 600) {
                return HttpStatusCodeRange.SERVER_ERROR_RANGE;
            } 
            return HttpStatusCodeRange.UNKNOWN;
        }
    }

    private static URI getFullAPIURI(URI relative){
        return APIBaseURI.resolve(relative);
    }


    
}
