package uk.ac.ed.inf.aqmaps.client;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class SensorData {

    public static JsonDeserializer<SensorData> getDeserializer(){
        return deserializer;
    }
    
    public SensorData() {
    }

    public SensorData(String location, float battery, float reading) {
        this.location = location;
        this.battery = battery;
        this.reading = reading;
    }

    public String getLocation() {
        return this.location;
    }

    public Float getBattery() {
        return this.battery;
    }

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


    private static JsonDeserializer<SensorData> deserializer = new JsonDeserializer<SensorData>(){
            
        @Override
        public SensorData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
            JsonObject jsonObject = json.getAsJsonObject();

            var readingElement = jsonObject.get("reading");
            float reading = 0f;
            if(readingElement.getAsString().equals("null")
                || readingElement.getAsString().equals("NaN")){
                reading = Float.NaN;
            } else {
                reading = Float.parseFloat(readingElement.getAsString());
            }

            return new SensorData(
                jsonObject.get("location").getAsString(),
                jsonObject.get("battery").getAsFloat(),
                reading);
            
  
          
        }
    };

    private String location;
    private float battery;
    private float reading;
}
