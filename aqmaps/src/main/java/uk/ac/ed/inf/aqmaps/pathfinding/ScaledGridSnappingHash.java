package uk.ac.ed.inf.aqmaps.pathfinding;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

/**
 * hashes real coordinates by scaling them and "snapping" them to a unit length grid.
 */
public class ScaledGridSnappingHash implements SpatialHash {

    public ScaledGridSnappingHash(double gridSize, Coordinate gridCenter){
        this.GRID_SIZE = gridSize;
        this.GRID_CENTER = gridCenter;
    }

    @Override
    public int getHash(Coordinate a) {

        Vector2D directionFromGrid = Vector2D.create(GRID_CENTER, a);

        int snappedX = (int)(directionFromGrid.getX() / GRID_SIZE);
        int snappedY = (int)(directionFromGrid.getY() / GRID_SIZE);

        // cantor hash 
        // https://www.singlelunch.com/2018/09/26/programming-trick-cantor-pairing-perfect-hashing-of-two-integers/
        
        return (int)(((0.5 * (snappedX + snappedY)) * (snappedX + snappedY + 1)) + snappedY);
    }
    
    private final double GRID_SIZE;
    private final Coordinate GRID_CENTER;

}
