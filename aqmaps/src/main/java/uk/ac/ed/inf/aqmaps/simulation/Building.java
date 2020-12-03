package uk.ac.ed.inf.aqmaps.simulation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import uk.ac.ed.inf.aqmaps.pathfinding.Obstacle;
import uk.ac.ed.inf.aqmaps.utilities.GeometryFactorySingleton;
import uk.ac.ed.inf.aqmaps.utilities.GeometryUtilities;

/**
 * An obstacle with a polygonal shape representing a building
 */
public class Building implements Obstacle {

  
    /**
     * initialize new building with the given shape
     * @param shape
     */
    public Building(Polygon shape) {
        this.shape = shape;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Polygon getShape() {
        return shape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean intersectsPath(Coordinate a, Coordinate b) {
        LineString ls = GeometryFactorySingleton.getGeometryFactory()
            .createLineString(
                new Coordinate[]{a,b});

        return !ls.disjoint(shape) || ls.touches(shape);
    }    

    /**
     * The shape of this obstacle
     */
    private final Polygon shape;

}
