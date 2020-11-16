package uk.ac.ed.inf.aqmaps.simulation;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingGoal;

public interface Sensor extends PathfindingGoal {
    public float getReading();
    public Coordinate getCoordinates();
    public boolean hasBeenRead();
    public void setHaveBeenRead(boolean read);
    public float getBatteryLevel();
    public String getW3WLocation();
}
