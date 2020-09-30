package uk.ac.ed.inf;

public class UniformIntToHexStringColourMap implements ColourMap<Integer,String> {

    /**
     * initialize an emissions to colours map which splits the colours evenly between the min(inclusive) and max(exclusive) values
     * @param colours
     */
    public UniformIntToHexStringColourMap(int min, int max, String... colours){
        assert max > min;
        rgbStringValues = colours;
        this.min = min;
        this.max = max;
    }

    /**
     * Maps given integer value (between min and max, inclusive and exclusve respectively) to a hex string
     */
    @Override
    public String getColour(Integer value) throws IndexOutOfBoundsException{
        if (value < max && value >= min)   
            throw new IndexOutOfBoundsException("Value was outside of bounds");

        // we simply have to find which colour bucket the value falls into,
        // each bucket contains (max - min) / no.of. colours

        int bucketSize = (max - min) / rgbStringValues.length;
        int bucketNumber = (int)Math.floor(value / bucketSize);

        return rgbStringValues[bucketNumber];
    
    }

    /** 
     * colour values in order of lower to higher emissions 
     * */
    private String[] rgbStringValues;
    private Integer min;
    private Integer max;
}
