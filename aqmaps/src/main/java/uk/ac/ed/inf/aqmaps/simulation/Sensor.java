package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Objects;
import org.locationtech.jts.geom.Coordinate;
import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;

/**
 * Sensors contain a reading value and a battery level as well as the what 3 words location and coordinates of the sensor.
 * This sensor will contain placeholder values for readings and battery status untill it is set to have been read.
 */
public abstract class Sensor implements PathfindingGoal {

    public Sensor(Coordinate coordinates, float reading, float batteryLevel, String W3WLocation){
        this.coordinates = coordinates;
        this.reading = reading;
        this.batteryLevel = batteryLevel;
        this.W3WLocation = W3WLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sensor)) {
            return false;
        }
        Sensor aQSensor = (Sensor) o;
        return reading == aQSensor.reading && Objects.equals(coordinates, aQSensor.coordinates) && hasBeenRead == aQSensor.hasBeenRead && batteryLevel == aQSensor.batteryLevel && Objects.equals(W3WLocation, aQSensor.W3WLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reading, coordinates, hasBeenRead, batteryLevel, W3WLocation);
    }

    @Override
    public String toString() {
        return "{" +
            " reading='" + getReading() + "'" +
            ", coordinates='" + getCoordinates() + "'" +
            ", hasBeenRead='" + hasBeenRead() + "'" +
            ", batteryLevel='" + getBatteryLevel() + "'" +
            ", W3WLocation='" + getW3WLocation() + "'" +
            "}";
    }
    
    /**
     * Sets the sensor state to "read". If the sensor is not set to have been read, asking it for readings and battery levels will return placeholder values
     */
    public void setHaveBeenRead(boolean read) {
        this.hasBeenRead = read;
    }

    /**
     * Return the coordinates of the sensor
     */
    public Coordinate getCoordinates() {
        return this.coordinates;
    }
    
    /**
     * Return the reading at this sensor. Will return NaN untill the setHaveBeenRead(true) has been called 
     */
    public float getReading() {
        return hasBeenRead ? this.reading : Float.NaN;
    }

    /**
     * Return the battery level at this sensor. Will return NaN untill the setHaveBeenRead(true) has been called 
     */
    public float getBatteryLevel() {
        return hasBeenRead ? this.batteryLevel : Float.NaN;
    }

    /**
     * Returns true if this sensor has been read (i.e. setHaveBeenRead(true) has been called on this object)
     */
    public boolean hasBeenRead() {
        return this.hasBeenRead;
    }
    
    /**
     * Retrieves the 3 word address (w3w) of this sensor
     */
    public String getW3WLocation() {
        return this.W3WLocation;
    }

    private boolean hasBeenRead;
    private float reading;
    private Coordinate coordinates;
    private float batteryLevel;
    private String W3WLocation;
}
