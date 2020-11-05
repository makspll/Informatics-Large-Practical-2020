package uk.ac.ed.inf.aqmaps.simulation.planning;

import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Obstacle;
import uk.ac.ed.inf.aqmaps.simulation.PathSegment;
import uk.ac.ed.inf.aqmaps.simulation.Sensor;

public class OptimalConstrainedPathPlanner extends BaseConstrainedPathPlanner {


	public OptimalConstrainedPathPlanner(double moveLength, double readingRange, int maxMoves, int minAngle,
			int maxAngle, int angleIncrement) {
		super(moveLength, readingRange, maxMoves, minAngle, maxAngle, angleIncrement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Queue<PathSegment> planPath(Coordinate startCoordinate, Queue<Sensor> route, Collection<Obstacle> obstacles) {
		// TODO Auto-generated method stub
		return null;
	}


}
