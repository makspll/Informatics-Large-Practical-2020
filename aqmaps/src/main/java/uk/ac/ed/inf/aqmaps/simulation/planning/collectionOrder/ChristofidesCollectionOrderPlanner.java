package uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.CollectionOrderOptimiser;

public class ChristofidesCollectionOrderPlanner extends BaseCollectionOrderPlanner {

    public ChristofidesCollectionOrderPlanner(Collection<CollectionOrderOptimiser> optimisers, DistanceMatrix distMat) {
        super(optimisers, distMat);
    }

    @Override
    protected int[] planInitialRoute(int startSensorIdx, Sensor[] sensors, DistanceMatrix distanceMatrix,
            boolean formLoop) {
        
        // find MST
        List<Node> mstTour = findMST(distanceMatrix,startSensorIdx, sensors.length);

        return null;
    }


    /**
     * finds the minimum spanning tree in adjacency matrix format
     * @param dm
     * @param startPoint
     * @return 
     */
    private List<Node> findMST(DistanceMatrix dm, int startPoint, int n){

        // pick nearest unvisited node to the tour at each point 
        // and attach it to the closest node in the tour
        var startNode = new Node(startPoint);
        var visited = new HashSet<Integer>();
        
        var tour = new ArrayList<Node>();

        visited.add(startPoint);
        tour.add(startNode);

        // find nearest unvisited node to one of the nodes in the tour
        while(visited.size() != n){

            double smallestDistance = Double.MAX_VALUE;
            int nearestSensor = -1;
            Node nearestNodeInTour = null;

            for (int i = 0; i < n; i++) {
                if(visited.contains(i))
                    continue;
                
                for (Node node : tour) {
                    double distance = dm.distanceBetween(node.nodeIdx,i);
                    
                    if(distance < smallestDistance){
                        smallestDistance = distance;
                        nearestSensor = i;
                        nearestNodeInTour = node;
                    }
                }
            }

            var newNode = new Node(nearestSensor);
            newNode.edges.add(nearestNodeInTour);
            nearestNodeInTour.edges.add(newNode);

            visited.add(nearestSensor);
            tour.add(newNode);
        }

        return tour;
    }

    private List<Node> findMinimumWeightMatching(List<Node> nodesToMatch){
        return null;
    }


    
    private class Node{
        public int nodeIdx;
        public List<Node> edges;

        public Node(int nodeIdx){
            this.nodeIdx = nodeIdx;
        }
    }
}
