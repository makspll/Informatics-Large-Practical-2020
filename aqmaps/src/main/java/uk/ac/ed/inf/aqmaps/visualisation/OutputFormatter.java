package uk.ac.ed.inf.aqmaps.visualisation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Queue;
import java.util.stream.Stream;

import com.mapbox.geojson.FeatureCollection;

import org.locationtech.jts.geom.Coordinate;

import uk.ac.ed.inf.aqmaps.simulation.Sensor;
import uk.ac.ed.inf.aqmaps.simulation.planning.path.PathSegment;

public class OutputFormatter {

    public static void writePath(Queue<PathSegment> flightPath, OutputStream file) throws IOException{

        // open writer to the output stream
        try(OutputStreamWriter writer = new OutputStreamWriter(file)){
            
            // number of leftover segments
            int segmentCount = flightPath.size();
            
            // for each path segment writa a comma separated line
            for (PathSegment pathSegment : flightPath) {

                // start point - long,lat
                Coordinate startPoint = pathSegment.getStartPoint();
                writer.write(String.format("%s,",startPoint.getX()));
                writer.write(String.format("%s,",startPoint.getY()));

                // direction - int
                int direction = pathSegment.getDirection();
                writer.write(String.format("%s,",direction));

                // end point - long,lat
                Coordinate endPoint = pathSegment.getEndPoint();
                writer.write(String.format("%s,",endPoint.getX()));
                writer.write(String.format("%s,",endPoint.getY()));

                // location of connected sensor, or null if none read
                Sensor readSensor = pathSegment.getSensorRead();
                writer.write(readSensor == null ? "null" : readSensor.getW3WLocation());

                // this will be 0 when we just processed last segment
                segmentCount -= 1;

                // write newline character after each line except the last one
                if(segmentCount != 0)
                    writer.write("\n");

            }
        }

        // resources will be disposed off automatically via try block
    }

    public static void writeReadingsMap(FeatureCollection readingsMap,OutputStream file) throws IOException{
        
        // open writer to the output stream
        try(OutputStreamWriter writer = new OutputStreamWriter(file)){
            
            // convert feature collection to json and dump it to the output stream
            writer.write(readingsMap.toJson());
        }

        // resources will be disposed off automatically via try block
    }
}
