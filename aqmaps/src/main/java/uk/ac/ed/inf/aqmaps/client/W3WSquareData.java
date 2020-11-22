package uk.ac.ed.inf.aqmaps.client;

import java.util.Objects;

import com.mapbox.geojson.Point;

public class W3WSquareData {

    public W3WSquareData() {
    }

    public W3WSquareData(Point southwest, Point northeast) {
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public Point getSouthwest() {
        return this.southwest;
    }
    public Point getNorthwest() {
        return this.northeast;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof W3WSquareData)) {
            return false;
        }
        W3WSquareData w3WSquare = (W3WSquareData) o;
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
