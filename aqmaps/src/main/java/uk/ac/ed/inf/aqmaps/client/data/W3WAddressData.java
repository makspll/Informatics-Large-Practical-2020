package uk.ac.ed.inf.aqmaps.client.data;

import java.util.Objects;
import com.mapbox.geojson.Point;

/**
 * An object containing information about a what-3-word square including it's coordinates and meta-data
 */
public class W3WAddressData {

    /**
     * Empty constructor, necessary for proper de-serialization
     */
    public W3WAddressData() {
    }

    /**
     * @param country
     * @param square
     * @param nearestPlace
     * @param coordinates
     * @param words
     * @param language
     * @param map
     */
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

    public W3WSquareData getSquare() {
        return this.square;
    }


    public String getNearestPlace() {
        return this.nearestPlace;
    }


    public Point getCoordinates() {
        return this.coordinates;
    }

    public String getWords() {
        return this.words;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getMap() {
        return this.map;
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
}
