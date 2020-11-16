package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.List;

public interface Graph {
    public  List<SpatialTreeSearchNode> getNeighbouringNodes(SpatialTreeSearchNode node);
}   
