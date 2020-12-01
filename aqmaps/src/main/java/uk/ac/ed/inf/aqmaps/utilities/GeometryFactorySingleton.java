package uk.ac.ed.inf.aqmaps.utilities;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * The geometry factory containing the precision model to be used when generating geometries with JTS
 */
public class GeometryFactorySingleton {
    
    /**
     * Retrieve the geometry factory, containing the precision model
     */
    public static GeometryFactory getGeometryFactory(){
        return gf;
    }

    /**
     * We choose a very specific precision model
     */
    private static PrecisionModel pm = new PrecisionModel(1000000000000000d);
    private static GeometryFactory gf = new GeometryFactory(pm);
}
