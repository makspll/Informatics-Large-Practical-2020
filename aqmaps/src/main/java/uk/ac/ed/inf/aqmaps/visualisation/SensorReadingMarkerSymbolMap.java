package uk.ac.ed.inf.aqmaps.visualisation;


public class SensorReadingMarkerSymbolMap implements AttributeMap<Float,MarkerSymbol>{

    public SensorReadingMarkerSymbolMap(Float minValue,Float maxValue,MarkerSymbol... symbols){
        this.min = minValue;
        this.max = maxValue;
        this.markerSymbols = symbols;
    }

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

    private Float min;
    private Float max;
    private MarkerSymbol[] markerSymbols;
}
