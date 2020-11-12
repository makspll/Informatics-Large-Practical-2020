package uk.ac.ed.inf.aqmaps.simulation;

import java.util.Arrays;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

public class Building implements Obstacle {

    public Building(Polygon shape) {
        this.shape = shape;
    }

    @Override
    public Polygon getShape() {
        return shape;
    }

    @Override
    public boolean intersectsPath(Queue<PathSegment> path) {

        for (PathSegment pathSegment : path) {
        
            boolean intersection = intersectsPath(
                pathSegment.getStartPoint(), 
                pathSegment.getEndPoint());

            if(intersection)
                return true;
        }
        
        return false;
    }

    private final Polygon shape;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));

    @Override
    public boolean intersectsPath(Coordinate a, Coordinate b) {
        LineString ls = geometryFactory
            .createLineString(
                new Coordinate[]{a,b});

        return ls.touches(shape) || ls.crosses(shape);
    }      
}