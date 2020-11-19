package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.List;

/**
 * Search trees (or graphs) define the function whose domain and range are all possible search nodes. 
 * Given a node the graph defines the neighbouring nodes available to reach from that node in the context of a search.
 */
public interface SearchTree<T extends SearchNode<T>> {
    public  List<T> getNeighbouringNodes(T node);
}   
