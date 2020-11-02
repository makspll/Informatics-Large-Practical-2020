package uk.ac.ed.inf.aqmaps.visualisation;

public class SensorReadingColourMap implements AttributeMap<Float,String>{


    public SensorReadingColourMap(Float minValue,Float maxValue,String... colours){
        this.min = minValue;
        this.max = maxValue;
        this.colours = colours;
    }

    @Override
	public String getFor(Float value) {
        if (value >= max || value < min)   
            throw new IndexOutOfBoundsException("Value was outside of bounds");

        // we simply have to find which colour bucket the value falls into,
        // each bucket contains (max - min) / no.of. colours

        Float bucketSize = (max - min) / colours.length;
        int bucketNumber = Math.round(value / bucketSize);

        return colours[bucketNumber];
    }

    private Float min;
    private Float max;
    private String[] colours;
}