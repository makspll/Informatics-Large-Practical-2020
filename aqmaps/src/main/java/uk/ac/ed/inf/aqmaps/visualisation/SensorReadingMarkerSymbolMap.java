package uk.ac.ed.inf.aqmaps.visualisation;


/**
 * The mapping of sensor reading values to marker symbols for visualisation purposes
 */
public class SensorReadingMarkerSymbolMap implements AttributeMap<Float,MarkerSymbol>{

    /**
     * Create new reading to marker symbol mapping with the given minimum and maximum reading values and marker symbols
     * @param minValue the minimum sensor reading allowed (inclusive)
     * @param maxValue the maximum sensor reading allowed (exclusive)
     * @param symbols the list of symbols to be attributed to buckets of equal size between the minimum and maximum
     */
    public SensorReadingMarkerSymbolMap(Float minValue,Float maxValue,MarkerSymbol... symbols){
        this.min = minValue;
        this.max = maxValue;
        this.markerSymbols = symbols;
    }


    /**
     * {@inheritDoc}
     */
    @Override
	public MarkerSymbol getFor(Float value) {
        if (value >= max || value < min)   
            throw new IndexOutOfBoundsException("Value was outside of bounds");

        // we simply have to find which symbols bucket the value falls into,
        // each bucket contains (max - min) / no.of. markerSymbols

        Float bucketSize = (max - min) / markerSymbols.length;
        int bucketNumber = (int)Math.floor(value / bucketSize);

        return markerSymbols[bucketNumber];
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
     * List of marker symbol buckets
     */
    private MarkerSymbol[] markerSymbols;
}
