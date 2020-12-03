package uk.ac.ed.inf.aqmaps.simulation;

import java.util.ArrayList;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;
import uk.ac.ed.inf.aqmaps.simulation.collection.SensorDataCollector;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.EuclidianDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreatestAvoidanceDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.GreedyCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.NearestInsertionCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.Optimiser2Opt;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.SimplePathPlanner;

/**
 * A utility class for instantiating sensor data collectors with correct parameter values in one call
 */
public class SensorDataCollectorFactory {
    
    /**
     * Create a new collector within the given domain and with the given parameters, filling in missing parameters with optimal values.
     * @param g the graph defining the movement capabilities
     * @param readingRange the minimum distance between the collector and the sensor required for a reading to be taken
     * @param maxMoves the maximum number of path segments returned by the collector
     * @param heuristicType the pathfinding heuristic to use
     * @param plannerType the type of planner to use
     * @param matrixType the type of distance matrix to use
     * @return
     */
    public static SensorDataCollector createCollector(
        ConstrainedTreeGraph g,double readingRange,int maxMoves,
        CollectorType collectorType,
        PathfindingHeuristicType heuristicType,
        CollectionOrderPlannerType plannerType,
        DistanceMatrixType matrixType){
        return(createCollector(g, readingRange, maxMoves, collectorType, heuristicType, plannerType, matrixType,1.5f,g.getMoveLength()/75d,0.1d * g.getMoveLength()));
    }

    /**
     * Create a new collector within the given domain and with the given parameters
     * @param g the graph defining the movement capabilities
     * @param readingRange the minimum distance between the collector and the sensor required for a reading to be taken
     * @param maxMoves the maximum number of path segments returned by the collector
     * @param heuristicType the pathfinding heuristic to use
     * @param plannerType the type of planner to use
     * @param matrixType the type of distance matrix to use
     * @param relaxationFactor the relaxation factor to use (applies to certain heuristics)
     * @param hashingGridWidth the width of the spatial hashing grid
     * @param opt2Epsilon the minimum required improvement threshold in the path for 2opt to keep optimising
     * @return
     */
    public static SensorDataCollector createCollector(
        ConstrainedTreeGraph g,
        double readingRange,
        int maxMoves,
        CollectorType collectorType,
        PathfindingHeuristicType heuristicType,
        CollectionOrderPlannerType plannerType,
        DistanceMatrixType matrixType,
        float relaxationFactor,
        double hashingGridWidth,
        double opt2Epsilon){

        // set everything up with optimal values
        var hashingAlgorithm = new GridSnappingSpatialHash(
            hashingGridWidth ,
            g.getBoundary().getCentroid().getCoordinate());
        
        
        var algorithm = new AstarTreeSearch<DirectedSearchNode>(
            new StraightLineDistance(relaxationFactor), 
            hashingAlgorithm);

        
        var flightPlanner = new SimplePathPlanner(
            readingRange,
            maxMoves, 
            algorithm);


        var distMatrix = (matrixType == DistanceMatrixType.EUCLIDIAN) ?
            new EuclidianDistanceMatrix():
            new GreatestAvoidanceDistanceMatrix(g.getObstacles());

        BaseCollectionOrderPlanner routePlanner = null;
        var optimisers = new ArrayList<CollectionOrderOptimiser>();

        switch(plannerType){
            case GREEDY:
                optimisers.add(new Optimiser2Opt(opt2Epsilon));
                routePlanner = new GreedyCollectionOrderPlanner(optimisers, distMatrix);
				break;
            case NEAREST_INSERTION:
                optimisers.add(new Optimiser2Opt(opt2Epsilon));
                routePlanner = new NearestInsertionCollectionOrderPlanner(optimisers, distMatrix);
				break;
        }

        var collector = new Drone(flightPlanner, routePlanner);

        return collector;
    }

    /**
     * The type of sensor data collector to use
     */
    public enum CollectorType{
        DRONE
    }

    /**
     * The type of distance matrix to use for the TSP solver
     */
    public enum DistanceMatrixType{
        EUCLIDIAN,GREATEST_AVOIDANCE
    }

    /**
     * The type of the collection order planner to use 
     */
    public enum CollectionOrderPlannerType{
        GREEDY,NEAREST_INSERTION
    }

    /**
     * The type of pathfinding heuristic to use
     */
    public enum PathfindingHeuristicType{
        STRAIGHT_LINE
    }

    
}
