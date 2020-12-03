package uk.ac.ed.inf.aqmaps.unitTests.simulation.planning.collectionOrder.optimisers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.ac.ed.inf.aqmaps.simulation.planning.DistanceMatrix;
import uk.ac.ed.inf.aqmaps.simulation.planning.collectionOrder.optimisers.Optimiser2Opt;

@SuppressWarnings("all")
public class Optimiser2OptTest {

    DistanceMatrix mockDistanceMatrix = mock(DistanceMatrix.class);
    @Test
    public void simpleCrossoverTest(){

        /**
         * the test matrix represents a "knot", swapping 2 and 3 is what makes 2-opt work
         * 1  2
         *  \/|
         *  / \
         * 0  3
         * 
         */

        double[][] dm = new double[][]{
            /**   0,    1,      2,      3 */
            /*0*/{0,    1,      1.5,    1},
            /*1*/{1,    0,      1,      1.5},
            /*2*/{1.5,  1,      0,      1},
            /*3*/{1,    1.5,    1,      0}};

        when(mockDistanceMatrix.distanceBetween(anyInt(),anyInt()))
            .thenAnswer(new Answer() {
                public Object answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    Object mock = invocation.getMock();
                    return dm[(int)args[0]][(int)args[1]];
                }
           });

        when(mockDistanceMatrix.totalDistance(any()))
        .thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                int[] input = (int[])args[0];

                double sum = 0;
                for(int i = 0; i < input.length - 1;i++){
                    sum+= dm[input[i]][input[i+1]];
                }

                return sum;
            }
        });

        Optimiser2Opt testUnit = new Optimiser2Opt(0.001d);

        var output = new int[]{0,2,3,1};
        testUnit.optimise(mockDistanceMatrix, output);


        assertArrayEquals(new int[]{0,3,2,1}, output);

    }
}
