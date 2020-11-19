package uk.ac.ed.inf.aqmaps.simulation.planning;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;

/**
 * Class which stores distance information between sensors
 */
public abstract class DistanceMatrix {
    
    /**
     * Creates new blank distance matrix
     */
    public DistanceMatrix(){}

    /**
     * Fills in the distance matrix with distance data for the given sensors
     * @param sensors the list of sensors
     */
    public void setupDistanceMatrix(Sensor[] sensors){

        // compute avoidance distance between each pair of sensors
        int n = sensors.length;
        distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {

                if(i == j)
                    distanceMatrix[i][j] = 0;
                else{
                    double distance = distanceMetric(sensors[i], sensors[j]);
                    distanceMatrix[i][j] = distance;
                    distanceMatrix[j][i] = distance;
                }

            }
        }
    }

    /**
     * returs the distance from sensor a to sensor b at the given indices in the sensor list
     * @param a sensor index
     * @param b sensor index
     * @return
     */
    public double distanceBetween(int a, int b){
        return distanceMatrix[a][b];
    }   

    /**
     * calculates the total distance of the subpath specified with start and end indices within the given path of sensor indices.
     * @param route the path
     * @param startIdx inclusive start index
     * @param endIdx exclusive end index
     * @return
     */
    public double totalDistance(int[] route, int startIdx, int endIdx){
        assert endIdx <= route.length 
            && startIdx >= 0;

        double distance = 0;
        for (int i = startIdx; i < endIdx - 1; i++) {
            int curr = route[i];
            int next = route[i+1];
            distance += distanceBetween(curr,next);
        }

        return distance;
    }

    public double totalDistance(int[] route){
        return totalDistance(route,0, route.length);
    }

    /**
     * the specific distance measure used to calculate distances. Does NOT have to be symmetric
     * @param a sensor 1
     * @param b sensor 2
     * @return
     */
    protected abstract double distanceMetric(Sensor a, Sensor b);

    protected double[][] distanceMatrix;
}
