package uk.ac.ed.inf.aqmaps.client;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mapbox.geojson.Point;

public class Sensor {

    public static JsonDeserializer<Sensor> getDeserializer(){
        return deserializer;
    }
    
    public Sensor() {
    }

    public Sensor(String location, float battery, float reading) {
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

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public float getReading() {
        return this.reading;
    }

    public void setReading(float reading) {
        this.reading = reading;
    }

    public Sensor location(String location) {
        this.location = location;
        return this;
    }

    public Sensor battery(float battery) {
        this.battery = battery;
        return this;
    }

    public Sensor reading(float reading) {
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


    private static JsonDeserializer<Sensor> deserializer = new JsonDeserializer<Sensor>(){
            
        @Override
        public Sensor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject jsonObject = json.getAsJsonObject();

            var readingElement = jsonObject.get("reading");
            float reading = 0f;
            if(readingElement.getAsString().equals("null")
                || readingElement.getAsString().equals("NaN")){
                reading = Float.NaN;
            } else {
                reading = Float.parseFloat(readingElement.getAsString());
            }

            return new Sensor(
                jsonObject.get("location").getAsString(),
                jsonObject.get("battery").getAsFloat(),
                reading);
            
  
          
        }
    };

    private String location;
    private float battery;
    private float reading;
}
