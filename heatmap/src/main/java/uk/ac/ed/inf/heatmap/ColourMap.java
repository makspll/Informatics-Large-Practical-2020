package uk.ac.ed.inf.heatmap;

/**
 * An interface guaranteeing a value to colour mapping functionality for different types
 */
public interface ColourMap<I,C> {
    public C getColour(I value) throws IndexOutOfBoundsException;
}
