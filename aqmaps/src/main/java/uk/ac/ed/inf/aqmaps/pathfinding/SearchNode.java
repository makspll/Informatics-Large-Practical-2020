package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;

/**
 * Search Nodes are used to hold the path information in pathfinding algorithms including heuristic, cost and parent node values. Search nodes also contain information about which
 * pathfinding goals can be achieved from their position (can be multiple).
 */
public class SearchNode<T extends SearchNode<T>> {


    /**
     * Creates a new  search node which is fully specified apart from
     * the heuristic value
     * @param location the location of the new node
     * @param parent the parent of the given node (if null the node is considered to be the starting node)
     * @param cost the cost of reaching this node from the start of the search
     */
    public SearchNode(Coordinate location,
        T parent,
        double cost){
            this.location = location;
            this.parentNode = parent;
            this.cost = cost;

        }

    /**
     * Creates a fully specified search node
     * @param location
     * @param parent
     * @param heuristic
     * @param cost
     */
    public SearchNode(Coordinate location,
        T parent, 
        double heuristic, 
        double cost
        ){

        this.location = location;
        this.parentNode = parent;
        this.heuristic = heuristic;
        this.cost = cost;
    }

    /**
     * Retrieve the deque of goals achievable from this search node's position
     */
    public Deque<PathfindingGoal> getGoalsReached() {
        return this.goalsReached;
    }

    /**
     * Set the goals achievable from this search node's position
     * @param goalReached
     */
    public void setGoalsReached(Deque<PathfindingGoal> goalReached) {
        this.goalsReached = goalReached;
    }

    /**
     * Add a goal achievable from this node's position to the tail of its deque
     * @param goalReached
     */
    public void addGoalReached(PathfindingGoal goalReached){
        this.goalsReached.add(goalReached);
    }

    /**
     * Retrieves the number of goals reached from this node
     * @return
     */
    public int getNumberOfGoalsReached(){
        return this.goalsReached.size();
    }

    /**
     * pop the first goal achievable at this node's position from its deque
     * @param goalReached
     */
    public void removeGoalReached(PathfindingGoal goalReached){
        this.goalsReached.remove(goalReached);
    }

    /**
     * Retrieve the heuristic value of this node
     */
    public double getHeuristic() {
        return this.heuristic;
    }

    /**
     * Retrieve the coordinates in space of this node
     */
    public Coordinate getCoordinates() {
        return this.location;
    }

    /**
     * Set the heuristic value of this node
     * @param heuristic
     */
    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Retrieve the cost of reaching this search node
     * @return
     */
    public double getCost() {
        return this.cost;
    }

    /**
     * Retrieve the parent node of this node
     * @return
     */
    public T getParentNode() {
        return this.parentNode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return "{" +
            "location='" + getCoordinates() + "'" +
            ", heuristic+cost='" + (getHeuristic() + getCost())  + "'" +
            "}";
    }

    protected double heuristic = -1;
    protected double cost = -1;
    protected T parentNode = null;
    protected Coordinate location;
    protected Deque<PathfindingGoal> goalsReached = new LinkedList<PathfindingGoal>();
}
