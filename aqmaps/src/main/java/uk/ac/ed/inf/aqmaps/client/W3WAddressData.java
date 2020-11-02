package uk.ac.ed.inf.aqmaps.client;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mapbox.geojson.Point;

public class W3WAddressData {

    public static JsonDeserializer<W3WAddressData> getDeserializer(){
        return deserializer;
    }

    public W3WAddressData() {
    }

    public W3WAddressData(String country, W3WSquareData square, String nearestPlace, Point coordinates, String words, String language, String map) {
        this.country = country;
        this.square = square;
        this.nearestPlace = nearestPlace;
        this.coordinates = coordinates;
        this.words = words;
        this.language = language;
        this.map = map;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public W3WSquareData getSquare() {
        return this.square;
    }

    public void setSquare(W3WSquareData square) {
        this.square = square;
    }

    public String getNearestPlace() {
        return this.nearestPlace;
    }

    public void setNearestPlace(String nearestPlace) {
        this.nearestPlace = nearestPlace;
    }

    public Point getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public String getWords() {
        return this.words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMap() {
        return this.map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof W3WAddressData)) {
            return false;
        }
        W3WAddressData w3WAddress = (W3WAddressData) o;
        return Objects.equals(country, w3WAddress.country) && Objects.equals(square, w3WAddress.square) && Objects.equals(nearestPlace, w3WAddress.nearestPlace) && Objects.equals(coordinates, w3WAddress.coordinates) && Objects.equals(words, w3WAddress.words) && Objects.equals(language, w3WAddress.language) && Objects.equals(map, w3WAddress.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, square, nearestPlace, coordinates, words, language, map);
    }

    @Override
    public String toString() {
        return "{" +
            " country='" + getCountry() + "'" +
            ", square='" + getSquare() + "'" +
            ", nearestPlace='" + getNearestPlace() + "'" +
            ", coordinates='" + getCoordinates() + "'" +
            ", words='" + getWords() + "'" +
            ", language='" + getLanguage() + "'" +
            ", map='" + getMap() + "'" +
            "}";
    }

    private String country;
    private W3WSquareData square;
    private String nearestPlace;
    private Point coordinates;
    private String words;
    private String language;
    private String map;

    private static JsonDeserializer<W3WAddressData> deserializer = new JsonDeserializer<W3WAddressData>(){
            
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
