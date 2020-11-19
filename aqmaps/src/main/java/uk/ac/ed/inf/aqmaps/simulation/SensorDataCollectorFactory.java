package uk.ac.ed.inf.aqmaps.simulation;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.Graph;
import uk.ac.ed.inf.aqmaps.pathfinding.GreatestAvoidanceDistance;
import uk.ac.ed.inf.aqmaps.pathfinding.ScaledGridSnappingHash;
import uk.ac.ed.inf.aqmaps.pathfinding.StraightLineDistance;
import uk.ac.ed.inf.aqmaps.pathfinding.TreePathfindingAlgorithm;
import uk.ac.ed.inf.aqmaps.simulation.collection.Drone;
import uk.ac.ed.inf.aqmaps.simulation.collection.SensorDataCollector;
import uk.ac.ed.inf.aqmaps.simulation.planning.DiscreteStepAndAngleGraph;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.EuclidianDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.GreatestAvoidanceDistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.ChristofidesCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.CollectionOrderOptimiser;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.CollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.GreedyCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.NearestInsertionCollectionOrderPlanner;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.Optimiser2Opt;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.ConstrainedPathPlanner;

public class SensorDataCollectorFactory {
    
    public static SensorDataCollector createCollector(
        DiscreteStepAndAngleGraph g,double readingRange,int maxMoves,
        PathfindingHeuristicType heuristicType,
        CollectionOrderPlannerType plannerType,
        DistanceMatrixType matrixType){

        // set everything up with optimal values
        var hashingAlgorithm = new ScaledGridSnappingHash(
            g.getMoveLength()/75d, 
            g.getBoundary().getCentroid().getCoordinate());
        
        
        var algorithm = new AstarTreeSearch(
            heuristicType == PathfindingHeuristicType.GREATEST_AVOIDANCE ? 
                new GreatestAvoidanceDistance(g.getObstacles()):
                new StraightLineDistance(1.5), 
            hashingAlgorithm);

        
        var flightPlanner = new ConstrainedPathPlanner(
            readingRange,
            maxMoves, 
            algorithm);


        var distMatrix = (matrixType == DistanceMatrixType.EUCLIDIAN) ?
            new EuclidianDistanceMatrix():
            new GreatestAvoidanceDistanceMatrix(g.getObstacles());

        CollectionOrderPlanner routePlanner = null;
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
