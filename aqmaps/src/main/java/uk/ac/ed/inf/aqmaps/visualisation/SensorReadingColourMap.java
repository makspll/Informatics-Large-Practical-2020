package uk.ac.ed.inf.aqmaps.visualisation;

/**
 * A mapping of sensor readings to colours for visualisation purposes
 */
public class SensorReadingColourMap implements AttributeMap<Float,String>{


    /**
     * Create new colour map with the domain specified by the minimum and maximum sensor readings and 
     * the number of colors defined by the colours list
     * @param minValue
     * @param maxValue
     * @param colours
     */
    public SensorReadingColourMap(Float minValue,Float maxValue,String... colours){
        this.min = minValue;
        this.max = maxValue;
        this.colours = colours;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public String getFor(Float value) {
        if (value >= max || value < min)
            throw new IndexOutOfBoundsException("Value was outside of bounds");
       
        // we simply have to find which colour bucket the value falls into,
        // each bucket contains (max - min) / no.of. colours

        Float bucketSize = (max - min) / (float)colours.length;
        int bucketNumber = (int)Math.floor(value / bucketSize);

        return colours[bucketNumber];
    }

    /**
     * Minimum sensor reading
     */
    private Float min;

    /**
     * Maximum sensor reading
     */
    private Float max;

    /**
     * List of colour buckets
     */
    private String[] colours;
}