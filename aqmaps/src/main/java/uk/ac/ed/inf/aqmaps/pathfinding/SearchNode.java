package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.pathfinding.goals.PathfindingGoal;

public abstract class SearchNode<T extends SearchNode<T>> {
    /**
     * Creates a new spatial tree search node which is fully specified apart from
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
     * Creates fully specified search node
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

    public Deque<PathfindingGoal> getGoalsReached() {
        return this.goalsReached;
    }

    public void setGoalsReached(Deque<PathfindingGoal> goalReached) {
        this.goalsReached = goalReached;
    }

    public void addGoalReached(PathfindingGoal goalReached){
        this.goalsReached.add(goalReached);
    }

    public int getNumberOfGoalsReached(){
        return this.goalsReached.size();
    }

    public void removeGoalReached(PathfindingGoal goalReached){
        this.goalsReached.remove(goalReached);
    }

    public double getHeuristic() {
        return this.heuristic;
    }

    public Coordinate getLocation() {
        return this.location;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getCost() {
        return this.cost;
    }

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
            "location='" + getLocation() + "'" +
            ", heuristic+cost='" + (getHeuristic() + getCost())  + "'" +
            "}";
    }

    protected double heuristic = -1;
    protected double cost = -1;
    protected T parentNode = null;
    protected Coordinate location;
    protected Deque<PathfindingGoal> goalsReached = new LinkedList<PathfindingGoal>();
}
