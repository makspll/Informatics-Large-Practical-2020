package uk.ac.ed.inf.aqmaps.unitTests.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import uk.ac.ed.inf.aqmaps.pathfinding.AstarTreeSearch;
import uk.ac.ed.inf.aqmaps.pathfinding.Graph;
import uk.ac.ed.inf.aqmaps.pathfinding.PathfindingGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.PointGoal;
import uk.ac.ed.inf.aqmaps.pathfinding.SpatialTreeSearchNode;
import uk.ac.ed.inf.aqmaps.pathfinding.StraightLineDistance;

public class AstarTreeSearchTest {


    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void standardGridDeadEndTest(){
        Collection<Polygon> obstacles = new ArrayList<Polygon>(Arrays.asList(
            gf.createPolygon(new Coordinate[]{
                new Coordinate(0,-3),
                new Coordinate(0,-2),
                new Coordinate(2,-2),
                new Coordinate(2,1),
                new Coordinate(0,1),
                new Coordinate(0,2),
                new Coordinate(3,2),
                new Coordinate(3,-3),
                new Coordinate(0,-3)  
            })
        ));

        Graph g = new GridGraph(obstacles);

        var testSearch = new AstarTreeSearch(new StraightLineDistance());

        Coordinate startCoordinate = new Coordinate(0.5,-0.5);
        Coordinate endCoordinate = new Coordinate(3.5,-0.5);

        var startNode = new SpatialTreeSearchNode(startCoordinate, 
            null,
            -1,
            startCoordinate.distance(endCoordinate),
            0);

        var endNode = new PointGoal(endCoordinate);

        var output = new LinkedList<SpatialTreeSearchNode>();

        testSearch.findPath(g, endNode, startNode, 0.0000001d,output);

        assertEquals(startNode,output.peek());
        assertEquals(endCoordinate,output.peekLast().getLocation());
        assertEquals(12,output.size());
    }


    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void standardGridSmallTest(){
        Collection<Polygon> obstacles = new ArrayList<Polygon>(Arrays.asList(
            gf.createPolygon(new Coordinate[]{
                new Coordinate(1,0),
                new Coordinate(1,5),
                new Coordinate(2,5),
                new Coordinate(2,0),
                new Coordinate(1,0),  
            }),
            gf.createPolygon(new Coordinate[]{
                new Coordinate(3,2),
                new Coordinate(4,2),
                new Coordinate(4,1),
                new Coordinate(3,1),
                new Coordinate(3,2),  
            }),
            gf.createPolygon(new Coordinate[]{
                new Coordinate(3,4),
                new Coordinate(3,6),
                new Coordinate(4,6),
                new Coordinate(4,4),
                new Coordinate(3,4),  
            })
        ));

        Graph g = new GridGraph(obstacles);

        var testSearch = new AstarTreeSearch(new StraightLineDistance());

        Coordinate startCoordinate = new Coordinate(0.5,0.5);
        Coordinate endCoordinate = new Coordinate(5.5,5.5);

        var startNode = new SpatialTreeSearchNode(startCoordinate, 
            null,
            -1,
            startCoordinate.distance(endCoordinate),
            0);

        var endNode = new PointGoal(endCoordinate);

        var output = new LinkedList<SpatialTreeSearchNode>();

        testSearch.findPath(g, endNode, startNode, 0.0000001d,output);

        assertEquals(startNode,output.peek());
        assertEquals(endCoordinate,output.peekLast().getLocation());
        assertEquals(13,output.size());


    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void standardGridMultipleGoalsObstaclesTest(){
        Collection<Polygon> obstacles = new ArrayList<Polygon>(Arrays.asList(
            gf.createPolygon(new Coordinate[]{
                new Coordinate(1,0),
                new Coordinate(1,5),
                new Coordinate(2,5),
                new Coordinate(2,0),
                new Coordinate(1,0),  
            }),
            gf.createPolygon(new Coordinate[]{
                new Coordinate(3,2),
                new Coordinate(4,2),
                new Coordinate(4,1),
                new Coordinate(3,1),
                new Coordinate(3,2),  
            }),
            gf.createPolygon(new Coordinate[]{
                new Coordinate(3,4),
                new Coordinate(3,6),
                new Coordinate(4,6),
                new Coordinate(4,4),
                new Coordinate(3,4),  
            })
        ));

        Graph g = new GridGraph(obstacles);

        var testSearch = new AstarTreeSearch(new StraightLineDistance());

        Coordinate startCoordinate = new Coordinate(0.5,0.5);
        Coordinate middleCoordinate = new Coordinate(1.5,-0.5);
        Coordinate middleEndCoordinate = new Coordinate(2.5,3.5);

        Coordinate endCoordinate = new Coordinate(5.5,5.5);

        var startNode = new SpatialTreeSearchNode(startCoordinate, 
            null,
            -1,
            startCoordinate.distance(endCoordinate),
            0);


        PathfindingGoal middleNode = new PointGoal(middleCoordinate);
        PathfindingGoal middleEndNode = new PointGoal(middleEndCoordinate);
        PathfindingGoal endNode = new PointGoal(endCoordinate);

        var output = testSearch.findPath(g, 
            new LinkedList<PathfindingGoal>(
                Arrays.asList(middleNode,middleEndNode,endNode)), 
            startNode, 
            0.000001d);



        assertEquals(13,output.size());
        assertEquals(startNode,output.peek());
        SpatialTreeSearchNode lastNode = null;
        for (SpatialTreeSearchNode n : output) {
            lastNode = n;
        }
        assertEquals(endNode.getPosition(),lastNode.getLocation());

    }

    @Test
    @Timeout(value = 10, unit=TimeUnit.SECONDS) 
    public void standardGridMultipleGoalsInOneTest(){
        Collection<Polygon> obstacles = new ArrayList<Polygon>(Arrays.asList(
 
        ));

        Graph g = new GridGraph(obstacles);

        var testSearch = new AstarTreeSearch(new StraightLineDistance());

        Coordinate startCoordinate = new Coordinate(0,0);
        Coordinate middleCoordinate = new Coordinate(0,0.5);
        Coordinate endCoordinate = new Coordinate(0,-0.5);

        var startNode = new SpatialTreeSearchNode(startCoordinate, 
            null,
            -1,
            startCoordinate.distance(endCoordinate),
            0);


        var middleNode = new PointGoal(middleCoordinate);

        var endNode = new PointGoal(endCoordinate);

        var output = testSearch.findPath(g, new LinkedList<PathfindingGoal>(
                Arrays.asList(middleNode, endNode)), startNode,
                0.55f);

        assertEquals(1,output.size());
        assertEquals(2,startNode.getGoalsReached().size());
        assertEquals(startNode,output.peek());
    }

    private static GeometryFactory gf = new GeometryFactory();

    private class GridGraph implements Graph{

        private Collection<Polygon> obstacles;
        
        public GridGraph(Collection<Polygon> obstacles){
            this.obstacles = obstacles;
        }

        @Override
        public List<SpatialTreeSearchNode> getNeighbouringNodes(SpatialTreeSearchNode node) {

            List<SpatialTreeSearchNode> output = new ArrayList<SpatialTreeSearchNode>();

            var gridDirections = new Vector2D[]{
                new Vector2D(1,0),
                new Vector2D(0,-1),
                new Vector2D(-1,0),
                new Vector2D(0, 1),

            };

            var directionInts = new Integer[]{
                0,90,180,270
            };

            int idx = 0;
            for (Vector2D dir : gridDirections) {

                Coordinate a = node.getLocation();
                Coordinate b = dir.translate(node.getLocation());

                LineString ls = gf.createLineString(new Coordinate[]{a,b});

                boolean intersectsObstacle = false;
                for (Polygon p : obstacles) {
                    
                    if(p.intersects(ls)){
                        intersectsObstacle = true;
                        break;
                    }
                }

                if(intersectsObstacle)
                    continue;

                output.add(new SpatialTreeSearchNode(
                    b, 
                    node, 
                    directionInts[idx],
                    node.getCost() + 1));

                idx += 1;
            }

            return output;
            
        }

    }
}