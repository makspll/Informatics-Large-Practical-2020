package uk.ac.ed.inf.aqmaps.visualisation;

/**
 * A general attribute map which maps a range of values from a (min,max) range to attribute buckets of size (max-min)/buckets number uniformly.
 */
public class UniformAttributeMap<C> implements AttributeMap<Float,C>{

    @SuppressWarnings("unchecked")    
    public UniformAttributeMap(Float min, Float max, C ... attributes){
        this.min = min;
        this.max = max;
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public C getFor(Float value) {
        if (value >= max || value < min)   
            throw new IndexOutOfBoundsException("Value was outside of bounds");

        // we simply have to find which symbols bucket the value falls into,
        // each bucket contains (max - min) / no.of. attributes

        Float bucketSize = (max - min) / attributes.length;
        int bucketNumber = (int)Math.floor(value / bucketSize);

        return attributes[bucketNumber];
    }

    /**
     * Minimum value
     */
    private Float min;

    /**
     * Maximum value
     */
    private Float max;

    /**
     * List of attributes corresponding to each bucket
     */
    private C[] attributes;
}
