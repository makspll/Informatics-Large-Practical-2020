package uk.ac.ed.inf.aqmaps.visualisation;

/**
 * Attribute maps divide their domain into attribute buckets and allow for quick retrieval of the necessary attributes
 */
public interface AttributeMap<T,C> {

    /**
     * Retrieve attribute for the given input
     * @param o input
     * @return
     */
    public C getFor(T o);
}
