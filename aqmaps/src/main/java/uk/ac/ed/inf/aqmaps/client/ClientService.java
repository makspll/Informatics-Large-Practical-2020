package uk.ac.ed.inf.aqmaps.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;

public interface ClientService {
    public List<SensorData> fetchSensorsForDate(LocalDate date) throws IOException, InterruptedException;
    public W3WAddressData fetchW3WAddress(String wordAddress) throws IOException, InterruptedException;
    public FeatureCollection fetchBuildings() throws IOException, InterruptedException;
    
}
