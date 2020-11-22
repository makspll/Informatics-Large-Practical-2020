package uk.ac.ed.inf.aqmaps.pathfinding.hashing;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

/**
 * hashes real coordinates by scaling them and "snapping" them to a grid..
 */
public class GridSnappingSpatialHash implements SpatialHash {

    /**
     * Create new instance of grid snapping hash
     * @param gridSize the width of each square of the grid
     * @param gridCenter the center of the grid
     */
    public GridSnappingSpatialHash(double gridSize, Coordinate gridCenter){
        this.GRID_SIZE = gridSize;
        this.GRID_CENTER = gridCenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHash(Coordinate a) {

        Vector2D directionFromGrid = Vector2D.create(GRID_CENTER, a);

        int snappedX = (int)(directionFromGrid.getX() / GRID_SIZE);
        int snappedY = (int)(directionFromGrid.getY() / GRID_SIZE);

        // cantor hash (perfect and reversible)
        // https://www.singlelunch.com/2018/09/26/programming-trick-cantor-pairing-perfect-hashing-of-two-integers/
        // we modify it to allow for non-negative numbers
        snappedX = makePositive(snappedX);
        snappedY = makePositive(snappedY);

        return (int)(((0.5 * (snappedX + snappedY)) * (snappedX + snappedY + 1)) + snappedY);
    }

    /**
     * "shifts" positive numbers in order to fit the negative numbers on the positive number line
     * @param n
     * @return
     */
    private int makePositive(int n){
        if(n >= 0){
            return n*= 2;
        }else{
            return (-n)*2 -1;
        }
    }
    /**
     * the length of each square's width in the grid
     */
    private final double GRID_SIZE;

    /**
     * the center of the grid
     */
    private final Coordinate GRID_CENTER;

}
