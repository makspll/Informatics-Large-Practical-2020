package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;


public class PathSegment {

    public PathSegment() {
    }

    public PathSegment(Coordinate startPoint, int direction, Coordinate endPoint, Sensor sensorRead) {
        this.startPoint = startPoint;
        this.direction = direction;
        this.endPoint = endPoint;
        this.sensorRead = sensorRead;
    }

    public Coordinate getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(Coordinate startPoint) {
        this.startPoint = startPoint;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Coordinate getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(Coordinate endPoint) {
        this.endPoint = endPoint;
    }

    public Sensor getSensorRead() {
        return this.sensorRead;
    }

    public void setSensorRead(Sensor sensorRead) {
        this.sensorRead = sensorRead;
    }

    public PathSegment startPoint(Coordinate startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    public PathSegment direction(int direction) {
        this.direction = direction;
        return this;
    }

    public PathSegment endPoint(Coordinate endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public PathSegment sensorRead(Sensor sensorRead) {
        this.sensorRead = sensorRead;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PathSegment)) {
            return false;
        }
        PathSegment move = (PathSegment) o;
        return Objects.equals(startPoint, move.startPoint) && direction == move.direction && Objects.equals(endPoint, move.endPoint) && Objects.equals(sensorRead, move.sensorRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, direction, endPoint, sensorRead);
    }

    @Override
    public String toString() {
        return "{" +
            " startPoint='" + getStartPoint() + "'" +
            ", direction='" + getDirection() + "'" +
            ", endPoint='" + getEndPoint() + "'" +
            ", sensorRead='" + getSensorRead() + "'" +
            "}";
    }
    
    private Coordinate startPoint;
    private int direction;
    private Coordinate endPoint;
    private Sensor sensorRead;


}
