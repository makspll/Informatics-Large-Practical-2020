package uk.ac.ed.inf.aqmaps.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;


/**
 * Bounding Volume Hierarchy Node. This class forms a tree of AABB (Axis aligned bounding boxes) for internal nodes
 * and of any shapes at the leaf nodes. Allows for quick broad phase collision checks between objects. Will never return a false negative but might return
 * false positives. I.e. this structure only tells you which objects are possibly colliding (whose AABB's intersect).
*/
public class BVHNode<T extends Shape>{

    /**
     * Construct a new bvh hierarchy with the given shapes at the leaf nodes
     * @param shapes
     */
    public BVHNode(Collection<T> shapes){
        
        // envelop every shape
        var envelopeShapePairs = new ArrayList<EnvelopeShapePair>(shapes.size());

        for (T shape : shapes) {
            envelopeShapePairs.add(new EnvelopeShapePair(shape));
        }

        // create AABB from all envelopes present in this node
        AABB = getContainingEnvelope(envelopeShapePairs);

        // divide the children shapes into 2 sub-trees
        partitionIntoChildren(envelopeShapePairs);
    }

    /**
     * Retrieves all possibly coliding objects (their bounding boxes intersect) from within the tree.
     * @param other
     * @return
     */
    public Collection<T> getPossibleCollisions(Geometry other){
        Collection<T> colisions = new ArrayList<T>();

        var otherEnvelope = other.getEnvelopeInternal();
        writePossibleCollisionsWithInto(otherEnvelope, colisions);

        return colisions;
    }

    /**
     * constructs a node given already pre-computed envelope shape pairs
     * @param shapeEnvelopePairs
     */
    private BVHNode(ArrayList<EnvelopeShapePair> shapeEnvelopePairs){

        AABB = getContainingEnvelope(shapeEnvelopePairs);

        partitionIntoChildren(shapeEnvelopePairs);
    }
    
    /**
     * utility function to save on the creation of new collections. Retreives possibly coliding objects under or in this node and 
     * writes them to the output array
     * @param other
     * @param output the output array
     */
    private void writePossibleCollisionsWithInto(Envelope other,Collection<T> output){

        if(contents != null){
            // if we reached this node this means that this AABB is intersecting with the given envelope
            output.add(contents);
        } else {

            // if the AABB's of either child do not intersect with the given envelope
            // then we do not need to search within them
            if(leftChild != null 
                && !leftChild.AABB.disjoint(other))
                leftChild.writePossibleCollisionsWithInto(other, output);

            if(rightChild != null
                && !rightChild.AABB.disjoint(other))
                rightChild.writePossibleCollisionsWithInto(other, output);
        }
    }

    /**
     * Forms an envelope around all the given shape envelope pairs
     * @param shapeEnvelopePairs
     * @return
     */
    private Envelope getContainingEnvelope(List<EnvelopeShapePair> shapeEnvelopePairs){
        var shapes = new Geometry[shapeEnvelopePairs.size()]; 

        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = shapeEnvelopePairs.get(i).SHAPE.getShape();
        }

        return GeometryUtilities.geometryFactory
                .createGeometryCollection(shapes)
                .getEnvelopeInternal();

    }

    /**
     * distributes the given shapes into left and right children nodes as evenly as possible.
     * If only one shape is given, it is placed under the current node.
     * @param shapes
     */
    private void partitionIntoChildren(List<EnvelopeShapePair> shapes){

        if(shapes.size() == 0){
            return;
        }

        // if we have one shape only, place the object as a leaf
        if(shapes.size() == 1){
            contents = shapes.get(0).SHAPE;
            return;
        }

        // otherwise
        // find "longest" axis

        double smallestX = Double.MIN_VALUE;
        double largestX = Double.MAX_VALUE; 
        double smallestY = Double.MIN_VALUE;
        double largestY = Double.MAX_VALUE;

        for (int i = 0; i < shapes.size(); i++) {

            var currEnvelope = shapes.get(i).ENVELOPE;

            if(currEnvelope.getMaxX() > largestX)
                largestX = currEnvelope.getMaxX();
            else if(currEnvelope.getMinX() < smallestX){
                smallestX = currEnvelope.getMinX();
            }
            
            if(currEnvelope.getMaxY() > largestY)
                largestY = currEnvelope.getMaxY();
            else if (currEnvelope.getMinY() < smallestY)
                smallestY = currEnvelope.getMinY();
        }
        
        double xWidth = largestX - smallestX;
        double yWidth = largestY - smallestY;
        
        // we create a comparator to sort by the longest axis
        Comparator<EnvelopeShapePair> envelopeShapePairComparator = null;
        
        if(xWidth > yWidth){
            // sort by x values
            envelopeShapePairComparator = Comparator.comparing(a->a.ENVELOPE.centre().getX());
        } else {
            // sort by y values
            envelopeShapePairComparator = Comparator.comparing(a->a.ENVELOPE.centre().getY());
        }
        
        //sort the envelope/shape pairs
        shapes.sort(envelopeShapePairComparator);

        // pick split index s
        // such that every child shape at index i < s is placed in the left child node
        // and every child shape at index i >= on the right

        // even splits for even lengths,
        // for odd lengths the right child receives more children
        int s = (int)Math.ceil(((float)(shapes.size()-1) / 2f));

        // form left subtree (number of objects here is equal to s)
        // if s is zero this subtree gets no objects
        var shapesLeft = new ArrayList<EnvelopeShapePair>(s);
        for (int i = 0; i < s; i++) {
            shapesLeft.add(shapes.get(i));
        }

        var shapesRight = new ArrayList<EnvelopeShapePair>(s);
        for (int i = s; i < shapes.size(); i++) {
            shapesRight.add(shapes.get(i));
        }

        if(shapesLeft.size() > 0)
            leftChild = new BVHNode<T>(shapesLeft);
        if(shapesRight.size() > 0)
            rightChild = new BVHNode<T>(shapesRight);
    }


    /**
     * Utility class holding envelope and shape information for a shape
     */
    private class EnvelopeShapePair{
        public final Envelope ENVELOPE;
        public final T SHAPE;

        public EnvelopeShapePair(T geo){
            this.ENVELOPE = geo.getShape().getEnvelopeInternal();
            this.SHAPE = geo;
        }
    }

    /**
     * the AABB of this node, envelops all the children nodes and leaf nodes under this node
     */
    private Envelope AABB;

    /**
     * The left child node
     */
    private BVHNode<T> leftChild;

    /**
     * The right child node
     */
    private BVHNode<T> rightChild;

    /**
     * The shape contained in this node if it is a leaf node
     */
    private T contents = null;
}
