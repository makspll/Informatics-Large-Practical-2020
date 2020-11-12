package uk.ac.ed.inf.aqmaps.utilities;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public class MathUtilities {
    /**
     * returns the angle from the eastern direction clockwise between 0 and 360 of the vector
     * @param a a vector representing a direction
     * @return
     */
    public static double angleFromEast(Vector2D a){
        double angle = -Math.toDegrees(Math.atan2(a.getX(), a.getY())) + 90;

        // convert the  -180 to -0 to
        if (angle < 0.0) {
            angle += 360.0;
        }  
        
        return angle;
    }

    /**
     * gets unit vector in the direction of angle
     * @param angle double between 0 and 360 representing angle from the eastern direction anticlockwise (phase angle in range (0,pi))
     * @return
     */
    public static Vector2D getHeadingVector(double angle){
        angle = Math.toRadians(angle);

        double x = Math.cos(angle);
        double y = Math.sin(angle);

        return new Vector2D(x, y).normalize();
    }

    public static boolean thresholdEquals(double a, double b, double epsilon){
        return Math.abs(a - b) < epsilon;
    }
    public static boolean thresholdEquals(double a, double b){
        return Math.abs(a - b) < epsilon;
    }


    public static boolean thresholdEquals(Coordinate a, Coordinate b, double epsilon){
        return Math.abs(a.distance(b)) < epsilon;
    }
    public static boolean thresholdEquals(Coordinate a, Coordinate b){
        return Math.abs(a.distance(b)) < epsilon;
    }

    public static double epsilon = 0.000001;
}
