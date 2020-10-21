package uk.ac.ed.inf.aqmaps.client;

import java.util.Objects;

public class W3WAddress {
    private String country;
    private W3WSquare square;
    private String nearestPlace;
    private Point coordinates;
    private String words;
    private String language;
    private String map;


    public W3WAddress() {
    }

    public W3WAddress(String country, W3WSquare square, String nearestPlace, Point coordinates, String words, String language, String map) {
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

    public W3WSquare getSquare() {
        return this.square;
    }

    public void setSquare(W3WSquare square) {
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

    public W3WAddress country(String country) {
        this.country = country;
        return this;
    }

    public W3WAddress square(W3WSquare square) {
        this.square = square;
        return this;
    }

    public W3WAddress nearestPlace(String nearestPlace) {
        this.nearestPlace = nearestPlace;
        return this;
    }

    public W3WAddress coordinates(Point coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public W3WAddress words(String words) {
        this.words = words;
        return this;
    }

    public W3WAddress language(String language) {
        this.language = language;
        return this;
    }

    public W3WAddress map(String map) {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof W3WAddress)) {
            return false;
        }
        W3WAddress w3WAddress = (W3WAddress) o;
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



}
