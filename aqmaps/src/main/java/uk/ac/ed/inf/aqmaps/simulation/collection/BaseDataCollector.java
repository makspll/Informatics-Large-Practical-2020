package uk.ac.ed.inf.aqmaps.simulation.collection;

import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathPlanner;

public abstract class BaseDataCollector implements SensorDataCollector {
    
    public BaseDataCollector(PathPlanner fp, BaseCollectionOrderPlanner rp){
        pathPlanner = fp;
        collectionOrderPlanner = rp;
    }


    PathPlanner pathPlanner;
    BaseCollectionOrderPlanner collectionOrderPlanner;
}
