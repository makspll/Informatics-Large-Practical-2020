package uk.ac.ed.inf.aqmaps.utilities;

import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;



public class ConversionUtilities {
    
    public static Coordinate PointToCoordinate(Point p){
        return new Coordinate(p.longitude(),p.latitude());
    }

        
    public static Point CoordinateToPoint(Coordinate p){
        return Point.fromLngLat(p.getX(),p.getY());
    }

    public static Polygon PolygonToPolygon(com.mapbox.geojson.Polygon p){

        var rings = p.coordinates();
        LinearRing shell = null;
        LinearRing[] holes = null;
        
        if(rings.size() > 1){
            holes = new LinearRing[rings.size() - 1];
        }

        for(int i = 0; i < rings.size(); i++){
            var currRing = rings.get(i);

            Coordinate[] coordinates = new Coordinate[currRing.size()];

            for(int j = 0; j < currRing.size(); j++){
                coordinates[j] = PointToCoordinate(currRing.get(j));
            }

            LinearRing convertedRing = geometryFactory.createLinearRing(coordinates);
            if(i == 0){
                shell = convertedRing;
            } else {
                holes[i - 1] =convertedRing; 
            }
        }
        
        return geometryFactory.createPolygon(shell,holes);
    }


    public final static GeometryFactory geometryFactory = new GeometryFactory(
                                                            new PrecisionModel(PrecisionModel.FLOATING));
}
