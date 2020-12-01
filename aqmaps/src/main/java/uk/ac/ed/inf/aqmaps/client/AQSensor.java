package uk.ac.ed.inf.aqmaps.client;

import uk.ac.ed.inf.aqmaps.client.data.SensorData;
import uk.ac.ed.inf.aqmaps.client.data.W3WAddressData;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * {@inheritdoc}
 * An implementation of the Sensor interface for consumption in the simulation module. Conveniently can be formed from sensor and address data 
 * corresponding to the data received via API client.
 */
public class AQSensor extends Sensor{

    /**
     * Construct new AQ sensor from sensor data representing the sensor's readings and battery information, and with the address data containing 
     * the w3w address words and coordinates of the sensor
     * @param sensorData
     * @param w3WAddressData
     */
    public AQSensor(SensorData sensorData, W3WAddressData w3WAddressData){
        super(GeometryUtilities.MapboxPointToJTSCoordinate(w3WAddressData.getCoordinates()),
            sensorData.getReading(), 
            sensorData.getBattery(), 
            w3WAddressData.getWords());
    }

}
