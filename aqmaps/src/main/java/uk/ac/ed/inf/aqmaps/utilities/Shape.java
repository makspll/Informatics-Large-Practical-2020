package uk.ac.ed.inf.aqmaps.utilities;

import org.locationtech.jts.geom.Geometry;

/**
 * Anything which contains a shape can implement this interface. Shapes can be polygons, or absolutely any other geometry
 */
public interface Shape {
    /**
     * Return this shape's geometry
     */
    public Geometry getShape();
}
