package uk.ac.ed.inf.aqmaps.pathfinding.hashing;

import org.locationtech.jts.geom.Coordinate;

/**
 * Classes implementing this interface allow fuzzy spatial hashing of coordinates. This is used for
 * detecting whether or not 2 points are roughly equivalent or in special data structures.
 */
public interface SpatialHash {

    /**
     * Returns a hash for the given coordinate. A good hash will be equal for points which are near each other
     * according to some metric, and different for ones that are not.
     * @param a the coordinate to hash
     * @return
     */
    public int getHash(Coordinate a);
}
