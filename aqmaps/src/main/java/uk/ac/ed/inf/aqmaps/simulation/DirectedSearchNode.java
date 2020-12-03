package uk.ac.ed.inf.aqmaps.simulation;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.SearchNode;

/**
 * A data structure representing a tree search node for 
 * spatial pathfinding problems with integer angles.
 */
public class DirectedSearchNode extends SearchNode<DirectedSearchNode>{


    /**
     * Creates a new spatial tree search node which is fully specified apart from
     * the heuristic value
     * @param location the location of the new node
     * @param parent the parent of the given node (if null the node is considered to be the starting node)
     * @param directionFromParent the direction from the parent node taken to reach the node
     * @param cost the cost of reaching this node from the start of the search
     */
    public DirectedSearchNode(Coordinate location,
        DirectedSearchNode parent,
        int directionFromParent,
        double cost){
            super(location, parent, cost);
            this.directionFromParent = directionFromParent;
        }

    /**
     * Creates fully specified tree search node
     * @param location
     * @param parent
     * @param directionFromParent
     * @param heuristic
     * @param cost
     */
    public DirectedSearchNode(Coordinate location,
        DirectedSearchNode parent, 
        int directionFromParent, 
        double heuristic, 
        double cost
        ){
        super(location, parent, heuristic, cost);
        this.directionFromParent = directionFromParent;
    }
    
    /**
     * retrieves the direction between this node's parent and itself 
     */
    public int getDirectionFromParent() {
        return this.directionFromParent;
    }


    private int directionFromParent = -1;

}
