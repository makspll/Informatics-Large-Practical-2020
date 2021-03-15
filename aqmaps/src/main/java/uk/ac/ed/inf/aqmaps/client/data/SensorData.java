package uk.ac.ed.inf.aqmaps.client.data;

import java.util.Objects;


/**
 * Object representing the reading, battery and location data of a sensor
 */
public class SensorData {

    /**
     * Empty constructor, necessary for proper de-serialization
     */
    public SensorData() {
    }

    /**
     * Constructs a new sensor data object
     * @param location the what 3 words location of this sensor
     * @param battery the battery reading of the sensor
     * @param reading the sensor reading 
     */
    public SensorData(String W3WLocation, float battery, float reading) {
        this.W3WLocation = W3WLocation;
        this.battery = battery;
        this.reading = reading;
    }

    /**
     * Returns the w3w location of this sensor
     */
    public String getW3WLocation() {
        return this.W3WLocation;
    }

    /**
     * Returns the battery level of the sensor, always a normal float value
     */
    public Float getBattery() {
        return this.battery;
    }

    /**
     * Returns the reading value of the sensor, can be NaN or a float
     * @return
     */
    public float getReading() {
        return this.reading;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SensorData)) {
            return false;
        }
        SensorData sensor = (SensorData) o;
        return Objects.equals(W3WLocation, sensor.W3WLocation) && Objects.equals(battery, sensor.battery) && Objects.equals(reading, sensor.reading);
    }

    @Override
    public int hashCode() {
        return Objects.hash(W3WLocation, battery, reading);
    }

    @Override
    public String toString() {
        return "{" +
            " location='" + getW3WLocation() + "'" +
            ", battery='" + getBattery() + "'" +
            ", reading='" + getReading() + "'" +
            "}";
    }

    private String W3WLocation;
    private float battery;
    private float reading;
}
