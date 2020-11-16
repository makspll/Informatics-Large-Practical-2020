package uk.ac.ed.inf.aqmaps.client;

import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

public class AQSensor implements Sensor{

    public AQSensor(SensorData sensorData, W3WAddressData addressData){
        reading = sensorData.getReading();
        coordinates = GeometryUtilities.MapboxPointToJTSCoordinate(addressData.getCoordinates());
        hasBeenRead = false;
        batteryLevel = sensorData.getBattery();
        W3WLocation = addressData.getWords();
    }


    public float getReading() {
        return this.reading;
    }


    public Coordinate getCoordinates() {
        return this.coordinates;
    }

    public boolean hasBeenRead() {
        return this.hasBeenRead;
    }
    
    @Override
    public void setHaveBeenRead(boolean read) {
        this.hasBeenRead = read;
    }

    public float getBatteryLevel() {
        return this.batteryLevel;
    }

    public String getW3WLocation() {
        return this.W3WLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AQSensor)) {
            return false;
        }
        AQSensor aQSensor = (AQSensor) o;
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
    
    private float reading;
    private Coordinate coordinates;
    private boolean hasBeenRead;
    private float batteryLevel;
    private String W3WLocation;

    @Override
    public Coordinate getPosition() {
        return getCoordinates();
    }




 
}
