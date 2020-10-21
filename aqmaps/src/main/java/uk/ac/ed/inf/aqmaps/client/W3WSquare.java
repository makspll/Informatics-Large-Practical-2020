package uk.ac.ed.inf.aqmaps.client;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mapbox.geojson.Point;

public class W3WSquare {

    public W3WSquare() {
    }

    public W3WSquare(Point southwest, Point northeast) {
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public Point getSouthwest() {
        return this.southwest;
    }

    public void setSouthwest(Point southwest) {
        this.southwest = southwest;
    }

    public Point getNorthwest() {
        return this.northeast;
    }

    public void setNorthwest(Point northwest) {
        this.northeast = northwest;
    }

    public W3WSquare southwest(Point southwest) {
        this.southwest = southwest;
        return this;
    }

    public W3WSquare northwest(Point northwest) {
        this.northeast = northwest;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof W3WSquare)) {
            return false;
        }
        W3WSquare w3WSquare = (W3WSquare) o;
        return Objects.equals(southwest, w3WSquare.southwest) && Objects.equals(northeast, w3WSquare.northeast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(southwest, northeast);
    }

    @Override
    public String toString() {
        return "{" +
            " southwest='" + getSouthwest() + "'" +
            ", northwest='" + getNorthwest() + "'" +
            "}";
    }

    private Point southwest;
    private Point northeast;

}
