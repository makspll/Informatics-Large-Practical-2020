package uk.ac.ed.inf.aqmaps.client.data;

import java.util.Objects;

import com.mapbox.geojson.Point;

/**
 * Object representing information about a what-3-words square's coordinates.
 */
public class W3WSquareData {

    /**
     * Empty constructor, necessary for proper de-serialization
     */
    public W3WSquareData() {
    }

    /**
     * Create a new w3w square from two opposite corners
     * @param southwest
     * @param northeast
     */
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
