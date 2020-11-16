package uk.ac.ed.inf.aqmaps.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;

/**
 * A data structure representing a tree search node for 
 * spatial pathfinding problems with limited angles.
 */
public class SpatialTreeSearchNode{


    /**
     * Creates a new spatial tree search node which is fully specified apart from
     * the heuristic value
     * @param location
     * @param parent
     * @param directionFromParent
     * @param cost
     */
    public SpatialTreeSearchNode(Coordinate location,
        SpatialTreeSearchNode parent,
        int directionFromParent,
        double cost){
            this.location = location;
            this.parentNode = parent;
            this.directionFromParent = directionFromParent;
            this.cost = cost;
        }

    /**
     * Creates fully specified tree search node
     * @param location
     * @param parent
     * @param directionFromParent
     * @param heuristic
     * @param cost
     */
    public SpatialTreeSearchNode(Coordinate location,
        SpatialTreeSearchNode parent, 
        int directionFromParent, 
        double heuristic, 
        double cost
        ){

        this.location = location;
        this.parentNode = parent;
        this.directionFromParent = directionFromParent;
        this.heuristic = heuristic;
        this.cost = cost;
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

    public SpatialTreeSearchNode getParentNode() {
        return this.parentNode;
    }

    public int getDirectionFromParent() {
        return this.directionFromParent;
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

    private double heuristic = -1;
    private double cost = -1;
    private SpatialTreeSearchNode parentNode = null;
    private int directionFromParent = -1;
    private Coordinate location;
    private Deque<PathfindingGoal> goalsReached = new LinkedList<PathfindingGoal>();
}
