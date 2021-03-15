package uk.ac.ed.inf.aqmaps.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;

/**
 * A client service retrieves the necessary information to create an AQ map
 */
public interface ClientService {
    public List<SensorData> fetchSensorsForDate(LocalDate date) throws IOException, InterruptedException;
    public W3WAddressData fetchW3WAddress(String wordAddress) throws IOException, InterruptedException;
    public FeatureCollection fetchBuildings() throws IOException, InterruptedException;
    
}
