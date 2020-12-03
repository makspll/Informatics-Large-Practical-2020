package uk.ac.ed.inf.aqmaps.simulation.collection;

import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathPlanner;

/**
 * Each data collector follows the same pattern, it uses a path planner to find a way between two points, as well as a collection order planner which
 * sets out the route around all the sensors. Each collector may use this data differently, for example it may discard the path given by a path planner under certain circumstances, or change the route mid-way. 
 */
public abstract class BaseDataCollector implements SensorDataCollector {
    
    /**
     * Constructs a data collector with the given path planner and the given collection order planner
     * @param fp
     * @param rp
     */
    public BaseDataCollector(PathPlanner fp, BaseCollectionOrderPlanner rp){
        pathPlanner = fp;
        collectionOrderPlanner = rp;
    }


    PathPlanner pathPlanner;
    BaseCollectionOrderPlanner collectionOrderPlanner;
}
