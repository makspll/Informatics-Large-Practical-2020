package uk.ac.ed.inf.aqmaps.simulation;

import org.locationtech.jts.geom.Coordinate;

public interface Sensor {
    public float getReading();
    public Coordinate getCoordinates();
    public boolean hasBeenRead();
    public float getBatteryLevel();
    public String getW3WLocation();
}
