package uk.ac.ed.inf.aqmaps.simulation;

import java.util.ArrayList;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.hashing.GridSnappingSpatialHash;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.GreatestAvoidanceDistance;
import uk.ac.ed.inf.aqmaps.pathfinding.heuristics.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;
import uk.ac.ed.inf.aqmaps.simulation.collection.SensorDataCollector;
import uk.ac.ed.inf.aqmaps.simulation.planning.ConstrainedTreeGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.EuclidianDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreatestAvoidanceDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.ChristofidesCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.BaseCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.GreedyCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.NearestInsertionCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.Optimiser2Opt;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.BasePathPlanner;

/**
 * A utility class for instantiating sensor data collectors with optimal values in one call
 */
public class SensorDataCollectorFactory {
    
    /**
     * Create a new collector within the given domain and with the given parameters.
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
        PathfindingHeuristicType heuristicType,
        CollectionOrderPlannerType plannerType,
        DistanceMatrixType matrixType){

        // set everything up with optimal values
        var hashingAlgorithm = new GridSnappingSpatialHash(
            g.getMoveLength()/75d, 
            g.getBoundary().getCentroid().getCoordinate());
        
        
        var algorithm = new AstarTreeSearch<DirectedSearchNode>(
            heuristicType == PathfindingHeuristicType.GREATEST_AVOIDANCE ? 
                new GreatestAvoidanceDistance(g.getObstacles()):
                new StraightLineDistance(1.5), 
            hashingAlgorithm);

        
        var flightPlanner = new BasePathPlanner(
            readingRange,
            maxMoves, 
            algorithm);


        var distMatrix = (matrixType == DistanceMatrixType.EUCLIDIAN) ?
            new EuclidianDistanceMatrix():
            new GreatestAvoidanceDistanceMatrix(g.getObstacles());

        BaseCollectionOrderPlanner routePlanner = null;
        var optimisers = new ArrayList<CollectionOrderOptimiser>();

        switch(plannerType){
            case CHRISTOFIDES:
                routePlanner = new ChristofidesCollectionOrderPlanner(optimisers, distMatrix);
				break;
            case GREEDY:
                optimisers.add(new Optimiser2Opt(g.getMoveLength() * 0.1d));
                routePlanner = new GreedyCollectionOrderPlanner(optimisers, distMatrix);
				break;
            case NEAREST_INSERTION:
                optimisers.add(new Optimiser2Opt(g.getMoveLength() * 0.1d));
                routePlanner = new NearestInsertionCollectionOrderPlanner(optimisers, distMatrix);
				break;
        }

        var collector = new Drone(flightPlanner, routePlanner);

        return collector;
    }


    public enum DistanceMatrixType{
        EUCLIDIAN,GREATEST_AVOIDANCE
    }


    public enum CollectionOrderPlannerType{
        CHRISTOFIDES,GREEDY,NEAREST_INSERTION
    }

    public enum PathfindingHeuristicType{
        STRAIGHT_LINE,GREATEST_AVOIDANCE
    }

    
}
