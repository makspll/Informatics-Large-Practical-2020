package uk.ac.ed.inf.aqmaps.client;

import java.util.Objects;

public class Sensor {
    private String location;
    private Float battery;
    private String reading;


    public Sensor() {
    }

    public Sensor(String location, Float battery, String reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getBattery() {
        return this.battery;
    }

    public void setBattery(Float battery) {
        this.battery = battery;
    }

    public String getReading() {
        return this.reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public Sensor location(String location) {
        this.location = location;
        return this;
    }

    public Sensor battery(Float battery) {
        this.battery = battery;
        return this;
    }

    public Sensor reading(String reading) {
        this.reading = reading;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sensor)) {
            return false;
        }
        Sensor sensor = (Sensor) o;
        return Objects.equals(location, sensor.location) && Objects.equals(battery, sensor.battery) && Objects.equals(reading, sensor.reading);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, battery, reading);
    }

    @Override
    public String toString() {
        return "{" +
            " location='" + getLocation() + "'" +
            ", battery='" + getBattery() + "'" +
            ", reading='" + getReading() + "'" +
            "}";
    }

}
