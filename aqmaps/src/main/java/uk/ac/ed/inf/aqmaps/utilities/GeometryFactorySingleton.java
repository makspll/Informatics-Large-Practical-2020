package uk.ac.ed.inf.aqmaps.utilities;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryFactorySingleton {
    
    public static GeometryFactory getGeometryFactory(){
        return gf;
    }

    private static PrecisionModel pm = new PrecisionModel(1000000000000000d);
    private static GeometryFactory gf = new GeometryFactory(pm);
}
