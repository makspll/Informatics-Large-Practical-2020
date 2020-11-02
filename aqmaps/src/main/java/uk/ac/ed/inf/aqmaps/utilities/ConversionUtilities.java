package uk.ac.ed.inf.aqmaps.utilities;

import com.mapbox.geojson.Point;

import org.locationtech.jts.geom.Coordinate;

public class ConversionUtilities {
    
    public static Coordinate PointToCoordinate(Point p){
        return new Coordinate(p.longitude(),p.latitude());
    }

        
    public static Point CoordinateToPoint(Coordinate p){
        return Point.fromLngLat(p.getX(),p.getY());
    }
}
