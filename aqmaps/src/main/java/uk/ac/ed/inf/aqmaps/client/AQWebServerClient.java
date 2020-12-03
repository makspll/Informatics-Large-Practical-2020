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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.client.data.W3WSquareData;

import java.lang.reflect.Type;


/**
 * The web server client communicates with a server which contains the necessary information and retrieves it
 */
public class AQWebServerClient implements ClientService{

    /**
     * Create a new web server client, if the base api uri is localhost then the base api would be : http://localhost
     * @param client
     * @param APIBaseURI
     */
    public AQWebServerClient(HttpClient client,URI APIBaseURI){
        this.APIBaseURI = APIBaseURI;
        this.client = client;
    }

    /**
     * Retrieves all SensorData for sensors to be collected on the given date
     */
    public List<SensorData> fetchSensorsForDate(LocalDate date) throws IOException, InterruptedException {

        //// work out the relative api uri in the /maps/yyyy/mm/dd/air-quality-data.json format
        var requestURI = getFullAPIURI(
            URI.create(
                String.format("/maps/%04d/%02d/%02d/air-quality-data.json",
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
        // register custom deserializers
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SensorData.class, sensorDataDeserializer);
        Gson gson = gsonBuilder.create();

        // capture type of list of sensors ussing annonymous inner class
        Type sensorListType = 
            new TypeToken<ArrayList<SensorData>>() {}.getType(); 


        ArrayList<SensorData> sensorList =
            gson.fromJson(response.body(), sensorListType);


        return sensorList;

    }
    
    /**
     * Retrieves detail information about a what-3-words address (i.e. word.word.word)
     */
    public W3WAddressData fetchW3WAddress(String wordAddress) throws IOException, InterruptedException {
        String[] words = wordAddress.split("\\.");
        assert words.length == 3;
        return fetchW3WAddress(words[0],words[1],words[2]);
    }

    /**
     * Convenience method, accepts a split w3w address 
     * @param w1
     * @param w2
     * @param w3
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public W3WAddressData fetchW3WAddress(String w1, String w2, String w3) throws IOException, InterruptedException {
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

        // make sure status code represents success
        if(responseCodeRange != HttpStatusCodeRange.SUCCESS_RANGE){
            throw new HTTPException(response.statusCode(),"Could not fetch word data from web server" + "," + requestURI.toString());
        }

        //// parse the json
        // register custom deserializers
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(W3WAddressData.class, w3wAddressDeserializer);
        Gson gson = gsonBuilder.create();
        
        W3WAddressData address =
            gson.fromJson(response.body(), W3WAddressData.class);

        return address;
    }

    /**
     * Retrieves all te no fly-zones i.e. the buildings present
     */
    public FeatureCollection fetchBuildings() throws IOException, InterruptedException {
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

    private URI getFullAPIURI(URI relative){
        return APIBaseURI.resolve(relative);
    }

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



    private URI APIBaseURI;
    private final HttpClient client;

    public static JsonDeserializer<SensorData> sensorDataDeserializer = new JsonDeserializer<SensorData>(){
            
        @Override
        public SensorData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject jsonObject = json.getAsJsonObject();

            var readingElement = jsonObject.get("reading");
            float reading = 0f;
            if(readingElement.getAsString().equals("null")
                || readingElement.getAsString().equals("NaN")){
                reading = Float.NaN;
            } else {
                reading = Float.parseFloat(readingElement.getAsString());
            }

            return new SensorData(
                jsonObject.get("location").getAsString(),
                jsonObject.get("battery").getAsFloat(),
                reading);
            
  
          
        }
    };

    public static JsonDeserializer<W3WAddressData> w3wAddressDeserializer = new JsonDeserializer<W3WAddressData>(){
            
        @Override
        public W3WAddressData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject jsonObject = json.getAsJsonObject();
            var square = jsonObject.getAsJsonObject("square");

            var squareW3W = new W3WSquareData(
                Point.fromLngLat(
                    square.getAsJsonObject("southwest").get("lng").getAsDouble(), 
                    square.getAsJsonObject("southwest").get("lat").getAsDouble()),
                Point.fromLngLat(
                    square.getAsJsonObject("northeast").get("lng").getAsDouble(), 
                    square.getAsJsonObject("northeast").get("lat").getAsDouble()));

            var coordinates = Point.fromLngLat(
                    jsonObject.getAsJsonObject("coordinates").get("lng").getAsDouble(),
                    jsonObject.getAsJsonObject("coordinates").get("lat").getAsDouble());

            return new W3WAddressData(
                jsonObject.get("country").getAsString(),
                squareW3W,
                jsonObject.get("nearestPlace").getAsString(),
                coordinates,
                jsonObject.get("words").getAsString(),
                jsonObject.get("language").getAsString(),
                jsonObject.get("map").getAsString()
            );
        }
    };
}
