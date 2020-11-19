package uk.ac.ed.inf.aqmaps.utilities;

import com.mapbox.geojson.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;


/**
 * A collection of utility geometry methods 
 */
public class GeometryUtilities {
    
    /**
     * Convert a mapbox point to a jts coordinate
     * @param p
     * @return
     */
    public static Coordinate MapboxPointToJTSCoordinate(Point p){
        return new Coordinate(p.longitude(),p.latitude());
    }

    
    /**
     * Convert a jts cooridnate to a mapbox point
     * @param p
     * @return
     */
    public static Point JTSCoordinateToMapboxPoint(Coordinate p){
        return Point.fromLngLat(p.getX(),p.getY());
    }

    /**
     * Convert mapbox polygon to jts polygon
     * @param p
     * @return
     */
    public static Polygon MapboxPolygonToJTSPolygon(com.mapbox.geojson.Polygon p){

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
                coordinates[j] = MapboxPointToJTSCoordinate(currRing.get(j));
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

    /**
     * The general precision model to use in geometry generation
     */
    public final static PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);

    /**
     * The geometry factory to use in generating jts geometries
     */
    public final static GeometryFactory geometryFactory = new GeometryFactory(precisionModel);


}
